package cn.jstxjf_.rte.mixin;

import org.mtr.mod.client.VehicleRidingMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleRidingMovement.class, remap = false)
public interface VehicleRidingMovementAccessor {

    @Accessor(value = "ridingVehicleId", remap = false)
    static long rte$getRidingVehicleId() {
        throw new AssertionError();
    }

    @Accessor(value = "ridingVehicleCarNumber", remap = false)
    static int rte$getRidingVehicleCarNumber() {
        throw new AssertionError();
    }

    @Accessor(value = "ridingVehicleX", remap = false)
    static double rte$getRidingVehicleX() {
        throw new AssertionError();
    }

    @Accessor(value = "ridingVehicleY", remap = false)
    static double rte$getRidingVehicleY() {
        throw new AssertionError();
    }

    @Accessor(value = "ridingVehicleZ", remap = false)
    static double rte$getRidingVehicleZ() {
        throw new AssertionError();
    }
}
