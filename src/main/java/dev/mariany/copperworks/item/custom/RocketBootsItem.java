package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.advancement.criterion.ModCriteria;
import dev.mariany.copperworks.item.ModArmorMaterials;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class RocketBootsItem extends ArmorItem {
    private static final int MAX_CHARGE = 64;
    private static final int CHARGE_RATE = 8;

    private static final float MAX_SPEED = 2F;
    private static final float MAX_THRUST_ALLOWANCE = 1.15F;
    private static final float MIN_SPEED = 0.05F;
    private static final float SECONDS_TO_INCREASE_THRUST = 0.1F;
    private static final float TURN_THRESHOLD = 0.025F;
    private static final float WIND_UP = 0.2F;

    public RocketBootsItem(Settings settings) {
        this(settings, 0, MAX_CHARGE, CHARGE_RATE);
    }

    public RocketBootsItem(Settings settings, int initialCharge, int maxCharge, int chargeRate) {
        super(ModArmorMaterials.ROCKET_BOOTS, Type.BOOTS,
                settings.component(CopperworksComponents.CHARGE, initialCharge)
                        .component(CopperworksComponents.MAX_CHARGE, maxCharge)
                        .component(CopperworksComponents.CHARGE_RATE, chargeRate));
    }

    public static boolean isHalting(LivingEntity entity, ItemStack boots) {
        return ModUtils.itemHasSomeCharge(boots) && entity.isSneaking();
    }

    private static boolean isOutOfBounds(Entity entity) {
        return entity.getY() > entity.getWorld().getDimension().height();
    }

    public void spawnParticles(LivingEntity entity, BipedEntityModel<?> model, ItemStack bootsStack) {
        World world = entity.getWorld();

        boolean halting = (isHalting(entity, bootsStack) && !entity.isOnGround()) || isOutOfBounds(entity);
        boolean thrusting = bootsStack.getOrDefault(CopperworksComponents.THRUST, 0F) > 0F;

        if (!halting && !thrusting) {
            return;
        }

        if (entity instanceof PlayerEntity player) {
            if (player.getAbilities().flying) {
                return;
            }
        }

        if (halting) {
            for (int i = 0; i < 3; i++) {
                if (!world.getBlockState(entity.getBlockPos().down(i)).isAir()) {
                    return;
                }
            }
        }

        SimpleParticleType particleType = halting ? ParticleTypes.POOF : ParticleTypes.DRAGON_BREATH;

        if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() == this) {
            spawnParticles(world, entity, particleType, model.rightLeg.pitch + 0.05, -0.1);
            spawnParticles(world, entity, particleType, model.leftLeg.pitch + 0.05, 0.1);
        }
    }

    private void spawnParticles(World world, LivingEntity entity, SimpleParticleType particleType, double pitch,
                                double zOffset) {
        double yRot = entity.bodyYaw;
        double forwardOffsetX = Math.cos(yRot * Math.PI / 180) * zOffset;
        double forwardOffsetZ = Math.sin(yRot * Math.PI / 180) * zOffset;
        double sideOffsetX = Math.cos((yRot - 90) * Math.PI / 180) * pitch;
        double sideOffsetZ = Math.sin((yRot - 90) * Math.PI / 180) * pitch;

        world.addParticle(particleType, true, entity.getX() + forwardOffsetX + sideOffsetX, entity.getY(),
                entity.getZ() + sideOffsetZ + forwardOffsetZ, 0, 0, 0);
    }

    private void decrementChargeAndDamage(ItemStack stack, LivingEntity entity) {
        if (entity.age % 15 == 0) {
            if (MathHelper.nextFloat(entity.getRandom(), 0, 1) <= 0.25F) {
                ModUtils.decrementCharge(entity, stack);
            }
        }
    }

    private Vec3d calculateNewVelocity(Vec3d rotationVector, Vec3d velocity, float currentThrust) {
        double scalingFactor = currentThrust / MAX_SPEED;
        double inertiaFactor = 1 - scalingFactor;

        return velocity.multiply(inertiaFactor).add(rotationVector.multiply(scalingFactor * MAX_SPEED));
    }

    private void applyVelocity(LivingEntity entity, Vec3d velocity) {
        entity.setVelocity(velocity);
    }

    private void resetThrust(LivingEntity entity, ItemStack stack) {
        if (!entity.getWorld().isClient) {
            stack.set(CopperworksComponents.THRUST, 0F);
        }
    }

    private void increaseThrust(LivingEntity entity, ItemStack stack, float currentThrust) {
        if (entity.age % Math.round(SECONDS_TO_INCREASE_THRUST * 20) == 0) {
            float newThrust = MathHelper.clamp(currentThrust + WIND_UP, MIN_SPEED, MAX_SPEED);
            if (!entity.getWorld().isClient) {
                stack.set(CopperworksComponents.THRUST, newThrust);
            }
        }
    }

    private void decreaseThrust(LivingEntity entity, ItemStack stack, double angleChange) {
        if (!entity.getWorld().isClient) {
            float thrustDecrease = (float) (MAX_SPEED * (MAX_THRUST_ALLOWANCE - angleChange));
            float newThrust = MathHelper.clamp(thrustDecrease, WIND_UP, MAX_SPEED);
            stack.set(CopperworksComponents.THRUST, newThrust);
        }
    }

    private boolean applyThrust(ItemStack bootsStack, LivingEntity entity) {
        float currentThrust = MathHelper.clamp(bootsStack.getOrDefault(CopperworksComponents.THRUST, 0F), MIN_SPEED,
                MAX_SPEED);

        if (isHalting(entity, bootsStack)) {
            resetThrust(entity, bootsStack);
            return true; // allows for fall damage reset
        }

        if (isOutOfBounds(entity)) {
            resetThrust(entity, bootsStack);
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                ModCriteria.REACH_OUT_OF_BOUNDS.trigger(serverPlayer);
            }
            return false;
        }

        boolean thrusted = false;

        if (entity.isFallFlying()) {
            Vec3d rotationVector = entity.getRotationVec(1F);
            Vec3d velocity = entity.getVelocity();

            double cosAngle = velocity.normalize().dotProduct(rotationVector.normalize());
            double angleChange = Math.toDegrees(Math.acos(MathHelper.clamp(cosAngle, -1, 1)));

            Vec3d newVelocity = calculateNewVelocity(rotationVector, velocity, currentThrust);
            applyVelocity(entity, newVelocity);

            if ((angleChange / 1000) > TURN_THRESHOLD) {
                decreaseThrust(entity, bootsStack, angleChange);
            } else {
                increaseThrust(entity, bootsStack, currentThrust);
            }

            thrusted = true;
        } else if (currentThrust != 0) {
            resetThrust(entity, bootsStack);
        }

        return thrusted;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity livingEntity) {
            if (stack.getOrDefault(CopperworksComponents.THRUST, 0F) != 0F) {
                resetThrust(livingEntity, stack);
            }

            if (ModUtils.itemHasSomeCharge(stack)) {
                if (livingEntity.getEquippedStack(EquipmentSlot.FEET) == stack) {
                    if (applyThrust(stack, livingEntity) && !world.isClient) {
                        entity.fallDistance = 0;
                        decrementChargeAndDamage(stack, livingEntity);
                        if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
                            ModCriteria.USE_ROCKET_BOOTS.trigger(serverPlayer);
                        }
                    }
                }
            }
        }
    }
}
