package dev.mariany.copperworks.screen;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<EnhancedSculkSensorScreenHandler> ENHANCED_SCULK_SENSOR_HANDLER;

    public static void registerScreenHandlers() {
        Copperworks.LOGGER.info("Registering Screen Handlers for " + Copperworks.MOD_ID);

        ENHANCED_SCULK_SENSOR_HANDLER = new ScreenHandlerType(EnhancedSculkSensorScreenHandler::new,
                FeatureFlags.VANILLA_FEATURES);
        Registry.register(Registries.SCREEN_HANDLER, Copperworks.id("enhanced_sculk_sensor"),
                ENHANCED_SCULK_SENSOR_HANDLER);
    }
}
