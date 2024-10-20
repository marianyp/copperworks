package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.sensor.ChargedSensorBlock;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class SensorBlockEntity extends BlockEntity {
    private static final IntProperty POWER = Properties.POWER;
    private static final IntProperty SENSOR_RANGE = ModProperties.SENSOR_RANGE;

    public SensorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.SENSOR, pos, state);
    }

    public SensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Direction facing = ChargedSensorBlock.getDirection(blockState);

        Box visibleArea = Box.enclosing(pos, pos.offset(facing, blockState.get(SENSOR_RANGE)));

        List<Entity> visibleEntities = world.getEntitiesByClass(Entity.class, visibleArea,
                entity -> !entity.isRemoved());

        int power = Math.clamp(visibleEntities.size(), 0, 15);

        if (blockState.get(POWER) != power) {
            world.setBlockState(pos, blockState.with(POWER, power), Block.NOTIFY_ALL);
            world.updateNeighborsAlways(pos, block);
            world.updateNeighborsAlways(pos.offset(facing.getOpposite()), block);
        }
    }
}
