package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.util.MufflerUtils;
import dev.mariany.copperworks.world.client.MufflerStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void injectPlaySound(SoundInstance soundInstance, CallbackInfo ci) {
        if (!(soundInstance instanceof TickableSoundInstance) && MufflerUtils.shouldMuffle(soundInstance)) {
            ci.cancel();
        }
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    private void injectPlay(SoundInstance soundInstance, int delay, CallbackInfo ci) {
        if (!(soundInstance instanceof TickableSoundInstance) && MufflerUtils.shouldMuffle(soundInstance)) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "method_19748", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Source;setPosition(Lnet/minecraft/util/math/Vec3d;)V"))
    private static void wrapSetPosition(Source instance, Vec3d pos, Operation<Void> original) {
        original.call(instance, pos);

        BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
        BlockPos playerPos = null;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            playerPos = player.getBlockPos();
        }

        boolean shouldMuffle = MufflerUtils.shouldMuffle(blockPos);
        boolean shouldMuffleAtPlayer = (playerPos != null && MufflerUtils.shouldMuffle(playerPos));

        if (shouldMuffle || shouldMuffleAtPlayer) {
            instance.setVolume(0);
        }
    }
}
