package dev.mariany.copperworks.client.render;

import dev.mariany.copperworks.Copperworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderLayers extends RenderLayer {
    private static final String CHARGED_GLINT_PREFIX = "charged_glint";
    private static final Identifier CHARGED_GLINT_TEXTURE = Copperworks.id("textures/misc/charged_glint.png");

    private static final String CHARGING_GLINT_PREFIX = "charging_glint";
    private static final Identifier CHARGING_GLINT_TEXTURE = Copperworks.id("textures/misc/charging_glint.png");

    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                        boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        throw new UnsupportedOperationException("Unexpected instantiation");
    }

    public static final RenderLayer armorEntityChargedGlint = buildArmorEntityGlintRenderLayer(CHARGED_GLINT_PREFIX,
            CHARGED_GLINT_TEXTURE);
    public static final RenderLayer entityChargedGlint = buildEntityGlintRenderLayer(CHARGED_GLINT_PREFIX,
            CHARGED_GLINT_TEXTURE);
    public static final RenderLayer chargedGlint = buildGlintRenderLayer(CHARGED_GLINT_PREFIX, CHARGED_GLINT_TEXTURE);
    public static final RenderLayer chargedGlintDirectEntity = buildGlintDirectEntityRenderLayer(CHARGED_GLINT_PREFIX,
            CHARGED_GLINT_TEXTURE);
    public static final RenderLayer chargedGlintTranslucent = buildGlintTranslucentRenderLayer(CHARGED_GLINT_PREFIX,
            CHARGED_GLINT_TEXTURE);

    public static final RenderLayer armorEntityChargingGlint = buildArmorEntityGlintRenderLayer(CHARGING_GLINT_PREFIX,
            CHARGING_GLINT_TEXTURE);
    public static final RenderLayer entityChargingGlint = buildEntityGlintRenderLayer(CHARGING_GLINT_PREFIX,
            CHARGING_GLINT_TEXTURE);
    public static final RenderLayer chargingGlint = buildGlintRenderLayer(CHARGING_GLINT_PREFIX,
            CHARGING_GLINT_TEXTURE);
    public static final RenderLayer chargingGlintDirectEntity = buildGlintDirectEntityRenderLayer(CHARGING_GLINT_PREFIX,
            CHARGING_GLINT_TEXTURE);
    public static final RenderLayer chargingGlintTranslucent = buildGlintTranslucentRenderLayer(CHARGING_GLINT_PREFIX,
            CHARGING_GLINT_TEXTURE);

    public static final RenderLayer RELAY_HIGHLIGHT = RenderLayer.of("relay_highlight", VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.TRIANGLE_STRIP, 1536, false, true,
            RenderLayer.MultiPhaseParameters.builder().program(COLOR_PROGRAM).layering(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY).writeMaskState(COLOR_MASK).depthTest(ALWAYS_DEPTH_TEST)
                    .build(false));

    public static List<RenderLayer> GLINT_LAYERS = List.of(armorEntityChargedGlint, entityChargedGlint, chargedGlint,
            chargedGlintDirectEntity, chargedGlintTranslucent, armorEntityChargingGlint, entityChargingGlint,
            chargingGlint, chargingGlintDirectEntity, chargingGlintTranslucent);

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map) {
        for (RenderLayer renderLayer : GLINT_LAYERS) {
            addGlintTypes(map, renderLayer);
        }
    }

    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage,
                                      RenderLayer layer) {
        builderStorage.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
    }

    private static RenderLayer buildGlintRenderLayer(String prefix, Identifier texture) {
        return of(prefix, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintTranslucentRenderLayer(String prefix, Identifier texture) {
        return of(layerName(prefix, "translucent"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.TRANSLUCENT_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).target(ITEM_ENTITY_TARGET).build(false));
    }

    private static RenderLayer buildEntityGlintRenderLayer(String prefix, Identifier texture) {
        return of(layerName(prefix, "entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .target(ITEM_ENTITY_TARGET).texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintDirectEntityRenderLayer(String prefix, Identifier texture) {
        return of(layerName(prefix, "entity_direct"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.DIRECT_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer(String prefix, Identifier texture) {
        return of(layerName(prefix, "armor_entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.ARMOR_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
    }

    private static String layerName(String prefix, String name) {
        return prefix + "_" + name;
    }
}
