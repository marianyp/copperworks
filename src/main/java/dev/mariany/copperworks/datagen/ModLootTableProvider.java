package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.ModProperties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    private static final List<Block> POWERED_RELAYS = List.of(ModBlocks.COPPER_RELAY, ModBlocks.COPPER_RELAY_BOUND,
            ModBlocks.COPPER_RELAY_CHARGED, ModBlocks.COPPER_RELAY_RADIO_BOUND);

    public ModLootTableProvider(FabricDataOutput dataOutput,
                                CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.COPPER_BATTERY, this::batteryDrop);
        addDrop(ModBlocks.COPPER_CLOCK);
        addDrop(ModBlocks.COPPER_FRAME);
        addDrop(ModBlocks.COPPER_LEVER);
        addDrop(ModBlocks.PATINA);
        addDrop(ModBlocks.STICKY_COPPER);
        addDrop(ModBlocks.STICKY_COPPER_HONEY);
        addDrop(ModBlocks.COPPER_SENSOR);
        addDrop(ModBlocks.COPPER_SENSOR_CHARGED);
        addDrop(ModBlocks.COMPARATOR_MIRROR);
        addDrop(ModBlocks.DEACTIVATED_REDSTONE_BLOCK);

        addRelayDrops();
    }

    private void addRelayDrops() {
        addDrop(ModBlocks.COPPER_RELAY);

        for (Block relay : POWERED_RELAYS) {
            addDrop(relay, ModBlocks.COPPER_RELAY_CHARGED.asItem());
        }
    }

    private LootTable.Builder batteryDrop(Block drop) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(drop)
                        .apply(CopyStateLootFunction.builder(drop).addProperty(ModProperties.CHARGE))));
    }
}
