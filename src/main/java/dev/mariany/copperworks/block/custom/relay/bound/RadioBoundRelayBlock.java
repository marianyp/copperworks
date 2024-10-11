package dev.mariany.copperworks.block.custom.relay.bound;

import dev.mariany.copperworks.block.custom.relay.AbstractRelayBlock;
import dev.mariany.copperworks.block.custom.relay.BindableRelay;
import dev.mariany.copperworks.block.entity.custom.relay.RadioBoundRelayBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class RadioBoundRelayBlock extends AbstractRelayBlock implements BindableRelay {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int POWERED_TICKS = 30;

    public RadioBoundRelayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.FALSE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(POWERED)) {
            world.scheduleBlockTick(pos, this, POWERED_TICKS);
        }

        state = state.cycle(POWERED);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RadioBoundRelayBlockEntity(pos, state);
    }
}
