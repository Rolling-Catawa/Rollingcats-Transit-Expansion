package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.tool.Vector;

import java.util.Locale;

public final class ScriptVector {

    public final double x;
    public final double y;
    public final double z;

    public ScriptVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static ScriptVector of(Vector vector) {
        return vector == null ? null : new ScriptVector(vector.x(), vector.y(), vector.z());
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public ScriptVector add(double x, double y, double z) {
        return of(toVector().add(x, y, z));
    }

    public ScriptVector add(ScriptVector vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public ScriptVector multiply(double x, double y, double z) {
        return of(toVector().multiply(x, y, z));
    }

    public ScriptVector multiply(ScriptVector vector) {
        return multiply(vector.x, vector.y, vector.z);
    }

    public ScriptVector rotateX(double angle) {
        return of(toVector().rotateX(angle));
    }

    public ScriptVector rotateY(double angle) {
        return of(toVector().rotateY(angle));
    }

    public ScriptVector rotateZ(double angle) {
        return of(toVector().rotateZ(angle));
    }

    public double distanceTo(ScriptVector vector) {
        return toVector().distanceTo(vector.toVector());
    }

    public ScriptVector normalize() {
        return of(toVector().normalize());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(%s, %s, %s)", x, y, z);
    }
}
