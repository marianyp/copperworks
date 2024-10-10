package dev.mariany.copperworks.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

import java.util.HashSet;
import java.util.Set;

public record ShockEnchantmentEffect(FloatProvider shockDamagePercentage,
                                     IntProvider delayTicks) implements EnchantmentEntityEffect {
    public static final float DEFAULT_DAMAGE_PERCENTAGE = 0.5F;
    public static final int DEFAULT_DELAY_TICKS = 6;

    public static final MapCodec<ShockEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatProvider.VALUE_CODEC.optionalFieldOf("shock_damage_percentage",
                            ConstantFloatProvider.create(DEFAULT_DAMAGE_PERCENTAGE))
                    .forGetter(ShockEnchantmentEffect::shockDamagePercentage),
            IntProvider.VALUE_CODEC.optionalFieldOf("delay_ticks", ConstantIntProvider.create(DEFAULT_DELAY_TICKS))
                    .forGetter(ShockEnchantmentEffect::delayTicks)).apply(instance, ShockEnchantmentEffect::new));

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity victim, Vec3d pos) {
        if (context.owner() instanceof LivingEntity user) {
            if (victim instanceof LivingEntity livingEntityVictim) {
                DamageSource damageSource = livingEntityVictim.getRecentDamageSource();

                if (damageSource != null) {
                    ItemStack stack = context.stack();
                    Random random = livingEntityVictim.getRandom();

                    float baseAttackDamage = (float) user.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    float originalDamage = EnchantmentHelper.getDamage(world, stack, livingEntityVictim, damageSource,
                            baseAttackDamage);

                    float damage = shockDamagePercentage.get(random) * originalDamage;
                    int delay = delayTicks.get(random);

                    Set<Entity> ignore = new HashSet<>();
                    ignore.add(user);
                    ignore.add(livingEntityVictim);

                    ModUtils.shockEntity(ignore, livingEntityVictim, level + 1, damage, delay, true);
                }
            }
        }
    }

    @Override
    public MapCodec<ShockEnchantmentEffect> getCodec() {
        return CODEC;
    }
}