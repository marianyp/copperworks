package dev.mariany.copperworks.interaction;

import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PropertyToggleInteraction extends AbstractBatteryInteraction {
    final BooleanProperty booleanProperty;

    private PropertyToggleInteraction(BatteryInteractionType interactionType, InteractionSound sound,
                                      BooleanProperty booleanProperty) {
        super(interactionType, sound);
        this.booleanProperty = booleanProperty;
    }

    public static PropertyToggleInteraction create(BatteryInteractionType interactionType, InteractionSound sound,
                                                   BooleanProperty booleanProperty) {
        return new PropertyToggleInteraction(interactionType, sound, booleanProperty);
    }

    @Override
    public void executeInteraction(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        boolean currentValue = blockState.get(this.booleanProperty);

        world.setBlockState(pos, blockState.with(this.booleanProperty, !currentValue));
        world.updateNeighborsAlways(pos, blockState.getBlock());
    }

    public BooleanProperty getBooleanProperty() {
        return this.booleanProperty;
    }
}
