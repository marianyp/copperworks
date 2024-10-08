package dev.mariany.copperworks.entity.villager;

import com.google.common.collect.ImmutableSet;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.sound.ModSoundEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModVillagers {
    public static final String COPPER_BATTERY_POI = "copper_battery_poi";
    public static final RegistryKey<PointOfInterestType> COPPER_BATTERY_POI_KEY = poiKey(COPPER_BATTERY_POI);

    public static final VillagerProfession ENGINEER = registerProfession("engineer", COPPER_BATTERY_POI_KEY);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Copperworks.id(name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), ModSoundEvents.CHARGE));
    }

    private static void registerPoi(String name, Block block) {
        PointOfInterestHelper.register(Copperworks.id(name), 1, 1, block);
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Copperworks.id(name));
    }

    public static void registerVillagers() {
        Copperworks.LOGGER.info("Registering Mod Villagers for " + Copperworks.MOD_ID);
        registerPoi(COPPER_BATTERY_POI, ModBlocks.COPPER_BATTERY);
    }
}
