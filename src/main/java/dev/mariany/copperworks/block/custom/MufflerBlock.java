package dev.mariany.copperworks.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MufflerBlock extends Block {
    public static int RANGE = 8;

    public MufflerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!(newState.getBlock() instanceof MufflerBlock)) {
            if (!world.isClient) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        RegistryEntry.of(SoundEvents.BLOCK_WOOL_BREAK), SoundCategory.BLOCKS, 1F, 1F,
                        world.getRandom().nextLong());
            }
        }
    }
}
