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
import dev.mariany.copperworks.item.component.ModComponents;
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
            new RelayBlock(AbstractBlock.Settings.copyShallow(COPPER_RELAY_CHARGED)), ModConstants.DEFAULT_MAX_CHARGE,
            ModConstants.DEFAULT_CHARGE_RATE, COPPER_RELAY_CHARGED);

    public static final Block COPPER_RELAY_BOUND = registerBlock("copper_relay_bound",
            new BoundRelayBlock(AbstractBlock.Settings.copyShallow(COPPER_RELAY_CHARGED)));

    public static final Block COPPER_RELAY_RADIO_BOUND = registerBlock("copper_relay_radio_bound",
            new RadioBoundRelayBlock(AbstractBlock.Settings.copyShallow(COPPER_RELAY_CHARGED)));

    public static final Block PATINA = registerPatina();

    public static final Block STICKY_COPPER = registerBlock("sticky_copper",
            new StickyBlock(genericCopperSettings().velocityMultiplier(0)));

    public static final Block STICKY_COPPER_HONEY = registerBlock("sticky_copper_honey",
            new StickyBlock(AbstractBlock.Settings.copyShallow(STICKY_COPPER)));

    public static final Block COPPER_SENSOR_CHARGED = registerBlock("copper_sensor_charged",
            new ChargedSensorBlock(genericCopperSettings().strength(2)));

    public static final Block COPPER_SENSOR = registerChargeable("copper_sensor",
            new SensorBlock(AbstractBlock.Settings.copyShallow(COPPER_SENSOR_CHARGED)), ModConstants.DEFAULT_MAX_CHARGE,
            ModConstants.DEFAULT_CHARGE_RATE, COPPER_SENSOR_CHARGED);

    public static final Block COMPARATOR_MIRROR = registerBlock("comparator_mirror",
            new ComparatorMirrorBlock(genericCopperSettings().sounds(BlockSoundGroup.COPPER_BULB)));

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
        Item.Settings settings = new Item.Settings().component(ModComponents.CHARGE, 0)
                .component(ModComponents.MAX_CHARGE, maxCharge).component(ModComponents.CHARGE_RATE, chargeRate);

        if (convertsTo != null) {
            settings.component(ModComponents.CONVERTS_TO,
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
            entries.addAfter(Items.TARGET, COPPER_CLOCK);
            entries.addAfter(Items.LEVER, COPPER_LEVER);

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
        });
    }
}
