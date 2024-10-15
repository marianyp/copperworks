package dev.mariany.copperworks.block.entity.renderer;

import dev.mariany.copperworks.block.entity.custom.relay.RadioBoundRelayBlockEntity;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.item.custom.RadioItem;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

public class RadioBoundRelayBlockEntityRenderer extends AbstractBoundRelayBlockEntityRenderer<RadioBoundRelayBlockEntity> {
    public RadioBoundRelayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(false);
    }

    @Override
    protected float incrementData(RadioBoundRelayBlockEntity boundRelayBlockEntity, float tickDelta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientWorld world = minecraft.world;
        ClientPlayerEntity player = minecraft.player;

        BlockPos pos = boundRelayBlockEntity.getPos();

        if (player != null) {
            if (minecraft.crosshairTarget != null) {
                if (minecraft.crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {
                    BlockHitResult blockHitResult = (BlockHitResult) minecraft.crosshairTarget;
                    BlockPos targetedPos = blockHitResult.getBlockPos();

                    if (targetedPos.equals(pos)) {
                        boolean holdingRadio = isRadio(player.getMainHandStack()) || isRadio(player.getOffHandStack());

                        if (holdingRadio) {
                            GlobalPos mainHandBoundPos = getRadioBoundPos(player, Hand.MAIN_HAND);
                            GlobalPos offHandBoundPos = getRadioBoundPos(player, Hand.OFF_HAND);

                            if (mainHandBoundPos != null || offHandBoundPos != null) {
                                boolean targetMatches = globalPosIsHere(world, mainHandBoundPos,
                                        targetedPos) || globalPosIsHere(world, offHandBoundPos, targetedPos);

                                if (targetMatches) {
                                    return boundRelayBlockEntity.getClientData().increment(tickDelta);
                                }
                            }
                        }
                    }
                }
            }
        }

        return boundRelayBlockEntity.getClientData().reset();
    }

    private boolean isRadio(ItemStack itemStack) {
        return itemStack.getItem() instanceof RadioItem;
    }

    @Nullable
    private GlobalPos getRadioBoundPos(ClientPlayerEntity player, Hand hand) {
        return getRadioBoundPos(player.getStackInHand(hand));
    }

    @Nullable
    private GlobalPos getRadioBoundPos(ItemStack itemStack) {
        return itemStack.get(CopperworksComponents.RELAY_POSITION);
    }

    private boolean globalPosIsHere(ClientWorld world, @Nullable GlobalPos globalPos, BlockPos thisPos) {
        if (globalPos == null) {
            return false;
        }

        BlockPos blockPos = globalPos.pos();

        if (!thisPos.equals(blockPos)) {
            return false;
        }

        return ModUtils.isSameDimension(world, globalPos);
    }
}