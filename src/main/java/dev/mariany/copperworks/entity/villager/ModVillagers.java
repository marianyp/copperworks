package dev.mariany.copperworks.entity.villager;

import com.google.common.collect.ImmutableSet;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.world.poi.ModPointOfInterestTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModVillagers {
    public static final VillagerProfession ENGINEER = registerProfession("engineer",
            ModPointOfInterestTypes.ENGINEER);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Copperworks.id(name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), ModSoundEvents.CHARGE));
    }

    public static void registerVillagers() {
        Copperworks.LOGGER.info("Registering Mod Villagers for " + Copperworks.MOD_ID);
    }
}
