package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output,
                             CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        createPlateRecipe(ModItems.COPPER_PLATE, Items.COPPER_INGOT, exporter);
        createPlateRecipe(ModItems.IRON_PLATE, Items.IRON_INGOT, exporter);
        createCopperClockRecipe(exporter);
        createCopperLeverRecipe(exporter);
    }

    private static void createPlateRecipe(Item plate, Item ingot, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, plate).pattern("II").pattern("II").input('I', ingot)
                .criterion(hasItem(plate), conditionsFromItem(plate)).offerTo(exporter);
    }

    private void createCopperClockRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_CLOCK).pattern(" P ").pattern("PCP")
                .pattern(" P ").input('P', ModItems.COPPER_PLATE).input('C', Items.CLOCK)
                .criterion(hasItem(Items.CLOCK), conditionsFromItem(Items.CLOCK)).offerTo(exporter);
    }

    private void createCopperLeverRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_LEVER).pattern("R").pattern("D")
                .input('R', Items.LIGHTNING_ROD).input('D', Items.COBBLED_DEEPSLATE)
                .criterion(hasItem(ModBlocks.COPPER_LEVER), conditionsFromItem(ModBlocks.COPPER_LEVER))
                .offerTo(exporter);
    }
}
