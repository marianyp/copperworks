package dev.mariany.copperworks.compat.jei.charging;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ChargingRecipe(ItemStack input, ItemStack output) {
    @Nullable
    public Integer getSecondsToCharge() {
        Integer maxCharge = input.get(CopperworksComponents.MAX_CHARGE);
        Integer chargeRate = input.get(CopperworksComponents.CHARGE_RATE);

        if (maxCharge != null && chargeRate != null) {
            if (maxCharge > 0 && chargeRate > 0) {
                return Math.round(maxCharge * ((float) chargeRate / 20));
            }
        }

        return null;
    }

    public Identifier getUid() {
        return Copperworks.id("charging." + output.getItem().getName().getLiteralString());
    }
}
