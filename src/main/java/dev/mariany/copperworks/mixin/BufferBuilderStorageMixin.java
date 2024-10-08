package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.client.render.GlintRenderLayers;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin {
    @Inject(method = "method_54639(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V", at = @At("HEAD"))
    private void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map, CallbackInfo ci) {
        GlintRenderLayers.addGlintTypes(map);
    }
}
