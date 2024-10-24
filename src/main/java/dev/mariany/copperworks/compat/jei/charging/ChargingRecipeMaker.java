package dev.mariany.copperworks.compat.jei.charging;

import dev.mariany.copperworks.item.component.CopperworksComponents;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class ChargingRecipeMaker {
    public static List<ChargingRecipe> getChargingRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream().filter(stack -> {
            if (!stack.contains(CopperworksComponents.CHARGE)) {
                return false;
            }
            return stack.contains(CopperworksComponents.MAX_CHARGE);
        }).map(stack -> {
            Integer maxCharge = stack.get(CopperworksComponents.MAX_CHARGE);
            ContainerComponent convertsToContainer = stack.get(CopperworksComponents.CONVERTS_TO);


            ItemStack convertsTo;
            if (convertsToContainer != null) {
                convertsTo = convertsToContainer.copyFirstStack();
            } else {
                convertsTo = stack.copy();
                convertsTo.set(CopperworksComponents.CHARGE, maxCharge);
            }

            return new ChargingRecipe(stack, convertsTo);
        }).toList();
    }
}
