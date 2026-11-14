package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.TrainCollisionServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Inject(method = "noBlocksAround", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$standingOnTrainIsNotFloating(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ServerPlayer player && TrainCollisionServer.isStandingOnTrain(player)) {
            cir.setReturnValue(false);
        }
    }
}
