package cn.jstxjf_.rte.gfx.math;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sowcer's internal PoseStack independent of Minecraft's MatrixStack.
 * Used for batching operations.
 */
public class PoseStack implements Posture {

    private final Deque<Pose> poseStack = new ArrayDeque<>();

    public PoseStack() {
        poseStack.add(new Pose());
    }
    
    public PoseStack(Pose pose) {
        poseStack.add(pose);
    }

    public void pushPose() {
        Pose pose = poseStack.getLast();
        poseStack.add(new Pose(pose.pose.copy(), pose.normal.copy()));
    }

    public void popPose() {
        poseStack.removeLast();
    }

    public Pose last() {
        return poseStack.getLast();
    }

    public void translate(double x, double y, double z) {
        Pose pose = poseStack.getLast();
        pose.pose.translate((float)x, (float)y, (float)z);
    }

    public void scale(float x, float y, float z) {
        Pose pose = poseStack.getLast();
        pose.pose.scale(x, y, z);
        if (x == y && y == z && x > 0.0F) {
            return;
        }
        pose.normal.scale(1.0F / x, 1.0F / y, 1.0F / z);
    }

    public void mulPose(Quaternionf quaternion) {
        Pose pose = poseStack.getLast();
        pose.pose.multiply(quaternion);
        pose.normal.multiply(quaternion);
    }

    public void rotateY(float rad) {
        Pose pose = poseStack.getLast();
        pose.pose.rotateY(rad);
        pose.normal.rotateY(rad);
    }

    public void rotateX(float rad) {
        Pose pose = poseStack.getLast();
        pose.pose.rotateX(rad);
        pose.normal.rotateX(rad);
    }

    public void rotateZ(float rad) {
        Pose pose = poseStack.getLast();
        pose.pose.rotateZ(rad);
        pose.normal.rotateZ(rad);
    }

    @Override
    public Matrix4f getAsMatrix4f() {
        return last().pose;
    }

    @Override
    public Matrix3f getAsMatrix3f() {
        return last().normal;
    }

    @Override
    public Matrices getAsMatrices() {
        return new Matrices(last().pose, last().normal);
    }

    @Override
    public Pose getAsPose() {
        return last();
    }

    @Override
    public PoseStack getAsPoseStack() {
        return this;
    }
}
