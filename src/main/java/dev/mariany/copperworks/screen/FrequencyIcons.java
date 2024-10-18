package dev.mariany.copperworks.screen;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.util.Identifier;

import java.util.List;

public final class FrequencyIcons {
    public static final Identifier MOVEMENT = Copperworks.id("container/enhanced_sculk_sensor/frequency/movement");
    public static final Identifier LANDING = Copperworks.id("container/enhanced_sculk_sensor/frequency/landing");
    public static final Identifier ITEM_USE = Copperworks.id("container/enhanced_sculk_sensor/frequency/item_use");
    public static final Identifier UNIQUE_ACTION = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/unique_action");
    public static final Identifier DISMOUNTING_AND_EQUIPPING = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/dismounting_and_equipping");
    public static final Identifier MOUNTING_AND_INTERACTIONS = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/mounting_and_interactions");
    public static final Identifier DAMAGE_TAKEN = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/damage_taken");
    public static final Identifier EATING = Copperworks.id("container/enhanced_sculk_sensor/frequency/eating");
    public static final Identifier BLOCK_DEACTIVATING = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/block_deactivating");
    public static final Identifier BLOCK_ACTIVATING = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/block_activating");
    public static final Identifier BLOCK_CHANGING = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/block_changing");
    public static final Identifier BLOCK_DESTROYED = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/block_destroyed");
    public static final Identifier BLOCK_PLACED = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/block_placed");
    public static final Identifier TELEPORT_AND_SPAWN = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/teleport_and_spawn");
    public static final Identifier DEATH_AND_EXPLOSION = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency/death_and_explosion");

    public static List<Identifier> all() {
        return List.of(MOVEMENT, LANDING, ITEM_USE, UNIQUE_ACTION, DISMOUNTING_AND_EQUIPPING, MOUNTING_AND_INTERACTIONS,
                DAMAGE_TAKEN, EATING, BLOCK_DEACTIVATING, BLOCK_ACTIVATING, BLOCK_CHANGING, BLOCK_DESTROYED,
                BLOCK_PLACED, TELEPORT_AND_SPAWN, DEATH_AND_EXPLOSION);
    }
}
