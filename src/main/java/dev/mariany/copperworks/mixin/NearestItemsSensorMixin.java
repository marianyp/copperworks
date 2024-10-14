package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.entity.villager.ModVillagers;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NearestItemsSensor.class)
class NearestItemsSensorMixin {
    @WrapOperation(method = "method_24646", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;canGather(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean wrapCanSee(MobEntity mobEntity, ItemStack itemStack, Operation<Boolean> original) {
        return original.call(mobEntity, itemStack) || canUpgradeItem(mobEntity, itemStack);
    }

    @Unique
    private static boolean canUpgradeItem(MobEntity mobEntity, ItemStack itemStack) {
        if (mobEntity instanceof VillagerEntity villager) {
            if (villager.getVillagerData().getProfession().equals(ModVillagers.ENGINEER)) {
                return ModUtils.engineerCanUpgrade(villager, itemStack);
            }
        }
        return false;
    }
}

