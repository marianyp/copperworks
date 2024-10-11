package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
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
        addDrop(ModBlocks.COPPER_BATTERY);
        addDrop(ModBlocks.COPPER_CLOCK);
        addDrop(ModBlocks.COPPER_FRAME);
        addDrop(ModBlocks.COPPER_LEVER);
        addDrop(ModBlocks.PATINA);

        addRelayDrops();
    }

    private void addRelayDrops() {
        addDrop(ModBlocks.COPPER_RELAY);

        for (Block relay : POWERED_RELAYS) {
            addDrop(relay, ModBlocks.COPPER_RELAY_CHARGED.asItem());
        }
    }
}
