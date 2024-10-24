package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.battery.BatteryBlock;
import dev.mariany.copperworks.block.custom.battery.BatteryClientData;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.util.ModConstants;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlockEntity extends BlockEntity implements SingleStackInventory.SingleStackBlockEntityInventory, SidedInventory {
    private static final String CHARGING_ITEM_NBT = "ChargingItem";
    private static final IntProperty CHARGE = ModProperties.CHARGE;

    private ItemStack chargingItem = ItemStack.EMPTY;
    private final BatteryClientData clientData = new BatteryClientData();

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.BATTERY, pos, state);
    }

    public BatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.chargingItem.isEmpty()) {
            nbt.put(CHARGING_ITEM_NBT, this.chargingItem.encode(registryLookup));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(CHARGING_ITEM_NBT, NbtElement.COMPOUND_TYPE)) {
            this.chargingItem = ItemStack.fromNbt(registryLookup, nbt.getCompound(CHARGING_ITEM_NBT))
                    .orElse(ItemStack.EMPTY);
        } else {
            this.chargingItem = ItemStack.EMPTY;
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER,
                ContainerComponent.fromStacks(List.of(this.chargingItem)));
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        this.chargingItem = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
                .copyFirstStack();
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(CHARGING_ITEM_NBT);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    public BatteryClientData getClientData() {
        return this.clientData;
    }

    @Override
    public ItemStack getStack() {
        return this.chargingItem;
    }

    @Override
    public void setStack(ItemStack chargingItem) {
        setStack(chargingItem, true);
    }

    public void setStack(ItemStack chargingItem, boolean applyCharging) {
        ItemStack itemStack = chargingItem.copy();
        if (applyCharging && ModUtils.itemNeedsCharge(itemStack)) {
            itemStack.set(CopperworksComponents.CHARGING, true);
        }
        this.chargingItem = itemStack;
        notifyChange(this);
    }

    public void takeStack(PlayerEntity player) {
        this.chargingItem.remove(CopperworksComponents.CHARGING);
        if (!player.getWorld().isClient) {
            player.giveItemStack(this.chargingItem);
        }
        this.chargingItem = ItemStack.EMPTY;
        notifyChange(this);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    public boolean isCharging() {
        if (this.world != null) {
            BlockState blockState = this.world.getBlockState(this.pos);
            if (blockState.getBlock() instanceof BatteryBlock) {
                if (blockState.get(CHARGE) <= 0) {
                    return false;
                }
            }
        }
        return ModUtils.itemNeedsCharge(this.chargingItem, true);
    }

    public int getChargeRate() {
        return this.chargingItem.getOrDefault(CopperworksComponents.CHARGE_RATE, 1);
    }

    private int getMaxCharge() {
        return this.chargingItem.getOrDefault(CopperworksComponents.MAX_CHARGE, 0) + 1;
    }

    private int getCurrentCharge() {
        return this.chargingItem.getOrDefault(CopperworksComponents.CHARGE, 0);
    }

    public float getChargeProgress() {
        int maxCharge = this.getMaxCharge();
        int charge = this.getCurrentCharge();
        if (maxCharge == 0) {
            return 0;
        }
        return (float) charge / (float) maxCharge;
    }

    public void tick(World world, BlockPos pos, BlockState blockState, BatteryBlockEntity batteryBlockEntity) {
        ItemStack chargeStack = batteryBlockEntity.getStack();
        boolean isCharging = batteryBlockEntity.isCharging();
        boolean stackCharging = ModUtils.isCharging(chargeStack);

        if (isCharging != stackCharging) {
            chargeStack.set(CopperworksComponents.CHARGING, isCharging);
            notifyChange(batteryBlockEntity);
        }

        if (isCharging) {
            int chargeRate = batteryBlockEntity.getChargeRate();
            boolean chargeEveryTick = chargeRate <= 1;
            boolean didCharge;

            if (chargeEveryTick || world.getTime() % chargeRate == 0) {
                didCharge = batteryBlockEntity.chargeItem(chargeStack);
                if (didCharge) {
                    notifyChange(batteryBlockEntity);
                }
            }

            ContainerComponent convertsToContainer = chargeStack.getOrDefault(CopperworksComponents.CONVERTS_TO,
                    ContainerComponent.DEFAULT);
            ItemStack convertsTo = convertsToContainer.copyFirstStack();

            if (batteryBlockEntity.getCurrentCharge() >= batteryBlockEntity.getMaxCharge()) {
                if (!convertsTo.isEmpty()) {
                    setStack(convertsTo);
                    notifyChange(batteryBlockEntity);
                }

                playDoneChargingSound(world, pos);

                if (!world.isClient) {
                    int batteryChargeDecrementAmount = MathHelper.nextInt(world.getRandom(), 0, 1);
                    int newBatteryCharge = MathHelper.clamp(blockState.get(CHARGE) - batteryChargeDecrementAmount, 0,
                            ModConstants.MAX_BATTERY_CHARGE);

                    if (batteryChargeDecrementAmount != 0) {
                        world.setBlockState(pos, blockState.with(CHARGE, newBatteryCharge));
                        world.updateNeighborsAlways(pos, blockState.getBlock());
                        if (newBatteryCharge <= 0) {
                            playOutOfChargeSound(world, pos);
                        }
                    }
                }
            }
        }
    }

    public static void notifyChange(BatteryBlockEntity batteryBlockEntity) {
        World world = batteryBlockEntity.getWorld();
        if (world != null) {
            BlockPos blockPos = batteryBlockEntity.getPos();
            BlockState blockState = batteryBlockEntity.getCachedState();
            batteryBlockEntity.markDirty();
            world.updateListeners(blockPos, blockState, blockState, 0);
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
        }
    }

    private void playChargeSound(@NotNull World world, BlockPos pos) {
        world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSoundEvents.CHARGE,
                SoundCategory.BLOCKS, 0.5F + world.random.nextFloat(), 0.7F + 0.5F * this.getChargeProgress(), false);
    }

    private void playDoneChargingSound(@NotNull World world, BlockPos pos) {
        world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSoundEvents.DONE_CHARGING,
                SoundCategory.BLOCKS, 0.2F, 1F, false);
    }

    private void playOutOfChargeSound(@NotNull World world, BlockPos pos) {
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSoundEvents.OUT_OF_CHARGE,
                SoundCategory.BLOCKS, 0.375F, 1F);
    }

    private boolean chargeItem(ItemStack itemStack) {
        int maxCharge = this.getMaxCharge();
        int charge = this.getCurrentCharge();

        if (maxCharge > charge) {
            itemStack.set(CopperworksComponents.CHARGE, charge + 1);
            return true;
        }

        return false;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.chargingItem.isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public static final class Client {
        public static void tick(World world, BlockPos pos, BatteryClientData clientData) {
            if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
                if (batteryBlockEntity.getStack().isEmpty()) {
                    clientData.resetRotation();
                    clientData.resetTicksUntilSound();
                } else {
                    boolean isCharging = batteryBlockEntity.isCharging();
                    int flag = BatteryClientData.NO_ITEM;

                    if (!batteryBlockEntity.getStack().isEmpty()) {
                        flag = isCharging ? BatteryClientData.CHARGING : BatteryClientData.NOT_CHARGING;
                        if (isCharging && clientData.incrementTicksUntilSound()) {
                            batteryBlockEntity.playChargeSound(world, pos);
                        }
                    }

                    clientData.rotateDisplay(flag);
                }
            }
        }
    }
}
