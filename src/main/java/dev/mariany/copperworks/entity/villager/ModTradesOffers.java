package dev.mariany.copperworks.entity.villager;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public class ModTradesOffers {
    public static void registerVillagerTrades() {
        TradeOfferHelper.registerVillagerOffers(ModVillagers.ENGINEER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 1),
                    new ItemStack(Items.REPEATER, 2), 12, 1, 0.2f));
            factories.add((entity, random) -> new TradeOffer(new TradedItem(ModItems.COPPER_PLATE, 4),
                    Optional.of(new TradedItem(Items.EMERALD, 6)), ModItems.COPPER_BROADSWORD.getDefaultStack(), 1, 1,
                    0.2f));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.ENGINEER, 2, factories -> {
            factories.add((entity, random) -> plateTrades(Items.COPPER_INGOT, ModItems.COPPER_PLATE));
            factories.add((entity, random) -> plateTrades(Items.IRON_INGOT, ModItems.IRON_PLATE));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.ENGINEER, 3, factories -> {
            factories.add((entity, random) -> railTrades(Items.RAIL, 16, 8));
            factories.add((entity, random) -> railTrades(Items.POWERED_RAIL, 4, 12));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.ENGINEER, 4, factories -> {
            factories.add((entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 12),
                    new ItemStack(ModItems.COPPER_UPGRADE_SMITHING_TEMPLATE), 3, 15, 0.2f));
            factories.add((entity, random) -> new TradeOffer(new TradedItem(ModItems.COPPER_PLATE, 6),
                    Optional.of(new TradedItem(Items.GLASS_PANE, 6)), new ItemStack(ModBlocks.COMPARATOR_MIRROR, 1), 12,
                    15, 0));
            factories.add((entity, random) -> new TradeOffer(new TradedItem(ModItems.COPPER_PLATE, 10),
                    Optional.of(new TradedItem(Items.DIAMOND, 5)), new ItemStack(ModBlocks.COPPER_RELAY, 2), 12, 15,
                    0.2f));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.ENGINEER, 5, factories -> {
            factories.add((entity, random) -> new TradeOffer(new TradedItem(ModItems.COPPER_PLATE, 12),
                    Optional.of(new TradedItem(ModItems.IRON_PLATE, 12)), ModItems.COPPER_DRILL.getDefaultStack(), 1,
                    30, 0.2f));
        });
    }

    private static TradeOffer plateTrades(Item input, Item output) {
        return new TradeOffer(new TradedItem(input, 3), new ItemStack(output, 1), 12, 5, 0F);
    }

    private static TradeOffer railTrades(Item rail, int output, int cost) {
        return new TradeOffer(new TradedItem(Items.COPPER_INGOT, cost), new ItemStack(rail, output), 12, 10, 0.2F);
    }
}
