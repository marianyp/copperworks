package dev.mariany.copperworks.block.custom.battery;

import net.minecraft.util.math.MathHelper;

public class BatteryClientData {
    public static final int SOUND_INTERVAL_TICKS = 40;
    public static final float IDLE_ROTATION_SPEED = 4F;
    public static final float CHARGE_ROTATION_SPEED = 16F;

    public static final int NO_ITEM = 0;
    public static final int CHARGING = 1;
    public static final int NOT_CHARGING = 2;

    private int chargingFlag = NO_ITEM;

    private int ticksUntilSound;
    private float displayRotation;
    private float prevDisplayRotation;
    private float currentRotationSpeed = IDLE_ROTATION_SPEED;

    public float getDisplayRotation() {
        return this.displayRotation;
    }

    public float getPreviousDisplayRotation() {
        return this.prevDisplayRotation;
    }

    public boolean incrementTicksUntilSound() {
        if (this.ticksUntilSound > SOUND_INTERVAL_TICKS) {
            this.ticksUntilSound = 0;
            return false;
        }

        return ++this.ticksUntilSound == SOUND_INTERVAL_TICKS;
    }

    public void resetTicksUntilSound() {
        this.ticksUntilSound = 0;
    }

    public void rotateDisplay(int flag) {
        if (flag != chargingFlag) {
            boolean charging = (flag == CHARGING);
            float targetRotationSpeed = charging ? CHARGE_ROTATION_SPEED : IDLE_ROTATION_SPEED;
            float delta = charging ? 0.025F : 0.05F;
            this.currentRotationSpeed = MathHelper.lerp(delta, this.currentRotationSpeed, targetRotationSpeed);
            if ((charging && (this.currentRotationSpeed - delta >= targetRotationSpeed)) || (!charging && (this.currentRotationSpeed - delta < targetRotationSpeed))) {
                this.chargingFlag = flag;
                this.currentRotationSpeed = targetRotationSpeed;
            }
        }

        this.prevDisplayRotation = this.displayRotation;
        this.displayRotation = MathHelper.wrapDegrees(this.displayRotation + this.currentRotationSpeed);
    }

    public void resetRotation() {
        currentRotationSpeed = IDLE_ROTATION_SPEED;
        displayRotation = 0;
        prevDisplayRotation = 0;
        chargingFlag = NO_ITEM;
    }
}
