package dev.mariany.copperworks.block.custom.relay.bound;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractBoundRelayClientData {
    private static final float MAX_OPACITY = 0.35F;
    private static final float MIN_OPACITY = 0.2F;
    private static final float OPACITY_RANGE = MAX_OPACITY - MIN_OPACITY;
    private static final float FADE_SPEED = 0.02F;

    private float accumulatedDelta = 0F;
    private float angle = 0F;
    private float opacity = 0F;

    private Vec3d color;

    public AbstractBoundRelayClientData(Vec3d color) {
        this.color = color;
    }

    public float increment(float tickDelta) {
        return increment(tickDelta, 1);
    }

    protected float increment(float tickDelta, float opacityMultiplier) {
        accumulatedDelta += tickDelta;
        if (accumulatedDelta >= 1) {
            angle += FADE_SPEED;
            accumulatedDelta -= 1;
        }

        opacity = (MIN_OPACITY + (MathHelper.sin(angle) + 1) / 2 * OPACITY_RANGE) * opacityMultiplier;
        return opacity;
    }

    public float reset() {
        angle = 0;
        opacity = 0;
        accumulatedDelta = 0;
        return 0;
    }

    public Vec3d getColor() {
        return color;
    }

    public void setColor(Vec3d newColor) {
        this.color = newColor;
    }
}