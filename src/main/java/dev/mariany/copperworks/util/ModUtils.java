package dev.mariany.copperworks.util;

import dev.mariany.copperworks.item.component.ModComponents;
import net.minecraft.item.ItemStack;

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
}
