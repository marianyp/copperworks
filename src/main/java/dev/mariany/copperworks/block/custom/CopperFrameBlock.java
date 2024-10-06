package dev.mariany.copperworks.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CopperFrameBlock extends Block implements Waterloggable {
    private static final VoxelShape SHAPE = VoxelShapes.union(VoxelShapes.cuboid(0.0, 0.0, 0.125, 0.125, 0.125, 0.875),
            VoxelShapes.cuboid(0.0, 0.0, 0.875, 1.0, 0.125, 1.0), VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.125, 0.125),
            VoxelShapes.cuboid(0.875, 0.0, 0.125, 1.0, 0.125, 0.875),
            VoxelShapes.cuboid(0.0, 0.875, 0.0, 1.0, 1.0, 1.0),
            VoxelShapes.cuboid(0.875, 0.125, 0.0, 1.0, 0.875, 0.125),
            VoxelShapes.cuboid(0.0, 0.125, 0.875, 0.125, 0.875, 1.0),
            VoxelShapes.cuboid(0.0, 0.125, 0.0, 0.125, 0.875, 0.125),
            VoxelShapes.cuboid(0.875, 0.125, 0.875, 1.0, 0.875, 1.0));

    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CopperFrameBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        return this.getDefaultState().with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER);
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

        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable BlockEntity blockEntity, ItemStack tool) {
        List<ItemStack> stacksToDrop = getDroppedStacks(state, (ServerWorld) world, pos, null);
        Set<ItemStack> remainingStacks = stacksToDrop.stream()
                .filter(stack -> !player.getInventory().insertStack(stack)).collect(Collectors.toSet());


        if (remainingStacks.size() >= stacksToDrop.size()) {
            super.afterBreak(world, player, pos, state, blockEntity, tool);
            return;
        }

        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);

        if (remainingStacks.isEmpty()) {
            return;
        }

        remainingStacks.forEach(stack -> dropStack(world, pos, stack));
    }
}
