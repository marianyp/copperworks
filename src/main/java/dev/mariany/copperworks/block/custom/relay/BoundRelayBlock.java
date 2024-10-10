package dev.mariany.copperworks.block.custom.relay;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

public class BoundRelayBlock extends AbstractRelayBlock implements BindableRelay {
    public static final MapCodec<BoundRelayBlock> CODEC = BoundRelayBlock.createCodec(BoundRelayBlock::new);
    public static final IntProperty POWER = Properties.POWER;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public BoundRelayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.FALSE).with(POWER, 0));
    }

    @Override
    public boolean isBound(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
            return boundRelayBlockEntity.getBoundBlockState().isPresent();
        }

        return false;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    public static int getBoundRedstonePower(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
            Optional<GlobalPos> optionalBoundGlobalPos = boundRelayBlockEntity.getBoundPos();
            if (optionalBoundGlobalPos.isPresent()) {
                Optional<BlockState> optionalBoundBlockState = boundRelayBlockEntity.getBoundBlockState();

                if (optionalBoundBlockState.isPresent()) {
                    BlockState boundBlockState = optionalBoundBlockState.get();

                    if (boundBlockState.get(POWERED)) {
                        return 0;
                    }

                    OptionalInt power = Arrays.stream(Direction.values()).mapToInt(directionIteration -> {
                        BlockPos iterationPos = optionalBoundGlobalPos.get().pos().offset(directionIteration);
                        if (iterationPos.equals(pos)) {
                            return 0;
                        }
                        return world.getEmittedRedstonePower(iterationPos, directionIteration);
                    }).max();

                    if (power.isPresent()) {
                        return power.getAsInt();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWER);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoundRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BOUND_RELAY, (world1, pos, blockState, blockEntity) -> {
            if (world1 instanceof ServerWorld serverWorld) {
                blockEntity.tick(serverWorld, pos, blockState, blockEntity);
            }
        });
    }

    @Override
    protected MapCodec<? extends AbstractRelayBlock> getCodec() {
        return CODEC;
    }
}
