package dev.mariany.copperworks.packets.clientbound;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public record ChunkMufflersPayload(ChunkPos chunkPos, List<BlockPos> mufflers) implements CustomPayload {
    public static final CustomPayload.Id<ChunkMufflersPayload> ID = new CustomPayload.Id<>(
            Copperworks.id("chunk_mufflers"));
    public static final PacketCodec<RegistryByteBuf, ChunkMufflersPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), ChunkMufflersPayload::chunkPos,
            BlockPos.PACKET_CODEC.collect(PacketCodecs.toList()), ChunkMufflersPayload::mufflers,
            ChunkMufflersPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
