package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.client.GlintTypes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Unique
    private LivingEntity currentEntity;

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "HEAD"))
    private void captureRenderParams(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
                                     T livingEntity, float f, float g, float h, float j, float k, float l,
                                     CallbackInfo ci) {
        this.currentEntity = livingEntity;
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;getModel(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/client/render/entity/model/BipedEntityModel;"))
    private BipedEntityModel<T> redirectGetModel(ArmorFeatureRenderer<T, M, A> instance, EquipmentSlot slot,
                                                 Operation<A> original) {
        GlintTypes.setCharged(currentEntity.getEquippedStack(slot));
        return original.call(instance, slot);
    }

    @ModifyExpressionValue(method = "renderGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer copperworks$getArmorEntityGlint(RenderLayer original) {
        return GlintTypes.getArmorEntityGlint();
    }
}
