package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(at = @At("HEAD"), method = "hasGlint", cancellable = true)
    private void injectHasGlint(CallbackInfoReturnable<Boolean> cir) {
        if (ModUtils.isCharging(((ItemStack) (Object) this))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendAttributeModifiersTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void injectChargeTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type,
                                     CallbackInfoReturnable<List<Text>> cir, List<Text> list, MutableText mutableText,
                                     Consumer<Text> consumer) {
        ItemStack stack = (ItemStack) (Object) this;
        Integer charge = stack.get(ModComponents.CHARGE);
        Integer maxCharge = stack.get(ModComponents.MAX_CHARGE);
        if (charge != null && maxCharge != null) {
            Text chargeTooltip = Text.translatable("item.copperworks.charge.tooltip", generateBars(charge, maxCharge))
                    .withColor(Colors.RED);
            consumer.accept(chargeTooltip);
        }
    }

    @Unique
    private static String generateBars(int value, int max) {
        final int TOTAL_BLOCKS = 10;

        // Calculate the proportion of the value to the max
        double proportion = (double) value / max;

        // Scale the proportion to the total number of blocks
        int fullBlocks = (int) Math.round(proportion * TOTAL_BLOCKS);


        // Ensure fullBlocks is within the valid range
        fullBlocks = Math.max(0, Math.min(TOTAL_BLOCKS, fullBlocks));
        if (value > 0 && fullBlocks == 0) {
            fullBlocks = 1;
        } else if (value < max && fullBlocks == TOTAL_BLOCKS) {
            fullBlocks = TOTAL_BLOCKS - 1;
        }
        int emptyBlocks = TOTAL_BLOCKS - fullBlocks;

        String fullChar = "▮";
        String emptyChar = "▯";

        return fullChar.repeat(fullBlocks) + emptyChar.repeat(emptyBlocks);
    }
}