package dev.mariany.copperworks;

import dev.mariany.copperworks.datagen.ModBlockTagProvider;
import dev.mariany.copperworks.datagen.ModItemTagProvider;
import dev.mariany.copperworks.datagen.ModLootTableProvider;
import dev.mariany.copperworks.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CopperworksDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModLootTableProvider::new);
    }
}
