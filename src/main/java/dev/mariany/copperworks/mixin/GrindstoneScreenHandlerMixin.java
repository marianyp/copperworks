package dev.mariany.copperworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin {
    @WrapOperation(method = "getOutputStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasEnchantments(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean wrapGetOutputStack(ItemStack stack, Operation<Boolean> original) {
        if (ModUtils.isUpgraded(stack)) {
            return false;
        }
        return original.call(stack);
    }
}
