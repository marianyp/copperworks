package dev.mariany.copperworks.tag;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    public static class Blocks {
        public static TagKey<Block> DRILLABLE = createTag("mineable/drill");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Copperworks.id(name));
        }
    }

    public static class Items {
        public static TagKey<Item> DRILLS = createTag("drills");
        public static TagKey<Item> ENGINEER_CAN_UPGRADE = createTag("engineer_can_upgrade");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Copperworks.id(name));
        }
    }

    public static class Enchantments {
        public static TagKey<Enchantment> FROM_UPGRADE = createTag("from_upgrade");

        private static TagKey<Enchantment> createTag(String name) {
            return TagKey.of(RegistryKeys.ENCHANTMENT, Copperworks.id(name));
        }
    }
}
