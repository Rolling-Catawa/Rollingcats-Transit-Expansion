package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.tool.Vector;

import java.util.Locale;

public final class ScriptVector extends Vector {

    public ScriptVector(double x, double y, double z) {
        super(x, y, z);
    }

    public static ScriptVector of(Vector vector) {
        return vector == null ? null
            : vector instanceof ScriptVector ? (ScriptVector) vector
            : new ScriptVector(vector.x(), vector.y(), vector.z());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(%s, %s, %s)", x, y, z);
    }
}
