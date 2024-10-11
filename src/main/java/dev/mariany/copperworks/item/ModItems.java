package dev.mariany.copperworks.item;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item COPPER_PLATE = registerItem("copper_plate", new Item(new Item.Settings()));
    public static final Item IRON_PLATE = registerItem("iron_plate", new Item(new Item.Settings()));
    public static final Item COPPER_BROADSWORD = registerItem("copper_broadsword", new CopperBroadswordItem());
    public static final Item COPPER_DRILL = registerItem("copper_drill", new CopperDrillItem());
    public static final Item COPPER_BRACER = registerItem("copper_bracer", new CopperBracerItem());
    public static final Item PARTIAL_DRAGON_BREATH = registerItem("partial_dragon_breath", new PartialDragonBreathItem(
            new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).rarity(Rarity.UNCOMMON)
                    .component(ModComponents.DRAGON_BREATH_FILL, 0)));
    public static final Item ENDER_POWDER = registerItem("ender_powder", new Item(new Item.Settings()));
    public static final Item ROCKET_BOOTS = registerItem("rocket_boots",
            new RocketBootsItem(new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(33)).fireproof()));
    public static final Item AMETHYST_PIECE = registerItem("amethyst_piece", new AmethystPieceItem());
    public static final Item RADIO = registerItem("radio", new RadioItem(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Copperworks.id(name), item);
    }

    public static void registerModItems() {
        Copperworks.LOGGER.info("Registering Mod Items for " + Copperworks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            partialDragonBreathItemGroup(entries);

            entries.addAfter(Items.COPPER_INGOT, COPPER_PLATE);
            entries.addAfter(Items.IRON_INGOT, IRON_PLATE);
            entries.addAfter(Items.BLAZE_POWDER, ENDER_POWDER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.IRON_SWORD, COPPER_BROADSWORD);
            entries.addAfter(Items.TURTLE_HELMET, COPPER_BRACER);
            entries.addAfter(Items.NETHERITE_BOOTS, ROCKET_BOOTS);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addBefore(Items.COMPASS, RADIO);
            entries.addAfter(Items.NETHERITE_HOE, COPPER_DRILL);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.addBefore(ModBlocks.COPPER_BATTERY, RADIO);
        });
    }

    private static void partialDragonBreathItemGroup(FabricItemGroupEntries entries) {
        for (int i = 0; i < PartialDragonBreathItem.MAX_FILL; i++) {
            ItemStack itemStack = PARTIAL_DRAGON_BREATH.getDefaultStack();
            itemStack.set(ModComponents.DRAGON_BREATH_FILL, i);
            entries.addBefore(Items.DRAGON_BREATH, itemStack);
        }
    }
}
