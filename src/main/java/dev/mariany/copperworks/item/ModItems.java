package dev.mariany.copperworks.item;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.item.custom.CopperBracer;
import dev.mariany.copperworks.item.custom.CopperBroadswordItem;
import dev.mariany.copperworks.item.custom.CopperDrill;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item COPPER_PLATE = registerItem("copper_plate", new Item(new Item.Settings()));
    public static final Item IRON_PLATE = registerItem("iron_plate", new Item(new Item.Settings()));
    public static final Item COPPER_BROADSWORD = registerItem("copper_broadsword", new CopperBroadswordItem());
    public static final Item COPPER_DRILL = registerItem("copper_drill", new CopperDrill());
    public static final Item COPPER_BRACER = registerItem("copper_bracer", new CopperBracer());

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Copperworks.id(name), item);
    }

    public static void registerModItems() {
        Copperworks.LOGGER.info("Registering Mod Items for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.COPPER_INGOT, COPPER_PLATE);
            entries.addAfter(Items.IRON_INGOT, IRON_PLATE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.IRON_SWORD, COPPER_BROADSWORD);
            entries.addAfter(Items.TURTLE_HELMET, COPPER_BRACER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.NETHERITE_HOE, COPPER_DRILL);
        });
    }
}
