package net.sideways_sky.multimine.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.sideways_sky.multimine.DamagedBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionMixin {
    @Shadow
    private boolean isDestroyingBlock;
    @Shadow
    private int destroyProgressStart;
    @Shadow
    private int gameTicks;
    @Shadow
    private boolean hasDelayedDestroy;
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;

    @Redirect(
            method = "incrementDestroyProgress",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V"))
    public void incrementDestroyProgressUpdate(ServerLevel instance, int entityId, BlockPos pos, int progress) {
        DamagedBlock block = DamagedBlock.damagedBlocksMap.get(pos);
        if (block == null) {
            instance.destroyBlockProgress(entityId, pos, progress);
            return;
        }
        if (block.progress != progress) {
            if (progress >= 10) {
                this.isDestroyingBlock = false;
                block.delete();
            }
            block.progress = progress;
            block.sendUpdate();
        }
    }

    @Inject(method = "handleBlockBreakAction", at = @At(value = "HEAD"), cancellable = true)
    public void processHead(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK
                || action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            ci.cancel();
            this.isDestroyingBlock = false;
            this.hasDelayedDestroy = false;
            DamagedBlock block = DamagedBlock.damagedBlocksMap.get(pos);
            if (block != null) {
                block.progress = this.currentDestroyProgress(pos);
                block.sendUpdate();
                block.startFade();
            }
        }
    }

    @Inject(method = "handleBlockBreakAction", at = @At(value = "TAIL"))
    public void processTail(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        if (action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            DamagedBlock block = DamagedBlock.damagedBlocksMap.get(pos);
            if (block != null) {
                block.stopFade();
                BlockState blockState = this.level.getBlockState(pos);
                float delta = blockState.getDestroyProgress(this.player, this.player.level(), pos);
                int timeSave = (int) ((float) block.progress / 10.0F / delta);
                this.destroyProgressStart -= timeSave;
                block.sendUpdate();
            } else {
                DamagedBlock damagedBlock = new DamagedBlock(this.level, pos, this.currentDestroyProgress(pos));
                DamagedBlock.damagedBlocksMap.put(pos, damagedBlock);
            }
        }
    }

    @Inject(method = "destroyBlock", at = @At(value = "HEAD"))
    public void destroyHead(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        DamagedBlock block = DamagedBlock.damagedBlocksMap.remove(pos);
        if (block != null) {
            block.delete(false);
        }
    }

    private int currentDestroyProgress(BlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        if (blockState.isAir()) {
            return 0;
        }
        float destroyProgress = blockState.getDestroyProgress(this.player, this.player.level(), pos)
                * (this.gameTicks - this.destroyProgressStart + 1);
        return Mth.clamp((int) (destroyProgress * 10.0F), 0, 10);
    }
}