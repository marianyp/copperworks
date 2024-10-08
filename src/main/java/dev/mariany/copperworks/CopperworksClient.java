package dev.mariany.copperworks;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.renderer.BatteryBlockEntityRenderer;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class CopperworksClient implements ClientModInitializer {
    private static final Identifier CHARGED = Copperworks.id("charged");
    private static final Identifier FULLY_CHARGED = Copperworks.id("fully_charged");
    private static final Identifier DRAGON_BREATH_FILL = Copperworks.id("dragon_breath_fill");

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemHasSomeCharge(itemStack) ? 1 : 0);

        ModelPredicateProviderRegistry.register(FULLY_CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemNeedsCharge(itemStack) ? 0 : 1);

        ModelPredicateProviderRegistry.register(ModItems.PARTIAL_DRAGON_BREATH, DRAGON_BREATH_FILL,
                (itemStack, clientWorld, livingEntity, seed) -> itemStack.getOrDefault(ModComponents.DRAGON_BREATH_FILL,
                        0));
    }

    @Override
    public void onInitializeClient() {
        registerModelPredicateProviders();

        BlockEntityRendererFactories.register(ModBlockEntities.BATTERY, BatteryBlockEntityRenderer::new);
    }
}
