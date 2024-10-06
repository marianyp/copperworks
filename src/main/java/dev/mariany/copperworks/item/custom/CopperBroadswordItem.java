package dev.mariany.copperworks.item.custom;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public class CopperBroadswordItem extends SwordItem {
    private static final ToolMaterial MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 375;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 6;
        }

        @Override
        public float getAttackDamage() {
            return 2;
        }

        @Override
        public TagKey<Block> getInverseTag() {
            return null;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public CopperBroadswordItem() {
        super(MATERIAL,
                new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(MATERIAL, 3, -1.25F)));
    }
}
