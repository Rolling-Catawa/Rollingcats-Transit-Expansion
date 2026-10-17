package cn.jstxjf_.rte.bridge;

import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.Vector3d;

public final class MtrTypes {

    private MtrTypes() {
    }

    public static Vec3 vec(Vector3d vector) {
        return vector == null ? null : vector.data;
    }

    public static Vector3d vector3d(Vec3 vector) {
        return vector == null ? null : new Vector3d(vector);
    }
}
