package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.interaction.PropertyToggleInteraction;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PropertyToggleInteractionType implements BatteryInteractionType {
    @Nullable
    @Override
    public AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound, JsonObject json) {
        String propertyString = json.get("property").getAsString();

        List<Property<?>> matchedProperties = block.getStateManager().getProperties().stream()
                .filter(property -> property.getName().equals(propertyString) && property instanceof BooleanProperty)
                .toList();

        if (matchedProperties.isEmpty()) {
            Copperworks.LOGGER.info("Boolean property {} does not exist on block {}", propertyString,
                    Registries.BLOCK.getEntry(block).getIdAsString());
            return null;
        }

        BooleanProperty booleanProperty = (BooleanProperty) matchedProperties.getFirst();
        return PropertyToggleInteraction.create(this, sound, booleanProperty);
    }
}
