package dev.mariany.copperworks.packets.serverbound;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.ChunkPos;

public record ChunkLoadedPayload(ChunkPos chunkPos) implements CustomPayload {
    public static final CustomPayload.Id<ChunkLoadedPayload> ID = new CustomPayload.Id<>(
            Copperworks.id("chunk_loaded"));
    public static final PacketCodec<RegistryByteBuf, ChunkLoadedPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), ChunkLoadedPayload::chunkPos,
            ChunkLoadedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
