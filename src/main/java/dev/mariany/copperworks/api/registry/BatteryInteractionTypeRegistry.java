package dev.mariany.copperworks.api.registry;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.interaction.type.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BatteryInteractionTypeRegistry {
    private static final Map<Identifier, BatteryInteractionType> interactionTypes = new HashMap<>();

    static {
        registerInteractionType(of("convert_block"), new ConvertBlockInteractionType());
        registerInteractionType(of("extend_pulse"), new ExtendPulseInteractionType());
        registerInteractionType(of("property_increment"), new PropertyIncrementInteractionType());
        registerInteractionType(of("property_toggle"), new PropertyToggleInteractionType());
        registerInteractionType(of("reset_clock"), new ResetClockInteractionType());
    }

    public static void registerInteractionType(Identifier id, BatteryInteractionType type) {
        interactionTypes.put(id, type);
    }

    public static BatteryInteractionType getInteractionType(Identifier id) {
        return interactionTypes.get(id);
    }

    private static Identifier of(String name) {
        return Copperworks.id(name);
    }
}
