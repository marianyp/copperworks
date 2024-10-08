package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.client.render.GlintRenderLayers;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Final
    @Shadow
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render", at = @At(value = "INVOKE", ordinal = 21, shift = At.Shift.AFTER, target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V"))
    private void injectCustomDraw(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
                                  GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                  Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        VertexConsumerProvider.Immediate immediate = bufferBuilders.getEntityVertexConsumers();

        for (RenderLayer renderLayer : GlintRenderLayers.ALL) {
            immediate.draw(renderLayer);
        }
    }
}
