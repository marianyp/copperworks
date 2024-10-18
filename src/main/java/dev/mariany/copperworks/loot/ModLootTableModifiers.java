package dev.mariany.copperworks.loot;

import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class ModLootTableModifiers {
    private static final List<RegistryKey<LootTable>> CONTAINS_COPPER_UPGRADE = List.of(
            LootTables.ABANDONED_MINESHAFT_CHEST);

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, provider) -> {
            if (CONTAINS_COPPER_UPGRADE.contains(key)) {
                LootPool.Builder poolBuilder = LootPool.builder().rolls(UniformLootNumberProvider.create(1, 2))
                        .conditionally(RandomChanceLootCondition.builder(0.8f))
                        .with(ItemEntry.builder(ModItems.COPPER_UPGRADE_SMITHING_TEMPLATE));

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
