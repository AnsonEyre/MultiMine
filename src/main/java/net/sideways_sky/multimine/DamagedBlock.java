package net.sideways_sky.multimine;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class DamagedBlock {
    public static final Map<BlockPos, DamagedBlock> damagedBlocksMap = new HashMap<>();
    private final int entityId;
    public final ServerLevel world;
    public final BlockPos pos;
    public int progress;
    @Nullable
    private Scheduler.Task fading;

    public DamagedBlock(ServerLevel world, BlockPos pos, int currentProgress) {
        this.entityId = world.getNextEntityId();
        this.world = world;
        this.pos = pos;
        this.progress = currentProgress;
    }

    public void stopFade() {
        if (this.fading != null) {
            this.fading.cancel();
            this.fading = null;
        }
    }

    public void startFade() {
        if (this.fading != null) {
            return;
        }
        this.fading = Scheduler.repeat(40L, 20L, task -> {
            --this.progress;
            if (this.progress < 1) {
                this.world.destroyBlockProgress(this.entityId, this.pos, -1);
                this.delete();
                return;
            }
            this.sendUpdate();
        });
    }

    public void delete() {
        this.delete(true);
    }

    public void delete(boolean update) {
        this.stopFade();
        this.progress = -1;
        if (update) {
            this.sendUpdate();
        }
        damagedBlocksMap.remove(this.pos);
    }

    public void sendUpdate() {
        this.world.destroyBlockProgress(this.entityId, this.pos, this.progress);
    }
}