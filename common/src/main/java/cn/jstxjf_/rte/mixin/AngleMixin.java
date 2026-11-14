package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.AngleExtra;
import org.mtr.core.tool.Angle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Angle.class, remap = false)
public abstract class AngleMixin implements AngleExtra {
    @Shadow(remap = false) @Final public float angleDegrees;
    @Shadow(remap = false) @Final @Mutable public double angleRadians;
    @Shadow(remap = false) @Final @Mutable public double sin;
    @Shadow(remap = false) @Final @Mutable public double cos;
    @Shadow(remap = false) @Final @Mutable public double tan;
    @Shadow(remap = false) @Final @Mutable public double halfTan;

    @Invoker(value = "<init>", remap = false)
    private static Angle rte$create(String name, int ordinal, float angleDegrees) {
        throw new IllegalStateException();
    }

    @Override
    public Angle rte$fromDegrees(double degrees) {
        return rte$fromRadians(Math.toRadians(degrees));
    }

    @Override
    public Angle rte$fromRadians(double radians) {
        if (!Double.isFinite(radians)) {
            radians = 0;
        }
        radians = rte$normalizeRadians(radians);
        float degrees = (float) Math.toDegrees(radians);
        Angle angle = rte$create(Angle.fromAngle(degrees).name(), -1, degrees);
        ((AngleMixin)(Object)angle).rte$setRadians(radians);
        return angle;
    }

    private static double rte$normalizeRadians(double radians) {
        radians = radians % (2 * Math.PI);
        if (radians <= -Math.PI) {
            radians += 2 * Math.PI;
        } else if (radians > Math.PI) {
            radians -= 2 * Math.PI;
        }
        return radians;
    }

    private void rte$setRadians(double radians) {
        this.angleRadians = radians;
        this.sin = Math.sin(radians);
        this.cos = Math.cos(radians);
        this.tan = Math.tan(radians);
        this.halfTan = Math.tan(radians / 2.0);
    }

    @Inject(method = "getOpposite", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$getOpposite(CallbackInfoReturnable<Angle> cir) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_ANGLE)) {
            return;
        }
        cir.setReturnValue(((AngleExtra) (Object) this).rte$fromRadians(angleRadians + Math.PI));
    }

    @Inject(method = "add", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$add(Angle other, CallbackInfoReturnable<Angle> cir) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_ANGLE)) {
            return;
        }
        cir.setReturnValue(((AngleExtra) (Object) this).rte$fromRadians(angleRadians + other.angleRadians));
    }

    @Inject(method = "sub", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$sub(Angle other, CallbackInfoReturnable<Angle> cir) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_ANGLE)) {
            return;
        }
        cir.setReturnValue(((AngleExtra) (Object) this).rte$fromRadians(angleRadians - other.angleRadians));
    }

    @Inject(method = "isParallel", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$isParallel(Angle other, CallbackInfoReturnable<Boolean> cir) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_ANGLE)) {
            return;
        }
        double diff = (angleRadians - other.angleRadians) % Math.PI;
        if (diff <= -Math.PI / 2) {
            diff += Math.PI;
        } else if (diff > Math.PI / 2) {
            diff -= Math.PI;
        }
        cir.setReturnValue(Math.abs(diff) < 0.001);
    }
}
