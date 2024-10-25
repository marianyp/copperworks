package dev.mariany.copperworks.advancement.criterion;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCriteria {
    public static final TickCriterion BOUND_RELAY = register("bound_relay", new TickCriterion());
    public static final TickCriterion COLLECT_PARTIAL_DRAGON_BREATH = register("collect_partial_dragon_breath",
            new TickCriterion());
    public static final TickCriterion REACH_OUT_OF_BOUNDS = register("reach_out_of_bounds", new TickCriterion());
    public static final TickCriterion SCRAPED_PATINA = register("scraped_patina", new TickCriterion());
    public static final TickCriterion TRIGGERED_RADIO_BOUND_RELAY = register("triggered_radio_bound_relay",
            new TickCriterion());
    public static final TickCriterion UPGRADED_ITEM = register("upgraded_item", new TickCriterion());
    public static final TickCriterion UPGRADE_WOODEN_RAIL = register("upgrade_wooden_rail", new TickCriterion());
    public static final TickCriterion USE_ROCKET_BOOTS = register("use_rocket_boots", new TickCriterion());
    public static final TickCriterion USE_STASIS_CHAMBER = register("use_stasis_chamber", new TickCriterion());
    public static final TickCriterion USE_WRENCH = register("use_wrench", new TickCriterion());

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Copperworks.id(name), criterion);
    }

    public static void registerModCriteria() {
        Copperworks.LOGGER.info("Registering Mod Criteria for " + Copperworks.MOD_ID);
    }
}
