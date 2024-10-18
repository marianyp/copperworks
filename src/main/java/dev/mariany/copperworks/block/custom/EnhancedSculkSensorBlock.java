package dev.mariany.copperworks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.EnhancedSculkSensorBlockEntity;
import dev.mariany.copperworks.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

public class EnhancedSculkSensorBlock extends SculkSensorBlock {
    public static final MapCodec<EnhancedSculkSensorBlock> CODEC = createCodec(EnhancedSculkSensorBlock::new);

    public EnhancedSculkSensorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        return stack.isOf(ModItems.WRENCH) ? ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION : super.onUseWithItem(
                stack, state, world, pos, player, hand, hit);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public int getCooldownTime() {
        return 10;
    }

    @Override
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool,
                                   boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, false);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnhancedSculkSensorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return !world.isClient ? validateTicker(type, ModBlockEntities.ENHANCED_SCULK_SENSOR,
                (worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick(worldx,
                        blockEntity.getVibrationListenerData(), blockEntity.getVibrationCallback())) : null;
    }

    @Override
    public MapCodec<EnhancedSculkSensorBlock> getCodec() {
        return CODEC;
    }
}
