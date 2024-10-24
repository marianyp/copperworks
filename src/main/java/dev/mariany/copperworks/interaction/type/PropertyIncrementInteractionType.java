package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.interaction.PropertyIncrementInteraction;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
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
            return null;
        }

        IntProperty intProperty = (IntProperty) matchedProperties.getFirst();

        Collection<Integer> values = intProperty.getValues();

        int max = Collections.max(values);
        int min = Collections.min(values);

        return PropertyIncrementInteraction.create(this, sound, intProperty, shouldWrap, max, min);
    }
}
