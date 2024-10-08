package dev.mariany.copperworks.client.render;

import dev.mariany.copperworks.Copperworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

import java.util.List;

public class GlintRenderLayers extends RenderLayer {
    private static final String PREFIX = "charged_glint";
    public static final Identifier CHARGED_GLINT_TEXTURE = Copperworks.id("textures/misc/charged_glint.png");

    public GlintRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode,
                             int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction,
                             Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        throw new UnsupportedOperationException("Unexpected instantiation");
    }

    public static RenderLayer armorEntityGlint = buildArmorEntityGlintRenderLayer();
    public static RenderLayer entityGlint = buildEntityGlintRenderLayer();
    public static RenderLayer glint = buildGlintRenderLayer();
    public static RenderLayer glintDirectEntity = buildGlintDirectEntityRenderLayer();
    public static RenderLayer glintTranslucent = buildGlintTranslucentRenderLayer();

    public static List<RenderLayer> ALL = List.of(armorEntityGlint, entityGlint, glint, glintDirectEntity,
            glintTranslucent);

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map) {
        for (RenderLayer renderLayer : ALL) {
            addGlintTypes(map, renderLayer);
        }
    }

    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage,
                                      RenderLayer layer) {
        builderStorage.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
    }

    private static RenderLayer buildGlintRenderLayer() {
        return of(PREFIX, VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintTranslucentRenderLayer() {
        return of(layerName("translucent"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(TRANSLUCENT_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(GLINT_TEXTURING).target(ITEM_ENTITY_TARGET).build(false));
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        return of(layerName("entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .target(ITEM_ENTITY_TARGET).texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildGlintDirectEntityRenderLayer() {
        return of(layerName("entity_direct"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(DIRECT_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        return of(layerName("armor_entity"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536,
                RenderLayer.MultiPhaseParameters.builder().program(ARMOR_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(CHARGED_GLINT_TEXTURE, true, false)).writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
    }

    private static String layerName(String name) {
        return PREFIX + "_" + name;
    }
}
