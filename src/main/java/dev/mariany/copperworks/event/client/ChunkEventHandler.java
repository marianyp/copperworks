package dev.mariany.copperworks.event.client;

import dev.mariany.copperworks.packets.serverbound.ChunkLoadedPayload;
import dev.mariany.copperworks.world.client.MufflerStorage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkEventHandler {
    public static void onChunkLoad(ClientWorld clientWorld, WorldChunk worldChunk) {
        ClientPlayNetworking.send(new ChunkLoadedPayload(worldChunk.getPos()));
    }

    public static void onChunkUnload(ClientWorld clientWorld, WorldChunk worldChunk) {
        MufflerStorage.removeChunk(worldChunk.getPos().toLong());
    }
}
