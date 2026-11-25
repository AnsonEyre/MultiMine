package net.sideways_sky.multimine.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.sideways_sky.multimine.mod.MultiMineMod;

@Mod(MultiMineMod.MOD_ID)
public final class MultiMineNeoForge {
    public MultiMineNeoForge() {
        MultiMineMod.init(FMLPaths.CONFIGDIR.get());
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> MultiMineMod.onServerTick());
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, event -> MultiMineMod.onServerStopping());
    }
}
