package cn.jstxjf_.rte.mapping.holder;

public final class Matrix4f extends cn.jstxjf_.rte.mapping.tool.HolderBase<org.joml.Matrix4f> {

    public Matrix4f(org.joml.Matrix4f p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Matrix4f cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Matrix4f((org.joml.Matrix4f) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof org.joml.Matrix4f;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int hashCode() {
        return ((org.joml.Matrix4f) this.data).hashCode();
    }

    public Matrix4f(org.joml.Matrix4dc p0) {
        super(new org.joml.Matrix4f(p0));
    }

    public Matrix4f(float p0, float p1, float p2, float p3, float p4, float p5, float p6, float p7, float p8, float p9, float p10, float p11, float p12, float p13, float p14, float p15) {
        super(new org.joml.Matrix4f(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15));
    }

    public Matrix4f(java.nio.FloatBuffer p0) {
        super(new org.joml.Matrix4f(p0));
    }

    public Matrix4f(org.joml.Vector4fc p0, org.joml.Vector4fc p1, org.joml.Vector4fc p2, org.joml.Vector4fc p3) {
        super(new org.joml.Matrix4f(p0, p1, p2, p3));
    }

    public Matrix4f() {
        super(new org.joml.Matrix4f());
    }

    public Matrix4f(org.joml.Matrix3fc p0) {
        super(new org.joml.Matrix4f(p0));
    }

    public Matrix4f(org.joml.Matrix4fc p0) {
        super(new org.joml.Matrix4f(p0));
    }

    public Matrix4f(org.joml.Matrix4x3fc p0) {
        super(new org.joml.Matrix4f(p0));
    }
}
