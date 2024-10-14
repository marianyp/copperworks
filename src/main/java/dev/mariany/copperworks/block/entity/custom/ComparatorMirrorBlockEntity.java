package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ComparatorMirrorBlockEntity extends BlockEntity {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final BooleanProperty LOCKED = Properties.LOCKED;
    private static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public ComparatorMirrorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.COMPARATOR_MIRROR, pos, state);
    }

    public ComparatorMirrorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState blockState) {
        boolean powered = false;
        List<Pair<BlockPos, BlockState>> triggeredBlocks = new ArrayList<>();

        if (!blockState.get(POWERED) && !blockState.get(LOCKED)) {
            for (Direction direction : Direction.values()) {
                BlockPos iterationPos = pos.offset(direction);
                BlockState iterationBlockState = world.getBlockState(iterationPos);

                boolean iterationTriggered = iterationBlockState.contains(TRIGGERED) && iterationBlockState.get(
                        TRIGGERED);

                if (iterationTriggered && iterationBlockState.hasComparatorOutput()) {
                    triggeredBlocks.add(new Pair<>(iterationPos, iterationBlockState));
                }

                if (iterationBlockState.getComparatorOutput(world, iterationPos) > 0) {
                    powered = true;
                }
            }
        }

        if (blockState.get(Properties.POWERED) != powered) {
            if (!triggeredBlocks.isEmpty() && powered) {
                for (Pair<BlockPos, BlockState> triggeredBlock : triggeredBlocks) {
                    BlockPos triggeredPos = triggeredBlock.getLeft();
                    BlockState triggeredState = triggeredBlock.getRight();
                    world.setBlockState(triggeredPos, triggeredState.with(TRIGGERED, false), Block.NOTIFY_ALL);
                    world.updateNeighborsAlways(triggeredPos, triggeredState.getBlock());
                }
            }

            if (world.getTime() % 2 == 0) {
                world.setBlockState(pos, blockState.with(POWERED, powered), Block.NOTIFY_ALL);
                world.updateNeighborsAlways(pos, blockState.getBlock());
            }
        }
    }
}
