package net.sideways_sky.multimine.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** A block position in a specific level. Used as the {@code DamageTracker} key. */
public record FabricBlock(ServerLevel level, BlockPos pos) {
    public FabricBlock {
        pos = pos.immutable();
    }
}
