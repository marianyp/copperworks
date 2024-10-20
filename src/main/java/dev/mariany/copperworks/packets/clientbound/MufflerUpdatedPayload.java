package dev.mariany.copperworks.packets.clientbound;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public record MufflerUpdatedPayload(ChunkPos chunkPos, int range, BlockPos blockPos,
                                    boolean removed) implements CustomPayload {
    public static final CustomPayload.Id<MufflerUpdatedPayload> ID = new CustomPayload.Id<>(
            Copperworks.id("muffler_updated"));
    public static final PacketCodec<RegistryByteBuf, MufflerUpdatedPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), MufflerUpdatedPayload::chunkPos,
            PacketCodecs.VAR_INT, MufflerUpdatedPayload::range, BlockPos.PACKET_CODEC, MufflerUpdatedPayload::blockPos,
            PacketCodecs.BOOL, MufflerUpdatedPayload::removed, MufflerUpdatedPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
