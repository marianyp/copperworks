package dev.mariany.copperworks.block.entity.renderer;

import dev.mariany.copperworks.block.custom.battery.BatteryBlock;
import dev.mariany.copperworks.block.custom.battery.BatteryClientData;
import dev.mariany.copperworks.block.entity.custom.BatteryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BatteryBlockEntityRenderer implements BlockEntityRenderer<BatteryBlockEntity> {
    private static final float ITEM_DISTANCE = 1.375F;

    private final ItemRenderer itemRenderer;
    private final Random random = Random.create();

    public BatteryBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BatteryBlockEntity batteryBlockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = batteryBlockEntity.getWorld();
        ItemStack chargingItem = batteryBlockEntity.getStack().copyWithCount(1);

        if (world == null || chargingItem.isEmpty()) {
            return;
        }

        BlockState state = world.getBlockState(batteryBlockEntity.getPos());

        if (!(state.getBlock() instanceof BatteryBlock)) {
            return;
        }

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        BlockFace face = state.get(Properties.BLOCK_FACE);

        int lightAbove = WorldRenderer.getLightmapCoordinates(world, batteryBlockEntity.getPos().up());
        BatteryClientData clientData = batteryBlockEntity.getClientData();

        float previousRotation = clientData.getPreviousDisplayRotation();
        float rotation = clientData.getDisplayRotation();

        renderDisplayItem(tickDelta, batteryBlockEntity.getWorld(), matrices, vertexConsumers, lightAbove, chargingItem,
                this.itemRenderer, previousRotation, rotation, facing, face, this.random);
    }

    public static void renderDisplayItem(float tickDelta, World world, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, ItemStack stack,
                                         ItemRenderer itemRenderer, float prevRotation, float rotation,
                                         Direction facing, BlockFace face, Random random) {
        matrices.push();

        // Center the item at the block's position
        float half = 0.5F;
        matrices.translate(half, half, half);

        float distance = ITEM_DISTANCE - half;
        float offset = half / 4;

        switch (face) {
            case FLOOR:
                matrices.translate(0.0F, distance, 0.0F);
                break;
            case CEILING:
                distance = -(distance + offset);
                matrices.translate(0.0F, distance, 0.0F);
                break;
            case WALL:
                switch (facing) {
                    case NORTH:
                        matrices.translate(0.0F, -offset, -distance);
                        break;
                    case SOUTH:
                        matrices.translate(0.0F, -offset, distance);
                        break;
                    case WEST:
                        matrices.translate(-distance, -offset, 0.0F);
                        break;
                    case EAST:
                        matrices.translate(distance, -offset, 0.0F);
                        break;
                }
                break;
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                MathHelper.lerpAngleDegrees(tickDelta, prevRotation, rotation)));

        ItemEntityRenderer.renderStack(itemRenderer, matrices, vertexConsumers, light, stack, random, world);

        matrices.pop();
    }
}
