package dev.mariany.copperworks.block.custom.battery;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.ExtendPulseInteraction;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.api.registry.BatteryInteractionRegistry;
import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.WallMountedBlockWithEntity;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.BatteryBlockEntity;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.util.ModConstants;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BatteryBlock extends WallMountedBlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<BatteryBlock> CODEC = BatteryBlock.createCodec(BatteryBlock::new);
    public static final IntProperty CHARGE = ModProperties.CHARGE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public BatteryBlock(Settings settings) {
        super(settings);
    }

    protected BlockState applyDefaultState(BlockState state) {
        return super.applyDefaultState(state).with(CHARGE, ModConstants.MAX_BATTERY_CHARGE).with(POWERED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CHARGE, POWERED);
    }

    private void sendPulse(World world, BlockPos pos, Direction direction) {
        BlockPos iterationPosition = pos.offset(direction, 1);
        while (true) {
            BlockState iterationBlockState = world.getBlockState(iterationPosition);
            Block iterationBlock = iterationBlockState.getBlock();
            AbstractBatteryInteraction interaction = BatteryInteractionRegistry.getInteraction(iterationBlock);

            if (iterationBlock instanceof BatteryBlock) {
                sendPulse(world, iterationPosition, getDirection(iterationBlockState));
                return;
            }

            if (interaction == null) {
                return;
            }

            if (interaction instanceof ExtendPulseInteraction extendPulseInteraction) {
                if (extendPulseInteraction.sameAxis) {
                    if (iterationBlockState.contains(Properties.FACING)) {
                        if (!iterationBlockState.get(Properties.FACING).getAxis().equals(direction.getAxis())) {
                            return;
                        }
                    }
                }
            } else {
                interaction.executeInteraction(world, iterationPosition);
                if (!world.isClient) {
                    playInteractionSound(world, iterationPosition, interaction.getSound());
                }
                return;
            }

            iterationPosition = iterationPosition.offset(direction, 1);
        }
    }

    private void playInteractionSound(World world, BlockPos pos, InteractionSound interactionSound) {
        float volume = interactionSound.volume();
        float pitch = interactionSound.pitch();
        Optional<SoundEvent> optionalSoundEvent = interactionSound.sound();
        optionalSoundEvent.ifPresent(
                soundEvent -> world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundEvent,
                        SoundCategory.BLOCKS, volume, pitch));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
                                  boolean notify) {
        boolean powered = ModUtils.isPowered(world, pos);
        if (powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);

            if (powered && state.get(CHARGE) > 0) {
                sendPulse(world, pos, getDirection(state));
            }
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack useStack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (useStack.isEmpty()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (useStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();

            if (block instanceof BatteryBlock) {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (BatteryInteractionRegistry.getInteraction(block) != null) {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }

        Block aboveBlock = world.getBlockState(pos.up()).getBlock();

        if (!(aboveBlock instanceof AirBlock) && !(aboveBlock instanceof FluidBlock)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(world.getBlockEntity(
                pos) instanceof BatteryBlockEntity batteryBlockEntity) || !batteryBlockEntity.isEmpty()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        player.incrementStat(Stats.USED.getOrCreateStat(useStack.getItem()));

        ItemStack itemStack = useStack.splitUnlessCreative(1, player);
        batteryBlockEntity.setStack(itemStack);

        playItemPlopSound(world, pos);

        BatteryBlockEntity.notifyChange(batteryBlockEntity);
        return ItemActionResult.success(world.isClient);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            ItemStack handStack = player.getMainHandStack();
            ItemStack currentItemStack = batteryBlockEntity.getStack();
            boolean canTake = handStack.isEmpty() || ItemStack.areEqual(handStack.copyWithCount(1),
                    currentItemStack.copyWithCount(1));
            if (canTake && player.getOffHandStack().isEmpty()) {
                if (!batteryBlockEntity.getStack().isEmpty()) {
                    batteryBlockEntity.takeStack(player);
                    playItemPlopSound(world, pos);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    private void playItemPlopSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F,
                (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            ItemStack stack = batteryBlockEntity.getStack();

            if (ModUtils.isCharging(stack)) {
                stack.remove(CopperworksComponents.CHARGING);
                batteryBlockEntity.setStack(stack);
            }
        }
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean replacingAir = ctx.getWorld().getBlockState(ctx.getBlockPos()).isAir();
        if (!replacingAir && ctx.canReplaceExisting()) {
            return this.getDefaultState().with(FACE, BlockFace.FLOOR);
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative() && world.getGameRules()
                .getBoolean(GameRules.DO_TILE_DROPS) && world.getBlockEntity(
                pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            int charge = state.get(CHARGE);
            if (charge != ModConstants.MAX_BATTERY_CHARGE) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.applyComponentsFrom(batteryBlockEntity.createComponentMap());
                itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(CHARGE, charge));
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            if (batteryBlockEntity.isCharging()) {
                return 15;
            }

            if (!batteryBlockEntity.getStack().isEmpty()) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        ModUtils.appendBlockStateChargeTooltip(stack, tooltip, ModConstants.MAX_BATTERY_CHARGE);
    }

    @Override
    protected MapCodec<? extends WallMountedBlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BATTERY, (worldx, pos, statex, blockEntity) -> {
            blockEntity.tick(worldx, pos, statex, blockEntity);
            if (worldx.isClient) {
                BatteryBlockEntity.Client.tick(worldx, pos, blockEntity.getClientData());
            }
        });
    }
}
