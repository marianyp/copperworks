package dev.mariany.copperworks.event.entity;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.event.server.ServerWorldTickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class MinecartTickHandler implements ServerWorldTickHandler {
    private static final List<Block> BOOSTED_RAILS = List.of(ModBlocks.WOODEN_RAIL, ModBlocks.COPPER_RAIL);
    private static final List<Block> FRAGILE_RAILS = List.of(ModBlocks.WOODEN_RAIL);

    private static final float BOOST_SPEED = 0.5F;
    private static final float FRAGILE_RAIL_BREAK_CHANCE = 0.03F;

    @Override
    public void onServerWorldTick(ServerWorld world) {
        for (AbstractMinecartEntity minecart : world.getEntitiesByType(
                TypeFilter.instanceOf(AbstractMinecartEntity.class), entity -> !entity.isRemoved())) {
            onMinecartTick(minecart);
        }
    }

    private void onMinecartTick(AbstractMinecartEntity minecart) {
        ServerWorld world = (ServerWorld) minecart.getWorld();
        BlockPos railPos = minecart.getSteppingPos();
        BlockState railBlockState = world.getBlockState(railPos);
        Block railBlock = railBlockState.getBlock();
        Entity passenger = minecart.getFirstPassenger();

        if (passenger != null) {
            if (BOOSTED_RAILS.contains(railBlock) && !isCurvedRail(railBlockState)) {
                applyBoostToMinecart(minecart);
                Vec3d currentVelocity = minecart.getVelocity();
                double currentSpeed = currentVelocity.length();
                double breakThreshold = BOOST_SPEED / 2;
                int tickCount = passenger.age;
                if (FRAGILE_RAILS.contains(railBlock)) {
                    if (tickCount % 10 == 0 && passenger instanceof PlayerEntity player) {
                        player.getHungerManager().addExhaustion(0.3F);
                    }
                    if (currentSpeed >= breakThreshold && tickCount % 20 == 0 && world.getRandom()
                            .nextFloat() < FRAGILE_RAIL_BREAK_CHANCE) {
                        breakWoodenRail(world, railPos);
                    }
                }
            }
        }

    }

    private static boolean isCurvedRail(BlockState state) {
        RailShape shape = state.get(Properties.RAIL_SHAPE);
        return shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST;
    }

    private static void applyBoostToMinecart(AbstractMinecartEntity minecart) {
        Vec3d currentVelocity = minecart.getVelocity();
        Vec3d newVelocity = currentVelocity.normalize().multiply(BOOST_SPEED);
        minecart.setVelocity(newVelocity);
        minecart.velocityModified = true;
    }

    private static void breakWoodenRail(ServerWorld world, BlockPos blockPos) {
        world.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.1F, 1F);
        world.breakBlock(blockPos, false);
    }
}
