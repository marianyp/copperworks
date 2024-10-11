package dev.mariany.copperworks.enchantment;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.enchantment.effect.entity.AttractEntitiesEnchantmentEffect;
import dev.mariany.copperworks.enchantment.effect.entity.DamageAbsorbEnchantmentEffect;
import dev.mariany.copperworks.enchantment.effect.entity.ShockEnchantmentEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.value.MultiplyEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.EnchantmentLevelLootNumberProvider;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> CHARGED_ATTRACTION = of("charged_attraction");
    public static final RegistryKey<Enchantment> LIFE_CURRENT = of("life_current");
    public static final RegistryKey<Enchantment> SHOCK = of("shock");

    static {
        register("attract_entities", AttractEntitiesEnchantmentEffect.CODEC);
        register("damage_absorb", DamageAbsorbEnchantmentEffect.CODEC);
        register("shock", ShockEnchantmentEffect.CODEC);
    }

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Item> itemRegistryEntryLookup = registry.getRegistryLookup(RegistryKeys.ITEM);

        EntityPredicate.Builder builder = EntityPredicate.Builder.create().periodicTick(1)
                .flags(EntityFlagsPredicate.Builder.create());

        register(registry, CHARGED_ATTRACTION, Enchantment.builder(
                        Enchantment.definition(itemRegistryEntryLookup.getOrThrow(ConventionalItemTags.TOOLS), 1, 1,
                                Enchantment.constantCost(15), Enchantment.constantCost(65), 8, AttributeModifierSlot.ANY))
                .addEffect(EnchantmentEffectComponentTypes.TICK, new AttractEntitiesEnchantmentEffect(
                                RegistryEntryList.of(EntityType.ITEM.getRegistryEntry(),
                                        EntityType.EXPERIENCE_ORB.getRegistryEntry()),
                                ConstantFloatProvider.create(AttractEntitiesEnchantmentEffect.DEFAULT_BASE_RANGE)),
                        EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, builder)));

        register(registry, LIFE_CURRENT, Enchantment.builder(
                        Enchantment.definition(itemRegistryEntryLookup.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 1, 1,
                                Enchantment.constantCost(15), Enchantment.constantCost(65), 8, AttributeModifierSlot.ARMOR))
                .addEffect(EnchantmentEffectComponentTypes.POST_ATTACK, EnchantmentEffectTarget.VICTIM,
                        EnchantmentEffectTarget.VICTIM, new DamageAbsorbEnchantmentEffect(
                                ConstantFloatProvider.create(DamageAbsorbEnchantmentEffect.DEFAULT_MAX_HEAL_PERCENTAGE),
                                ConstantIntProvider.create(DamageAbsorbEnchantmentEffect.DEFAULT_REGENERATION_DURATION),
                                ConstantIntProvider.create(DamageAbsorbEnchantmentEffect.DEFAULT_COOLDOWN)),
                        RandomChanceLootCondition.builder(
                                EnchantmentLevelLootNumberProvider.create(EnchantmentLevelBasedValue.linear(0.624F)))));

        register(registry, SHOCK, Enchantment.builder(
                        Enchantment.definition(itemRegistryEntryLookup.getOrThrow(ItemTags.WEAPON_ENCHANTABLE), 1, 5,
                                Enchantment.constantCost(15), Enchantment.constantCost(65), 8, AttributeModifierSlot.ANY))
                .addEffect(EnchantmentEffectComponentTypes.POST_ATTACK, EnchantmentEffectTarget.ATTACKER,
                        EnchantmentEffectTarget.VICTIM, new ShockEnchantmentEffect(
                                ConstantFloatProvider.create(ShockEnchantmentEffect.DEFAULT_DAMAGE_PERCENTAGE),
                                ConstantIntProvider.create(ShockEnchantmentEffect.DEFAULT_DELAY_TICKS)),
                        EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.create().build()))
                .addEffect(EnchantmentEffectComponentTypes.DAMAGE, new MultiplyEnchantmentEffect(
                        EnchantmentLevelBasedValue.constant(ShockEnchantmentEffect.DEFAULT_DAMAGE_PERCENTAGE + 1))));
    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key,
                                 Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }

    private static <T extends EnchantmentEntityEffect> void register(String name, MapCodec<T> codec) {
        Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Copperworks.id(name), codec);
    }

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Copperworks.id(id));
    }

    public static void registerModEnchantments() {
        Copperworks.LOGGER.info("Registering Mod Enchantments for " + Copperworks.MOD_ID);
    }
}
