package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.world.client.MufflerStorage;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void injectPlaySound(SoundInstance soundInstance, CallbackInfo ci) {
        BlockPos soundPos = new BlockPos((int) soundInstance.getX(), (int) soundInstance.getY(),
                (int) soundInstance.getZ());

        for (Set<Pair<Integer, BlockPos>> mufflerPairs : MufflerStorage.loadedMufflers.values()) {
            if (mufflerPairs != null) {
                for (Pair<Integer, BlockPos> pair : mufflerPairs) {
                    int range = pair.getLeft() + 1;
                    BlockPos mufflerPos = pair.getRight();
                    double distance = mufflerPos.getSquaredDistance(soundPos);
                    if (distance > 0 && mufflerPos.isWithinDistance(soundPos, range)) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}
