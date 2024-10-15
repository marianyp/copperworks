package dev.mariany.copperworks.api.registry;

import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BatteryInteractionRegistry {
    private static final Map<RegistryKey<Block>, AbstractBatteryInteraction> interactions = new HashMap<>();

    public static void registerInteraction(RegistryKey<Block> block, AbstractBatteryInteraction interaction) {
        interactions.put(block, interaction);
    }

    @Nullable
    public static AbstractBatteryInteraction getInteraction(Block block) {
        Optional<RegistryKey<Block>> optionalBlockRegistryKey = Registries.BLOCK.getKey(block);
        return optionalBlockRegistryKey.map(interactions::get).orElse(null);

    }

    public static void clearInteractions() {
        interactions.clear();
    }

    public static Map<RegistryKey<Block>, AbstractBatteryInteraction> getAllInteractions() {
        return interactions;
    }
}
