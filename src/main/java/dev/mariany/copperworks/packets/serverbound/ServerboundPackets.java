package dev.mariany.copperworks.packets.serverbound;

import dev.mariany.copperworks.packets.clientbound.ChunkMufflersPayload;
import dev.mariany.copperworks.tag.ModPointOfInterestTypeTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.util.List;

public class ServerboundPackets {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ChunkLoadedPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getServerWorld();
            ChunkPos chunkPos = payload.chunkPos();

            List<BlockPos> mufflerPositions = world.getPointOfInterestStorage()
                    .getInChunk(poiType -> poiType.isIn(ModPointOfInterestTypeTags.MUFFLER), chunkPos,
                            PointOfInterestStorage.OccupationStatus.ANY).map(PointOfInterest::getPos).toList();

            context.responseSender().sendPacket(new ChunkMufflersPayload(chunkPos, mufflerPositions));
        });

    }
}
