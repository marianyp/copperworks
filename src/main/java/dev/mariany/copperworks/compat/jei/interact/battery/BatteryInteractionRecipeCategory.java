package dev.mariany.copperworks.compat.jei.interact.battery;

import dev.mariany.copperworks.compat.jei.ModRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BatteryInteractionRecipeCategory extends AbstractRecipeCategory<BatteryInteractionRecipe> {
    public BatteryInteractionRecipeCategory(IGuiHelper guiHelper) {
        super(ModRecipeTypes.BATTERY_INTERACTION, Text.translatable("gui.jei.category.copperworks.battery_interaction"),
                guiHelper.createDrawableItemLike(Items.LIGHTNING_ROD), 74, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BatteryInteractionRecipe recipe, IFocusGroup focuses) {
        ItemStack block = recipe.block().asItem().getDefaultStack();
        ItemStack convertsTo = recipe.convertsTo();

        IRecipeSlotBuilder inputSlot = builder.addInputSlot(1, 1).addItemStack(block).setStandardSlotBackground();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(57, 1).setStandardSlotBackground()
                .addItemStack(convertsTo);

        builder.createFocusLink(inputSlot, outputSlot);
    }

    @Override
    public @Nullable Identifier getRegistryName(BatteryInteractionRecipe recipe) {
        return recipe.getUid();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BatteryInteractionRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(26, 1);
    }
}