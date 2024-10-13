package dev.mariany.copperworks.block.entity.custom.relay;

import dev.mariany.copperworks.block.custom.relay.bound.AbstractBoundRelayClientData;
import dev.mariany.copperworks.block.custom.relay.bound.radio.RadioBoundRelayClientData;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RadioBoundRelayBlockEntity extends AbstractBoundRelayBlockEntity {
    @Environment(EnvType.CLIENT)
    private final RadioBoundRelayClientData clientData = new RadioBoundRelayClientData();

    public RadioBoundRelayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RADIO_BOUND_RELAY, pos, state);
    }

    public AbstractBoundRelayClientData getClientData() {
        return clientData;
    }
}
