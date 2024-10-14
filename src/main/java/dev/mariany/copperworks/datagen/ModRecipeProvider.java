package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
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

        createCopperBatteryRecipe(exporter);
        createCopperBracerRecipe(exporter);
        createCopperClockRecipe(exporter);
        createCopperFrameRecipe(exporter);
        createCopperLeverRecipe(exporter);
        createEnderPowderRecipe(exporter);
        createRocketBootsRecipe(exporter);
        createRadioRecipes(exporter);
        createCopperRelayRecipe(exporter);
        createStickyCopperRecipes(exporter);
        createCopperSensorRecipe(exporter);
        createComparatorMirror(exporter);
    }

    private void createCopperBatteryRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModBlocks.COPPER_BATTERY)
                .input('C', ModItems.COPPER_PLATE).input('I', ModItems.IRON_PLATE).input('R', Items.REDSTONE_BLOCK)
                .input('G', Items.COMPARATOR).pattern("CCC").pattern("RGR").pattern("III")
                .criterion(hasItem(Items.COMPARATOR), conditionsFromItem(Items.COMPARATOR)).offerTo(exporter);
    }

    private void createCopperBracerRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.COPPER_BRACER).input('P', ModItems.COPPER_PLATE)
                .pattern("P P").pattern("PPP").pattern("PPP")
                .criterion(hasItem(ModItems.COPPER_PLATE), conditionsFromItem(ModItems.COPPER_PLATE)).offerTo(exporter);
    }

    private static void createPlateRecipe(Item plate, Item ingot, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, plate).pattern("II").pattern("II").input('I', ingot)
                .criterion(hasItem(ingot), conditionsFromItem(ingot)).offerTo(exporter);
    }

    private void createCopperClockRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_CLOCK).pattern(" P ").pattern("PCP")
                .pattern(" P ").input('P', ModItems.COPPER_PLATE).input('C', Items.CLOCK)
                .criterion(hasItem(Items.CLOCK), conditionsFromItem(Items.CLOCK)).offerTo(exporter);
    }

    private void createCopperFrameRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COPPER_FRAME, 4).pattern(" C ")
                .pattern("CSC").pattern(" C ").input('C', Items.COPPER_INGOT).input('S', Items.SCAFFOLDING)
                .criterion(hasItem(Items.SCAFFOLDING), conditionsFromItem(Items.SCAFFOLDING)).offerTo(exporter);
    }

    private void createCopperLeverRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_LEVER).pattern("R").pattern("D")
                .input('R', Items.LIGHTNING_ROD).input('D', Items.COBBLED_DEEPSLATE)
                .criterion(hasItem(Items.LIGHTNING_ROD), conditionsFromItem(Items.LIGHTNING_ROD)).offerTo(exporter);
    }

    private void createEnderPowderRecipe(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ENDER_POWDER).input(Items.BLAZE_POWDER)
                .input(Items.DRAGON_BREATH)
                .criterion(hasItem(Items.DRAGON_BREATH), conditionsFromItem(Items.DRAGON_BREATH)).offerTo(exporter);
    }

    private void createRocketBootsRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, ModItems.ROCKET_BOOTS)
                .input('P', ModItems.COPPER_PLATE).input('N', Items.NETHERITE_INGOT).input('E', ModItems.ENDER_POWDER)
                .pattern("P P").pattern("N N").pattern("E E")
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(exporter);
    }

    private void createRadioRecipes(RecipeExporter exporter) {
        String group = "radio";
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.RADIO).input('C', ModItems.COPPER_PLATE)
                .input('I', ModItems.IRON_PLATE).input('R', Items.REDSTONE).pattern("CRC").pattern("CIC").pattern("CCC")
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE)).group(group).offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.RADIO).input(ModItems.RADIO)
                .criterion(hasItem(ModItems.RADIO), conditionsFromItem(ModItems.RADIO)).group(group)
                .offerTo(exporter, Copperworks.id("reset_radio"));
    }

    private void createCopperRelayRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_RELAY)
                .input('P', ModItems.COPPER_PLATE).input('E', Items.ECHO_SHARD).pattern("PPP").pattern("EEE")
                .pattern("PPP").criterion(hasItem(Items.ECHO_SHARD), conditionsFromItem(Items.ECHO_SHARD))
                .offerTo(exporter);
    }

    private void createStickyCopperRecipe(RecipeExporter exporter, Item input, ItemConvertible output) {
        String group = "sticky_copper";
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output).input('P', ModItems.COPPER_PLATE)
                .input('C', Items.COPPER_INGOT).input('I', input).pattern("PPP").pattern("CIC").pattern("PPP")
                .criterion(hasItem(input), conditionsFromItem(input)).group(group).offerTo(exporter);
    }

    private void createStickyCopperRecipes(RecipeExporter exporter) {
        createStickyCopperRecipe(exporter, Items.SLIME_BALL, ModBlocks.STICKY_COPPER);
        createStickyCopperRecipe(exporter, Items.HONEY_BOTTLE, ModBlocks.STICKY_COPPER_HONEY);
    }

    private void createCopperSensorRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_SENSOR)
                .input('P', ModItems.COPPER_PLATE).input('E', Items.ENDER_EYE).input('O', Items.OBSERVER).pattern("PEP")
                .pattern("POP").criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .offerTo(exporter);
    }

    private void createComparatorMirror(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COMPARATOR_MIRROR)
                .input('C', ModItems.COPPER_PLATE).input('G', Items.COMPARATOR).input('P', Items.PRISMARINE_CRYSTALS)
                .pattern("CGC").pattern("GPG").pattern("CGC")
                .criterion(hasItem(Items.PRISMARINE_CRYSTALS), conditionsFromItem(Items.PRISMARINE_CRYSTALS))
                .offerTo(exporter);
    }
}
