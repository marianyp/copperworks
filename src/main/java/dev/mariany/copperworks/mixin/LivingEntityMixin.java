package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.item.custom.RocketBootsItem;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    public void injectTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (ModUtils.isEntityStuck(entity)) {
            entity.updateLimbs(entity instanceof Flutterer);
            entity.setVelocity(Vec3d.ZERO);
            ci.cancel();
        }
    }

    @WrapOperation(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean wrapOnDamage(LivingEntity entity, DamageSource source, float amount, Operation<Boolean> original) {
        if (shouldCancel(entity)) {
            return false;
        }
        return original.call(entity, source, amount);
    }

    @WrapOperation(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void wrapOnPlaySound(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch,
                                 Operation<Boolean> original) {
        if (!shouldCancel(entity)) {
            original.call(entity, soundEvent, volume, pitch);
        }
    }

    @Unique
    private boolean shouldCancel(LivingEntity entity) {
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        if (boots.getItem() instanceof RocketBootsItem) {
            boolean bootsInUse = entity.isFallFlying() && ModUtils.itemHasSomeCharge(boots);
            return bootsInUse || RocketBootsItem.isHalting(entity, boots);
        }

        return false;
    }
}
