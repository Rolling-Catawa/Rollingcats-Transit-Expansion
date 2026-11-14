package cn.jstxjf_.rte.render.train;

import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;

import javax.annotation.Nullable;

public final class RenderVehicleCarFrameState {

    public final VehicleExtension vehicle;
    public final int carNumber;
    @Nullable
    public final Vector3d offsetVector;
    @Nullable
    public final Double offsetRotation;
    @Nullable
    public final PositionAndRotation ridingCarPositionAndRotation;
    @Nullable
    public final Vector3d cameraShakeOffset;
    @Nullable
    public final PositionAndRotation absolutePositionAndRotation;

    public RenderVehicleCarFrameState(VehicleExtension vehicle, int carNumber) {
        this(vehicle, carNumber, null, null, null, null, null);
    }

    public RenderVehicleCarFrameState(VehicleExtension vehicle, int carNumber,
                                      @Nullable Vector3d offsetVector, @Nullable Double offsetRotation,
                                      @Nullable PositionAndRotation ridingCarPositionAndRotation,
                                      @Nullable Vector3d cameraShakeOffset,
                                      @Nullable PositionAndRotation absolutePositionAndRotation) {
        this.vehicle = vehicle;
        this.carNumber = carNumber;
        this.offsetVector = offsetVector;
        this.offsetRotation = offsetRotation;
        this.ridingCarPositionAndRotation = ridingCarPositionAndRotation;
        this.cameraShakeOffset = cameraShakeOffset;
        this.absolutePositionAndRotation = absolutePositionAndRotation;
    }
}
