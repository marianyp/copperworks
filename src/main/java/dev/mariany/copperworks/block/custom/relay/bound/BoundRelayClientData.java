package dev.mariany.copperworks.block.custom.relay.bound;

import net.minecraft.util.math.Vec3d;

public class BoundRelayClientData extends AbstractBoundRelayClientData {
    private final static int TICK_DURATION = 20 * 3;

    private float remainingTicks;

    public BoundRelayClientData() {
        super(new Vec3d(0.8039, 0.6196, 0.9490));
    }

    public boolean isVisible() {
        return remainingTicks > 0;
    }

    public void show() {
        remainingTicks = TICK_DURATION;
    }

    public void tick() {
        if (remainingTicks > 0) {
            --remainingTicks;
        }
    }

    @Override
    public float increment(float tickDelta) {
        return super.increment(tickDelta, remainingTicks / TICK_DURATION);
    }
}
