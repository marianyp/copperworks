package dev.mariany.copperworks.block.entity;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.custom.*;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import dev.mariany.copperworks.block.entity.custom.relay.RadioBoundRelayBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {
    public static final BlockEntityType<ClockBlockEntity> CLOCK = register("clock",
            BlockEntityType.Builder.create(ClockBlockEntity::new, ModBlocks.COPPER_CLOCK).build());

    public static final BlockEntityType<BatteryBlockEntity> BATTERY = register("battery",
            BlockEntityType.Builder.create(BatteryBlockEntity::new, ModBlocks.COPPER_BATTERY).build());

    public static final BlockEntityType<BoundRelayBlockEntity> BOUND_RELAY = register("bound_relay",
            BlockEntityType.Builder.create(BoundRelayBlockEntity::new, ModBlocks.COPPER_RELAY_BOUND).build());

    public static final BlockEntityType<RadioBoundRelayBlockEntity> RADIO_BOUND_RELAY = register("radio_bound_relay",
            BlockEntityType.Builder.create(RadioBoundRelayBlockEntity::new, ModBlocks.COPPER_RELAY_RADIO_BOUND)
                    .build());

    public static final BlockEntityType<SensorBlockEntity> SENSOR = register("sensor",
            BlockEntityType.Builder.create(SensorBlockEntity::new, ModBlocks.COPPER_SENSOR_CHARGED).build());

    public static final BlockEntityType<ComparatorMirrorBlockEntity> COMPARATOR_MIRROR = register("comparator_mirror",
            BlockEntityType.Builder.create(ComparatorMirrorBlockEntity::new, ModBlocks.COMPARATOR_MIRROR).build());

    public static final BlockEntityType<StasisChamberBlockEntity> STASIS_CHAMBER = register("stasis_chamber",
            BlockEntityType.Builder.create(StasisChamberBlockEntity::new, ModBlocks.COPPER_STASIS_CHAMBER_CHARGED)
                    .build());

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Copperworks.id(name), blockEntityType);
    }

    public static void registerModBlockEntities() {
        Copperworks.LOGGER.info("Registering Mod Block Entities for " + Copperworks.MOD_ID);
    }
}
