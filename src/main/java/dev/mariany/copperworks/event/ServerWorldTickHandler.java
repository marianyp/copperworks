package dev.mariany.copperworks.event;

import net.minecraft.server.world.ServerWorld;

public interface ServerWorldTickHandler {
    void onServerWorldTick(ServerWorld world);
}
