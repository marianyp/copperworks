package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClockBlockEntity extends BlockEntity {
    private static final String TARGET_PROGRESS_NBT = "TargetProgress";
    private static final String PROGRESS_NBT = "Progress";

    private static final int SECOND_IN_TICKS = 20;
    private static final int MAX_TARGET_PROGRESS = 12 * SECOND_IN_TICKS;
    private static final int MIN_TARGET_PROGRESS = SECOND_IN_TICKS;

    private int targetProgress = SECOND_IN_TICKS;
    private int progress = 0;

    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLOCK, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState blockState, ClockBlockEntity clockBlockEntity) {
        if (clockBlockEntity.progress >= clockBlockEntity.targetProgress) {
            this.resetProgress();
        } else {
            if (++clockBlockEntity.progress == clockBlockEntity.targetProgress) {
                world.updateNeighborsAlways(pos, blockState.getBlock());
            }
        }
    }

    public boolean isPowered() {
        return this.progress >= this.targetProgress;
    }

    private void resetProgress() {
        if (this.progress > 0) {
            this.progress = 0;
            if (this.world != null) {
                BlockState blockState = this.world.getBlockState(this.getPos());
                if (blockState != null) {
                    world.updateNeighborsAlways(pos, blockState.getBlock());
                }
            }
        }
    }

    public int cycleTargetProgress(boolean shrink) {
        this.resetProgress();

        if (shrink) {
            this.targetProgress = this.targetProgress - SECOND_IN_TICKS;
            if (this.targetProgress < MIN_TARGET_PROGRESS) {
                this.targetProgress = MAX_TARGET_PROGRESS; // Wrap to maximum if below minimum
            }
        } else {
            this.targetProgress = this.targetProgress + SECOND_IN_TICKS;
            if (this.targetProgress > MAX_TARGET_PROGRESS) {
                this.targetProgress = MIN_TARGET_PROGRESS; // Wrap to minimum if above maximum
            }
        }

        return this.targetProgress;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.targetProgress = nbt.getInt(TARGET_PROGRESS_NBT);
        this.progress = nbt.getInt(PROGRESS_NBT);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(PROGRESS_NBT, this.progress);
        nbt.putInt(TARGET_PROGRESS_NBT, this.targetProgress);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(TARGET_PROGRESS_NBT);
        nbt.remove(PROGRESS_NBT);
    }
}
