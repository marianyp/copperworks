package dev.mariany.copperworks.event.server;

import net.minecraft.server.world.ServerWorld;

public interface ServerWorldTickHandler {
    void onServerWorldTick(ServerWorld world);
}
