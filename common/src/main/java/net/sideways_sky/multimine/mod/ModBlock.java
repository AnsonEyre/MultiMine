package net.sideways_sky.multimine.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** A block position in a specific level. Used as the {@code DamageTracker} key. */
public record ModBlock(ServerLevel level, BlockPos pos) {
    public ModBlock {
        pos = pos.immutable();
    }
}
