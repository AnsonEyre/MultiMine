package net.sideways_sky.multimine.mod;

import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.sideways_sky.multimine.core.MultiMinePlatform;

final class ModPlatform implements MultiMinePlatform<ModBlock, ServerPlayer> {
    @Override
    public void sendProgress(ModBlock block, int entityId, int stage) {
        block.level().destroyBlockProgress(entityId, block.pos(), stage);
    }

    @Override
    public void breakBlock(ModBlock block, ServerPlayer breaker) {
        BlockState state = block.level().getBlockState(block.pos());
        if (breaker.gameMode.destroyBlock(block.pos())) {
            // Vanilla sends the break particles and sound to everyone except the breaker, because
            // the breaker's client normally plays them itself. Here the server broke the block
            // before the client finished, so send them to the breaker too.
            breaker.connection.send(new ClientboundLevelEventPacket(
                    LevelEvent.PARTICLES_DESTROY_BLOCK, block.pos(), Block.getId(state), false));
        }
    }

    @Override
    public Task scheduleRepeating(ModBlock block, long delay, long period, Runnable task) {
        return Scheduler.repeat(delay, period, t -> task.run())::cancel;
    }

    @Override
    public void debug(String message) {
        MultiMineMod.LOGGER.info("Debug | {}", message);
    }
}
