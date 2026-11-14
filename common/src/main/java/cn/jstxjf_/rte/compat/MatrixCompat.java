package cn.jstxjf_.rte.compat;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class MatrixCompat {

    //#if MC<11903
    private static final ThreadLocal<java.nio.FloatBuffer> BUFFER = ThreadLocal.withInitial(() -> java.nio.FloatBuffer.allocate(16));
    //#endif

    private MatrixCompat() {
    }

    public static Matrix4f pose(PoseStack poseStack) {
        return pose(poseStack.last());
    }

    public static Matrix4f pose(PoseStack.Pose pose) {
        //#if MC>=11903
        return pose.pose();
        //#else
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        pose.pose().store(buffer);
        buffer.rewind();
        return new Matrix4f().set(buffer);
        //#endif
    }

    //#if MC<11903
    public static Matrix4f toJoml(com.mojang.math.Matrix4f matrix) {
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        matrix.store(buffer);
        buffer.rewind();
        return new Matrix4f().set(buffer);
    }
    //#endif

    public static Matrix3f normal(PoseStack.Pose pose) {
        //#if MC>=11903
        return pose.normal();
        //#else
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        pose.normal().store(buffer);
        buffer.rewind();
        buffer.limit(9);
        return new Matrix3f().set(buffer);
        //#endif
    }

    public static void setPose(PoseStack.Pose pose, Matrix4f matrix) {
        //#if MC>=11903
        pose.pose().set(matrix);
        //#else
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        matrix.get(buffer);
        buffer.rewind();
        pose.pose().load(buffer);
        //#endif
    }

    public static void mulPose(PoseStack poseStack, Quaternionf rotation) {
        //#if MC>=11903
        poseStack.mulPose(rotation);
        //#else
        poseStack.mulPose(new com.mojang.math.Quaternion(rotation.x, rotation.y, rotation.z, rotation.w));
        //#endif
    }

    public static void mulPoseMatrix(PoseStack poseStack, Matrix4f matrix) {
        //#if MC>=12101
        poseStack.mulPose(matrix);
        //#elseif MC>=11903
        poseStack.mulPoseMatrix(matrix);
        //#else
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        matrix.get(buffer);
        buffer.rewind();
        final com.mojang.math.Matrix4f converted = new com.mojang.math.Matrix4f();
        converted.load(buffer);
        poseStack.mulPoseMatrix(converted);
        //#endif
    }

    public static com.mojang.blaze3d.vertex.VertexConsumer vertex(com.mojang.blaze3d.vertex.VertexConsumer consumer, Matrix4f matrix, float x, float y, float z) {
        //#if MC>=12101
        return consumer.addVertex(matrix, x, y, z);
        //#elseif MC>=11903
        return consumer.vertex(matrix, x, y, z);
        //#else
        final org.joml.Vector4f transformed = matrix.transform(new org.joml.Vector4f(x, y, z, 1));
        return consumer.vertex(transformed.x, transformed.y, transformed.z);
        //#endif
    }

    public static void setUniform(Uniform uniform, Matrix4f matrix) {
        //#if MC>=11903
        uniform.set(matrix);
        //#else
        final java.nio.FloatBuffer buffer = BUFFER.get();
        buffer.clear();
        matrix.get(buffer);
        buffer.rewind();
        final com.mojang.math.Matrix4f converted = new com.mojang.math.Matrix4f();
        converted.load(buffer);
        uniform.set(converted);
        //#endif
    }
}
