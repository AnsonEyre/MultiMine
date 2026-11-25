package net.sideways_sky.multimine;

import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;

public class Events implements Listener {
    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        if (e.getInstaBreak()) {
            return;
        }
        MultiMine.tracker.onDamageStart(e.getBlock(), e.getPlayer());
    }

    @EventHandler
    public void onBlockBreakProgressUpdate(BlockBreakProgressUpdateEvent e) {
        MultiMine.tracker.onProgress(e.getBlock(), e.getEntity(), e.getProgress());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        MultiMine.tracker.onBroken(e.getBlock());
    }

    @EventHandler
    public void onBlockDamageAbort(BlockDamageAbortEvent e) {
        MultiMine.tracker.onAbort(e.getBlock(), e.getPlayer());
    }
}
