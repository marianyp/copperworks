package dev.mariany.copperworks;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.*;

public class StateSaverAndLoader extends PersistentState {
    private static final String CHUNK_LOADERS_NBT = "ChunkLoaders";

    public final Map<ChunkPos, Set<BlockPos>> chunkLoaders = new HashMap<>();

    private static final Type<StateSaverAndLoader> type = new Type<>(StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt, null);

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound chunkLoadersNbt = new NbtCompound();

        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : chunkLoaders.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            Set<BlockPos> blockPositions = entry.getValue();

            NbtCompound chunkPosNbt = new NbtCompound();
            chunkPosNbt.putInt("x", chunkPos.x);
            chunkPosNbt.putInt("z", chunkPos.z);

            NbtList posList = new NbtList();
            for (BlockPos pos : blockPositions) {
                NbtCompound posNbt = new NbtCompound();
                posNbt.putInt("x", pos.getX());
                posNbt.putInt("y", pos.getY());
                posNbt.putInt("z", pos.getZ());
                posList.add(posNbt);
            }

            chunkLoadersNbt.put(getChunkPosKey(chunkPos), posList);
        }

        nbt.put(CHUNK_LOADERS_NBT, chunkLoadersNbt);

        return nbt;
    }

    private static String getChunkPosKey(ChunkPos chunkPos) {
        return chunkPos.x + "," + chunkPos.z;
    }

    private static ChunkPos getChunkPosFromKey(String chunkPosKey) {
        int[] chunkCoordinates = Arrays.stream(chunkPosKey.split(",")).mapToInt(Integer::parseInt).toArray();
        return new ChunkPos(chunkCoordinates[0], chunkCoordinates[1]);
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        if (nbt.contains(CHUNK_LOADERS_NBT)) {
            NbtCompound chunkLoadersNbt = nbt.getCompound(CHUNK_LOADERS_NBT);

            for (String key : chunkLoadersNbt.getKeys()) {
                NbtList posList = chunkLoadersNbt.getList(key, NbtCompound.COMPOUND_TYPE);

                ChunkPos chunkPos = getChunkPosFromKey(key);

                Set<BlockPos> blockPositions = new HashSet<>();
                for (int i = 0; i < posList.size(); i++) {
                    NbtCompound posNbt = posList.getCompound(i);
                    BlockPos pos = new BlockPos(posNbt.getInt("x"), posNbt.getInt("y"), posNbt.getInt("z"));
                    blockPositions.add(pos);
                }

                state.chunkLoaders.put(chunkPos, blockPositions);
            }
        }

        return state;
    }

    public static StateSaverAndLoader getWorldState(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, Copperworks.MOD_ID);

        state.markDirty();

        return state;
    }

}