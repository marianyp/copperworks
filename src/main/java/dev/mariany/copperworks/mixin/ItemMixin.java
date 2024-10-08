package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.custom.PartialDragonBreathItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getStack().getItem() instanceof GlassBottleItem) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            ItemStack stack = context.getStack();

            if (!world.isClient && world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
                if (player != null) {
                    PartialDragonBreathItem.playFillSound(world, player);
                    stack.decrement(1);
                    player.giveItemStack(ModItems.PARTIAL_DRAGON_BREATH.getDefaultStack());
                }

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}