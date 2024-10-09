package dev.mariany.copperworks.util;

import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ModUtils {
    public static boolean itemNeedsCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(ModComponents.CHARGE);
        Integer maxChargeComponent = itemStack.get(ModComponents.MAX_CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        if (chargeComponent == null || maxChargeComponent == null) {
            return false;
        }

        return chargeComponent < maxChargeComponent;
    }

    public static boolean itemHasSomeCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(ModComponents.CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        if (chargeComponent == null) {
            return false;
        }

        return chargeComponent > 0;
    }

    public static boolean engineerCanUpgrade(ItemStack itemStack) {
        return itemStack.isIn(ModTags.Items.ENGINEER_CAN_UPGRADE) && !itemStack.hasEnchantments();
    }

    public static boolean isUpgraded(ItemStack itemStack) {
        return itemStack.getOrDefault(ModComponents.UPGRADED, false);
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack) {
        decrementCharge(entity, itemStack, 0, 2);
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack, int min, int max) {
        Random random = entity.getRandom();
        World world = entity.getWorld();

        Integer charge = itemStack.get(ModComponents.CHARGE);
        Integer maxCharge = itemStack.get(ModComponents.MAX_CHARGE);

        if (charge != null && maxCharge != null && charge > 0) {
            int newCharge = MathHelper.clamp(charge - MathHelper.nextInt(random, min, max), 0, maxCharge);
            itemStack.set(ModComponents.CHARGE, newCharge);

            if (newCharge <= 0) {
                world.playSoundFromEntity(null, entity, ModSoundEvents.OUT_OF_CHARGE, SoundCategory.NEUTRAL, 0.375F,
                        1F);
            }
        }
    }
}
