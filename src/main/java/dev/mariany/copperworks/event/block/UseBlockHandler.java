package dev.mariany.copperworks.event.block;

import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.custom.PartialDragonBreathItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class UseBlockHandler {
    @FunctionalInterface
    public interface QuadFunction<P1, P2, P3, P4, R> {
        R apply(P1 one, P2 two, P3 three, P4 four);
    }

    private static final List<QuadFunction<PlayerEntity, World, Hand, BlockHitResult, ActionResult>> HANDLERS = List.of(
            UseBlockHandler::handleCollectDragonBreath);

    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        for (QuadFunction<PlayerEntity, World, Hand, BlockHitResult, ActionResult> handler : HANDLERS) {
            ActionResult result = handler.apply(player, world, hand, blockHitResult);

            if (result.isAccepted()) {
                return result;
            }
        }

        return ActionResult.PASS;
    }

    private static ActionResult handleCollectDragonBreath(PlayerEntity player, World world, Hand hand,
                                                          BlockHitResult blockHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof GlassBottleItem) {
            BlockPos pos = blockHitResult.getBlockPos();

            if (!world.isClient && world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
                PartialDragonBreathItem.playFillSound(world, player);
                stack.decrementUnlessCreative(1, player);
                player.giveItemStack(ModItems.PARTIAL_DRAGON_BREATH.getDefaultStack());
                world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
