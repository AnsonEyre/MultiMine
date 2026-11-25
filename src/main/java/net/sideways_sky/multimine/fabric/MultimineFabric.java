package net.sideways_sky.multimine.fabric;

import net.fabricmc.api.ModInitializer;
import net.sideways_sky.multimine.Multimine;

public final class MultimineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Multimine.init();
    }
}