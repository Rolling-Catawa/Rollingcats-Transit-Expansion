package cn.jstxjf_.rte.bridge;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;

public final class MtrTypes {

    private MtrTypes() {
    }

    public static Vec3 vec(Vector3d vector) {
        return vector == null ? null : vector.data;
    }

    public static Vector3d vector3d(Vec3 vector) {
        return vector == null ? null : new Vector3d(vector);
    }

    public static net.minecraft.core.BlockPos blockPos(org.mtr.mapping.holder.BlockPos pos) {
        return pos == null ? null : pos.data;
    }

    public static org.mtr.mapping.holder.BlockPos blockPos(net.minecraft.core.BlockPos pos) {
        return pos == null ? null : new org.mtr.mapping.holder.BlockPos(pos);
    }

    public static Level level(World world) {
        return world == null ? null : world.data;
    }

    public static World world(Level level) {
        return level == null ? null : new World(level);
    }
}
