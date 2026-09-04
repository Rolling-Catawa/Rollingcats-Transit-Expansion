package cn.jstxjf_.rte.gfx.math;

import org.mtr.mapping.holder.Direction;

public class Vector3f {

    public static final Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
    public static final Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
    public static final Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
    public static final Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
    public static final Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
    public static final Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
    public static final Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
    
    private float x;
    private float y;
    private float z;

    public Vector3f() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(org.mtr.mapping.holder.Vector3f vec) {
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3f(org.joml.Vector3f vec) {
        this(vec.x, vec.y, vec.z);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && this.getClass() == other.getClass()) {
            Vector3f vector3f = (Vector3f)other;
            if (Float.compare(vector3f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(vector3f.y, this.y) != 0) {
                return false;
            } else {
                return Float.compare(vector3f.z, this.z) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return i;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public void mul(float multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
    }

    public void mul(float mx, float my, float mz) {
        this.x *= mx;
        this.y *= my;
        this.z *= mz;
    }

    public void clamp(float min, float max) {
        if (this.x < min) {
            this.x = min;
        } else if (this.x > max) {
            this.x = max;
        }

        if (this.y < min) {
            this.y = min;
        } else if (this.y > max) {
            this.y = max;
        }

        if (this.z < min) {
            this.z = min;
        } else if (this.z > max) {
            this.z = max;
        }

    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(Vector3f other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void sub(Vector3f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }

    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if ((double)f < 1.0E-5D) {
            return false;
        } else {
            float g = fastInvSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            return true;
        }
    }

    public void cross(Vector3f other) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = other.x();
        float j = other.y();
        float k = other.z();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

    public void transform(Quaternionf quaternion) {
        Quaternionf quaternionf = new Quaternionf(quaternion);
        quaternionf.mul(new Quaternionf(this.x(), this.y(), this.z(), 0.0F));
        Quaternionf quaternionf2 = new Quaternionf(quaternion);
        quaternionf2.conjugate();
        quaternionf.mul(quaternionf2);
        this.set(quaternionf.i(), quaternionf.j(), quaternionf.k());
    }

    public void transform(Matrix3f matrix) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        this.x = Mth.mulAdd(matrix.asMoj().m00(), f, Mth.mulAdd(matrix.asMoj().m01(), g, matrix.asMoj().m02() * h));
        this.y = Mth.mulAdd(matrix.asMoj().m10(), f, Mth.mulAdd(matrix.asMoj().m11(), g, matrix.asMoj().m12() * h));
        this.z = Mth.mulAdd(matrix.asMoj().m20(), f, Mth.mulAdd(matrix.asMoj().m21(), g, matrix.asMoj().m22() * h));
    }

    public void transform(Matrix4f matrix) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        this.x = Mth.mulAdd(matrix.asMoj().m00(), f, Mth.mulAdd(matrix.asMoj().m01(), g, Mth.mulAdd(matrix.asMoj().m02(), h, matrix.asMoj().m03())));
        this.y = Mth.mulAdd(matrix.asMoj().m10(), f, Mth.mulAdd(matrix.asMoj().m11(), g, Mth.mulAdd(matrix.asMoj().m12(), h, matrix.asMoj().m13())));
        this.z = Mth.mulAdd(matrix.asMoj().m20(), f, Mth.mulAdd(matrix.asMoj().m21(), g, Mth.mulAdd(matrix.asMoj().m22(), h, matrix.asMoj().m23())));
    }

    public void rotDeg(Vector3f axis, float angle) {
        rotate(new Quaternionf(axis, (float) Math.toRadians(angle)));
    }

    public void rotate(Quaternionf rotation) {
        Quaternionf quaternionf = new Quaternionf(rotation);
        quaternionf.mul(new Quaternionf(this.x(), this.y(), this.z(), 0.0F));
        Quaternionf quaternionf2 = new Quaternionf(rotation);
        quaternionf2.conjugate();
        quaternionf.mul(quaternionf2);
        this.set(quaternionf.i(), quaternionf.j(), quaternionf.k());
    }
    
    public void rot(Vector3f axis, float angleRadians) {
        rotate(new Quaternionf(axis, angleRadians));
    }

    public void rotX(float rad) {
        final float cos = (float) Math.cos(rad);
        final float sin = (float) Math.sin(rad);
        final float newY = this.y * cos - this.z * sin;
        final float newZ = this.y * sin + this.z * cos;
        this.y = newY;
        this.z = newZ;
    }

    public void rotY(float rad) {
        final float cos = (float) Math.cos(rad);
        final float sin = (float) Math.sin(rad);
        final float newX = this.x * cos + this.z * sin;
        final float newZ = -this.x * sin + this.z * cos;
        this.x = newX;
        this.z = newZ;
    }

    public void rotZ(float rad) {
        final float cos = (float) Math.cos(rad);
        final float sin = (float) Math.sin(rad);
        final float newX = this.x * cos - this.y * sin;
        final float newY = this.x * sin + this.y * cos;
        this.x = newX;
        this.y = newY;
    }

    public void lerp(Vector3f other, float delta) {
        float f = 1.0F - delta;
        this.x = this.x * f + other.x * delta;
        this.y = this.y * f + other.y * delta;
        this.z = this.z * f + other.z * delta;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
    
    public Vector3f copy() {
        return new Vector3f(x, y, z);
    }
    
    public org.mtr.mapping.holder.Vector3f asMoj() {
        return new org.mtr.mapping.holder.Vector3f(x, y, z);
    }

    public org.joml.Vector3f asJoml() {
        return new org.joml.Vector3f(x, y, z);
    }
    
    public static float fastInvSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }
    
    // Tiny helper class to avoid importing Mth
    private static class Mth {
        public static float mulAdd(float a, float b, float c) {
             return a * b + c;
        }
    }

    public float distance(Vector3f other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public float distanceSq(Vector3f other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public float length() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }
}