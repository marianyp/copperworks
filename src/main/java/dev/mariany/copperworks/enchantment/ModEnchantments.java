package dev.mariany.copperworks.enchantment;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.enchantment.effect.entity.AttractEntitiesEnchantmentEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> CHARGED_ATTRACTION = of("charged_attraction");
    public static final RegistryKey<Enchantment> LIFE_CURRENT = of("life_current");
    public static final RegistryKey<Enchantment> SHOCK = of("shock");

    static {
        register("attract_entities", AttractEntitiesEnchantmentEffect.CODEC);
    }

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Item> itemRegistryEntryLookup = registry.getRegistryLookup(RegistryKeys.ITEM);

        EntityPredicate.Builder builder = EntityPredicate.Builder.create().periodicTick(1)
                .flags(EntityFlagsPredicate.Builder.create());

        register(registry, CHARGED_ATTRACTION, Enchantment.builder(
                        Enchantment.definition(itemRegistryEntryLookup.getOrThrow(ConventionalItemTags.TOOLS), 2, 1,
                                Enchantment.constantCost(15), Enchantment.constantCost(65), 8, AttributeModifierSlot.ANY))
                .addEffect(EnchantmentEffectComponentTypes.TICK, new AttractEntitiesEnchantmentEffect(
                                RegistryEntryList.of(EntityType.ITEM.getRegistryEntry(),
                                        EntityType.EXPERIENCE_ORB.getRegistryEntry()), ConstantFloatProvider.create(8F)),
                        EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, builder)));
    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key,
                                 Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }

    private static void register(String name, MapCodec<AttractEntitiesEnchantmentEffect> codec) {
        Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Copperworks.id(name), codec);
    }

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Copperworks.id(id));
    }

    public static void registerModEnchantments() {
        Copperworks.LOGGER.info("Registering Mod Enchantments for " + Copperworks.MOD_ID);
    }
}
