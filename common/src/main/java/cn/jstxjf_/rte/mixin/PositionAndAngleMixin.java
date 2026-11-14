package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Position;
import org.mtr.core.tool.Angle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "org.mtr.core.path.SidingPathFinder$PositionAndAngle", remap = false)
public abstract class PositionAndAngleMixin {

    @Shadow(remap = false) @Final private Position position;
    @Shadow(remap = false) @Final @Nullable private Angle angle;

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$equals(Object obj, CallbackInfoReturnable<Boolean> cir) {
        if (obj == this) {
            cir.setReturnValue(true);
            return;
        }
        if (!(obj instanceof PositionAndAngleMixin other)) {
            cir.setReturnValue(false);
            return;
        }
        if (!position.equals(other.position)) {
            cir.setReturnValue(false);
            return;
        }
        if (angle == null || other.angle == null || angle == other.angle) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(Angle.similarFacing(angle.angleDegrees, other.angle.angleDegrees));
    }

    @Inject(method = "hashCode", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$hashCode(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(position.hashCode());
    }
}
