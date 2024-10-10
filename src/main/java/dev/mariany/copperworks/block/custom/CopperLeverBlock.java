package dev.mariany.copperworks.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CopperLeverBlock extends ButtonBlock {
    private static final int PRESS_TICKS = 60;

    protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    public CopperLeverBlock(Settings settings) {
        super(BlockSetType.COPPER, PRESS_TICKS, settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(POWERED)) {
            this.togglePower(state, world, pos, player);
            return ActionResult.SUCCESS;
        }

        if (world.isClient) {
            spawnParticles(state, world, pos, 1F);
        }

        return super.onUse(state, world, pos, player, hit);
    }

    public void togglePower(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        state = state.cycle(POWERED);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);

        boolean powered = state.get(POWERED);
        playClickSound(player, world, pos, powered);
        world.emitGameEvent(player, powered ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
    }

    private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
        Direction direction = state.get(FACING).getOpposite();
        Direction direction2 = getDirection(state).getOpposite();
        double d = (double) pos.getX() + 0.5 + 0.1 * (double) direction.getOffsetX() + 0.2 * (double) direction2.getOffsetX();
        double e = (double) pos.getY() + 0.5 + 0.1 * (double) direction.getOffsetY() + 0.2 * (double) direction2.getOffsetY();
        double f = (double) pos.getZ() + 0.5 + 0.1 * (double) direction.getOffsetZ() + 0.2 * (double) direction2.getOffsetZ();
        world.addParticle(new DustParticleEffect(DustParticleEffect.RED, alpha), d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(POWERED) && random.nextFloat() < 0.25F) {
            spawnParticles(state, world, pos, 0.5F);
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        this.updateNeighbors(state, world, pos);
        if (moved && world instanceof ServerWorld serverWorld) {
            serverWorld.getBlockTickScheduler().clearNextTicks(new BlockBox(pos));
            world.scheduleBlockTick(pos, this, PRESS_TICKS);
        }
        super.onBlockAdded(state, world, pos, newState, moved);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved && state.get(POWERED)) {
            this.updateNeighbors(state, world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void powerOn(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getBlockTickScheduler().clearNextTicks(new BlockBox(pos));
            super.powerOn(state, world, pos, player);
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        float pitch = world.getBlockState(pos).get(POWERED) ? 0.6F : 0.5F;
        world.playSound(player, pos, SoundEvents.BLOCK_COPPER_PLACE, SoundCategory.BLOCKS, 1, pitch);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACE)) {
            case FLOOR -> switch ((state.get(FACING)).getAxis()) {
                case X -> FLOOR_X_AXIS_SHAPE;
                default -> FLOOR_Z_AXIS_SHAPE;
            };
            case WALL -> switch (state.get(FACING)) {
                case EAST -> EAST_WALL_SHAPE;
                case WEST -> WEST_WALL_SHAPE;
                case SOUTH -> SOUTH_WALL_SHAPE;
                default -> NORTH_WALL_SHAPE;
            };
            default -> switch ((state.get(FACING)).getAxis()) {
                case X -> CEILING_X_AXIS_SHAPE;
                default -> CEILING_Z_AXIS_SHAPE;
            };
        };
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }
}
