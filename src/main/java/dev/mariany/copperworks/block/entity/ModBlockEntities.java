package dev.mariany.copperworks.block.entity;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.custom.ClockBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {
    public static final BlockEntityType<ClockBlockEntity> CLOCK = register("clock",
            BlockEntityType.Builder.create(ClockBlockEntity::new, ModBlocks.COPPER_CLOCK).build());

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Copperworks.id(name), blockEntityType);
    }

    public static void registerModBlockEntities() {
        Copperworks.LOGGER.info("Registering Mod Block Entities for " + Copperworks.MOD_ID);
    }
}
