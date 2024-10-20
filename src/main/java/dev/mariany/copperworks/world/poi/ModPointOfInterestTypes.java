package dev.mariany.copperworks.world.poi;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;

public class ModPointOfInterestTypes {
    public static final RegistryKey<PointOfInterestType> MUFFLER = register("muffler", List.of(ModBlocks.MUFFLER));
    public static final RegistryKey<PointOfInterestType> ENGINEER = register("engineer",
            List.of(ModBlocks.COPPER_BATTERY));

    private static RegistryKey<PointOfInterestType> register(String name, List<Block> blocks) {
        Identifier id = Copperworks.id(name);
        for (Block block : blocks) {
            PointOfInterestHelper.register(id, 1, 1, block);
        }
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, id);
    }

    public static void registerModPointOfInterestTypes() {
        Copperworks.LOGGER.info("Registering Mod Point of Interest Types for " + Copperworks.MOD_ID);
    }
}
