package dev.mariany.copperworks.block.custom.sensor;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.custom.WallMountedBlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSensorBlock extends WallMountedBlockWithEntity implements BlockEntityProvider {
    protected static final VoxelShape CEILING_X_SHAPE = Block.createCuboidShape(4.0, 12.0, 2.0, 12.0, 16.0, 14.0);
    protected static final VoxelShape CEILING_Z_SHAPE = Block.createCuboidShape(2.0, 12.0, 4.0, 14.0, 16.0, 12.0);
    protected static final VoxelShape FLOOR_X_SHAPE = Block.createCuboidShape(4.0, 0.0, 2.0, 12.0, 4.0, 14.0);
    protected static final VoxelShape FLOOR_Z_SHAPE = Block.createCuboidShape(2.0, 0.0, 4.0, 14.0, 4.0, 12.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(2.0, 4.0, 12.0, 14.0, 12.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 4.0, 2.0, 4.0, 12.0, 14.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(2.0, 4.0, 0.0, 14.0, 12.0, 4.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(12.0, 4.0, 2.0, 16.0, 12.0, 14.0);

    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public AbstractSensorBlock(Settings settings) {
        super(settings);
    }

    protected BlockState applyDefaultState(BlockState state) {
        return super.applyDefaultState(state).with(WATERLOGGED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState defaultPlacementState = super.getPlacementState(ctx);
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        boolean waterlogged = world.getFluidState(blockPos).getFluid() == Fluids.WATER;

        if (defaultPlacementState == null) {
            return this.getDefaultState().with(WATERLOGGED, waterlogged);
        }

        return defaultPlacementState.with(WATERLOGGED, waterlogged);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                   WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        switch (state.get(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return FLOOR_X_SHAPE;
                }

                return FLOOR_Z_SHAPE;
            case WALL:
                return switch (direction) {
                    case EAST -> EAST_SHAPE;
                    case WEST -> WEST_SHAPE;
                    case SOUTH -> SOUTH_SHAPE;
                    case NORTH, UP, DOWN -> NORTH_SHAPE;
                };
            case CEILING:
            default:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_X_SHAPE;
                } else {
                    return CEILING_Z_SHAPE;
                }
        }
    }

    @Override
    protected MapCodec<? extends AbstractSensorBlock> getCodec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
