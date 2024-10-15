package dev.mariany.copperworks.block.custom.stasis;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public abstract class AbstractStasisChamber extends BlockWithEntity {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 13, 16);

    public AbstractStasisChamber(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }
}
