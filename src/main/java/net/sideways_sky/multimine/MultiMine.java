package net.sideways_sky.multimine;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class Multimine {
    public static final String MOD_ID = "multimine";

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(server -> Scheduler.tick());
    }
}