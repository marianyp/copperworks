package dev.mariany.copperworks.packets.clientbound;

import dev.mariany.copperworks.world.client.MufflerStorage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientboundPackets {
    public static void init() {
        // In Chunk
        ClientPlayNetworking.registerGlobalReceiver(ChunkMufflersPayload.ID, (payload, context) -> {
            ChunkPos chunkPos = payload.chunkPos();
            long key = chunkPos.toLong();

            List<Integer> ranges = payload.mufflerRanges();
            List<BlockPos> positions = payload.mufflers();

            Set<Pair<Integer, BlockPos>> mufflers = IntStream.range(0, ranges.size())
                    .mapToObj(i -> new Pair<>(ranges.get(i), positions.get(i))).collect(Collectors.toSet());

            MufflerStorage.loadedMufflers.put(key, new HashSet<>(mufflers));
        });

        // Existing payload updated or new payload added
        ClientPlayNetworking.registerGlobalReceiver(MufflerUpdatedPayload.ID, (payload, context) -> {
            ChunkPos chunkPos = payload.chunkPos();
            int range = payload.range();
            BlockPos mufflerPos = payload.blockPos();
            boolean removed = payload.removed();

            if (removed) {
                MufflerStorage.removeMuffler(chunkPos, mufflerPos);
            } else if (MufflerStorage.loadedMufflers.containsKey(chunkPos.toLong())) {
                // If key is present, chunk is loaded
                MufflerStorage.addMuffler(chunkPos, range, mufflerPos);
            }
        });
    }
}
