package dev.mariany.copperworks.block;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.custom.*;
import dev.mariany.copperworks.block.custom.battery.BatteryBlock;
import dev.mariany.copperworks.block.custom.relay.RelayBlock;
import dev.mariany.copperworks.block.custom.relay.bound.BoundRelayBlock;
import dev.mariany.copperworks.block.custom.relay.ChargedRelayBlock;
import dev.mariany.copperworks.block.custom.relay.bound.radio.RadioBoundRelayBlock;
import dev.mariany.copperworks.block.custom.sensor.ChargedSensorBlock;
import dev.mariany.copperworks.block.custom.sensor.SensorBlock;
import dev.mariany.copperworks.block.custom.stasis.StasisChamber;
import dev.mariany.copperworks.block.custom.stasis.StasisChamberCharged;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.item.custom.CopperFrameBlockItem;
import dev.mariany.copperworks.item.custom.PatinaItem;
import dev.mariany.copperworks.util.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModBlocks {
    public static final Block COPPER_CLOCK = registerBlock("copper_clock", new ClockBlock(genericCopperSettings()));

    public static final Block COPPER_LEVER = registerBlock("copper_lever", new CopperLeverBlock(
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).noCollision().strength(2).requiresTool()));

    public static final Block COPPER_FRAME = registerCopperFrame();

    public static final Block COPPER_BATTERY = registerBlock("copper_battery",
            new BatteryBlock(genericCopperSettings()));

    public static final Block COPPER_RELAY_CHARGED = registerBlock("copper_relay_charged",
            new ChargedRelayBlock(genericCopperSettings()));

    public static final Block COPPER_RELAY = registerChargeable("copper_relay",
            new RelayBlock(AbstractBlock.Settings.copy(COPPER_RELAY_CHARGED)), ModConstants.DEFAULT_MAX_CHARGE,
            ModConstants.DEFAULT_CHARGE_RATE, COPPER_RELAY_CHARGED);

    public static final Block COPPER_RELAY_BOUND = registerBlock("copper_relay_bound",
            new BoundRelayBlock(AbstractBlock.Settings.copy(COPPER_RELAY_CHARGED)));

    public static final Block COPPER_RELAY_RADIO_BOUND = registerBlock("copper_relay_radio_bound",
            new RadioBoundRelayBlock(AbstractBlock.Settings.copy(COPPER_RELAY_CHARGED)));

    public static final Block PATINA = registerPatina();

    public static final Block STICKY_COPPER = registerBlock("sticky_copper",
            new StickyBlock(genericCopperSettings().velocityMultiplier(0)));

    public static final Block STICKY_COPPER_HONEY = registerBlock("sticky_copper_honey",
            new StickyBlock(AbstractBlock.Settings.copy(STICKY_COPPER)));

    public static final Block COPPER_SENSOR_CHARGED = registerBlock("copper_sensor_charged",
            new ChargedSensorBlock(genericCopperSettings().strength(2)));

    public static final Block COPPER_SENSOR = registerChargeable("copper_sensor",
            new SensorBlock(AbstractBlock.Settings.copy(COPPER_SENSOR_CHARGED)), ModConstants.DEFAULT_MAX_CHARGE,
            ModConstants.DEFAULT_CHARGE_RATE, COPPER_SENSOR_CHARGED);

    public static final Block COMPARATOR_MIRROR = registerBlock("comparator_mirror",
            new ComparatorMirrorBlock(genericCopperSettings().sounds(BlockSoundGroup.COPPER_BULB)));

    public static final Block DEACTIVATED_REDSTONE_BLOCK = registerBlock("deactivated_redstone_block",
            new Block(AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)));

    public static final Block COPPER_STASIS_CHAMBER_CHARGED = registerBlock("copper_stasis_chamber_charged",
            new StasisChamberCharged(genericCopperSettings().sounds(BlockSoundGroup.COPPER_BULB)));

    public static final Block COPPER_STASIS_CHAMBER = registerChargeable("copper_stasis_chamber",
            new StasisChamber(AbstractBlock.Settings.copy(COPPER_STASIS_CHAMBER_CHARGED)),
            ModConstants.DEFAULT_MAX_CHARGE * 2, ModConstants.DEFAULT_CHARGE_RATE, COPPER_STASIS_CHAMBER_CHARGED);

    public static final Block WOODEN_RAIL = registerBlock("wooden_rail", new RailBlock(
            AbstractBlock.Settings.create().noCollision().strength(0.4F).sounds(BlockSoundGroup.LADDER)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final Block COPPER_RAIL = registerBlock("copper_rail",
            new RailBlock(AbstractBlock.Settings.copy(Blocks.RAIL)));

    public static final Block ENHANCED_SCULK_SENSOR = registerBlock("enhanced_sculk_sensor",
            new EnhancedSculkSensorBlock(AbstractBlock.Settings.copy(Blocks.CALIBRATED_SCULK_SENSOR)));

    public static final Block MUFFLER = registerBlock("muffler",
            new MufflerBlock(genericCopperSettings().pistonBehavior(PistonBehavior.NORMAL)));

    private static AbstractBlock.Settings genericCopperSettings() {
        return AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(3, 6)
                .requiresTool().solidBlock(Blocks::never).pistonBehavior(PistonBehavior.BLOCK);
    }

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

    private static Block registerChargeable(String name, Block block, int maxCharge, int chargeRate) {
        return registerChargeable(name, block, maxCharge, chargeRate, null);
    }

    private static Block registerChargeable(String name, Block block, int maxCharge, int chargeRate,
                                            @Nullable Block convertsTo) {
        Identifier id = Copperworks.id(name);
        Item.Settings settings = new Item.Settings().component(CopperworksComponents.CHARGE, 0)
                .component(CopperworksComponents.MAX_CHARGE, maxCharge)
                .component(CopperworksComponents.CHARGE_RATE, chargeRate);

        if (convertsTo != null) {
            settings.component(CopperworksComponents.CONVERTS_TO,
                    ContainerComponent.fromStacks(List.of(convertsTo.asItem().getDefaultStack())));
        }

        Registry.register(Registries.ITEM, id, new BlockItem(block, settings));
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static Block registerCopperFrame() {
        Identifier id = Copperworks.id("copper_frame");
        CopperFrameBlock copperFrameBlock = new CopperFrameBlock(
                AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).noCollision().strength(2).requiresTool()
                        .solidBlock(Blocks::never).allowsSpawning(Blocks::never));
        Registry.register(Registries.ITEM, id, new CopperFrameBlockItem(copperFrameBlock, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, copperFrameBlock);
    }

    private static Block registerPatina() {
        Identifier id = Copperworks.id("patina");
        PatinaBlock patinaBlock = new PatinaBlock(
                AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY));
        Registry.register(Registries.ITEM, id, new PatinaItem(patinaBlock, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, patinaBlock);
    }

    public static void registerModBlocks() {
        Copperworks.LOGGER.info("Registering Mod Blocks for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.addAfter(Items.REDSTONE_BLOCK, DEACTIVATED_REDSTONE_BLOCK);
            entries.addAfter(Items.COMPARATOR, COMPARATOR_MIRROR);
            entries.addAfter(Items.TARGET, COPPER_CLOCK);
            entries.addAfter(Items.LEVER, COPPER_LEVER);

            entries.addAfter(Items.CALIBRATED_SCULK_SENSOR, ENHANCED_SCULK_SENSOR);

            entries.addBefore(Items.RAIL, WOODEN_RAIL);
            entries.addAfter(Items.RAIL, COPPER_RAIL);

            entries.add(COPPER_BATTERY);
            entries.add(COPPER_RELAY);
            entries.add(COPPER_RELAY_CHARGED);
            entries.add(COPPER_SENSOR);
            entries.add(COPPER_SENSOR_CHARGED);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.addBefore(Items.COPPER_BLOCK, COPPER_FRAME);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.GUNPOWDER, PATINA);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.addBefore(Items.CRAFTING_TABLE, STICKY_COPPER);
            entries.addAfter(STICKY_COPPER, STICKY_COPPER_HONEY);
            entries.addAfter(Items.RESPAWN_ANCHOR, COPPER_STASIS_CHAMBER);
            entries.addAfter(COPPER_STASIS_CHAMBER, COPPER_STASIS_CHAMBER_CHARGED);
            entries.addAfter(Items.JUKEBOX, MUFFLER);
        });
    }
}
