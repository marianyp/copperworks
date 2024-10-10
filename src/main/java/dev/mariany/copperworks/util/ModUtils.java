package dev.mariany.copperworks.util;

import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class ModUtils {
    public static boolean itemNeedsCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(ModComponents.CHARGE);
        Integer maxChargeComponent = itemStack.get(ModComponents.MAX_CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        if (chargeComponent == null || maxChargeComponent == null) {
            return false;
        }

        return chargeComponent < maxChargeComponent;
    }

    public static boolean itemHasSomeCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(ModComponents.CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        if (chargeComponent == null) {
            return false;
        }

        return chargeComponent > 0;
    }

    public static boolean engineerCanUpgrade(ItemStack itemStack) {
        return itemStack.isIn(ModTags.Items.ENGINEER_CAN_UPGRADE) && !itemStack.hasEnchantments();
    }

    public static boolean isUpgraded(ItemStack itemStack) {
        return itemStack.getOrDefault(ModComponents.UPGRADED, false);
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack) {
        decrementCharge(entity, itemStack, 0, 2);
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack, int min, int max) {
        Random random = entity.getRandom();
        World world = entity.getWorld();

        Integer charge = itemStack.get(ModComponents.CHARGE);
        Integer maxCharge = itemStack.get(ModComponents.MAX_CHARGE);

        if (charge != null && maxCharge != null && charge > 0) {
            int newCharge = MathHelper.clamp(charge - MathHelper.nextInt(random, min, max), 0, maxCharge);
            itemStack.set(ModComponents.CHARGE, newCharge);

            if (newCharge <= 0) {
                world.playSoundFromEntity(null, entity, ModSoundEvents.OUT_OF_CHARGE, SoundCategory.NEUTRAL, 0.375F,
                        1F);
            }
        }
    }

    public static void shockEntity(Set<@NotNull Entity> ignore, LivingEntity victim, int shockChain, float damage,
                                   int delay) {
        shockEntity(ignore, victim, shockChain, damage, delay, false);
    }

    public static void shockEntity(Set<@NotNull Entity> ignore, LivingEntity victim, int shockChain, float damage,
                                   int delay, boolean skipDamage) {
        World world = victim.getWorld();

        if (damage > 0) {
            if (victim.isAlive() && !skipDamage) {
                DamageSources damageSources = victim.getDamageSources();
                victim.damage(damageSources.lightningBolt(), damage);
            }

            if (shockChain > 0) {
                if (victim instanceof LivingEntity victimLivingEntity) {
                    TargetPredicate targetPredicate = getTargetPredicate(ignore, victimLivingEntity);

                    LivingEntity nextVictim = world.getClosestEntity(LivingEntity.class, targetPredicate,
                            victimLivingEntity, victim.getX(), victim.getY(), victim.getZ(),
                            Box.from(victim.getPos()).expand(damage * 2));

                    if (nextVictim != null) {
                        ignore.add(nextVictim);

                        Vec3d victimPos = victim.getPos();
                        Vec3d origin = new Vec3d(victimPos.x, victim.getBodyY(0.5), victimPos.z);

                        markForShock(origin, ignore, nextVictim, shockChain - 1, damage, delay);
                    }
                }
            }
        }
    }

    private static @NotNull TargetPredicate getTargetPredicate(Set<@NotNull Entity> ignore, LivingEntity victim) {
        TargetPredicate targetPredicate = TargetPredicate.createNonAttackable();

        return targetPredicate.setPredicate(entity -> {
            if (victim instanceof Monster && !(entity instanceof Monster)) {
                return false;
            }
            if (entity instanceof PassiveEntity && !(victim instanceof PassiveEntity)) {
                return false;
            }

            int progress = entity.getAttachedOrElse(ModAttachmentTypes.SHOCK_DELAY_PROGRESS, 0);
            if (shockPropertiesDefined(entity) && progress > 0) {
                return false;
            }

            return !ignore.contains(entity) && TargetPredicate.createNonAttackable().test(victim, entity);
        });
    }

    public static boolean shockPropertiesDefined(LivingEntity entity) {
        Vec3d origin = entity.getAttached(ModAttachmentTypes.SHOCK_ORIGIN);
        Float damage = entity.getAttached(ModAttachmentTypes.SHOCK_DAMAGE);
        Integer chain = entity.getAttached(ModAttachmentTypes.SHOCK_CHAIN);
        Integer delay = entity.getAttached(ModAttachmentTypes.SHOCK_DELAY);
        List<UUID> ignore = entity.getAttached(ModAttachmentTypes.SHOCK_IGNORE);

        return Stream.of(origin, damage, chain, delay, ignore).allMatch(Objects::nonNull);
    }

    private static void markForShock(Vec3d origin, Set<Entity> ignore, LivingEntity nextVictim, int shockChain,
                                     float damage, int delay) {
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_ORIGIN, origin);
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_CHAIN, shockChain);
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_DAMAGE, damage);
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_DELAY, delay);
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_DELAY_PROGRESS, delay);
        nextVictim.setAttached(ModAttachmentTypes.SHOCK_IGNORE, ignore.stream().map(Entity::getUuid).toList());
    }
}
