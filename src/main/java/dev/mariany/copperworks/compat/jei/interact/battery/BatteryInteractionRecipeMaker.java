package dev.mariany.copperworks.compat.jei.interact.battery;

import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.registry.BatteryInteractionRegistry;
import dev.mariany.copperworks.interaction.ConvertBlockInteraction;
import dev.mariany.copperworks.interaction.PropertyIncrementInteraction;
import dev.mariany.copperworks.interaction.PropertyToggleInteraction;
import dev.mariany.copperworks.interaction.type.ExtendPulseInteractionType;
import dev.mariany.copperworks.interaction.type.ResetClockInteractionType;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public final class BatteryInteractionRecipeMaker {
    public static List<BatteryInteractionRecipe> getInteractions() {
        return BatteryInteractionRegistry.getAllInteractions().entrySet().stream().map(interactionEntry -> {
            RegistryKey<Block> blockInteractionKey = interactionEntry.getKey();

            AbstractBatteryInteraction blockInteraction = interactionEntry.getValue();
            BatteryInteractionType interactionType = blockInteraction.getInteractionType();

            Block block = Registries.BLOCK.get(blockInteractionKey);

            if (block != null) {
                ItemStack convertsTo = block.asItem().getDefaultStack();
                Text details = null;

                if (blockInteraction instanceof ConvertBlockInteraction convertBlockInteraction) {
                    convertsTo = convertBlockInteraction.getConvertTo().asItem().getDefaultStack();
                } else if (blockInteraction instanceof PropertyToggleInteraction propertyToggleInteraction) {
                    details = Text.translatable("gui.jei.category.copperworks.battery_interaction.property_toggle",
                            propertyToggleInteraction.getBooleanProperty().getName());
                } else if (blockInteraction instanceof PropertyIncrementInteraction propertyIncrementInteraction) {
                    details = Text.translatable("gui.jei.category.copperworks.battery_interaction.property_increment",
                            propertyIncrementInteraction.getIntProperty().getName());
                } else if (interactionType instanceof ResetClockInteractionType) {
                    details = Text.translatable("gui.jei.category.copperworks.battery_interaction.clock");
                } else if (interactionType instanceof ExtendPulseInteractionType) {
                    return null;
                }

                if (details != null) {
                    Text formattedDetails = details.copy().setStyle(Style.EMPTY.withColor(Formatting.AQUA));
                    convertsTo.set(DataComponentTypes.LORE, new LoreComponent(List.of(), List.of(formattedDetails)));
                }

                return new BatteryInteractionRecipe(getName(blockInteractionKey), block, convertsTo, details);
            }

            return null;
        }).filter(Objects::nonNull).toList();
    }

    public static String getName(RegistryKey<Block> key) {
        Identifier id = key.getValue();
        return id.getNamespace() + "." + id.getPath();
    }
}
