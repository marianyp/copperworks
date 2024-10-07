package dev.mariany.copperworks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.ClockBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClockBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<ClockBlock> CODEC = ClockBlock.createCodec(ClockBlock::new);

    public ClockBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof ClockBlockEntity clockBlockEntity) {
            return clockBlockEntity.isPowered() ? 15 : 0;
        }

        return 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ClockBlockEntity clockBlockEntity) {
            int targetProgress = clockBlockEntity.cycleTargetProgress(player.isSneaking());
            int targetProgressInSeconds = targetProgress == 0 ? 0 : MathHelper.floor((float) targetProgress / 20);
            if (!world.isClient) {
                player.sendMessage(Text.translatable("block.copperworks.clock.cycled", targetProgressInSeconds), true);
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_HAT,
                        SoundCategory.NEUTRAL, 0.5F, (float) (1.6 - (Math.min(12, targetProgressInSeconds) - 1) * 0.1));
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClockBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.CLOCK,
                (world1, pos, blockState, blockEntity) -> blockEntity.tick(world1, pos, blockState, blockEntity));
    }
}
