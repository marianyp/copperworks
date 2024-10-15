package dev.mariany.copperworks.api.interaction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractBatteryInteraction {
    protected BatteryInteractionType interactionType;
    protected InteractionSound sound;

    public AbstractBatteryInteraction(BatteryInteractionType interactionType, InteractionSound sound) {
        this.interactionType = interactionType;
        this.sound = sound;
    }

    public abstract void executeInteraction(World world, BlockPos pos);

    public BatteryInteractionType getInteractionType() {
        return this.interactionType;
    }

    public InteractionSound getSound() {
        return this.sound;
    }
}
