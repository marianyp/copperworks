package dev.mariany.copperworks.block;

import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.state.property.IntProperty;

public class ModProperties {
    public static final IntProperty RANGE = IntProperty.of("range", 0, ModConstants.MAX_SENSOR_RANGE + 1);
}
