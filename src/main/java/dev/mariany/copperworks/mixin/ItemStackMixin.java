package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
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
        Integer charge = stack.get(CopperworksComponents.CHARGE);
        Integer maxCharge = stack.get(CopperworksComponents.MAX_CHARGE);
        if (charge != null && maxCharge != null) {
            consumer.accept(ModUtils.generateChargeTooltip(charge, maxCharge));
        }
    }
}