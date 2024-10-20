package dev.mariany.copperworks.block.custom.relay;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.custom.relay.bound.BindableRelay;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class AbstractRelayBlock extends BlockWithEntity implements BlockEntityProvider {
    protected AbstractRelayBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (this instanceof BindableRelay) {
            return ModBlocks.COPPER_RELAY_CHARGED.asItem().getDefaultStack();
        }

        return new ItemStack(this);
    }


    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends AbstractRelayBlock> getCodec() {
        return null;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
