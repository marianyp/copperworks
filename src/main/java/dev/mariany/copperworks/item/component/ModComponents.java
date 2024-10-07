package dev.mariany.copperworks.item.component;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;

public class ModComponents {
    public static final ComponentType<Integer> CHARGE = register("charge",
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> MAX_CHARGE = register("max_charge",
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> CHARGE_SPEED = register("charge_speed",
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).cache());

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Copperworks.id(name), builder.build());
    }

    public static void registerModComponents() {
        Copperworks.LOGGER.info("Registering Mod Components for " + Copperworks.MOD_ID);
    }
}
