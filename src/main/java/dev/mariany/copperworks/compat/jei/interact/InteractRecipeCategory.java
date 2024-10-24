package dev.mariany.copperworks.compat.jei.interact;

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
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class InteractRecipeCategory extends AbstractRecipeCategory<InteractRecipe> {
    private static final int COLOR_GRAY = 0xFF808080;

    public InteractRecipeCategory(IGuiHelper guiHelper) {
        super(ModRecipeTypes.INTERACT, Text.translatable("gui.jei.category.copperworks.interact"),
                guiHelper.createDrawableItemLike(Items.CARROT_ON_A_STICK), 125, 32);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InteractRecipe recipe, IFocusGroup focuses) {
        Ingredient validItems = Ingredient.ofStacks(recipe.validItems().stream());
        Ingredient validBlocks = Ingredient.ofStacks(
                recipe.validBlocks().stream().map(block -> block.asItem().getDefaultStack()));
        ItemStack output = recipe.output();

        IRecipeSlotBuilder leftInputSlot = builder.addInputSlot(1, 1).addIngredients(validItems)
                .setStandardSlotBackground();
        IRecipeSlotBuilder rightInputSlot = builder.addInputSlot(50, 1).addIngredients(validBlocks)
                .setStandardSlotBackground();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(108, 1).setStandardSlotBackground().addItemStack(output);

        int validItemsLength = validItems.getMatchingStacks().length;
        int validBlocksLength = validBlocks.getMatchingStacks().length;

        if (validItemsLength == validBlocksLength) {
            if (validItemsLength == 1) {
                builder.createFocusLink(leftInputSlot, rightInputSlot, outputSlot);
            }
        }
    }

    @Override
    public @Nullable Identifier getRegistryName(InteractRecipe recipe) {
        return recipe.getUid();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InteractRecipe recipe, IFocusGroup focuses) {
        float chance = recipe.chance();

        builder.addRecipePlusSign().setPosition(27, 3);
        builder.addRecipeArrow().setPosition(76, 1);

        if (1 > chance) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String chanceText = decimalFormat.format(chance * 100);
            Text text = Text.translatable("gui.jei.category.copperworks.interact.chance", chanceText);
            builder.addText(text, getWidth() - 20, 10)
                    .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                    .setTextAlignment(HorizontalAlignment.RIGHT).setTextAlignment(VerticalAlignment.BOTTOM)
                    .setColor(COLOR_GRAY);
        }
    }
}
