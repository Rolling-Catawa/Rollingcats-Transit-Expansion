package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.gfx.math.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;

public final class CameraPose {

    private static final org.joml.Matrix4f CAPTURED = new org.joml.Matrix4f();
    private static boolean valid;

    private CameraPose() {
    }

    public static void capture(PoseStack poseStack) {
        try {
            CAPTURED.set(cn.jstxjf_.rte.compat.MatrixCompat.pose(poseStack));
            valid = true;
        } catch (Throwable e) {
            valid = false;
        }
    }

    public static void invalidate() {
        valid = false;
    }

    public static boolean isValid() {
        return valid;
    }

    public static Matrix4f copy() {
        return new Matrix4f(new org.joml.Matrix4f(CAPTURED));
    }

    public static Matrix4f copyOrFallback() {
        if (valid) {
            return copy();
        }
        final net.minecraft.client.Camera camera =
            net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera();
        final org.joml.Matrix4f fallback = new org.joml.Matrix4f();
        fallback.rotateX((float) Math.toRadians(camera.getXRot()));
        fallback.rotateY((float) Math.toRadians(camera.getYRot() + 180.0f));
        final Matrix4f result = new Matrix4f(fallback);
        final cn.jstxjf_.rte.gfx.math.Quaternionf frameRoll = cn.jstxjf_.rte.data.Rolling.frameRoll();
        if (frameRoll != null) {
            result.multiply(frameRoll);
        }
        return result;
    }
}
