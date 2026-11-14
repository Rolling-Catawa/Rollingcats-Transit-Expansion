package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.item.ItemRailModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = ItemRailModifier.class, remap = false)
public abstract class ItemRailModifierCreateRailMixin {

    @Inject(method = "onConnect", at = @At("TAIL"), remap = false)
    private void rte$recordConnect(Level world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, ServerPlayer player, CallbackInfo ci) {
        if (player != null) {
            cn.jstxjf_.rte.data.RailUndoManager.record(world, posStart, posEnd, player.getUUID());
        }
        cn.jstxjf_.rte.data.RailAutoTangent.apply(world, posStart, posEnd);
    }

    @Inject(method = "createRail", at = @At("RETURN"))
    private void rte$createRail(UUID uuid, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, CallbackInfoReturnable<Rail> cir) {
        final Rail rail = cir.getReturnValue();
        if (rail == null) return;
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
        extra.setExtraAngle1Rad(facingStart.angleRadians);
        extra.setExtraAngle2Rad(facingEnd.angleRadians);

        final int pathMode = cn.jstxjf_.rte.data.RailPathModeState.lastPathMode;
        if (pathMode != 0) {
            extra.changePathMode(pathMode);
        }
    }
}
