package dev.mariany.copperworks.mixin;

import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.MufflerBlock;
import dev.mariany.copperworks.packets.clientbound.MufflerUpdatedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    public void injectOnBlockChanged(BlockPos pos, BlockState oldBlockState, BlockState newBlockState,
                                     CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        ChunkPos chunkPos = new ChunkPos(pos);
        Block oldBlock = oldBlockState.getBlock();
        Block newBlock = newBlockState.getBlock();
        int oldRange = oldBlockState.contains(ModProperties.MUFFLER_RANGE) ? oldBlockState.get(
                ModProperties.MUFFLER_RANGE) : -1;
        int newRange = newBlockState.contains(ModProperties.MUFFLER_RANGE) ? newBlockState.get(
                ModProperties.MUFFLER_RANGE) : -1;

        if ((oldRange != newRange) || !oldBlock.equals(newBlock)) {
            if (oldBlock instanceof MufflerBlock) {
                sendToPlayers(world, new MufflerUpdatedPayload(chunkPos, -1, pos, true));
            }

            if (newBlock instanceof MufflerBlock) {
                if (newBlockState.getBlock() instanceof MufflerBlock) {
                    int range = newBlockState.get(ModProperties.MUFFLER_RANGE);
                    sendToPlayers(world, new MufflerUpdatedPayload(chunkPos, range, pos, false));
                }
            }
        }
    }

    @Unique
    public void sendToPlayers(ServerWorld world, CustomPayload payload) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
