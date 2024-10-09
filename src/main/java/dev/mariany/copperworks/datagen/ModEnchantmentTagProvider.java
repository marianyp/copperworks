package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {
    public ModEnchantmentTagProvider(FabricDataOutput output,
                                     CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Enchantments.FROM_UPGRADE).addOptionalTag(EnchantmentTags.NON_TREASURE);
    }
}
