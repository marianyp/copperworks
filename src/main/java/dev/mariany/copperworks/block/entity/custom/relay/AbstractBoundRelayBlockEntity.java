package dev.mariany.copperworks.block.entity.custom.relay;

import dev.mariany.copperworks.block.custom.relay.bound.AbstractBoundRelayClientData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractBoundRelayBlockEntity extends BlockEntity {
    public AbstractBoundRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    abstract public AbstractBoundRelayClientData getClientData();
}
