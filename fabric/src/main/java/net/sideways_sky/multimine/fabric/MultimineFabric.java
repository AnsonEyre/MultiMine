package net.sideways_sky.multimine.fabric;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.sideways_sky.multimine.common.DamageTracker;
import net.sideways_sky.multimine.common.MultiMineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultimineFabric implements ModInitializer {
    public static final String MOD_ID = "multimine";
    public static final Logger LOGGER = LoggerFactory.getLogger("MultiMine");
    public static DamageTracker<FabricBlock, ServerPlayer> tracker;

    @Override
    public void onInitialize() {
        tracker = new DamageTracker<>(new FabricPlatform(), loadConfig());

        ServerTickEvents.END_SERVER_TICK.register(server -> Scheduler.tick());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> tracker.clear());
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level instanceof ServerLevel serverLevel) {
                tracker.onBroken(new FabricBlock(serverLevel, pos));
            }
        });
    }

    private static MultiMineConfig loadConfig() {
        MultiMineConfig config = new MultiMineConfig();
        Path path = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".properties");
        Properties props = new Properties();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                props.load(reader);
            } catch (IOException e) {
                LOGGER.error("Could not read {}, using defaults", path, e);
            }
        }
        try {
            config.fadeStartDelay = Integer.parseInt(props.getProperty("fade.start-delay", String.valueOf(config.fadeStartDelay)).trim());
            config.fadeInterval = Integer.parseInt(props.getProperty("fade.interval", String.valueOf(config.fadeInterval)).trim());
            config.fadeAmount = Float.parseFloat(props.getProperty("fade.amount", String.valueOf(config.fadeAmount)).trim());
            config.debug = Boolean.parseBoolean(props.getProperty("debug", String.valueOf(config.debug)).trim());
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid value in {}, using defaults", path, e);
            config = new MultiMineConfig();
        }
        if (!Files.exists(path)) {
            writeDefaultConfig(path, config);
        }
        return config;
    }

    private static void writeDefaultConfig(Path path, MultiMineConfig config) {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write("""
                    # How long between when the player stops damaging and the first fade (in ticks)
                    fade.start-delay=%d
                    # How long between each fade (in ticks)
                    fade.interval=%d
                    # Damage decrease per fade. A fully damaged block is 1, a non damaged block is 0
                    # Set to 1 or more to fade block instantly
                    fade.amount=%s
                    debug=%s
                    """.formatted(config.fadeStartDelay, config.fadeInterval, config.fadeAmount, config.debug));
        } catch (IOException e) {
            LOGGER.warn("Could not write default config to {}", path, e);
        }
    }
}
