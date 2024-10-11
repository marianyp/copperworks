package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.block.custom.PatinaBlock;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {
    @Inject(at = @At("HEAD"), cancellable = true, method = "canSpawn")
    private static <T extends Entity> void canSpawn(EntityType<T> type, ServerWorldAccess world,
                                                    SpawnReason spawnReason, BlockPos pos, Random random,
                                                    CallbackInfoReturnable<Boolean> cir) {
        List<BlockPos> positionsToCheck = new ArrayList<>();
        positionsToCheck.add(pos);
        positionsToCheck.add(pos.east());
        positionsToCheck.add(pos.south());
        positionsToCheck.add(pos.east().south());

        for (BlockPos basePos : positionsToCheck) {
            for (int i = 0; i < 3; i++) {
                BlockPos belowPos = basePos.down(i);
                if (world.getBlockState(belowPos).isSolidBlock(world, belowPos)) {
                    if (world.getBlockState(belowPos.down()).getBlock() instanceof PatinaBlock) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
