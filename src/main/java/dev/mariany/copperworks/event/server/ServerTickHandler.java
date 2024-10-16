package dev.mariany.copperworks.event.server;

import dev.mariany.copperworks.event.entity.ItemEntityTickHandler;
import dev.mariany.copperworks.event.entity.LivingEntityTickHandler;
import dev.mariany.copperworks.event.entity.MinecartTickHandler;
import dev.mariany.copperworks.event.entity.VillagerTickHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ServerTickHandler {
    private static final ServerWorldTickHandler villagerTickHandler = new VillagerTickHandler();
    private static final ServerWorldTickHandler livingEntityTickHandler = new LivingEntityTickHandler();
    private static final ServerWorldTickHandler itemEntityTickHandler = new ItemEntityTickHandler();
    private static final ServerWorldTickHandler minecartTickHandler = new MinecartTickHandler();

    private static final List<ServerWorldTickHandler> HANDLERS = List.of(villagerTickHandler, livingEntityTickHandler,
            itemEntityTickHandler, minecartTickHandler);

    public static void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (ServerWorldTickHandler handler : HANDLERS) {
                handler.onServerWorldTick(world);
            }
        }
    }
}
