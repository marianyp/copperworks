package dev.mariany.copperworks;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.item.ModArmorMaterials;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copperworks implements ModInitializer {
    public static final String MOD_ID = "copperworks";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModArmorMaterials.registerModArmorMaterials();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerModBlockEntities();
        ModSoundEvents.registerModSoundEvents();
    }

    public static Identifier id(String resource) {
        return Identifier.of(Copperworks.MOD_ID, resource);
    }
}