package dev.mariany.copperworks.util;

import dev.mariany.copperworks.world.client.MufflerStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class MufflerUtils {
    public static boolean shouldMuffle(BlockPos soundPos) {
        for (Set<Pair<Integer, BlockPos>> mufflerPairs : MufflerStorage.loadedMufflers.values()) {
            if (mufflerPairs != null) {
                for (Pair<Integer, BlockPos> pair : mufflerPairs) {
                    int range = pair.getLeft() + 1;
                    BlockPos mufflerPos = pair.getRight();
                    double distance = mufflerPos.getSquaredDistance(soundPos);
                    if (distance > 0 && mufflerPos.isWithinDistance(soundPos, range)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean shouldMuffle(SoundInstance soundInstance, BlockPos soundPos) {
        if (shouldMuffle(soundPos)) {
            return true;
        }

        if (soundInstance instanceof TickableSoundInstance) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                BlockPos playerPos = player.getBlockPos();
                return shouldMuffle(playerPos);
            }
        }

        return false;
    }

    public static boolean shouldMuffle(SoundInstance soundInstance) {
        BlockPos soundPos = new BlockPos((int) soundInstance.getX(), (int) soundInstance.getY(),
                (int) soundInstance.getZ());
        return shouldMuffle(soundInstance, soundPos);
    }
}
