package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.custom.battery.BatteryClientData;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BatteryBlockEntity extends BlockEntity implements SingleStackInventory.SingleStackBlockEntityInventory {
    private static final String CHARGING_ITEM_NBT = "ChargingItem";

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
        this.chargingItem = chargingItem;
    }

    public void takeStack(PlayerEntity player) {
        player.giveItemStack(this.chargingItem);
        this.chargingItem = ItemStack.EMPTY;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    public boolean isCharging() {
        return ModUtils.itemNeedsCharge(this.chargingItem);
    }

    public float getChargeProgress() {
        int charge = this.chargingItem.getOrDefault(ModComponents.CHARGE, 0);
        int maxCharge = this.chargingItem.getOrDefault(ModComponents.MAX_CHARGE, 0);
        if (maxCharge == 0) {
            return 0;
        }
        return (float) charge / (float) maxCharge;
    }

    public int getChargeRate() {
        return this.chargingItem.getOrDefault(ModComponents.CHARGE_RATE, 1);
    }

    public void tick(World world, BlockPos pos, BlockState blockState, BatteryBlockEntity batteryBlockEntity) {
        ItemStack chargeStack = batteryBlockEntity.getStack();
        int chargeRate = this.getChargeRate();

        if (chargeRate <= 1 || world.getTime() % chargeRate == 0) {
            if (chargeItem(chargeStack)) {
                batteryBlockEntity.markDirty();
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);

                int currentCharge = chargeStack.getOrDefault(ModComponents.CHARGE, 0);

                if (currentCharge % 20 * 4 == 0) {
                    batteryBlockEntity.playChargeSound(world, pos);
                }
            }
        }
    }

    public void playChargeSound(@NotNull World world, BlockPos pos) {
        world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSoundEvents.CHARGE,
                SoundCategory.BLOCKS, 0.5F + world.random.nextFloat(), 0.7F + 0.5F * this.getChargeProgress(), false);
    }

    private boolean chargeItem(ItemStack itemStack) {
        Integer charge = itemStack.getOrDefault(ModComponents.CHARGE, 0);
        Integer maxCharge = itemStack.getOrDefault(ModComponents.MAX_CHARGE, 0);

        if (charge < maxCharge) {
            itemStack.set(ModComponents.CHARGE, Math.min(charge + 1, maxCharge));
            return true;
        }

        return false;
    }

    public static final class Client {
        public static void tick(World world, BlockPos pos, BlockState state, BatteryClientData clientData) {
            if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
                if (batteryBlockEntity.getStack().isEmpty()) {
                    clientData.resetRotation();
                } else {
                    int flag = BatteryClientData.NO_ITEM;

                    if (!batteryBlockEntity.getStack().isEmpty()) {
                        flag = batteryBlockEntity.isCharging() ? BatteryClientData.CHARGING : BatteryClientData.NOT_CHARGING;
                    }

                    clientData.rotateDisplay(flag);
                }
            }
        }
    }
}
