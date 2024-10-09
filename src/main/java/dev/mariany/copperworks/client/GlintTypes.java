package dev.mariany.copperworks.client;

import dev.mariany.copperworks.client.render.GlintRenderLayers;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class GlintTypes {
    private static final ThreadLocal<Boolean> charged = new ThreadLocal<>();

    static {
        charged.set(false);
    }

    public static void setCharged(ItemStack itemStack) {
        charged.set(ModUtils.isUpgraded(itemStack));
    }

    public static RenderLayer getGlint() {
        return renderLayer(GlintRenderLayers.glint, RenderLayer::getGlint);
    }

    public static RenderLayer getGlintTranslucent() {
        return renderLayer(GlintRenderLayers.glintTranslucent, RenderLayer::getGlintTranslucent);
    }

    public static RenderLayer getEntityGlint() {
        return renderLayer(GlintRenderLayers.entityGlint, RenderLayer::getEntityGlint);
    }

    public static RenderLayer getDirectEntityGlint() {
        return renderLayer(GlintRenderLayers.glintDirectEntity, RenderLayer::getDirectEntityGlint);
    }

    public static RenderLayer getArmorEntityGlint() {
        return renderLayer(GlintRenderLayers.armorEntityGlint, RenderLayer::getArmorEntityGlint);
    }

    private static RenderLayer renderLayer(RenderLayer layer, Supplier<RenderLayer> vanilla) {
        return charged.get() ? layer : vanilla.get();
    }
}
