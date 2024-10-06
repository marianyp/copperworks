package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class CopperFrameBlockItem extends BlockItem {
    public CopperFrameBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack copperFrameStack = user.getStackInHand(hand);

        boolean isClose = user.isSneaking();
        double distance = isClose ? 1.5 : user.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);

        // Prevent placing with the offhand if the main hand has a Copper Frame
        if (hand.equals(Hand.OFF_HAND) && user.getMainHandStack().getItem().equals(this)) {
            return TypedActionResult.pass(copperFrameStack);
        }

        // Get the position where the block should be placed
        BlockPos placePos = getPlacePositionWithRaycast(world, user, distance);

        if (!isValidPlacementPosition(world, placePos)) {
            return TypedActionResult.fail(copperFrameStack);
        }

        if (!world.isClient) {
            // Place the copper frame at the determined position
            BlockState copperFrameState = ModBlocks.COPPER_FRAME.getDefaultState()
                    .with(Properties.WATERLOGGED, world.getFluidState(placePos).getFluid() == Fluids.WATER);
            world.setBlockState(placePos, copperFrameState);

            // Play block place sound
            BlockSoundGroup blockSoundGroup = copperFrameState.getSoundGroup();
            world.playSound(null, placePos, this.getPlaceSound(copperFrameState), SoundCategory.BLOCKS,
                    (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);

            // Emit the block place event
            world.emitGameEvent(GameEvent.BLOCK_PLACE, placePos, GameEvent.Emitter.of(user, copperFrameState));

            // Decrement the item stack if the player is not in creative mode
            copperFrameStack.decrementUnlessCreative(1, user);

            user.swingHand(hand, true);
        }

        return TypedActionResult.success(copperFrameStack, world.isClient());
    }

    private boolean isEntityOccupyingSpace(World world, @NotNull BlockPos pos, @Nullable PlayerEntity player) {
        return !world.getNonSpectatingEntities(Entity.class, Box.from(Vec3d.of(pos))).stream()
                .filter(entity -> !entity.equals(player)).collect(Collectors.toSet()).isEmpty();
    }

    private boolean isValidPlacementPosition(World world, @Nullable BlockPos pos) {
        if (pos == null) {
            return false;
        }

        if (isEntityOccupyingSpace(world, pos, null)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.isReplaceable();
    }

    @Nullable
    private BlockPos getPlacePositionWithRaycast(World world, PlayerEntity player, double maxDistance) {
        if (isEntityBlocking(world, player, maxDistance)) {
            return null;
        }

        double targetedFrameDistance = getTargetedFrameDistance(world, player, maxDistance);
        double remainingDistance = targetedFrameDistance > -1 ? targetedFrameDistance : maxDistance;

        Vec3d startPos = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);

        while (remainingDistance > 0) {
            Vec3d endPos = startPos.add(direction.multiply(remainingDistance));
            RaycastContext context = new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, player);
            BlockHitResult hitResult = world.raycast(context);

            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitBlockState = world.getBlockState(hitPos);
            Direction hitSide = hitResult.getSide();

            if (hitResult.getType() == HitResult.Type.MISS) {
                if (!hitBlockState.isOf(ModBlocks.COPPER_FRAME)) {
                    BlockPos placePos = BlockPos.ofFloored(endPos);

                    if (isValidPlacementPosition(world, placePos)) {
                        return placePos;
                    }
                }
            }

            remainingDistance -= 0.5;

            if (remainingDistance <= 0) {
                return null;
            }

            if (!hitBlockState.isOf(ModBlocks.COPPER_FRAME)) {
                BlockPos placePos = hitPos.offset(hitSide);
                if (isValidPlacementPosition(world, placePos)) {
                    return placePos;
                }
            }
        }

        return null;
    }

    private double getTargetedFrameDistance(World world, PlayerEntity player, double maxDistance) {
        Vec3d startPos = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);

        for (double currentDistance = 0; currentDistance < maxDistance; currentDistance += 0.05) {
            Vec3d endPos = startPos.add(direction.multiply(currentDistance));
            RaycastContext context = new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, player);
            BlockHitResult hitResult = world.raycast(context);

            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitBlockState = world.getBlockState(hitPos);

            if (hitResult.getType() == HitResult.Type.MISS) {
                if (hitBlockState.isOf(ModBlocks.COPPER_FRAME)) {
                    return currentDistance;
                }
            }
        }

        return -1;
    }

    private boolean isEntityBlocking(World world, PlayerEntity player, double maxDistance) {
        Vec3d startPos = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0F);

        double increment = 1;
        for (double currentDistance = increment; currentDistance < maxDistance; currentDistance += increment) {
            Vec3d endPos = startPos.add(direction.multiply(currentDistance));
            RaycastContext context = new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, player);
            BlockHitResult hitResult = world.raycast(context);

            if (isEntityOccupyingSpace(world, hitResult.getBlockPos(), player)) {
                return true;
            }
        }

        return false;
    }
}
