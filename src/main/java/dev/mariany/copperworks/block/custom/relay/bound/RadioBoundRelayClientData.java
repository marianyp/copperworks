package dev.mariany.copperworks.block.custom.relay.bound;

import net.minecraft.util.math.Vec3d;

public class RadioBoundRelayClientData extends AbstractBoundRelayClientData {
    public static final Vec3d RED = new Vec3d(1, 0, 0);

    public RadioBoundRelayClientData() {
        super(RED);
    }
}
