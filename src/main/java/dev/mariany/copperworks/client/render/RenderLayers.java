package dev.mariany.copperworks.client.render;

import dev.mariany.copperworks.Copperworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderLayers extends RenderLayer {
    private static final String GLINT_PREFIX = "charged_glint";
    public static final Identifier CHARGED_GLINT_TEXTURE = Copperworks.id("textures/misc/charged_glint.png");

    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                        boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        throw new UnsupportedOperationException("Unexpected instantiation");
    }

    public static final RenderLayer armorEntityGlint = buildArmorEntityGlintRenderLayer();
    public static final RenderLayer entityGlint = buildEntityGlintRenderLayer();
    public static final RenderLayer glint = buildGlintRenderLayer();
    public static final RenderLayer glintDirectEntity = buildGlintDirectEntityRenderLayer();
    public static final RenderLayer glintTranslucent = buildGlintTranslucentRenderLayer();

    public static final RenderLayer RELAY_HIGHLIGHT = RenderLayer.of("relay_highlight", VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.TRIANGLE_STRIP, 1536, false, true,
            RenderLayer.MultiPhaseParameters.builder().program(COLOR_PROGRAM).layering(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY).writeMaskState(COLOR_MASK).depthTest(ALWAYS_DEPTH_TEST)
                    .build(false));

    public static List<RenderLayer> GLINT_LAYERS = List.of(armorEntityGlint, entityGlint, glint, glintDirectEntity,
            glintTranslucent);

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map) {
        for (RenderLayer renderLayer : GLINT_LAYERS) {
            addGlintTypes(map, renderLayer);
        }
    }

    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage,
                                      RenderLayer layer) {
        builderStorage.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
    }

    private static RenderLayer buildGlintRenderLayer() {
        return of(GLINT_PREFIX, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintTranslucentRenderLayer() {
        return of(layerName("translucent"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.TRANSLUCENT_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).target(ITEM_ENTITY_TARGET).build(false));
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        return of(layerName("entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .target(ITEM_ENTITY_TARGET).texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintDirectEntityRenderLayer() {
        return of(layerName("entity_direct"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.DIRECT_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        return of(layerName("armor_entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GlintPrograms.ARMOR_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
    }

    private static String layerName(String name) {
        return GLINT_PREFIX + "_" + name;
    }
}
