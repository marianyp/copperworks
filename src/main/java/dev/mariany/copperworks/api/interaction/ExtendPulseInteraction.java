package dev.mariany.copperworks.api.interaction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExtendPulseInteraction extends AbstractBatteryInteraction {
    public final boolean sameAxis;

    public ExtendPulseInteraction(BatteryInteractionType interactionType, InteractionSound sound, boolean sameAxis) {
        super(interactionType, sound);
        this.sameAxis = sameAxis;
    }

    @Override
    public void executeInteraction(World world, BlockPos pos) {
    }
}
