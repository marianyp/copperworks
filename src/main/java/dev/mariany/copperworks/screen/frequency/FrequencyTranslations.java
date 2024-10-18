package dev.mariany.copperworks.screen.frequency;

import java.util.List;

public final class FrequencyTranslations {
    public static final String MOVEMENT = "container.copperworks.enhanced_sculk_sensor.frequency.movement";
    public static final String LANDING = "container.copperworks.enhanced_sculk_sensor.frequency.landing";
    public static final String ITEM_USE = "container.copperworks.enhanced_sculk_sensor.frequency.item_use";
    public static final String UNIQUE_ACTION = "container.copperworks.enhanced_sculk_sensor.frequency.unique_action";
    public static final String DISMOUNTING_AND_EQUIPPING = "container.copperworks.enhanced_sculk_sensor.frequency.dismounting_and_equipping";
    public static final String MOUNTING_AND_INTERACTIONS = "container.copperworks.enhanced_sculk_sensor.frequency.mounting_and_interactions";
    public static final String DAMAGE_TAKEN = "container.copperworks.enhanced_sculk_sensor.frequency.damage_taken";
    public static final String EATING = "container.copperworks.enhanced_sculk_sensor.frequency.eating";
    public static final String BLOCK_DEACTIVATING = "container.copperworks.enhanced_sculk_sensor.frequency.block_deactivating";
    public static final String BLOCK_ACTIVATING = "container.copperworks.enhanced_sculk_sensor.frequency.block_activating";
    public static final String BLOCK_CHANGING = "container.copperworks.enhanced_sculk_sensor.frequency.block_changing";
    public static final String BLOCK_DESTROYED = "container.copperworks.enhanced_sculk_sensor.frequency.block_destroyed";
    public static final String BLOCK_PLACED = "container.copperworks.enhanced_sculk_sensor.frequency.block_placed";
    public static final String TELEPORT_AND_SPAWN = "container.copperworks.enhanced_sculk_sensor.frequency.teleport_and_spawn";
    public static final String DEATH_AND_EXPLOSION = "container.copperworks.enhanced_sculk_sensor.frequency.death_and_explosion";

    public static List<String> all() {
        return List.of(MOVEMENT, LANDING, ITEM_USE, UNIQUE_ACTION, DISMOUNTING_AND_EQUIPPING, MOUNTING_AND_INTERACTIONS,
                DAMAGE_TAKEN, EATING, BLOCK_DEACTIVATING, BLOCK_ACTIVATING, BLOCK_CHANGING, BLOCK_DESTROYED,
                BLOCK_PLACED, TELEPORT_AND_SPAWN, DEATH_AND_EXPLOSION);
    }
}
