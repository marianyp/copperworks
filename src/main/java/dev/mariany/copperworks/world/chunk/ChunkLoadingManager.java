package dev.mariany.copperworks.world.chunk;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class ChunkLoadingManager {
    public static ChunkPos getChunkPos(ServerWorld world, BlockPos blockPos) {
        return world.getChunk(blockPos).getPos();
    }

    public static void startLoading(ServerWorld world, BlockPos blockPos) {
        StateSaverAndLoader worldState = StateSaverAndLoader.getWorldState(world);
        ChunkPos chunkPos = getChunkPos(world, blockPos);

        Set<BlockPos> existingChunkLoaders = new HashSet<>();

        if (worldState.chunkLoaders.containsKey(chunkPos)) {
            existingChunkLoaders.addAll(worldState.chunkLoaders.get(chunkPos));
        }

        existingChunkLoaders.add(blockPos);

        world.setChunkForced(chunkPos.x, chunkPos.z, true);

        worldState.chunkLoaders.put(chunkPos, existingChunkLoaders);
        worldState.markDirty();

        Copperworks.LOGGER.info("Added chunk loader at block position {} for chunk {}", blockPos.toShortString(),
                chunkPos);
    }

    public static void stopLoading(ServerWorld world, BlockPos blockPos) {
        StateSaverAndLoader worldState = StateSaverAndLoader.getWorldState(world);
        ChunkPos chunkPos = getChunkPos(world, blockPos);

        Set<BlockPos> existingChunkLoaders = new HashSet<>();

        if (worldState.chunkLoaders.containsKey(chunkPos)) {
            existingChunkLoaders.addAll(worldState.chunkLoaders.get(chunkPos));
        }


        if (existingChunkLoaders.remove(blockPos)) {
            world.setChunkForced(chunkPos.x, chunkPos.z, !existingChunkLoaders.isEmpty());
        }

        if (existingChunkLoaders.isEmpty()) {
            worldState.chunkLoaders.remove(chunkPos);
        } else {
            worldState.chunkLoaders.put(chunkPos, existingChunkLoaders);
        }

        worldState.markDirty();

        Copperworks.LOGGER.info("Removed chunk loader at block position {} for chunk {}", blockPos.toShortString(),
                chunkPos);
    }

    public static void onServerStart(MinecraftServer server) {
        Copperworks.LOGGER.info("Attempting to load chunks containing chunk loaders");

        for (ServerWorld world : server.getWorlds()) {
            StateSaverAndLoader worldState = StateSaverAndLoader.getWorldState(world);

            Set<ChunkPos> chunksToLoad = new HashSet<>();
            Set<BlockPos> blockPosToRemove = new HashSet<>();

            for (Map.Entry<ChunkPos, Set<BlockPos>> chunkPosListEntry : worldState.chunkLoaders.entrySet()) {
                ChunkPos chunkPos = chunkPosListEntry.getKey();
                Set<BlockPos> chunkLoaderPositions = chunkPosListEntry.getValue();
                for (BlockPos chunkLoaderPos : chunkLoaderPositions) {
                    if (world.getBlockState(chunkLoaderPos).getBlock() instanceof ChunkLoaderBlock) {
                        chunksToLoad.add(chunkPos);
                    } else {
                        blockPosToRemove.add(chunkLoaderPos);
                    }
                }
            }

            for (ChunkPos chunkPos : chunksToLoad) {
                world.setChunkForced(chunkPos.x, chunkPos.z, true);
            }

            Copperworks.LOGGER.info("Loaded {} chunks from chunk loaders", chunksToLoad.size());

            for (BlockPos blockPos : blockPosToRemove) {
                Iterator<Map.Entry<ChunkPos, Set<BlockPos>>> iterator = worldState.chunkLoaders.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<ChunkPos, Set<BlockPos>> entry = iterator.next();
                    Set<BlockPos> positions = entry.getValue();

                    positions.removeIf(pos -> pos.equals(blockPos));

                    if (positions.isEmpty()) {
                        iterator.remove();
                    }
                }
            }

            if (!blockPosToRemove.isEmpty()) {
                worldState.markDirty();
            }
        }
    }
}
