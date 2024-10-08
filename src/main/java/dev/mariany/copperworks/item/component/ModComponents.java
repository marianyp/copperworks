package dev.mariany.copperworks.item.component;

import com.mojang.serialization.Codec;
import dev.mariany.copperworks.Copperworks;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModComponents {
    public static final ComponentType<Integer> CHARGE = register("charge",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> MAX_CHARGE = register("max_charge",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> CHARGE_RATE = register("charge_rate",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Float> THRUST = register("thrust",
            ComponentType.<Float>builder().codec(Codec.FLOAT).packetCodec(PacketCodecs.FLOAT).cache());
    public static final ComponentType<Integer> DRAGON_BREATH_FILL = register("dragon_breath_fill",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Copperworks.id(name), builder.build());
    }

    public static void registerModComponents() {
        Copperworks.LOGGER.info("Registering Mod Components for " + Copperworks.MOD_ID);
    }
}
