package dev.mariany.copperworks;

import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.world.chunk.ChunkLoadingManager;
import dev.mariany.copperworks.data.BatteryInteractionLoader;
import dev.mariany.copperworks.enchantment.ModEnchantments;
import dev.mariany.copperworks.entity.villager.ModTradesOffers;
import dev.mariany.copperworks.entity.villager.ModVillagers;
import dev.mariany.copperworks.event.server.ServerTickHandler;
import dev.mariany.copperworks.event.block.UseBlockHandler;
import dev.mariany.copperworks.item.ModArmorMaterials;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copperworks implements ModInitializer {
    public static final String MOD_ID = "copperworks";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BatteryInteractionLoader.register();

        ModArmorMaterials.registerModArmorMaterials();
        CopperworksComponents.registerModComponents();
        ModAttachmentTypes.registerAttachmentTypes();
        ModItems.registerModItems();
        ModEnchantments.registerModEnchantments();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerModBlockEntities();
        ModVillagers.registerVillagers();
        ModTradesOffers.registerVillagerTrades();
        ModSoundEvents.registerModSoundEvents();

        ServerLifecycleEvents.SERVER_STARTED.register(ChunkLoadingManager::onServerStart);
        ServerTickEvents.END_SERVER_TICK.register(ServerTickHandler::onServerTick);
        UseBlockCallback.EVENT.register(UseBlockHandler::onUseBlock);
    }

    public static Identifier id(String resource) {
        return Identifier.of(Copperworks.MOD_ID, resource);
    }
}