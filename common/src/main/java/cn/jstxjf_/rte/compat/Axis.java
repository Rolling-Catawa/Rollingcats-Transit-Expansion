package cn.jstxjf_.rte.compat;

//#if MC<11903
import com.mojang.math.Vector3f;

public final class Axis {

    public static final Vector3f XN = Vector3f.XN;
    public static final Vector3f XP = Vector3f.XP;
    public static final Vector3f YN = Vector3f.YN;
    public static final Vector3f YP = Vector3f.YP;
    public static final Vector3f ZN = Vector3f.ZN;
    public static final Vector3f ZP = Vector3f.ZP;

    private Axis() {
    }
}
//#endif
