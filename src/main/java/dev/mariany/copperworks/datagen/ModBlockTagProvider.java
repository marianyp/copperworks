package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.tag.CopperworksTags;
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
            ModBlocks.STICKY_COPPER_HONEY, ModBlocks.COPPER_SENSOR, ModBlocks.COPPER_SENSOR_CHARGED,
            ModBlocks.COMPARATOR_MIRROR, ModBlocks.DEACTIVATED_REDSTONE_BLOCK, ModBlocks.COPPER_STASIS_CHAMBER,
            ModBlocks.COPPER_STASIS_CHAMBER_CHARGED, ModBlocks.ENHANCED_SCULK_SENSOR, ModBlocks.MUFFLER);
    private static final List<Block> AXE_MINEABLE = List.of(ModBlocks.WOODEN_RAIL);
    private static final List<Block> HOE_MINEABLE = List.of(ModBlocks.ENHANCED_SCULK_SENSOR);
    private static final List<Block> NEEDS_STONE_TOOL = List.of(ModBlocks.COPPER_CLOCK, ModBlocks.COPPER_LEVER,
            ModBlocks.COPPER_FRAME, ModBlocks.COPPER_BATTERY, ModBlocks.COPPER_RELAY, ModBlocks.COPPER_RELAY_BOUND,
            ModBlocks.COPPER_RELAY_CHARGED, ModBlocks.COPPER_RELAY_RADIO_BOUND, ModBlocks.STICKY_COPPER,
            ModBlocks.STICKY_COPPER_HONEY, ModBlocks.COPPER_SENSOR, ModBlocks.COPPER_SENSOR_CHARGED,
            ModBlocks.COMPARATOR_MIRROR, ModBlocks.DEACTIVATED_REDSTONE_BLOCK, ModBlocks.COPPER_STASIS_CHAMBER,
            ModBlocks.COPPER_STASIS_CHAMBER_CHARGED, ModBlocks.MUFFLER);
    private static final List<Block> RAILS = List.of(ModBlocks.WOODEN_RAIL, ModBlocks.COPPER_RAIL);
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

        for (Block block : AXE_MINEABLE) {
            getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(block);
        }

        for (Block block : HOE_MINEABLE) {
            getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(block);
        }

        for (Block block : NEEDS_STONE_TOOL) {
            getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(block);
        }

        for (Block block : RAILS) {
            getOrCreateTagBuilder(BlockTags.RAILS).add(block);
        }

        for (TagKey<Block> blockTagKey : DRILLABLE) {
            getOrCreateTagBuilder(CopperworksTags.Blocks.DRILLABLE).forceAddTag(blockTagKey);
        }

        addWrenchBlacklist();
    }

    private void addWrenchBlacklist() {
        getOrCreateTagBuilder(CopperworksTags.Blocks.WRENCH_BLACKLIST).forceAddTag(BlockTags.BEDS);
    }
}
