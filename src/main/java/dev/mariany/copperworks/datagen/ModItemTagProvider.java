package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output,
                              CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.SWORDS).add(ModItems.COPPER_BROADSWORD);
        getOrCreateTagBuilder(ModTags.Items.DRILLS).add(ModItems.COPPER_DRILL);
        getOrCreateTagBuilder(ModTags.Items.ENGINEER_CAN_UPGRADE).add(ModItems.COPPER_BROADSWORD)
                .add(ModItems.COPPER_DRILL);

        List<TagKey<Item>> drillCapabilityTags = List.of(ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE,
                ItemTags.DURABILITY_ENCHANTABLE, ItemTags.BREAKS_DECORATED_POTS);

        for (TagKey<Item> drillCapabilityTag : drillCapabilityTags) {
            getOrCreateTagBuilder(drillCapabilityTag).addTag(ModTags.Items.DRILLS);
        }
    }
}