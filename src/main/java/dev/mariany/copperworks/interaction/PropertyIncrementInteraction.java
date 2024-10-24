package dev.mariany.copperworks.interaction;

import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PropertyIncrementInteraction extends AbstractBatteryInteraction {
    final IntProperty intProperty;
    final boolean shouldWrap;
    final int max;
    final int min;

    private PropertyIncrementInteraction(BatteryInteractionType interactionType, InteractionSound sound,
                                         IntProperty intProperty, boolean shouldWrap, int max, int min) {
        super(interactionType, sound);
        this.intProperty = intProperty;
        this.shouldWrap = shouldWrap;
        this.max = max;
        this.min = min;
    }

    public static PropertyIncrementInteraction create(BatteryInteractionType interactionType, InteractionSound sound,
                                                      IntProperty intProperty, boolean shouldWrap, int max, int min) {
        return new PropertyIncrementInteraction(interactionType, sound, intProperty, shouldWrap, max, min);
    }

    @Override
    public void executeInteraction(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        int currentValue = blockState.get(this.intProperty);
        int value = this.shouldWrap ? ModUtils.wrapIncrement(currentValue, this.min, this.max) : MathHelper.clamp(
                currentValue + 1, this.min, this.max);

        world.setBlockState(pos, blockState.with(this.intProperty, value));
        world.updateNeighborsAlways(pos, blockState.getBlock());
    }

    public IntProperty getIntProperty() {
        return this.intProperty;
    }
}
