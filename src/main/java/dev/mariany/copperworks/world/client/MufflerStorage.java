package dev.mariany.copperworks.world.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class MufflerStorage {
    public static Map<Long, Set<Pair<Integer, BlockPos>>> loadedMufflers = new HashMap<>();

    public static void addMuffler(ChunkPos chunkPos, int range, BlockPos blockPos) {
        long key = chunkPos.toLong();
        Set<Pair<Integer, BlockPos>> mufflerPositions = loadedMufflers.computeIfAbsent(key, k -> new HashSet<>());
        mufflerPositions.add(new Pair<>(range, blockPos));
        loadedMufflers.put(key, mufflerPositions);
    }

    public static void removeMuffler(ChunkPos chunkPos, BlockPos blockPos) {
        long key = chunkPos.toLong();
        Set<Pair<Integer, BlockPos>> mufflerPositions = loadedMufflers.get(key);
        if (mufflerPositions != null) {
            mufflerPositions.removeIf(pair -> pair.getRight().equals(blockPos));
        }
        loadedMufflers.put(key, mufflerPositions);
    }

    public static void removeChunk(long chunkPos) {
        loadedMufflers.remove(chunkPos);
    }
}
