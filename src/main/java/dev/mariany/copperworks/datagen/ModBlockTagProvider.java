package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    private static final List<Block> PICKAXE_MINEABLE = List.of(ModBlocks.COPPER_CLOCK, ModBlocks.COPPER_LEVER,
            ModBlocks.COPPER_FRAME, ModBlocks.COPPER_BATTERY, ModBlocks.COPPER_RELAY, ModBlocks.COPPER_RELAY_BOUND,
            ModBlocks.COPPER_RELAY_CHARGED, ModBlocks.COPPER_RELAY_RADIO_BOUND, ModBlocks.STICKY_COPPER,
            ModBlocks.STICKY_COPPER_HONEY);
    private static final List<Block> NEEDS_STONE_TOOL = List.of(ModBlocks.COPPER_CLOCK, ModBlocks.COPPER_LEVER,
            ModBlocks.COPPER_FRAME, ModBlocks.COPPER_BATTERY, ModBlocks.COPPER_RELAY, ModBlocks.COPPER_RELAY_BOUND,
            ModBlocks.COPPER_RELAY_CHARGED, ModBlocks.COPPER_RELAY_RADIO_BOUND, ModBlocks.STICKY_COPPER,
            ModBlocks.STICKY_COPPER_HONEY);
    private static final List<TagKey<Block>> DRILLABLE = List.of(BlockTags.PICKAXE_MINEABLE, BlockTags.SHOVEL_MINEABLE);

    public ModBlockTagProvider(FabricDataOutput output,
                               CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        for (Block block : PICKAXE_MINEABLE) {
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(block);
        }

        for (Block block : NEEDS_STONE_TOOL) {
            getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(block);
        }

        for (TagKey<Block> blockTagKey : DRILLABLE) {
            getOrCreateTagBuilder(ModTags.Blocks.DRILLABLE).forceAddTag(blockTagKey);
        }
    }
}
