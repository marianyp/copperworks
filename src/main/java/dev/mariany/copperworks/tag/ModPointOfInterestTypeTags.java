package dev.mariany.copperworks.tag;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPointOfInterestTypeTags {
    public static final TagKey<PointOfInterestType> MUFFLER = of("muffler");

    private static TagKey<PointOfInterestType> of(String name) {
        return TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Copperworks.id(name));
    }
}
