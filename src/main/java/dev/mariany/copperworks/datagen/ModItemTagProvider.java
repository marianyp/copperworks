package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    List<Item> ENGINEER_CAN_UPGRADE = List.of(ModItems.COPPER_BROADSWORD, ModItems.COPPER_DRILL, ModItems.COPPER_BRACER,
            ModItems.ROCKET_BOOTS);
    List<TagKey<Item>> DRILL_CAPABILITIES = List.of(ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE,
            ItemTags.BREAKS_DECORATED_POTS);

    public ModItemTagProvider(FabricDataOutput output,
                              CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS).add(ModItems.COPPER_BROADSWORD);
        getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS).add(ModItems.COPPER_DRILL);
        getOrCreateTagBuilder(ConventionalItemTags.ARMORS).add(ModItems.COPPER_BRACER).add(ModItems.ROCKET_BOOTS);

        getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ModItems.COPPER_BROADSWORD);
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ModItems.COPPER_BRACER);
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ModItems.ROCKET_BOOTS);
        getOrCreateTagBuilder(ModTags.Items.DRILLS).add(ModItems.COPPER_DRILL);

        addEngineerCanUpgrade();
        addDrillCapabilities();
    }

    private void addDrillCapabilities() {
        for (TagKey<Item> tag : DRILL_CAPABILITIES) {
            getOrCreateTagBuilder(tag).addTag(ModTags.Items.DRILLS);
        }
    }

    private void addEngineerCanUpgrade() {
        for (Item item : ENGINEER_CAN_UPGRADE) {
            getOrCreateTagBuilder(ModTags.Items.ENGINEER_CAN_UPGRADE).add(item);
        }
    }
}