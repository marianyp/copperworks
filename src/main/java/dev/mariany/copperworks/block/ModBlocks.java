package dev.mariany.copperworks.block;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.custom.battery.BatteryBlock;
import dev.mariany.copperworks.block.custom.ClockBlock;
import dev.mariany.copperworks.block.custom.CopperFrameBlock;
import dev.mariany.copperworks.block.custom.CopperLeverBlock;
import dev.mariany.copperworks.item.custom.CopperFrameBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {
    public static final Block COPPER_CLOCK = registerBlock("copper_clock", new ClockBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(3, 6)
                    .requiresTool().solidBlock(Blocks::never)));

    public static final Block COPPER_LEVER = registerBlock("copper_lever", new CopperLeverBlock(
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).noCollision().strength(2).requiresTool()));

    public static final Block COPPER_FRAME = registerCopperFrame();

    public static final Block COPPER_BATTERY = registerBlock("copper_battery", new BatteryBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(5, 6)
                    .requiresTool().solidBlock(Blocks::never).pistonBehavior(PistonBehavior.BLOCK)));

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

    private static Block registerCopperFrame() {
        Identifier id = Copperworks.id("copper_frame");
        Block copperFrameBlock = new CopperFrameBlock(
                AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).noCollision().strength(2).requiresTool()
                        .solidBlock(Blocks::never).allowsSpawning(Blocks::never));
        Registry.register(Registries.ITEM, id, new CopperFrameBlockItem(copperFrameBlock, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, copperFrameBlock);
    }

    public static void registerModBlocks() {
        Copperworks.LOGGER.info("Registering Mod Blocks for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(COPPER_BATTERY);
            entries.add(COPPER_CLOCK);
            entries.add(COPPER_LEVER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.addBefore(Items.COPPER_BLOCK, COPPER_FRAME);
        });
    }
}
