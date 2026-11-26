package net.sideways_sky.multimine;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.sideways_sky.multimine.core.MultiMinePlatform;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Uses only the public Paper API (no server internals), so the same jar works on every supported
 * Minecraft version.
 */
final class PaperPlatform implements MultiMinePlatform<Block, Entity> {
    /** Vanilla sends block crack animations to players within this many blocks. */
    private static final double VIEW_DISTANCE_SQUARED = 32 * 32;

    private final MultiMine plugin;

    PaperPlatform(MultiMine plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sendProgress(Block block, int entityId, int stage) {
        // sendBlockDamage takes progress (0 to 1) and sends stage (int) (9 * progress), or clears the
        // animation when progress is 0. Pick the progress that lands in the middle of the stage.
        float progress = stage >= 0 && stage <= 9 ? Math.min(1F, (stage + 0.5F) / 9F) : 0F;
        Location location = block.getLocation();
        Location center = location.clone().add(0.5, 0.5, 0.5);
        for (Player player : block.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(center) < VIEW_DISTANCE_SQUARED) {
                player.sendBlockDamage(location, progress, entityId);
            }
        }
    }

    @Override
    public void breakBlock(Block block, Entity breaker) {
        if (breaker instanceof Player player) {
            BlockData data = block.getBlockData();
            if (player.breakBlock(block)) {
                // The server sends the break particles and sound to everyone except the breaker,
                // because the breaker's client normally plays them itself. Here the server broke the
                // block before the client finished, so send them to the breaker too.
                player.playEffect(block.getLocation(), Effect.STEP_SOUND, data);
            }
        } else {
            block.breakNaturally(true);
        }
    }

    @Override
    public Task scheduleRepeating(Block block, long delay, long period, Runnable task) {
        ScheduledTask scheduled = plugin.getServer().getRegionScheduler()
                .runAtFixedRate(plugin, block.getLocation(), t -> task.run(), delay, period);
        return scheduled::cancel;
    }

    @Override
    public void debug(String message) {
        plugin.getLogger().info("Debug | " + message);
    }
}
