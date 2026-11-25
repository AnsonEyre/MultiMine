package net.sideways_sky.multimine.common;

import java.util.HashMap;
import java.util.Map;

/** One partially mined block and how much each entity currently mining it has contributed. */
public final class DamagedBlock<B, E> {
    private final DamageTracker<B, E> tracker;
    private final B block;
    private final int entityId;

    /** Total damage, 0 to 1. */
    public float damage = 0;
    /** Last progress reported for each entity's current mining session. */
    final Map<E, Float> entityDamageMap = new HashMap<>();
    private MultiMinePlatform.Task fadeTask = null;

    DamagedBlock(DamageTracker<B, E> tracker, B block) {
        this.tracker = tracker;
        this.block = block;
        this.entityId = tracker.platform.nextEntityId(block);
        debugMessage("Created");
    }

    public B block() {
        return block;
    }

    void debugMessage(String message) {
        tracker.debug("Block[ " + entityId + " ]: " + message);
    }

    void brake(E entity) {
        debugMessage("Brake");
        tracker.platform.breakBlock(block, entity);
    }

    void startFade() {
        if (fadeTask != null) {
            return;
        }
        debugMessage("Fade Start");
        MultiMineConfig config = tracker.config;
        fadeTask = tracker.platform.scheduleRepeating(block, config.fadeStartDelay, config.fadeInterval, this::fade);
    }

    void stopFade() {
        if (fadeTask != null) {
            debugMessage("Fade Stop");
            fadeTask.cancel();
            fadeTask = null;
        }
    }

    void delete() {
        stopFade();
        sendPacket(-1);
        tracker.blocks.remove(block);
        debugMessage("Deleted");
    }

    private void fade() {
        float before = damage;
        float amount = tracker.config.fadeAmount;
        damage -= amount;
        String message = "Fade: " + damage + " -= " + amount;
        if (damage < 0) {
            debugMessage(message + " -deleting");
            delete();
        } else if (Math.round(damage * 10F) != Math.round(before * 10F)) {
            debugMessage(message + " -updating");
            sendPacket();
        } else {
            debugMessage(message);
        }
    }

    void sendPacket() {
        sendPacket(Math.round(damage * 10F));
    }

    private void sendPacket(int stage) {
        tracker.platform.sendProgress(block, entityId, stage);
    }
}
