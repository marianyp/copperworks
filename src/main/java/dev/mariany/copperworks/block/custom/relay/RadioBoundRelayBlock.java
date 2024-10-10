package dev.mariany.copperworks.block.custom.relay;

import com.mojang.serialization.MapCodec;

public class RadioBoundRelayBlock extends AbstractRelayBlock implements BindableRelay {
    public RadioBoundRelayBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends AbstractRelayBlock> getCodec() {
        return null;
    }
}
