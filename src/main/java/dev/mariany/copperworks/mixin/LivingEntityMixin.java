package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.item.custom.RocketBootsItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean onDamage(LivingEntity entity, DamageSource source, float amount, Operation<Boolean> original) {
        if (shouldCancel(entity)) {
            return false;
        }
        return original.call(entity, source, amount);
    }

    @WrapOperation(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void onPlaySound(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch,
                             Operation<Boolean> original) {
        if (!shouldCancel(entity)) {
            original.call(entity, soundEvent, volume, pitch);
        }
    }

    @Unique
    private boolean shouldCancel(LivingEntity entity) {
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        if (boots.getItem() instanceof RocketBootsItem) {
            return RocketBootsItem.isHalting(entity, boots);
        }

        return false;
    }
}
