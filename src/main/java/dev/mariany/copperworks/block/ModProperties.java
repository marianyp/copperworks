package dev.mariany.copperworks.block;

import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.state.property.IntProperty;

public class ModProperties {
    public static final IntProperty SENSOR_RANGE = IntProperty.of("sensor_range", 0, ModConstants.MAX_SENSOR_RANGE + 1);
    public static final IntProperty CHARGE = IntProperty.of("charge", 0, 32);
    public static final IntProperty MUFFLER_RANGE = IntProperty.of("muffler_range", 0,
            ModConstants.MAX_MUFFLER_RANGE + 1);
}
