package net.sideways_sky.multimine.forge;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.sideways_sky.multimine.mod.MultiMineMod;

@Mod(MultiMineMod.MOD_ID)
public final class MultiMineForge {
    public MultiMineForge() {
        MultiMineMod.init(FMLPaths.CONFIGDIR.get());
        TickEvent.ServerTickEvent.Post.BUS.addListener(event -> MultiMineMod.onServerTick());
        ServerStoppingEvent.BUS.addListener(event -> MultiMineMod.onServerStopping());
    }
}
