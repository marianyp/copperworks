package dev.mariany.copperworks;

import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.enchantment.ModEnchantments;
import dev.mariany.copperworks.entity.villager.ModTradesOffers;
import dev.mariany.copperworks.entity.villager.ModVillagers;
import dev.mariany.copperworks.event.ServerTickHandler;
import dev.mariany.copperworks.item.ModArmorMaterials;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copperworks implements ModInitializer {
    public static final String MOD_ID = "copperworks";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModArmorMaterials.registerModArmorMaterials();
        ModComponents.registerModComponents();
        ModAttachmentTypes.registerAttachmentTypes();
        ModItems.registerModItems();
        ModEnchantments.registerModEnchantments();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerModBlockEntities();
        ModVillagers.registerVillagers();
        ModTradesOffers.registerVillagerTrades();
        ModSoundEvents.registerModSoundEvents();

        ServerTickEvents.END_SERVER_TICK.register(ServerTickHandler::onServerTick);
    }

    public static Identifier id(String resource) {
        return Identifier.of(Copperworks.MOD_ID, resource);
    }
}