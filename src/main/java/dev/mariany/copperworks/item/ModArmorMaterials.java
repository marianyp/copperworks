package dev.mariany.copperworks.item;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> COPPER = registerMaterial("copper",
            Map.of(ArmorItem.Type.HELMET, 2, ArmorItem.Type.CHESTPLATE, 5, ArmorItem.Type.LEGGINGS, 4,
                    ArmorItem.Type.BOOTS, 3, ArmorItem.Type.BODY, 5), 0, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
            () -> Ingredient.EMPTY, 1, 0.1F);

    public static final RegistryEntry<ArmorMaterial> ROCKET_BOOTS = registerMaterial("rocket_boots",
            Map.of(ArmorItem.Type.HELMET, 0, ArmorItem.Type.CHESTPLATE, 0, ArmorItem.Type.LEGGINGS, 0,
                    ArmorItem.Type.BOOTS, 3, ArmorItem.Type.BODY, 0), 0, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.EMPTY, 3, 0.1F);

    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints,
                                                                int enchantability,
                                                                RegistryEntry<SoundEvent> equipSound,
                                                                Supplier<Ingredient> repairIngredientSupplier,
                                                                float toughness, float knockbackResistance) {
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(Copperworks.id(id), "", false));

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier,
                layers, toughness, knockbackResistance);
        material = Registry.register(Registries.ARMOR_MATERIAL, Copperworks.id(id), material);

        return RegistryEntry.of(material);
    }

    public static void registerModArmorMaterials() {
        Copperworks.LOGGER.info("Registering Mod Armor Materials for " + Copperworks.MOD_ID);
    }
}
