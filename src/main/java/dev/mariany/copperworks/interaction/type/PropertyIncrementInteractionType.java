package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PropertyIncrementInteractionType implements BatteryInteractionType {
    @Nullable
    @Override
    public AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound, JsonObject json) {
        boolean shouldWrap = json.has("wrap") && json.get("wrap").getAsBoolean();
        String propertyString = json.get("property").getAsString();

        List<Property<?>> matchedProperties = block.getStateManager().getProperties().stream()
                .filter(property -> property.getName().equals(propertyString) && property instanceof IntProperty)
                .toList();

        if (matchedProperties.isEmpty()) {
            Copperworks.LOGGER.info("Integer property {} does not exist on block {}", propertyString,
                    Registries.BLOCK.getEntry(block).getIdAsString());
        }

        IntProperty intProperty = (IntProperty) matchedProperties.getFirst();

        Collection<Integer> values = intProperty.getValues();

        int max = Collections.max(values);
        int min = Collections.min(values);

        return new AbstractBatteryInteraction(this, sound) {
            @Override
            public void executeInteraction(World world, BlockPos pos) {
                BlockState blockState = world.getBlockState(pos);

                int currentValue = blockState.get(intProperty);
                int value = shouldWrap ? ModUtils.wrapIncrement(currentValue, min, max) : MathHelper.clamp(
                        currentValue + 1, min, max);

                world.setBlockState(pos, blockState.with(intProperty, value));
                world.updateNeighborsAlways(pos, block);
            }
        };
    }
}
