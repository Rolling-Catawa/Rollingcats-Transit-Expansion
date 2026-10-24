package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import org.mtr.core.data.Vehicle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Vehicle.class, remap = false)
public abstract class VehicleExtensionMixin implements TrainExtraSupplier {

    @Unique private Map<String, String> rte$customConfigs = new HashMap<>();
    @Unique private Map<String, ConfigResponder> rte$configResponders = new HashMap<>();
    @Unique private boolean rte$isConfigsChanged = false;

    @Override
    public Map<String, String> getCustomConfigs() {
        return rte$customConfigs;
    }

    @Override
    public void setCustomConfigs(Map<String, String> customConfigs) {
        this.rte$customConfigs = customConfigs;
    }

    @Override
    public boolean isConfigsChanged() {
        return rte$isConfigsChanged;
    }

    @Override
    public void isConfigsChanged(boolean isConfigsChanged) {
        this.rte$isConfigsChanged = isConfigsChanged;
    }

    @Override
    public Map<String, ConfigResponder> getConfigResponders() {
        return rte$configResponders;
    }

    @Override
    public void setConfigResponders(Map<String, ConfigResponder> configResponders) {
        this.rte$configResponders = configResponders;
    }

    @Override
    public boolean getVehicleReversed() {
        return ((VehicleAccessor) (Object) this).getIsReversed();
    }

    @Override
    public double getVehicleRailProgress() {
        return ((VehicleAccessor) (Object) this).getRailProgress();
    }

    @Override
    public double getVehicleSpeed() {
        return ((VehicleAccessor) (Object) this).getSpeed();
    }

    @Override
    public long getManualToAutomaticTime() {
        Vehicle vehicle = (Vehicle)(Object)this;
        if (vehicle.vehicleExtraData == null) return 0;
        return ((VehicleExtraDataAccessor) vehicle.vehicleExtraData).invokeGetManualToAutomaticTime();
    }

    @Override
    public float getRollAngleAt(double value) {
        Vehicle vehicle = (Vehicle)(Object)this;
        if (!Double.isFinite(value)) return 0;
        if (vehicle.vehicleExtraData == null) return 0;
        if (vehicle.vehicleExtraData.immutablePath.isEmpty()) return 0;

        int pathIndex = vehicle.vehicleExtraData.immutablePath.size() - 1;
        for (int i = 0; i < vehicle.vehicleExtraData.immutablePath.size(); i++) {
            if (value <= vehicle.vehicleExtraData.immutablePath.get(i).getEndDistance()) {
                pathIndex = i;
                break;
            }
        }

        org.mtr.core.data.PathData pathData = vehicle.vehicleExtraData.immutablePath.get(pathIndex);
        final double segmentLength = Math.max(0, pathData.getEndDistance() - pathData.getStartDistance());
        final double distanceInSegment = Math.max(0, Math.min(value - pathData.getStartDistance(), segmentLength));
        final float result = RailExtraSupplier.getRollAngleForPath(pathData.getRail(), distanceInSegment, pathData.reversePositions);
        return Float.isFinite(result) ? result : 0;
    }
}
