package dev.mariany.copperworks.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

import java.util.List;

public record AttractEntitiesEnchantmentEffect(RegistryEntryList<EntityType<?>> affectedEntities,
                                               FloatProvider baseRange) implements EnchantmentEntityEffect {
    public static final float DEFAULT_BASE_RANGE = 8F;

    public static final MapCodec<AttractEntitiesEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("affected_entities")
                                    .forGetter(AttractEntitiesEnchantmentEffect::affectedEntities),
                            FloatProvider.VALUE_CODEC.optionalFieldOf("base_range",
                                            ConstantFloatProvider.create(DEFAULT_BASE_RANGE))
                                    .forGetter(AttractEntitiesEnchantmentEffect::baseRange))
                    .apply(instance, AttractEntitiesEnchantmentEffect::new));

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        Random random = user.getRandom();
        float range = baseRange().get(random) * level;

        List<Entity> nearbyEntities = world.getOtherEntities(user, Box.from(user.getPos()).expand(range));

        for (Entity nearbyEntity : nearbyEntities) {
            boolean isAffected = affectedEntities.stream()
                    .anyMatch(affectedEntityEntry -> affectedEntityEntry.value().equals(nearbyEntity.getType()));

            if (isAffected) {
                attract(user, nearbyEntity);
            }
        }
    }

    private void attract(Entity centerEntity, Entity pullingEntity) {
        Vec3d centerPos = centerEntity.getEyePos();
        Vec3d vec3d = new Vec3d(centerPos.getX() - pullingEntity.getX(), centerPos.getY() - pullingEntity.getY(),
                centerPos.getZ() - pullingEntity.getZ()).multiply(0.0425);
        pullingEntity.setVelocity(pullingEntity.getVelocity().add(vec3d));
        pullingEntity.velocityModified = true;
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}
