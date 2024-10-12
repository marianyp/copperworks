package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.mariany.copperworks.client.GlintTypes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 1001)
public abstract class ItemRendererMixin {
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void setCharged(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
                            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
                            BakedModel model, CallbackInfo ci) {
        GlintTypes.updateGlint(stack);
    }

    @ModifyExpressionValue(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getArmorGlint(RenderLayer original) {
        return GlintTypes.getArmorEntityGlint();
    }

    @ModifyExpressionValue(method = "getDynamicDisplayGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getDynamicGlint(RenderLayer original) {
        return GlintTypes.getGlint();
    }

    @ModifyExpressionValue(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlint(RenderLayer original) {
        return GlintTypes.getGlint();
    }

    @ModifyExpressionValue(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlintTranslucent()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlintTranslucent(RenderLayer original) {
        return GlintTypes.getTranslucentGlint();
    }

    @ModifyExpressionValue(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getEntityGlint(RenderLayer original) {
        return GlintTypes.getEntityGlint();
    }

    @ModifyExpressionValue(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlintDirect(RenderLayer original) {
        return GlintTypes.getGlint();
    }

    @ModifyExpressionValue(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getDirectEntityGlint(RenderLayer original) {
        return GlintTypes.getDirectEntityGlint();
    }
}
