package dev.mariany.copperworks.compat.jei;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.compat.jei.charging.ChargingRecipe;
import dev.mariany.copperworks.compat.jei.interact.InteractRecipe;
import dev.mariany.copperworks.compat.jei.interact.battery.BatteryInteractionRecipe;
import mezz.jei.api.recipe.RecipeType;

public final class ModRecipeTypes {
    public static final RecipeType<ChargingRecipe> CHARGING = RecipeType.create(Copperworks.MOD_ID, "charging",
            ChargingRecipe.class);
    public static final RecipeType<InteractRecipe> INTERACT = RecipeType.create(Copperworks.MOD_ID, "interact",
            InteractRecipe.class);
    public static final RecipeType<BatteryInteractionRecipe> BATTERY_INTERACTION = RecipeType.create(Copperworks.MOD_ID,
            "battery_interaction", BatteryInteractionRecipe.class);
}
