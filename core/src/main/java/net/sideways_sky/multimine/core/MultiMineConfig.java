package net.sideways_sky.multimine.core;

/** Settings shared by every platform. Each platform loads these from its own config format. */
public final class MultiMineConfig {
    /** Ticks between the last player letting go of a block and its first fade. */
    public int fadeStartDelay = 40;
    /** Ticks between each fade. */
    public int fadeInterval = 20;
    /** Damage removed per fade. A fully damaged block is 1. Set to 1 or more to fade instantly. */
    public float fadeAmount = 0.1F;
    public boolean debug = false;
}
