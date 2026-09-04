package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.Rolling;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow public abstract void setPosition(Vec3 pos);
    @Shadow protected abstract Vec3 getPosition();
    @Shadow private float eyeHeight;
    @Shadow private float eyeHeightOld;
    @Shadow @Final private Quaternionf rotation;

    @Unique private Quaternionf rte$roll = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);

    @Inject(
        method = "setup",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void rte$injectSetup(BlockGetter blockGetter, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Rolling.refreshFrame();
        float eyeH = (float) Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight);
        Vec3 p = getPosition();
        if (!rte$isFinite(p) || !Float.isFinite(eyeH)
                || Math.abs(p.x) > Float.MAX_VALUE || Math.abs(p.y) > Float.MAX_VALUE || Math.abs(p.z) > Float.MAX_VALUE) {
            rte$roll.identity();
            return;
        }
        Vector3f pos = new Vector3f((float)p.x, (float)p.y, (float)p.z);
        pos = Rolling.applyRolling(pos, eyeH);
        final Vec3 rolledPosition = new Vec3(pos.x(), pos.y(), pos.z());
        if (rte$isFinite(rolledPosition)) {
            setPosition(rolledPosition);
        }
        final Quaternionf candidate = Rolling.getRollQuaternion().asMoj();
        rte$roll = rte$isFinite(candidate) ? candidate : new Quaternionf();
    }

    @Inject(
        method = "setRotation",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
            ordinal = 0,
            shift = At.Shift.AFTER,
            remap = false
        )
    )
    private void rte$injectSetRotation(float yRot, float xRot, CallbackInfo ci) {
        if (rte$isFinite(rte$roll)) {
            rotation.mul(rte$roll);
        }
        if (!rte$isFinite(rotation)) {
            final float degreesToRadians = (float) Math.PI / 180.0F;
            rotation.rotationYXZ((float) Math.PI - yRot * degreesToRadians, -xRot * degreesToRadians, 0.0F);
        }
    }

    @Unique
    private static boolean rte$isFinite(Vec3 vector) {
        return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }

    @Unique
    private static boolean rte$isFinite(Quaternionf quaternion) {
        return Float.isFinite(quaternion.x()) && Float.isFinite(quaternion.y())
            && Float.isFinite(quaternion.z()) && Float.isFinite(quaternion.w());
    }
}
