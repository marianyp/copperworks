package dev.mariany.copperworks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeOverideProvider extends FabricRecipeProvider {
    public ModRecipeOverideProvider(FabricDataOutput output,
                                    CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        alternativeIronTrapDoor(exporter);
        alternativeIronTrapdoor(exporter);
    }

    private void alternativeIronTrapdoor(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.IRON_TRAPDOOR, 2).input('I', Items.IRON_INGOT)
                .pattern("III").pattern("III")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
    }

    private void alternativeIronTrapDoor(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.IRON_BARS, 16).input('I', Items.IRON_INGOT)
                .pattern("I I").pattern("I I").pattern("I I")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
    }

    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return Identifier.ofVanilla(identifier.getPath());
    }

    @Override
    public String getName() {
        return "Copperworks/Recipe Overrides";
    }
}
