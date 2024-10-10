package dev.mariany.copperworks.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ServerTickHandler {
    private static final ServerWorldTickHandler villagerTickHandler = new VillagerTickHandler();
    private static final ServerWorldTickHandler livingEntityTickHandler = new LivingEntityTickHandler();

    private static final List<ServerWorldTickHandler> HANDLERS = List.of(villagerTickHandler, livingEntityTickHandler);

    public static void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (ServerWorldTickHandler handler : HANDLERS) {
                handler.onServerWorldTick(world);
            }
        }
    }
}
