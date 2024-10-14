package dev.mariany.copperworks.block.custom.battery;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.custom.WallMountedBlockWithEntity;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.BatteryBlockEntity;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BatteryBlock extends WallMountedBlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<BatteryBlock> CODEC = BatteryBlock.createCodec(BatteryBlock::new);

    public BatteryBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack useStack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!useStack.isEmpty() && world.getBlockEntity(pos) instanceof BatteryBlockEntity batteryBlockEntity) {
            if (batteryBlockEntity.isEmpty()) {
                player.incrementStat(Stats.USED.getOrCreateStat(useStack.getItem()));

                ItemStack itemStack = useStack.splitUnlessCreative(1, player);
                batteryBlockEntity.setStack(itemStack);

                playItemPlopSound(world, pos);

                BatteryBlockEntity.notifyChange(batteryBlockEntity);
                return ItemActionResult.success(world.isClient);
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
                stack.remove(ModComponents.CHARGING);
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
            blockEntity.tick(worldx, pos, blockEntity);
            if (worldx.isClient) {
                BatteryBlockEntity.Client.tick(worldx, pos, blockEntity.getClientData());
            }
        });
    }
}
