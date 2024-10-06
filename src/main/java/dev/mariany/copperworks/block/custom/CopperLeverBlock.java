package dev.mariany.copperworks.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CopperLeverBlock extends ButtonBlock {
    private static final int PRESS_TICKS = 60;

    protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    public CopperLeverBlock(Settings settings) {
        super(BlockSetType.COPPER, PRESS_TICKS, settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            world.scheduleBlockTick(new BlockPos(pos), this, PRESS_TICKS);
        }
        super.onBlockAdded(state, world, pos, newState, moved);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved && state.get(POWERED)) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        float pitch = world.getBlockState(pos).get(POWERED) ? 0.6F : 0.5F;
        world.playSound(player, pos, SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.BLOCKS, 1, pitch);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACE)) {
            case FLOOR -> switch ((state.get(FACING)).getAxis()) {
                case X -> FLOOR_X_AXIS_SHAPE;
                default -> FLOOR_Z_AXIS_SHAPE;
            };
            case WALL -> switch (state.get(FACING)) {
                case EAST -> EAST_WALL_SHAPE;
                case WEST -> WEST_WALL_SHAPE;
                case SOUTH -> SOUTH_WALL_SHAPE;
                default -> NORTH_WALL_SHAPE;
            };
            default -> switch ((state.get(FACING)).getAxis()) {
                case X -> CEILING_X_AXIS_SHAPE;
                default -> CEILING_Z_AXIS_SHAPE;
            };
        };
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }
}
