package net.sideways_sky.multimine.mod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.server.level.ServerPlayer;
import net.sideways_sky.multimine.core.DamageTracker;
import net.sideways_sky.multimine.core.MultiMineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader-independent mod setup. Each loader module calls {@link #init} once at startup, and
 * forwards its server tick and server stopping events to {@link #onServerTick} and
 * {@link #onServerStopping}. The block events come from {@code ServerPlayerGameModeMixin}.
 */
public final class MultiMineMod {
    public static final String MOD_ID = "multimine";
    public static final Logger LOGGER = LoggerFactory.getLogger("MultiMine");
    public static DamageTracker<ModBlock, ServerPlayer> tracker;

    private MultiMineMod() {
    }

    public static void init(Path configDir) {
        tracker = new DamageTracker<>(new ModPlatform(), loadConfig(configDir.resolve(MOD_ID + ".properties")));
        LOGGER.info("MultiMine initialized");
    }

    public static void onServerTick() {
        Scheduler.tick();
    }

    public static void onServerStopping() {
        tracker.clear();
    }

    private static MultiMineConfig loadConfig(Path path) {
        MultiMineConfig config = new MultiMineConfig();
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
