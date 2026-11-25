package net.sideways_sky.multimine.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.sideways_sky.multimine.mod.MultiMineMod;

public final class MultiMineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MultiMineMod.init(FabricLoader.getInstance().getConfigDir());
        ServerTickEvents.END_SERVER_TICK.register(server -> MultiMineMod.onServerTick());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> MultiMineMod.onServerStopping());
    }
}
