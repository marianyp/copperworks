package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.item.custom.RocketBootsItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.AFTER))
    private <E extends Entity> void injectRender(E entity, double x, double y, double z, float yaw, float tickDelta,
                                                 MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                                 int light, CallbackInfo ci) {
        EntityRenderer<? super E> entityRenderer = ((EntityRenderDispatcher) (Object) this).getRenderer(entity);

        if (entity instanceof LivingEntity livingEntity) {
            if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
                if (livingEntityRenderer.getModel() instanceof BipedEntityModel<?> bipedEntityModel) {
                    ItemStack boots = livingEntity.getEquippedStack(EquipmentSlot.FEET);
                    if (boots.getItem() instanceof RocketBootsItem rocketBootsItem) {
                        rocketBootsItem.spawnParticles(livingEntity, bipedEntityModel, boots);
                    }
                }
            }
        }
    }
}
