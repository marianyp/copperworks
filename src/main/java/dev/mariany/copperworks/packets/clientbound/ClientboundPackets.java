package dev.mariany.copperworks.packets.clientbound;

import dev.mariany.copperworks.world.client.MufflerStorage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.List;

public class ClientboundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ChunkMufflersPayload.ID, (payload, context) -> {
            ChunkPos chunkPos = payload.chunkPos();
            long key = chunkPos.toLong();
            List<BlockPos> mufflerPositions = payload.mufflers();

            MufflerStorage.loadedMufflers.put(key, new HashSet<>(mufflerPositions));
        });

        ClientPlayNetworking.registerGlobalReceiver(MufflerUpdatedPayload.ID, (payload, context) -> {
            ChunkPos chunkPos = payload.chunkPos();
            BlockPos mufflerPos = payload.blockPos();
            boolean removed = payload.removed();

            if (removed) {
                MufflerStorage.removeMuffler(chunkPos, mufflerPos);
            } else if (MufflerStorage.loadedMufflers.containsKey(chunkPos.toLong())) {
                // If key is present, chunk is loaded
                MufflerStorage.addMuffler(chunkPos, mufflerPos);
            }
        });
    }
}
