package net.sideways_sky.multimine.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Platform-independent MultiMine logic. Platforms forward four block events here:
 * mining started, mining progress, mining aborted, and block broken.
 *
 * <p>Damage is the sum of every mining session on a block. Each entity's progress is
 * tracked separately, so several players mining one block together add up.
 */
public final class DamageTracker<B, E> {
    final MultiMinePlatform<B, E> platform;
    final MultiMineConfig config;
    final Map<B, DamagedBlock<B, E>> blocks = new HashMap<>();

    public DamageTracker(MultiMinePlatform<B, E> platform, MultiMineConfig config) {
        this.platform = platform;
        this.config = config;
    }

    public MultiMineConfig config() {
        return config;
    }

    public DamagedBlock<B, E> get(B block) {
        return blocks.get(block);
    }

    private DamagedBlock<B, E> getOrCreate(B block) {
        return blocks.computeIfAbsent(block, b -> new DamagedBlock<>(this, b));
    }

    /** An entity started mining {@code block}. Call only for non-instant breaks. */
    public void onDamageStart(B block, E entity) {
        DamagedBlock<B, E> damagedBlock = getOrCreate(block);
        damagedBlock.debugMessage("DamageStart");
        damagedBlock.entityDamageMap.putIfAbsent(entity, 0F);
        damagedBlock.stopFade();
    }

    /** {@code progress} is the entity's progress in its current mining session (0 to 1+). */
    public void onProgress(B block, E entity, float progress) {
        if (progress <= 0) {
            return;
        }
        DamagedBlock<B, E> damagedBlock = getOrCreate(block);

        float previous = damagedBlock.entityDamageMap.getOrDefault(entity, 0F);
        float increment = progress - previous;
        if (increment <= 0) {
            return;
        }
        damagedBlock.debugMessage("Progress: " + damagedBlock.damage + " += " + increment
                + " (" + progress + " - " + previous + ") | " + entity);

        damagedBlock.damage += increment;
        damagedBlock.entityDamageMap.put(entity, progress);
        if (damagedBlock.damage >= 1) {
            damagedBlock.brake(entity);
        } else {
            damagedBlock.sendPacket();
        }
    }

    /** {@code block} was broken (by MultiMine or anything else). */
    public void onBroken(B block) {
        DamagedBlock<B, E> damagedBlock = blocks.get(block);
        if (damagedBlock != null) {
            damagedBlock.debugMessage("Broken");
            damagedBlock.delete();
        }
    }

    /** An entity stopped mining {@code block} before it broke. */
    public void onAbort(B block, E entity) {
        DamagedBlock<B, E> damagedBlock = blocks.get(block);
        if (damagedBlock == null) {
            return;
        }
        damagedBlock.debugMessage("Abort");
        damagedBlock.entityDamageMap.remove(entity);
        damagedBlock.sendPacket();
        if (damagedBlock.entityDamageMap.isEmpty()) {
            damagedBlock.startFade();
        }
    }

    public void clear() {
        for (DamagedBlock<B, E> block : blocks.values().toArray(DamagedBlock[]::new)) {
            block.stopFade();
        }
        blocks.clear();
    }

    void debug(String message) {
        if (config.debug) {
            platform.debug(message);
        }
    }
}
