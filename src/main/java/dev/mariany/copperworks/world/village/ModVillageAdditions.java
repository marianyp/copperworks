package dev.mariany.copperworks.world.village;

import dev.mariany.copperworks.Copperworks;
import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolRegistry;
import net.minecraft.util.Identifier;

public class ModVillageAdditions {
    public static void registerVillageStructures() {
        FabricStructurePoolRegistry.registerSimple(Identifier.ofVanilla("village/plains/houses"),
                Copperworks.id("village/plains/houses/plains_engineer"), 2);
    }
}
