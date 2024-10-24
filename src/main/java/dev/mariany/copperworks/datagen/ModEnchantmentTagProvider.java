package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.enchantment.ModEnchantments;
import dev.mariany.copperworks.tag.CopperworksTags;
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
        getOrCreateTagBuilder(CopperworksTags.Enchantments.FROM_UPGRADE).forceAddTag(EnchantmentTags.NON_TREASURE)
                .add(ModEnchantments.CHARGED_ATTRACTION).add(ModEnchantments.SHOCK).add(ModEnchantments.LIFE_CURRENT);
    }
}
