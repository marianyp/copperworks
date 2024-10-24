package dev.mariany.copperworks.event.entity;

import com.google.common.collect.Lists;
import dev.mariany.copperworks.attachment.ModAttachmentTypes;
import dev.mariany.copperworks.entity.villager.ModVillagers;
import dev.mariany.copperworks.event.server.ServerWorldTickHandler;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.CopperworksTags;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillageGossipType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class VillagerTickHandler implements ServerWorldTickHandler {
    private static final int MAX_PROGRESS = 10;

    private static final int MAX_PLAYER_RANGE = 32; // max range for villager to look for player
    private static final int GIVE_RANGE = 3; // range villager has to be to give item

    private static final int MAX_ENCHANT_LEVEL = 32;
    private static final int MIN_ENCHANT_LEVEL = 25;
    private static final int ENCHANTABILITY = 15;
    private static final float REPUTATION_MULTIPLIER = 0.02F;

    // TODO: Implement advancement for upgrading item
    private static final int ADVANCEMENT_RADIUS = 20;

    @Override
    public void onServerWorldTick(ServerWorld world) {
        for (VillagerEntity entity : world.getEntitiesByType(EntityType.VILLAGER, entity -> !entity.isRemoved())) {
            onVillagerTick(entity);
        }
    }

    private void onVillagerTick(VillagerEntity villager) {
        if (villager.getVillagerData().getProfession().equals(ModVillagers.ENGINEER)) {
            if (isBusy(villager)) {
                return;
            }

            handleItemPickup(villager);
            handleItemUpgrade(villager);

            if (villager.isDead()) {
                ItemStack upgradingItem = getUpgradingItem(villager);
                if (!upgradingItem.isEmpty()) {
                    dropUpgradingItem(villager);
                }
            }
        }
    }

    private boolean isBusy(VillagerEntity villager) {
        return villager.hasCustomer() || villager.isSleeping() || villager.isPanicking() || PanicTask.isHostileNearby(
                villager) || PanicTask.wasHurt(villager);
    }

    private void handleItemPickup(VillagerEntity villager) {
        World world = villager.getWorld();
        boolean canPickUpLoot = villager.canPickUpLoot();
        boolean isAlive = villager.isAlive();
        boolean gameruleAllows = world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        ItemStack upgradingItem = getUpgradingItem(villager);

        if (canPickUpLoot && isAlive && gameruleAllows && upgradingItem.isEmpty()) {
            Vec3i itemPickUpRangeExpander = new Vec3i(1, 0, 1);
            List<ItemEntity> itemEntities = world.getNonSpectatingEntities(ItemEntity.class, villager.getBoundingBox()
                    .expand(itemPickUpRangeExpander.getX(), itemPickUpRangeExpander.getY(),
                            itemPickUpRangeExpander.getZ()));

            for (ItemEntity itemEntity : itemEntities) {
                if (itemEntity.isRemoved()) {
                    continue;
                }

                if (itemEntity.getStack().isEmpty()) {
                    continue;
                }

                if (itemEntity.cannotPickup()) {
                    continue;
                }

                if (!ModUtils.engineerCanUpgrade(villager, itemEntity.getStack())) {
                    continue;
                }

                startUpgrading(itemEntity, villager);
            }
        }
    }

    private ItemStack getUpgradingItem(VillagerEntity villager) {
        return villager.getAttachedOrCreate(ModAttachmentTypes.UPGRADING_ITEM);
    }

    private void setUpgradingItem(VillagerEntity villager, ItemStack itemStack) {
        villager.setAttached(ModAttachmentTypes.UPGRADING_ITEM, itemStack);
    }

    private void dropUpgradingItem(VillagerEntity villager) {
        ItemStack upgradingItem = getUpgradingItem(villager);
        villager.dropStack(upgradingItem, 0.5F);
        clearUpgradingItem(villager);
    }

    private void clearUpgradingItem(VillagerEntity villager) {
        setUpgradingItem(villager, ItemStack.EMPTY);
        setUpgradeProgress(villager, 0);
    }

    private void startUpgrading(ItemEntity itemEntity, VillagerEntity villager) {
        ItemStack upgradingItem = itemEntity.getStack();

        setUpgradingItem(villager, upgradingItem.copyWithCount(1));
        upgradingItem.decrement(1);

        playSoundAtVillager(villager, SoundEvents.ENTITY_ITEM_PICKUP, 1F);
        playSoundAtVillager(villager, SoundEvents.ENTITY_WANDERING_TRADER_TRADE, 1.4F);
    }

    private void playSoundAtVillager(VillagerEntity villager, SoundEvent soundEvent, float pitch) {
        villager.getWorld()
                .playSound(null, villager.getX(), villager.getY(), villager.getZ(), soundEvent, SoundCategory.NEUTRAL,
                        0.5F, pitch, 0);
    }

    private void handleItemUpgrade(VillagerEntity villager) {
        ItemStack upgradingItem = getUpgradingItem(villager);
        int currentProgress = getUpgradeProgress(villager);

        if (!upgradingItem.isEmpty()) {
            if (ModUtils.getReputationFromItem(villager, upgradingItem) < 0) {
                dropUpgradingItem(villager);
                return;
            }

            ++currentProgress;

            if (currentProgress >= MAX_PROGRESS) {
                ItemStack upgradedItem = upgradeItem(villager, upgradingItem);
                if (giveToPlayer(villager, upgradedItem)) {
                    clearUpgradingItem(villager);
                    playSoundAtVillager(villager, SoundEvents.ENTITY_WANDERING_TRADER_YES, 1.2F);
                    return;
                }
            }

            if (currentProgress < MAX_PROGRESS) {
                if (villager.age % 20 == 0) {
                    if (currentProgress % 2 == 0) {
                        displayUpgradeParticle(villager);
                        playSoundAtVillager(villager, ModSoundEvents.CHARGE, 1F);
                    }

                    setUpgradeProgress(villager, MathHelper.clamp(currentProgress, 0, MAX_PROGRESS));
                }
            }
        } else if (currentProgress != 0) {
            setUpgradeProgress(villager, 0);
        }
    }

    private void displayUpgradeParticle(VillagerEntity villager) {
        ServerWorld world = (ServerWorld) villager.getWorld();
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, villager.getX(), villager.getY() + 2.5, villager.getZ(), 5,
                0.25, 0.25, 0.05, 0.01);
    }

    private int getUpgradeProgress(VillagerEntity villager) {
        return villager.getAttachedOrCreate(ModAttachmentTypes.UPGRADE_PROGRESS);
    }

    private void setUpgradeProgress(VillagerEntity villager, int progress) {
        villager.setAttached(ModAttachmentTypes.UPGRADE_PROGRESS, progress);
    }

    private ItemStack upgradeItem(VillagerEntity villager, ItemStack upgradingItem) {
        ItemStack upgradedItem = upgradingItem.copy();

        int reputation = ModUtils.getReputationFromItem(villager, upgradedItem);
        int enchantmentLevel = MIN_ENCHANT_LEVEL + Math.round(reputation * REPUTATION_MULTIPLIER);

        if (reputation > 0) {
            ++enchantmentLevel;
        }

        int clampedEnchantmentLevel = MathHelper.clamp(enchantmentLevel, MIN_ENCHANT_LEVEL, MAX_ENCHANT_LEVEL);

        List<EnchantmentLevelEntry> list = generateEnchantments(villager.getWorld(), upgradedItem,
                clampedEnchantmentLevel);

        for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
            upgradedItem.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
        }

        upgradedItem.set(CopperworksComponents.UPGRADED, true);

        return upgradedItem;
    }

    private boolean giveToPlayer(VillagerEntity villager, ItemStack upgradedItem) {
        ServerWorld world = (ServerWorld) villager.getWorld();
        PlayerEntity player = ModUtils.getItemStackOwner(world, upgradedItem);

        if (player == null || player.isSpectator()) {
            return false;
        }

        if (villager.distanceTo(player) > MAX_PLAYER_RANGE) {
            return false;
        }

        LookTargetUtil.lookAt(villager, player);

        if (isCloseEnough(villager, player)) {
            LookTargetUtil.give(villager, upgradedItem, player.getPos());
            resetPlayerReputation(player, villager);
            return true;
        }

        LookTargetUtil.walkTowards(villager, player, 0.5F, GIVE_RANGE - 1);
        return false;
    }

    private void resetPlayerReputation(PlayerEntity player, VillagerEntity villager) {
        for (VillageGossipType value : VillageGossipType.values()) {
            villager.getGossip().remove(player.getUuid(), value);
        }
    }

    private boolean isCloseEnough(VillagerEntity villager, PlayerEntity player) {
        BlockPos blockPos = player.getBlockPos();
        BlockPos blockPos2 = villager.getBlockPos();
        return blockPos2.isWithinDistance(blockPos, GIVE_RANGE);
    }

    public List<EnchantmentLevelEntry> generateEnchantments(World world, ItemStack stack, int level) {
        Random random = world.getRandom();
        DynamicRegistryManager registryManager = world.getRegistryManager();

        List<EnchantmentLevelEntry> enchantments = Lists.newArrayList();

        level += 1 + random.nextInt(ENCHANTABILITY / 4 + 1) + random.nextInt(ENCHANTABILITY / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
        level = MathHelper.clamp(Math.round(level + level * f), 1, Integer.MAX_VALUE);

        Optional<RegistryEntryList.Named<Enchantment>> registryEntries = registryManager.get(RegistryKeys.ENCHANTMENT)
                .getEntryList(CopperworksTags.Enchantments.FROM_UPGRADE);

        if (registryEntries.isEmpty()) {
            return enchantments;
        }

        List<EnchantmentLevelEntry> possibleEnchants = EnchantmentHelper.getPossibleEntries(level, stack,
                registryEntries.get().stream());

        if (!possibleEnchants.isEmpty()) {
            Weighting.getRandom(random, possibleEnchants).ifPresent(enchantments::add);

            while (random.nextInt(50) <= level) {
                if (!enchantments.isEmpty()) {
                    EnchantmentHelper.removeConflicts(possibleEnchants, Util.getLast(enchantments));
                }

                if (possibleEnchants.isEmpty()) {
                    break;
                }

                Weighting.getRandom(random, possibleEnchants).ifPresent(enchantments::add);
                level /= 2;
            }
        }

        return enchantments;
    }
}

