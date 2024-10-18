package dev.mariany.copperworks.item.component;

import com.mojang.serialization.Codec;
import dev.mariany.copperworks.Copperworks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.GlobalPos;

import java.util.List;
import java.util.UUID;

public class CopperworksComponents {
    public static final ComponentType<Integer> CHARGE = register("charge",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> MAX_CHARGE = register("max_charge",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Integer> CHARGE_RATE = register("charge_rate",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<ContainerComponent> CONVERTS_TO = register("converts_to",
            ComponentType.<ContainerComponent>builder().codec(ContainerComponent.CODEC)
                    .packetCodec(ContainerComponent.PACKET_CODEC).cache());
    public static final ComponentType<Float> THRUST = register("thrust",
            ComponentType.<Float>builder().codec(Codec.FLOAT).packetCodec(PacketCodecs.FLOAT).cache());
    public static final ComponentType<Integer> DRAGON_BREATH_FILL = register("dragon_breath_fill",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());
    public static final ComponentType<Boolean> UPGRADED = register("upgraded",
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).cache());
    public static final ComponentType<UUID> LAST_THROWN = register("last_thrown",
            ComponentType.<UUID>builder().codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC).cache());
    public static final ComponentType<Boolean> CHARGING = register("charging",
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).cache());
    public static final ComponentType<GlobalPos> RELAY_POSITION = register("relay_position",
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).cache());
    public static final ComponentType<List<Integer>> COPIED_FREQUENCY_WHITELIST = register("copied_frequency_whitelist",
            ComponentType.<List<Integer>>builder().codec(Codec.INT.listOf())
                    .packetCodec(PacketCodecs.VAR_INT.collect(PacketCodecs.toList())).cache());
    public static final ComponentType<Integer> COPIED_RANGE = register("copied_range",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).cache());

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Copperworks.id(name), builder.build());
    }

    public static void registerModComponents() {
        Copperworks.LOGGER.info("Registering Mod Components for " + Copperworks.MOD_ID);
    }
}
