package net.sideways_sky.multimine.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import net.sideways_sky.multimine.fabric.FabricBlock;
import net.sideways_sky.multimine.fabric.MultimineFabric;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires the same events Paper gives the plugin: BlockDamageEvent, BlockBreakProgressUpdateEvent
 * and BlockDamageAbortEvent. Block breaks come from Fabric API's PlayerBlockBreakEvents.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;

    /** A survival player really started mining a block that doesn't break instantly. */
    @Inject(method = "handleBlockBreakAction",
            at = @At(value = "CONSTANT", args = "stringValue=actual start of destroying"))
    private void multimine$startDestroying(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction,
                                           int maxBuildHeight, int sequence, CallbackInfo ci) {
        MultimineFabric.tracker.onDamageStart(new FabricBlock(level, pos), player);
    }

    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
    private void multimine$abortDestroying(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction,
                                           int maxBuildHeight, int sequence, CallbackInfo ci) {
        if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            MultimineFabric.tracker.onAbort(new FabricBlock(level, pos), player);
        }
    }

    /** Called every tick while mining, with this mining session's progress (0 to 1+). */
    @Inject(method = "incrementDestroyProgress", at = @At("RETURN"))
    private void multimine$progress(BlockState state, BlockPos pos, int startTick, CallbackInfoReturnable<Float> cir) {
        MultimineFabric.tracker.onProgress(new FabricBlock(level, pos), player, cir.getReturnValueF());
    }
}
