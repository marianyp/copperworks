package dev.mariany.copperworks.client;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.renderer.BatteryBlockEntityRenderer;
import dev.mariany.copperworks.block.entity.renderer.BoundRelayBlockEntityRenderer;
import dev.mariany.copperworks.block.entity.renderer.RadioBoundRelayBlockEntityRenderer;
import dev.mariany.copperworks.client.shaders.CoreShaders;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.screen.EnhancedSculkSensorScreen;
import dev.mariany.copperworks.screen.ModScreenHandlers;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.UncheckedIOException;

public class CopperworksClient implements ClientModInitializer {
    private static final Identifier CHARGED = Copperworks.id("charged");
    private static final Identifier FULLY_CHARGED = Copperworks.id("fully_charged");
    private static final Identifier DRAGON_BREATH_FILL = Copperworks.id("dragon_breath_fill");
    private static final Identifier BOUND = Copperworks.id("bound");

    @Override
    public void onInitializeClient() {
        registerCoreShaders();
        registerScreenHandlers();
        registerModelPredicateProviders();
        registryBlockEntityRenderers();
        registerBlockRenderLayers();
    }

    private void registerCoreShaders() {
        CoreShaderRegistrationCallback.EVENT.register(ctx -> CoreShaders.init((id, vertexFormat, onLoaded) -> {
            try {
                // TODO: Change to use mod namespace. I can't figure out why it's not currently allowing me to.
                ctx.register(Identifier.ofVanilla(id), vertexFormat, onLoaded);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }));
    }

    public static void registerScreenHandlers() {
        HandledScreens.register(ModScreenHandlers.ENHANCED_SCULK_SENSOR_HANDLER, EnhancedSculkSensorScreen::new);
    }

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemHasSomeCharge(itemStack) ? 1 : 0);

        ModelPredicateProviderRegistry.register(FULLY_CHARGED,
                (itemStack, clientWorld, livingEntity, seed) -> ModUtils.itemNeedsCharge(itemStack) ? 0 : 1);

        ModelPredicateProviderRegistry.register(ModItems.PARTIAL_DRAGON_BREATH, DRAGON_BREATH_FILL,
                (itemStack, clientWorld, livingEntity, seed) -> itemStack.getOrDefault(
                        CopperworksComponents.DRAGON_BREATH_FILL, 0));

        ModelPredicateProviderRegistry.register(ModItems.RADIO, BOUND,
                (itemStack, clientWorld, livingEntity, seed) -> itemStack.get(
                        CopperworksComponents.RELAY_POSITION) == null ? 0 : 1);
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PATINA, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WOODEN_RAIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPER_RAIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENHANCED_SCULK_SENSOR, RenderLayer.getCutout());
    }

    private void registryBlockEntityRenderers() {
        BlockEntityRendererFactories.register(ModBlockEntities.BATTERY, BatteryBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.BOUND_RELAY, BoundRelayBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.RADIO_BOUND_RELAY,
                RadioBoundRelayBlockEntityRenderer::new);
    }
}
