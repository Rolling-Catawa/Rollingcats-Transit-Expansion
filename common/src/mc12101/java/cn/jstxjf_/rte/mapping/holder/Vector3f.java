package cn.jstxjf_.rte.mapping.holder;

public final class Vector3f extends cn.jstxjf_.rte.mapping.tool.HolderBase<org.joml.Vector3f> {

    public Vector3f(org.joml.Vector3f p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3f cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Vector3f((org.joml.Vector3f) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof org.joml.Vector3f;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public Vector3f(java.nio.ByteBuffer p0) {
        super(new org.joml.Vector3f(p0));
    }

    public Vector3f(org.joml.Vector2ic p0, float p1) {
        super(new org.joml.Vector3f(p0, p1));
    }

    public Vector3f(float[] p0) {
        super(new org.joml.Vector3f(p0));
    }

    public Vector3f(int p0, java.nio.ByteBuffer p1) {
        super(new org.joml.Vector3f(p0, p1));
    }

    public Vector3f(java.nio.FloatBuffer p0) {
        super(new org.joml.Vector3f(p0));
    }

    public Vector3f(int p0, java.nio.FloatBuffer p1) {
        super(new org.joml.Vector3f(p0, p1));
    }

    public Vector3f(org.joml.Vector3fc p0) {
        super(new org.joml.Vector3f(p0));
    }

    public Vector3f() {
        super(new org.joml.Vector3f());
    }

    public Vector3f(float p0) {
        super(new org.joml.Vector3f(p0));
    }

    public Vector3f(float p0, float p1, float p2) {
        super(new org.joml.Vector3f(p0, p1, p2));
    }

    public Vector3f(org.joml.Vector2fc p0, float p1) {
        super(new org.joml.Vector3f(p0, p1));
    }

    public Vector3f(org.joml.Vector3ic p0) {
        super(new org.joml.Vector3f(p0));
    }

    public boolean isFinite() {
        return ((org.joml.Vector3f) this.data).isFinite();
    }

    public float getX() {
        return ((org.joml.Vector3f) this.data).x();
    }

    public float getY() {
        return ((org.joml.Vector3f) this.data).y();
    }

    public float getZ() {
        return ((org.joml.Vector3f) this.data).z();
    }

    public int hashCode() {
        return ((org.joml.Vector3f) this.data).hashCode();
    }
}
