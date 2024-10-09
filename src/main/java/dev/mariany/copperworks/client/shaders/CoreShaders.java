package dev.mariany.copperworks.client.shaders;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class CoreShaders {
    @Nullable
    private static ShaderProgram renderTypeArmorEntityGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeGlintTranslucentProgram;
    @Nullable
    private static ShaderProgram renderTypeGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityGlintDirectProgram;

    public static void init(TriConsumer<String, VertexFormat, Consumer<ShaderProgram>> registrations) {
        registrations.accept("rendertype_armor_entity_charged_glint", VertexFormats.POSITION_TEXTURE,
                program -> renderTypeArmorEntityGlintProgram = program);

        registrations.accept("rendertype_charged_glint_translucent", VertexFormats.POSITION_TEXTURE,
                program -> renderTypeGlintTranslucentProgram = program);

        registrations.accept("rendertype_charged_glint", VertexFormats.POSITION_TEXTURE,
                program -> renderTypeGlintProgram = program);

        registrations.accept("rendertype_entity_charged_glint", VertexFormats.POSITION_TEXTURE,
                program -> renderTypeEntityGlintProgram = program);

        registrations.accept("rendertype_entity_charged_glint_direct", VertexFormats.POSITION_TEXTURE,
                program -> renderTypeEntityGlintDirectProgram = program);
    }

    public static ShaderProgram renderTypeArmorEntityGlint() {
        return renderTypeArmorEntityGlintProgram;
    }

    public static ShaderProgram renderTypeGlintTranslucent() {
        return renderTypeGlintTranslucentProgram;
    }

    public static ShaderProgram renderTypeGlint() {
        return renderTypeGlintProgram;
    }

    public static ShaderProgram renderTypeEntityGlint() {
        return renderTypeEntityGlintProgram;
    }

    public static ShaderProgram renderTypeEntityGlintDirect() {
        return renderTypeEntityGlintDirectProgram;
    }
}