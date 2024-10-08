package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.item.ModArmorMaterials;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

public class CopperBracerItem extends ArmorItem {
    public CopperBracerItem() {
        super(ModArmorMaterials.COPPER, Type.CHESTPLATE,
                new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(16)));
    }
}
