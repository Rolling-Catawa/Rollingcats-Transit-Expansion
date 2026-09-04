package cn.jstxjf_.rte.data;

import org.mtr.core.data.Vehicle;

import java.util.Map;

public interface TrainExtraSupplier {

    Map<String, String> getCustomConfigs();

    void setCustomConfigs(Map<String, String> customConfigs);

    boolean isConfigsChanged();

    void isConfigsChanged(boolean isConfigsChanged);

    Map<String, ConfigResponder> getConfigResponders();

    void setConfigResponders(Map<String, ConfigResponder> configResponders);

    float getRollAngleAt(double rv);

    boolean getVehicleReversed();
    double getVehicleRailProgress();
    double getVehicleSpeed();
    long getManualToAutomaticTime();

    static float getRollAngleAt(Vehicle train, int car) {
        TrainExtraSupplier supplier = (TrainExtraSupplier) train;
        boolean b = supplier.getVehicleReversed();
        int cars = train.vehicleExtraData.immutableVehicleCars.size();
        if (cars == 0 || car < 0 || car >= cars) return 0;
        double spacing = train.vehicleExtraData.immutableVehicleCars.get(0).getLength();
        if (!Double.isFinite(spacing) || spacing <= 0) return 0;
        int c1 = (b ? cars - car : car);
        car += 1;
        int c2 = (b ? cars - car : car);
        double p = supplier.getVehicleRailProgress();
        if (!Double.isFinite(p)) return 0;
        double p1 = p - c1 * spacing;
        double p2 = p - c2 * spacing;
        final float roll1 = supplier.getRollAngleAt(p1);
        final float roll2 = supplier.getRollAngleAt(p2);
        if (!Float.isFinite(roll1) || !Float.isFinite(roll2)) return 0;
        final float result = (roll1 + roll2) / 2;
        return Float.isFinite(result) ? result : 0;
    }
}
