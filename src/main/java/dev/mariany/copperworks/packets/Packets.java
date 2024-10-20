package dev.mariany.copperworks.packets;

import dev.mariany.copperworks.packets.clientbound.ChunkMufflersPayload;
import dev.mariany.copperworks.packets.clientbound.MufflerUpdatedPayload;
import dev.mariany.copperworks.packets.serverbound.ChunkLoadedPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class Packets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(ChunkMufflersPayload.ID, ChunkMufflersPayload.CODEC);
        registry.register(MufflerUpdatedPayload.ID, MufflerUpdatedPayload.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(ChunkLoadedPayload.ID, ChunkLoadedPayload.CODEC);
    }
}
