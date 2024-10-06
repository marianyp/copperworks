package dev.mariany.copperworks.item;

import dev.mariany.copperworks.Copperworks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item COPPER_PLATE = registerItem("copper_plate", new Item(new Item.Settings()));
    public static final Item IRON_PLATE = registerItem("iron_plate", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Copperworks.id(name), item);
    }

    public static void registerModItems() {
        Copperworks.LOGGER.info("Registering Mod Items for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.COPPER_INGOT, COPPER_PLATE);
            entries.addAfter(Items.IRON_INGOT, IRON_PLATE);
        });
    }
}
