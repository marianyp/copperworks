package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CryingObsidianBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

public class PartialDragonBreathItem extends Item {
    public static final int MAX_FILL = 3;

    public PartialDragonBreathItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock() instanceof CryingObsidianBlock) {
            if (player != null && !world.isClient) {
                if (!world.canPlayerModifyAt(player, blockPos)) {
                    return ActionResult.PASS;
                }

                attemptFill(itemStack, player, hand);
                playFillSound(world, player);
                world.emitGameEvent(player, GameEvent.FLUID_PICKUP, player.getPos());
            }

            world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }

    public static void playFillSound(World world, PlayerEntity player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH,
                SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void attemptFill(ItemStack bottleToFill, @NotNull PlayerEntity player, Hand hand) {
        if (player.getRandom().nextFloat() <= ModConstants.COLLECT_DRAGON_BREATH_CHANCE) {
            int dragonBreathFill = bottleToFill.getOrDefault(CopperworksComponents.DRAGON_BREATH_FILL, 0);
            int amount = bottleToFill.getCount();

            if (dragonBreathFill < MAX_FILL - 1) {
                if (amount > 1) {
                    ItemStack newBottle = bottleToFill.copyWithCount(1);
                    newBottle.set(CopperworksComponents.DRAGON_BREATH_FILL, dragonBreathFill + 1);

                    bottleToFill.decrementUnlessCreative(1, player);
                    player.giveItemStack(newBottle);
                } else {
                    bottleToFill.set(CopperworksComponents.DRAGON_BREATH_FILL, dragonBreathFill + 1);
                }
            } else {
                player.setStackInHand(hand, Items.DRAGON_BREATH.getDefaultStack().copyWithCount(amount));
            }
        }
    }
}
