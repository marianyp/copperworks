package dev.mariany.copperworks.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mariany.copperworks.block.entity.custom.relay.AbstractBoundRelayBlockEntity;
import dev.mariany.copperworks.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public abstract class AbstractBoundRelayBlockEntityRenderer<T extends AbstractBoundRelayBlockEntity> implements BlockEntityRenderer<T> {
    private final boolean enhanced;

    public AbstractBoundRelayBlockEntityRenderer(boolean enhanced) {
        this.enhanced = enhanced;
    }

    @Override
    public void render(T radioBoundRelayBlockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Vec3d color = radioBoundRelayBlockEntity.getClientData().getColor();
        float opacity = incrementData(radioBoundRelayBlockEntity, tickDelta);

        if (opacity > 0) {
            enhance(false);
            renderHighlight(matrices, vertexConsumers, color, opacity);
            enhance(true);
        }
    }

    private void enhance(boolean enabled) {
        if (enhanced) {
            if (enabled) {
                RenderSystem.enableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
            } else {
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
            }
            RenderSystem.depthMask(enabled);
        }
    }

    private void renderHighlight(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d color,
                                 float opacity) {
        VoxelShapes.fullCube().forEachBox(
                (minX, minY, minZ, maxX, maxY, maxZ) -> WorldRenderer.renderFilledBox(matrices,
                        vertexConsumers.getBuffer(RenderLayers.RELAY_HIGHLIGHT), minX, minY, minZ, maxX, maxY, maxZ,
                        (float) color.getX(), (float) color.getY(), (float) color.getZ(), opacity));
    }

    abstract protected float incrementData(T boundRelayBlockEntity, float tickDelta);
}