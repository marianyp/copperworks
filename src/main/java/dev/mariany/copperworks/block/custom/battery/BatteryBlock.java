package dev.mariany.copperworks.block.custom.battery;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.custom.WallMountedBlockWithEntity;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.BatteryBlockEntity;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BatteryBlock extends WallMountedBlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<BatteryBlock> CODEC = BatteryBlock.createCodec(BatteryBlock::new);

    public BatteryBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FACE, BlockFace.FLOOR));
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack useStack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            ItemStack itemStack = batteryBlockEntity.getStack();
            if (ModUtils.itemNeedsCharge(useStack) && itemStack.isEmpty()) {
                player.incrementStat(Stats.USED.getOrCreateStat(useStack.getItem()));
                ItemStack itemStack2 = useStack.splitUnlessCreative(1, player);

                if (batteryBlockEntity.isEmpty()) {
                    batteryBlockEntity.setStack(itemStack2);
                } else {
                    itemStack.increment(1);
                }

                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F,
                        (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);

                batteryBlockEntity.playChargeSound(world, pos);

                batteryBlockEntity.markDirty();
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ItemActionResult.SUCCESS;
            }

            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            if (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()) {
                if (!batteryBlockEntity.getStack().isEmpty()) {
                    batteryBlockEntity.takeStack(player);
                    batteryBlockEntity.markDirty();
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
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
                BatteryBlockEntity.Client.tick(worldx, pos, state, blockEntity.getClientData());
            }
        });
    }
}
