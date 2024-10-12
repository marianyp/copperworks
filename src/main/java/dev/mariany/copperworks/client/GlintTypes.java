package dev.mariany.copperworks.client;

import dev.mariany.copperworks.client.render.RenderLayers;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class GlintTypes {
    private static final ThreadLocal<Boolean> charged = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> charging = new ThreadLocal<>();

    static {
        reset();
    }

    private static void reset() {
        charged.set(false);
        charging.set(false);
    }

    public static void updateGlint(ItemStack itemStack) {
        boolean isCharging = ModUtils.isCharging(itemStack);
        boolean isUpgraded = ModUtils.isUpgraded(itemStack);

        reset();

        if (isCharging) {
            charging.set(true);
        } else if (isUpgraded) {
            charged.set(true);
        }
    }

    public static RenderLayer getGlint() {
        return renderLayer(RenderLayers.chargingGlint, RenderLayers.chargedGlint, RenderLayer::getGlint);
    }

    public static RenderLayer getTranslucentGlint() {
        return renderLayer(RenderLayers.chargingGlintTranslucent, RenderLayers.chargedGlintTranslucent,
                RenderLayer::getGlintTranslucent);
    }

    public static RenderLayer getEntityGlint() {
        return renderLayer(RenderLayers.entityChargingGlint, RenderLayers.entityChargedGlint,
                RenderLayer::getEntityGlint);
    }

    public static RenderLayer getDirectEntityGlint() {
        return renderLayer(RenderLayers.chargingGlintDirectEntity, RenderLayers.chargedGlintDirectEntity,
                RenderLayer::getDirectEntityGlint);
    }

    public static RenderLayer getArmorEntityGlint() {
        return renderLayer(RenderLayers.armorEntityChargingGlint, RenderLayers.armorEntityChargedGlint,
                RenderLayer::getArmorEntityGlint);
    }

    private static RenderLayer renderLayer(RenderLayer chargingLayer, RenderLayer chargedLayer,
                                           Supplier<RenderLayer> vanilla) {
        if (charging.get()) {
            return chargingLayer;
        }

        if (charged.get()) {
            return chargedLayer;
        }

        return vanilla.get();
    }
}
