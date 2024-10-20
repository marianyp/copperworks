package dev.mariany.copperworks.block.custom;

import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MufflerBlock extends Block {
    private static final IntProperty MUFFLER_RANGE = ModProperties.MUFFLER_RANGE;

    public MufflerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(MUFFLER_RANGE, ModConstants.MAX_MUFFLER_RANGE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(MUFFLER_RANGE);
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
