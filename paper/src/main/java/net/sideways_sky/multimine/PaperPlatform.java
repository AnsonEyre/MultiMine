package net.sideways_sky.multimine;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.sideways_sky.multimine.common.MultiMinePlatform;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

final class PaperPlatform implements MultiMinePlatform<Block, Entity> {
    private final MultiMine plugin;

    PaperPlatform(MultiMine plugin) {
        this.plugin = plugin;
    }

    @Override
    public int nextEntityId(Block block) {
        return net.minecraft.world.entity.Entity.nextEntityId();
    }

    @Override
    public void sendProgress(Block block, int entityId, int stage) {
        ((CraftWorld) block.getWorld()).getHandle().destroyBlockProgress(entityId, ((CraftBlock) block).getPosition(), stage);
    }

    @Override
    public void breakBlock(Block block, Entity breaker) {
        if (breaker instanceof Player player) {
            if (player.breakBlock(block)) {
                block.getWorld().playSound(
                        block.getLocation(),
                        block.getBlockSoundGroup().getBreakSound(),
                        SoundCategory.BLOCKS,
                        1.0f, 1.0f
                );
                block.getWorld().spawnParticle(
                        Particle.BLOCK,
                        block.getLocation().add(0.5, 0.5, 0.5),
                        20, 0.3, 0.3, 0.3,
                        block.getBlockData());
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
