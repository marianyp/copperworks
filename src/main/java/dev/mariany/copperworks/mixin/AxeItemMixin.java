package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "tryStrip", at = @At(value = "HEAD"))
    private void wrapTryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state,
                              CallbackInfoReturnable<Optional<BlockState>> cir) {
        Optional<BlockState> optionalDecreasedOxidationState = Oxidizable.getDecreasedOxidationState(state);
        Random random = world.getRandom();
        if (random.nextFloat() <= ModConstants.HARVEST_PATINA_CHANCE) {
            return;
        }

        if (player != null && !world.isClient && optionalDecreasedOxidationState.isPresent()) {
            BlockHitResult raycast = raycast(world, player);
            if (world.getBlockState(raycast.getBlockPos()).getBlock() instanceof Oxidizable) {
                ItemStack patinaStack = new ItemStack(ModBlocks.PATINA, MathHelper.nextBetween(random, 1, 3));
                Block.dropStack(world, pos.offset(raycast.getSide()), patinaStack);
            }
        }
    }

    @Unique
    private static BlockHitResult raycast(World world, PlayerEntity player) {
        Vec3d eyePos = player.getEyePos();
        Vec3d maxPos = eyePos.add(player.getRotationVector(player.getPitch(), player.getYaw())
                .multiply(player.getBlockInteractionRange()));
        return world.raycast(
                new RaycastContext(eyePos, maxPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE,
                        player));
    }
}
