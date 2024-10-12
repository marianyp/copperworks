package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "canMoveVoluntarily", at = @At(value = "HEAD"), cancellable = true)
    public void injectCanMoveVoluntarily(CallbackInfoReturnable<Boolean> cir) {
        if (ModUtils.isEntityStuck((Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
