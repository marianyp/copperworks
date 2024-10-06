package dev.mariany.copperworks.block;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.custom.ClockBlock;
import dev.mariany.copperworks.block.custom.CopperLeverBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Rarity;

public class ModBlocks {
    public static final Block COPPER_CLOCK = registerBlock("copper_clock", new ClockBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(3, 6)
                    .requiresTool().solidBlock(Blocks::never)));

    public static final Block COPPER_LEVER = registerBlock("copper_lever",
            new CopperLeverBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).noCollision().strength(2).requiresTool()));

    private static Block registerBlock(String name, Block block) {
        return registerBlock(name, block, Rarity.COMMON);
    }

    private static Block registerBlock(String name, Block block, Rarity rarity) {
        registerBlockItem(name, block, rarity);
        return Registry.register(Registries.BLOCK, Copperworks.id(name), block);
    }

    private static void registerBlockItem(String name, Block block, Rarity rarity) {
        Item.Settings settings = new Item.Settings().rarity(rarity);

        Registry.register(Registries.ITEM, Copperworks.id(name), new BlockItem(block, settings));
    }

    public static void registerModBlocks() {
        Copperworks.LOGGER.info("Registering Mod Blocks for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(COPPER_CLOCK);
            entries.add(COPPER_LEVER);
        });
    }
}
