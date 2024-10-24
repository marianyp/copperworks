package dev.mariany.copperworks.compat.jei.charging;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.compat.jei.ModRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ChargingRecipeCategory extends AbstractRecipeCategory<ChargingRecipe> {
    private static final int COLOR_GRAY = 0xFF808080;

    public ChargingRecipeCategory(IGuiHelper guiHelper) {
        super(ModRecipeTypes.CHARGING, Text.translatable("gui.jei.category.copperworks.charging"),
                guiHelper.createDrawableItemLike(ModBlocks.COPPER_BATTERY), 74, 35);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChargingRecipe recipe, IFocusGroup focuses) {
        ItemStack input = recipe.input();
        ItemStack output = recipe.output();

        IRecipeSlotBuilder inputSlot = builder.addInputSlot(1, 1).addItemStack(input).setStandardSlotBackground();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(57, 1).setStandardSlotBackground().addItemStack(output);

        builder.createFocusLink(inputSlot, outputSlot);
    }

    @Override
    public @Nullable Identifier getRegistryName(ChargingRecipe recipe) {
        return recipe.getUid();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ChargingRecipe recipe, IFocusGroup focuses) {
        Integer secondsToCharge = recipe.getSecondsToCharge();

        secondsToCharge = secondsToCharge == null ? 0 : secondsToCharge;

        if (secondsToCharge > 0) {
            builder.addAnimatedRecipeArrow(secondsToCharge * 20).setPosition(26, 1);
        } else {
            builder.addRecipeArrow().setPosition(26, 1);
        }

        Text text = Text.translatable("gui.jei.category.copperworks.charging.seconds_to_charge", secondsToCharge);
        builder.addText(text, getWidth() - 20, 10)
                .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.RIGHT).setTextAlignment(VerticalAlignment.BOTTOM)
                .setColor(COLOR_GRAY);
    }
}
