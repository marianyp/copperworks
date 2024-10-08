package dev.mariany.copperworks.item.custom;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class CopperDrillItem extends DrillItem {
    private static final int MAX_CHARGE = 20 * 30;
    private static final int CHARGE_RATE = 1;

    private static final ToolMaterial MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 1640;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 12;
        }

        @Override
        public float getAttackDamage() {
            return 2;
        }

        @Override
        public TagKey<Block> getInverseTag() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
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

    public CopperDrillItem() {
        super(MATERIAL, new Item.Settings().attributeModifiers(DrillItem.createAttributeModifiers(MATERIAL, 1, -3)),
                MAX_CHARGE, CHARGE_RATE);
    }
}