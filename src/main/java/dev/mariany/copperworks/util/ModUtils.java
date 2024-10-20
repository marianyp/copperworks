package dev.mariany.copperworks.util;

import dev.mariany.copperworks.StateSaverAndLoader;
import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.StickyBlock;
import dev.mariany.copperworks.block.custom.battery.BatteryBlock;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.ModTags;
import dev.mariany.copperworks.world.chunk.ChunkLoadingManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class ModUtils {
    public static int wrapIncrement(int value, int min, int max) {
        return wrapIncrement(value, min, max, 1);
    }

    public static int wrapIncrement(int value, int min, int max, int incrementAmount) {
        value += incrementAmount;

        if (value > max) {
            value = min;
        } else if (value < min) {
            value = max;
        }

        return value;
    }

    public static boolean itemNeedsCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(CopperworksComponents.CHARGE);
        Integer maxChargeComponent = itemStack.get(CopperworksComponents.MAX_CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        if (chargeComponent == null || maxChargeComponent == null) {
            return false;
        }

        return chargeComponent < maxChargeComponent;
    }

    private static int getChargeFromBatteryStack(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof BatteryBlock) {
                BlockStateComponent blockStateComponent = itemStack.get(DataComponentTypes.BLOCK_STATE);

                if (blockStateComponent == null) {
                    return ModConstants.MAX_BATTERY_CHARGE;
                }

                Integer charge = blockStateComponent.getValue(ModProperties.CHARGE);

                if (charge != null) {
                    return charge;
                }
            }
        }

        return 0;
    }

    public static boolean itemHasSomeCharge(ItemStack itemStack) {
        Integer chargeComponent = itemStack.get(CopperworksComponents.CHARGE);

        if (itemStack.isEmpty()) {
            return false;
        }

        int batteryCharge = getChargeFromBatteryStack(itemStack);

        if (batteryCharge > 0) {
            return true;
        }

        if (chargeComponent == null) {
            return false;
        }

        return chargeComponent > 0;
    }

    @Nullable
    public static PlayerEntity getItemStackOwner(World world, ItemStack itemStack) {
        UUID lastThrownUUID = itemStack.get(CopperworksComponents.LAST_THROWN);

        if (lastThrownUUID != null) {
            return world.getPlayerByUuid(lastThrownUUID);
        }

        return null;
    }

    public static int getReputationFromItem(VillagerEntity villager, ItemStack itemStack) {
        PlayerEntity lastThrown = getItemStackOwner(villager.getWorld(), itemStack);
        if (lastThrown != null) {
            return villager.getReputation(lastThrown);
        }
        return 0;
    }

    public static boolean engineerCanUpgrade(VillagerEntity villager, ItemStack itemStack) {
        if (getReputationFromItem(villager, itemStack) < 0) {
            return false;
        }
        return itemStack.isIn(ModTags.Items.ENGINEER_CAN_UPGRADE) && !itemStack.hasEnchantments();
    }

    public static boolean isUpgraded(ItemStack itemStack) {
        return itemStack.getOrDefault(CopperworksComponents.UPGRADED, false);
    }

    public static boolean isCharging(ItemStack itemStack) {
        return Boolean.TRUE.equals(itemStack.get(CopperworksComponents.CHARGING));
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack) {
        decrementCharge(entity, itemStack, 0, 2);
    }

    public static void decrementCharge(LivingEntity entity, ItemStack itemStack, int min, int max) {
        Random random = entity.getRandom();
        World world = entity.getWorld();

        Integer charge = itemStack.get(CopperworksComponents.CHARGE);
        Integer maxCharge = itemStack.get(CopperworksComponents.MAX_CHARGE);

        if (charge != null && maxCharge != null && charge > 0) {
            int newCharge = MathHelper.clamp(charge - MathHelper.nextInt(random, min, max), 0, maxCharge);
            itemStack.set(CopperworksComponents.CHARGE, newCharge);

            if (newCharge <= 0) {
                world.playSoundFromEntity(null, entity, ModSoundEvents.OUT_OF_CHARGE, SoundCategory.NEUTRAL, 0.375F,
                        1F);
            }
        }
    }

    public static void appendBlockStateChargeTooltip(ItemStack itemStack, List<Text> tooltip, int maxCharge) {
        int charge = maxCharge;

        BlockStateComponent blockStateComponent = itemStack.get(DataComponentTypes.BLOCK_STATE);

        if (blockStateComponent != null) {
            Integer chargeState = blockStateComponent.getValue(ModProperties.CHARGE);
            if (chargeState != null) {
                charge = chargeState;
            }
        }

        tooltip.add(ModUtils.generateChargeTooltip(charge, maxCharge));
    }

    public static Text generateChargeTooltip(int charge, int max) {
        return Text.translatable("item.copperworks.charge.tooltip", generateBars(charge, max)).withColor(Colors.RED);
    }

    private static String generateBars(int value, int max) {
        final int TOTAL_BLOCKS = 10;

        // Calculate the proportion of the value to the max
        double proportion = (double) value / max;

        // Scale the proportion to the total number of blocks
        int fullBlocks = (int) Math.round(proportion * TOTAL_BLOCKS);


        // Ensure fullBlocks is within the valid range
        fullBlocks = Math.max(0, Math.min(TOTAL_BLOCKS, fullBlocks));
        if (value > 0 && fullBlocks == 0) {
            fullBlocks = 1;
        } else if (value < max && fullBlocks == TOTAL_BLOCKS) {
            fullBlocks = TOTAL_BLOCKS - 1;
        }
        int emptyBlocks = TOTAL_BLOCKS - fullBlocks;

        String fullChar = "▮";
        String emptyChar = "▯";

        return fullChar.repeat(fullBlocks) + emptyChar.repeat(emptyBlocks);
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
                DamageSources damageSources = world.getDamageSources();
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

    private static boolean targetPredicateFilter(Set<@NotNull Entity> ignore, LivingEntity victim,
                                                 LivingEntity possibleNextVictim) {
        if (victim instanceof Monster && !(possibleNextVictim instanceof Monster)) {
            return false;
        }
        if (possibleNextVictim instanceof PassiveEntity && !(victim instanceof PassiveEntity)) {
            return false;
        }

        int progress = possibleNextVictim.getAttachedOrElse(ModAttachmentTypes.SHOCK_DELAY_PROGRESS, 0);
        if (shockPropertiesDefined(possibleNextVictim) && progress > 0) {
            return false;
        }

        return !ignore.contains(possibleNextVictim) && TargetPredicate.createNonAttackable()
                .test(victim, possibleNextVictim);
    }

    private static TargetPredicate getTargetPredicate(Set<@NotNull Entity> ignore, LivingEntity currentVictim) {
        TargetPredicate targetPredicate = TargetPredicate.createNonAttackable();
        return targetPredicate.setPredicate(entity -> targetPredicateFilter(ignore, currentVictim, entity));
    }

    private static TargetPredicate getWaterTargetPredicate(Set<@NotNull Entity> ignore, LivingEntity currentVictim,
                                                           LivingEntity foundNextVictim) {
        TargetPredicate targetPredicate = TargetPredicate.createNonAttackable();
        return targetPredicate.setPredicate(entity -> {
            if (!currentVictim.isTouchingWater() || !entity.isTouchingWater()) {
                return false;
            }
            if (foundNextVictim.equals(entity)) {
                return false;
            }
            return targetPredicateFilter(ignore, currentVictim, entity);
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

    public static boolean isSameDimension(World world, GlobalPos globalPos) {
        return world.getRegistryKey().getValue().equals(globalPos.dimension().getValue());
    }

    public static boolean isChunkLoaded(ServerWorld world, BlockPos pos) {
        ChunkPos chunkPos = ChunkLoadingManager.getChunkPos(world, pos);

        boolean chunkLoaderPresent = StateSaverAndLoader.getWorldState(world).chunkLoaders.containsKey(chunkPos);

        if (chunkLoaderPresent) {
            return true;
        }

        WorldChunk worldChunk = world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z);
        return worldChunk != null && worldChunk.getLevelType() == ChunkLevelType.ENTITY_TICKING && world.isChunkLoaded(
                chunkPos.toLong());
    }

    public static boolean isEntityStuck(Entity entity) {
        if (!entity.isAlive()) {
            return false;
        }

        if (entity instanceof PlayerEntity) {
            return false;
        }

        return entity.getSteppingBlockState().getBlock() instanceof StickyBlock;
    }

    public static boolean isPowered(World world, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (world.isEmittingRedstonePower(blockPos.offset(direction), direction)) {
                return true;
            }
        }

        return false;
    }
}
