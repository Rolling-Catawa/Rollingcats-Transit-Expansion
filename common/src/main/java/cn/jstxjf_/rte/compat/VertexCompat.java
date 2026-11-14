package cn.jstxjf_.rte.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class VertexCompat {

    private final VertexConsumer consumer;

    private VertexCompat(VertexConsumer consumer) {
        this.consumer = consumer;
    }

    public static VertexCompat of(VertexConsumer consumer) {
        return new VertexCompat(consumer);
    }

    public VertexCompat vertex(Matrix4f matrix, float x, float y, float z) {
        //#if MC>=12101
        consumer.addVertex(matrix, x, y, z);
        //#else
        MatrixCompat.vertex(consumer, matrix, x, y, z);
        //#endif
        return this;
    }

    //#if MC<11903
    public VertexCompat vertex(com.mojang.math.Matrix4f matrix, float x, float y, float z) {
        consumer.vertex(matrix, x, y, z);
        return this;
    }

    public VertexCompat normal(com.mojang.math.Matrix3f matrix, float x, float y, float z) {
        consumer.normal(matrix, x, y, z);
        return this;
    }
    //#endif

    public VertexCompat vertex(double x, double y, double z) {
        //#if MC>=12101
        consumer.addVertex((float) x, (float) y, (float) z);
        //#else
        consumer.vertex(x, y, z);
        //#endif
        return this;
    }

    public VertexCompat color(int red, int green, int blue, int alpha) {
        //#if MC>=12101
        consumer.setColor(red, green, blue, alpha);
        //#else
        consumer.color(red, green, blue, alpha);
        //#endif
        return this;
    }

    public VertexCompat color(float red, float green, float blue, float alpha) {
        //#if MC>=12101
        consumer.setColor(red, green, blue, alpha);
        //#else
        consumer.color(red, green, blue, alpha);
        //#endif
        return this;
    }

    public VertexCompat uv(float u, float v) {
        //#if MC>=12101
        consumer.setUv(u, v);
        //#else
        consumer.uv(u, v);
        //#endif
        return this;
    }

    public VertexCompat overlayCoords(int overlay) {
        //#if MC>=12101
        consumer.setOverlay(overlay);
        //#else
        consumer.overlayCoords(overlay);
        //#endif
        return this;
    }

    public VertexCompat uv2(int light) {
        //#if MC>=12101
        consumer.setLight(light);
        //#else
        consumer.uv2(light);
        //#endif
        return this;
    }

    public VertexCompat normal(Matrix3f matrix, float x, float y, float z) {
        //#if MC>=12101
        final Vector3f normal = matrix.transform(new Vector3f(x, y, z));
        consumer.setNormal(normal.x(), normal.y(), normal.z());
        //#elseif MC>=11903
        consumer.normal(matrix, x, y, z);
        //#else
        final Vector3f normal = matrix.transform(new Vector3f(x, y, z));
        consumer.normal(normal.x(), normal.y(), normal.z());
        //#endif
        return this;
    }

    public VertexCompat normal(float x, float y, float z) {
        //#if MC>=12101
        consumer.setNormal(x, y, z);
        //#else
        consumer.normal(x, y, z);
        //#endif
        return this;
    }

    public void endVertex() {
        //#if MC<12101
        consumer.endVertex();
        //#endif
    }
}
