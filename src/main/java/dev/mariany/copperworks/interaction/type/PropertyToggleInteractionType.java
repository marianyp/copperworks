package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        }

        BooleanProperty booleanProperty = (BooleanProperty) matchedProperties.getFirst();

        return new AbstractBatteryInteraction(this, sound) {
            @Override
            public void executeInteraction(World world, BlockPos pos) {
                BlockState blockState = world.getBlockState(pos);

                boolean currentValue = blockState.get(booleanProperty);

                world.setBlockState(pos, blockState.with(booleanProperty, !currentValue));
                world.updateNeighborsAlways(pos, block);
            }
        };
    }
}
