package net.sideways_sky.multimine;

import net.sideways_sky.multimine.core.DamageTracker;
import net.sideways_sky.multimine.core.MultiMineConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiMine extends JavaPlugin {
    public static DamageTracker<Block, Entity> tracker;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        MultiMineConfig config = new MultiMineConfig();
        config.debug = getConfig().getBoolean("debug");
        config.fadeStartDelay = getConfig().getInt("fade.start-delay");
        config.fadeInterval = getConfig().getInt("fade.interval");
        config.fadeAmount = (float) getConfig().getDouble("fade.amount");
        tracker = new DamageTracker<>(new PaperPlatform(this), config);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        if (tracker != null) {
            tracker.clear();
        }
        Bukkit.getScheduler().cancelTasks(this);
    }
}
