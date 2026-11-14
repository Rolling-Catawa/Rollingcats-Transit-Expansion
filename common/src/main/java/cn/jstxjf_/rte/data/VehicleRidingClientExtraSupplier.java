package cn.jstxjf_.rte.data;

import net.minecraft.world.phys.Vec3;

public interface VehicleRidingClientExtraSupplier {
    float getRoll(int index);
    void setRoll(float[] rolls);
    void setPositions(org.mtr.mapping.holder.Vector3d[] positions);
    void setReversed(boolean reversed);
}
