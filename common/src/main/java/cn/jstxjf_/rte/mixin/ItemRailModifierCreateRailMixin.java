package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.mapping.holder.*;
import org.mtr.mod.item.ItemRailModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = ItemRailModifier.class, remap = false)
public abstract class ItemRailModifierCreateRailMixin {

    @Inject(method = "onConnect", at = @At("TAIL"), remap = false)
    private void rte$recordConnect(World world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, ServerPlayerEntity player, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (player != null) {
            cn.jstxjf_.rte.data.RailUndoManager.record(world.data, posStart.data, posEnd.data,
                ((net.minecraft.server.level.ServerPlayer) player.data).getUUID());
        }
        cn.jstxjf_.rte.data.RailAutoTangent.apply(world.data, posStart.data, posEnd.data);
    }

    @Inject(method = "createRail", at = @At("RETURN"))
    private void rte$createRail(UUID uuid, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, CallbackInfoReturnable<Rail> cir) {
        Rail rail = cir.getReturnValue();
        if (rail == null) return;
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
        extra.setExtraAngle1Rad(facingStart.angleRadians);
        extra.setExtraAngle2Rad(facingEnd.angleRadians);

        int pathMode = cn.jstxjf_.rte.data.RailPathModeState.lastPathMode;
        if (pathMode != 0) {
            extra.changePathMode(pathMode);
        }
    }
}
