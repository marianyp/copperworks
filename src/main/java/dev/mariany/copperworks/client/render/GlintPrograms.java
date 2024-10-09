package dev.mariany.copperworks.client.render;

import dev.mariany.copperworks.client.shaders.CoreShaders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderPhase;

@Environment(EnvType.CLIENT)
public abstract class GlintPrograms {
    public static final RenderPhase.ShaderProgram ARMOR_ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(
            CoreShaders::renderTypeArmorEntityGlint);
    public static final RenderPhase.ShaderProgram TRANSLUCENT_GLINT_PROGRAM = new RenderPhase.ShaderProgram(
            CoreShaders::renderTypeGlintTranslucent);
    public static final RenderPhase.ShaderProgram GLINT_PROGRAM = new RenderPhase.ShaderProgram(
            CoreShaders::renderTypeGlint);
    public static final RenderPhase.ShaderProgram ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(
            CoreShaders::renderTypeEntityGlint);
    public static final RenderPhase.ShaderProgram DIRECT_ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(
            CoreShaders::renderTypeEntityGlintDirect);
}
