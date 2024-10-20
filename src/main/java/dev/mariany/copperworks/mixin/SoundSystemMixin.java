package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.block.custom.MufflerBlock;
import dev.mariany.copperworks.world.client.MufflerStorage;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
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

        for (Set<BlockPos> mufflerPositions : MufflerStorage.loadedMufflers.values()) {
            if (mufflerPositions != null) {
                for (BlockPos mufflerPos : mufflerPositions) {
                    double distance = mufflerPos.getSquaredDistance(soundPos);
                    if (distance > 0 && mufflerPos.isWithinDistance(soundPos, MufflerBlock.RANGE)) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}
