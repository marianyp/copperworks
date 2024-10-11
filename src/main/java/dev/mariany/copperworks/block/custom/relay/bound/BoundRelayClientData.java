package dev.mariany.copperworks.block.custom.relay.bound;

import net.minecraft.util.math.Vec3d;

public class BoundRelayClientData extends AbstractBoundRelayClientData {
    public static final Vec3d PURPLE = new Vec3d(0.8039, 0.6196, 0.9490);
    public static final Vec3d BLACK = new Vec3d(0.05882, 0.05882, 0.05882);

    private final static int TICK_DURATION = 20 * 3;

    private float remainingTicks;

    public BoundRelayClientData() {
        super(PURPLE);
    }

    public boolean isVisible() {
        return remainingTicks > 0;
    }

    public void show(Vec3d color) {
        remainingTicks = TICK_DURATION;
        this.setColor(color);
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
