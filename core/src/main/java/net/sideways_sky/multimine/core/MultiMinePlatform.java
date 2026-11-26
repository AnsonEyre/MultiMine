package net.sideways_sky.multimine.core;

/**
 * What a server platform (Paper, Fabric, NeoForge, ...) has to provide for {@link DamageTracker} to work.
 *
 * @param <B> the platform's block reference. Must have value equality for the same block in the same world.
 * @param <E> the platform's entity / player type.
 */
public interface MultiMinePlatform<B, E> {
    /**
     * Send the crack animation for {@code block} to nearby players. {@code stage} is 0-9, or -1 to
     * clear it. {@code entityId} is the animation's "breaker" id; it's negative, so it never matches
     * a real entity.
     */
    void sendProgress(B block, int entityId, int stage);

    /** Break {@code block} as if {@code breaker} finished mining it. */
    void breakBlock(B block, E breaker);

    /** Run {@code task} every {@code period} ticks, starting after {@code delay} ticks. */
    Task scheduleRepeating(B block, long delay, long period, Runnable task);

    void debug(String message);

    interface Task {
        void cancel();
    }
}
