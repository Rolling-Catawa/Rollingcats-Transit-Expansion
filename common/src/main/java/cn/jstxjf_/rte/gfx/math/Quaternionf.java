package cn.jstxjf_.rte.gfx.math;

import org.joml.AxisAngle4f;

public class Quaternionf {
    public Quaternionf() {
        this(Vector3f.ZERO, 0.0f);
    }

    private final org.joml.Quaternionf impl;

    public Quaternionf(Vector3f axis, float angle) {
        this.impl = new org.joml.Quaternionf(new AxisAngle4f(angle, axis.asJoml()));
    }

    public Quaternionf(float x, float y, float z, float w) {
        this.impl = new org.joml.Quaternionf(x, y, z, w);
    }

    public Quaternionf(Quaternionf other) {
        this.impl = new org.joml.Quaternionf(other.impl);
    }

    public Quaternionf(org.joml.Quaternionf joml) {
        this.impl = new org.joml.Quaternionf(joml);
    }

    public Quaternionf mul(Quaternionf other) {
        this.impl.mul(other.impl);
        return this;
    }

    public float i() { return impl.x; }
    public float j() { return impl.y; }
    public float k() { return impl.z; }
    public float r() { return impl.w; }

    public void set(float i, float j, float k, float r) {
        impl.x = i;
        impl.y = j;
        impl.z = k;
        impl.w = r;
    }

    public org.joml.Quaternionf asMoj() {
        return impl;
    }

    public Quaternionf rotateX(double angle) {
        return rotateX((float) angle);
    }

    public Quaternionf rotateY(double angle) {
        return rotateY((float) angle);
    }

    public Quaternionf rotateZ(double angle) {
        return rotateZ((float) angle);
    }

    public Quaternionf rotateX(float angle) {
        return rotate(Vector3f.XP, angle);
    }

    public Quaternionf rotateY(float angle) {
        return rotate(Vector3f.YP, angle);
    }

    public Quaternionf rotateZ(float angle) {
        return rotate(Vector3f.ZP, angle);
    }

    public Quaternionf rotate(Vector3f axis, float angle) {
        mul(new Quaternionf(axis, angle));
        return this;
    }

    public void conjugate() {
        impl.conjugate();
    }
}
