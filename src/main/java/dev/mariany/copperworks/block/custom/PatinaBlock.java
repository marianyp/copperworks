package dev.mariany.copperworks.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PatinaBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 15, 3, 13, 16, 13);

    public PatinaBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState ceiling = world.getBlockState(blockPos);
        return ceiling.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
                                  boolean notify) {
        if (!world.isClient) {
            if (!state.canPlaceAt(world, pos)) {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }
        }
    }
}
