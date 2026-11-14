package cn.jstxjf_.rte.render.train;

import org.mtr.mod.resource.VehicleResource;

public final class VehicleBuildContext {

    private static final ThreadLocal<VehicleBuildContext> CURRENT = new ThreadLocal<>();

    public final VehicleResource vehicleResource;
    public final int carNumber;
    public final int totalCars;
    private int nextSlot;

    private VehicleBuildContext(VehicleResource vehicleResource, int carNumber, int totalCars) {
        this.vehicleResource = vehicleResource;
        this.carNumber = carNumber;
        this.totalCars = totalCars;
    }

    public static void begin(VehicleResource vehicleResource, int carNumber, int totalCars) {
        CURRENT.set(new VehicleBuildContext(vehicleResource, carNumber, totalCars));
    }

    public static void end() {
        CURRENT.remove();
    }

    public static Slot claimSlot() {
        final VehicleBuildContext context = CURRENT.get();
        if (context == null) {
            return null;
        }
        return new Slot(context.vehicleResource, context.carNumber, context.totalCars, context.nextSlot++);
    }

    public record Slot(VehicleResource vehicleResource, int carNumber, int totalCars, int index) {
    }
}
