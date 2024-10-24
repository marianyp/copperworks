package dev.mariany.copperworks.interaction;

import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConvertBlockInteraction extends AbstractBatteryInteraction {
    private final Block convertTo;

    private ConvertBlockInteraction(BatteryInteractionType interactionType, InteractionSound sound, Block convertsTo) {
        super(interactionType, sound);
        this.convertTo = convertsTo;
    }

    public static ConvertBlockInteraction create(BatteryInteractionType interactionType, InteractionSound sound,
                                                 Block convertsTo) {
        return new ConvertBlockInteraction(interactionType, sound, convertsTo);
    }

    @Override
    public void executeInteraction(World world, BlockPos pos) {
        world.setBlockState(pos, convertTo.getDefaultState());
        world.updateNeighborsAlways(pos, convertTo);
    }

    public Block getConvertTo() {
        return this.convertTo;
    }
}
