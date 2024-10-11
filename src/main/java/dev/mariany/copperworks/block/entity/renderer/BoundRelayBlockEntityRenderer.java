package dev.mariany.copperworks.block.entity.renderer;

import dev.mariany.copperworks.block.custom.relay.bound.BoundRelayBlock;
import dev.mariany.copperworks.block.custom.relay.bound.BoundRelayClientData;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.Vec3d;

public class BoundRelayBlockEntityRenderer extends AbstractBoundRelayBlockEntityRenderer<BoundRelayBlockEntity> {
    public BoundRelayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(true);
    }

    @Override
    protected float incrementData(BoundRelayBlockEntity boundRelayBlockEntity, float tickDelta) {
        BoundRelayClientData clientData = boundRelayBlockEntity.getClientData();

        if (clientData.isVisible()) {
            return clientData.increment(tickDelta);
        }

        return clientData.reset();
    }

    @Override
    public int getRenderDistance() {
        return BoundRelayBlock.getRenderDistance();
    }

    public boolean isInRenderDistance(BoundRelayBlockEntity blockEntity, Vec3d pos) {
        return BoundRelayBlock.isInRenderDistance(Vec3d.of(blockEntity.getPos()), pos);
    }

    @Override
    public boolean rendersOutsideBoundingBox(BoundRelayBlockEntity blockEntity) {
        return true;
    }
}
