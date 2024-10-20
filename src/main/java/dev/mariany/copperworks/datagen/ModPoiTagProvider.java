package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.tag.ModPointOfInterestTypeTags;
import dev.mariany.copperworks.world.poi.ModPointOfInterestTypes;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModPoiTagProvider extends TagProvider<PointOfInterestType> {
    private static final Map<TagKey<PointOfInterestType>, List<RegistryKey<PointOfInterestType>>> poiTypes = new HashMap<>();

    static {
        poiTypes.put(ModPointOfInterestTypeTags.MUFFLER, List.of(ModPointOfInterestTypes.MUFFLER));
        poiTypes.put(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE, List.of(ModPointOfInterestTypes.ENGINEER));
    }

    public ModPoiTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, RegistryKeys.POINT_OF_INTEREST_TYPE, registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        for (Map.Entry<TagKey<PointOfInterestType>, List<RegistryKey<PointOfInterestType>>> entry : poiTypes.entrySet()) {
            TagProvider.ProvidedTagBuilder<PointOfInterestType> tagBuilder = this.getOrCreateTagBuilder(entry.getKey());
            for (RegistryKey<PointOfInterestType> poi : entry.getValue()) {
                tagBuilder.add(poi);
            }
        }
    }
}
