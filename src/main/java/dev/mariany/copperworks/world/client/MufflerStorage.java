package dev.mariany.copperworks.world.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class MufflerStorage {
    public static Map<Long, Set<BlockPos>> loadedMufflers = new HashMap<>();

    public static void addMuffler(ChunkPos chunkPos, BlockPos blockPos) {
        long key = chunkPos.toLong();
        Set<BlockPos> mufflerPositions = loadedMufflers.computeIfAbsent(key, k -> new HashSet<>());
        mufflerPositions.add(blockPos);
        loadedMufflers.put(key, mufflerPositions);
    }

    public static void removeMuffler(ChunkPos chunkPos, BlockPos blockPos) {
        long key = chunkPos.toLong();
        Set<BlockPos> mufflerPositions = loadedMufflers.get(key);
        if (mufflerPositions != null) {
            mufflerPositions.remove(blockPos);
        }
        loadedMufflers.put(key, mufflerPositions);
    }

    public static void removeChunk(long chunkPos) {
        loadedMufflers.remove(chunkPos);
    }
}
