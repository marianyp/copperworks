package dev.mariany.copperworks.block.custom.relay;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BindableRelay {
    default boolean isBound(World world, BlockPos pos) {
        return true;
    }
}
