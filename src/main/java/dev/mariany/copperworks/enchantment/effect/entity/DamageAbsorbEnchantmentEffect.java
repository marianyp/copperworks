package dev.mariany.copperworks.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.copperworks.mixin.LivingEntityAccessor;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

public record DamageAbsorbEnchantmentEffect(FloatProvider maxHealPercentage,
                                            IntProvider regenerationDuration) implements EnchantmentEntityEffect {
    public static final float DEFAULT_MAX_HEAL_PERCENTAGE = 1.5F;
    public static final int DEFAULT_REGENERATION_DURATION = 20 * 5;

    public static final MapCodec<DamageAbsorbEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(FloatProvider.VALUE_CODEC.optionalFieldOf("max_heal_percentage",
                                            ConstantFloatProvider.create(DEFAULT_MAX_HEAL_PERCENTAGE))
                                    .forGetter(DamageAbsorbEnchantmentEffect::maxHealPercentage),
                            IntProvider.VALUE_CODEC.optionalFieldOf("regeneration_duration",
                                            ConstantIntProvider.create(DEFAULT_REGENERATION_DURATION))
                                    .forGetter(DamageAbsorbEnchantmentEffect::regenerationDuration))
                    .apply(instance, DamageAbsorbEnchantmentEffect::new));

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        Random random = user.getRandom();
        float maxHealPercentage = this.maxHealPercentage().get(random);
        int regenerationDuration = this.regenerationDuration().get(random);

        if (user.isAlive() && maxHealPercentage > 0) {
            if (user instanceof LivingEntity livingEntity) {
                ItemStack itemStack = context.stack();
                EquipmentSlot slot = context.slot();

                float lastDamageTaken = ((LivingEntityAccessor) livingEntity).copperworks$lastDamageTaken();
                float currentHealth = livingEntity.getHealth();
                float maxHealth = livingEntity.getMaxHealth();

                float maxHealAmount = maxHealth - currentHealth;
                float healPercentage = MathHelper.nextFloat(random, 0F, maxHealPercentage);
                float healAmount = MathHelper.clamp(lastDamageTaken * healPercentage, 1, maxHealAmount);
                float newHealth = Math.min(currentHealth + healAmount, maxHealth);

                if (healAmount > 0) {
                    itemStack.damage(1, livingEntity, slot);

                    if (regenerationDuration > 0) {
                        livingEntity.addStatusEffect(
                                new StatusEffectInstance(StatusEffects.REGENERATION, regenerationDuration, 0));
                    }

                    livingEntity.setHealth(newHealth);
                    if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.markHealthDirty();
                    }
                }
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}
