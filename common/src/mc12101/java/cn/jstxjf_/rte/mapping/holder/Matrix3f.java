package cn.jstxjf_.rte.mapping.holder;

public final class Matrix3f extends cn.jstxjf_.rte.mapping.tool.HolderBase<org.joml.Matrix3f> {

    public Matrix3f(org.joml.Matrix3f p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Matrix3f cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Matrix3f((org.joml.Matrix3f) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof org.joml.Matrix3f;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int hashCode() {
        return ((org.joml.Matrix3f) this.data).hashCode();
    }

    public Matrix3f(org.joml.Matrix4fc p0) {
        super(new org.joml.Matrix3f(p0));
    }

    public Matrix3f(float p0, float p1, float p2, float p3, float p4, float p5, float p6, float p7, float p8) {
        super(new org.joml.Matrix3f(p0, p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public Matrix3f(java.nio.FloatBuffer p0) {
        super(new org.joml.Matrix3f(p0));
    }

    public Matrix3f(org.joml.Vector3fc p0, org.joml.Vector3fc p1, org.joml.Vector3fc p2) {
        super(new org.joml.Matrix3f(p0, p1, p2));
    }

    public Matrix3f() {
        super(new org.joml.Matrix3f());
    }

    public Matrix3f(org.joml.Matrix2fc p0) {
        super(new org.joml.Matrix3f(p0));
    }

    public Matrix3f(org.joml.Matrix3fc p0) {
        super(new org.joml.Matrix3f(p0));
    }

    public boolean isFinite() {
        return ((org.joml.Matrix3f) this.data).isFinite();
    }
}
