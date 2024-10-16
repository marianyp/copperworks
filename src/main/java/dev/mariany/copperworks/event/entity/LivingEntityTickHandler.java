package dev.mariany.copperworks.event.entity;

import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.event.server.ServerWorldTickHandler;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class LivingEntityTickHandler implements ServerWorldTickHandler {
    @Override
    public void onServerWorldTick(ServerWorld world) {
        for (LivingEntity entity : world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                entity -> !entity.isRemoved())) {
            onLivingEntityTick(entity);
        }
    }

    private void onLivingEntityTick(LivingEntity entity) {
        handleShock(entity);
    }

    private void handleShock(LivingEntity entity) {
        ServerWorld world = (ServerWorld) entity.getWorld();

        Vec3d origin = entity.getAttached(ModAttachmentTypes.SHOCK_ORIGIN);
        Float damage = entity.getAttached(ModAttachmentTypes.SHOCK_DAMAGE);
        Integer chain = entity.getAttached(ModAttachmentTypes.SHOCK_CHAIN);

        int delay = entity.getAttachedOrElse(ModAttachmentTypes.SHOCK_DELAY, 0);
        int delayProgress = entity.getAttachedOrElse(ModAttachmentTypes.SHOCK_DELAY_PROGRESS, 0);

        List<UUID> ignoreUUIDs = entity.getAttachedOrElse(ModAttachmentTypes.SHOCK_IGNORE, List.of());
        Set<Entity> ignoreEntities = new HashSet<>(
                ignoreUUIDs.stream().map(world::getEntity).filter(Objects::nonNull).toList());

        if (!ModUtils.shockPropertiesDefined(entity)) {
            return;
        }

        if (delayProgress > 0) {
            delayProgress = Math.max(0, delayProgress - 1);
            entity.setAttached(ModAttachmentTypes.SHOCK_DELAY_PROGRESS, delayProgress);
            displayParticle(origin, entity, delay, delayProgress);
        } else {
            ModUtils.shockEntity(ignoreEntities, entity, chain, damage, delay);
            unmarkShock(entity);
        }
    }

    private void displayParticle(Vec3d origin, LivingEntity entity, int delay, int delayProgress) {
        ServerWorld world = (ServerWorld) entity.getWorld();

        float progress = 1F - (delayProgress / (float) delay);

        Vec3d entityPos = entity.getPos();

        double particleX = origin.x + (entityPos.x - origin.x) * progress;
        double particleY = origin.y + (entity.getBodyY(0.5) - origin.y) * progress;
        double particleZ = origin.z + (entityPos.z - origin.z) * progress;

        world.spawnParticles(ParticleTypes.CRIT, particleX, particleY, particleZ, 1, 0, 0, 0, 0);
    }

    private void unmarkShock(LivingEntity entity) {
        entity.removeAttached(ModAttachmentTypes.SHOCK_ORIGIN);
        entity.removeAttached(ModAttachmentTypes.SHOCK_DELAY);
        entity.removeAttached(ModAttachmentTypes.SHOCK_CHAIN);
        entity.removeAttached(ModAttachmentTypes.SHOCK_DAMAGE);
        entity.removeAttached(ModAttachmentTypes.SHOCK_DELAY_PROGRESS);
        entity.removeAttached(ModAttachmentTypes.SHOCK_IGNORE);
    }
}
