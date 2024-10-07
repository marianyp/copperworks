package dev.mariany.copperworks;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.renderer.BatteryBlockEntityRenderer;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class CopperworksClient implements ClientModInitializer {
    private static final Identifier CHARGED = Copperworks.id("charged");
    private static final Identifier FULLY_CHARGED = Copperworks.id("fully_charged");

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(ModItems.COPPER_DRILL, CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemHasSomeCharge(itemStack) ? 1 : 0);

        ModelPredicateProviderRegistry.register(ModItems.COPPER_DRILL, FULLY_CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemNeedsCharge(itemStack) ? 0 : 1);
    }

    @Override
    public void onInitializeClient() {
        registerModelPredicateProviders();

        BlockEntityRendererFactories.register(ModBlockEntities.BATTERY, BatteryBlockEntityRenderer::new);
    }
}
