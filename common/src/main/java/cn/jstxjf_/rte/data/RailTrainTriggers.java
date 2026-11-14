package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mechanism.MechanismVehicles;
import cn.jstxjf_.rte.mixin.SidingAccessor;
import cn.jstxjf_.rte.mixin.VehicleAccessor;
import net.minecraft.server.level.ServerLevel;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RailTrainTriggers {

    public static final String PREFIX = "train.";

    private static final Map<String, Map<Long, String>> LAST_RAIL = new ConcurrentHashMap<>();

    private RailTrainTriggers() {
    }

    public static void tick(ServerLevel level) {
        try {
            final Simulator simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(level);
            if (simulator == null) {
                return;
            }
            final Map<Long, String> lastRail = LAST_RAIL.computeIfAbsent(level.dimension().location().toString(), ignored -> new HashMap<>());
            final Set<Long> alive = new HashSet<>();
            for (final Siding siding : simulator.sidings) {
                for (final Vehicle vehicle : cn.jstxjf_.rte.compat.SidingCompat.vehicles(siding)) {
                    alive.add(vehicle.getId());
                    final Rail rail = headRail(vehicle);
                    if (rail == null) {
                        continue;
                    }
                    final String railId = rail.getHexId();
                    if (!railId.equals(lastRail.put(vehicle.getId(), railId))) {
                        apply(vehicle, rail);
                    }
                }
            }
            lastRail.keySet().retainAll(alive);
        } catch (Exception e) {
            RTE.LOGGER.debug("[RailTrigger] tick failed", e);
        }
    }

    public static boolean isTriggerKey(String key) {
        return key != null && key.length() > PREFIX.length() && key.startsWith(PREFIX);
    }

    private static Rail headRail(Vehicle vehicle) {
        final List<PathData> path = vehicle.vehicleExtraData.immutablePath;
        if (path.isEmpty()) {
            return null;
        }
        final double progress = ((VehicleAccessor) (Object) vehicle).getRailProgress();
        final PathData data = path.get(Math.min(path.size() - 1, MechanismVehicles.indexAt(path, progress)));
        return data == null ? null : data.getRail();
    }

    private static void apply(Vehicle vehicle, Rail rail) {
        if (!((Object) rail instanceof RailExtraSupplier railExtra) || !(vehicle instanceof TrainExtraSupplier train)) {
            return;
        }
        final Map<String, String> railConfigs = railExtra.getCustomConfigs();
        if (railConfigs == null || railConfigs.isEmpty()) {
            return;
        }
        final Map<String, String> trainConfigs = new HashMap<>(train.getCustomConfigs());
        boolean changed = false;
        for (final Map.Entry<String, String> entry : railConfigs.entrySet()) {
            if (!isTriggerKey(entry.getKey())) {
                continue;
            }
            final String key = entry.getKey().substring(PREFIX.length());
            final String value = entry.getValue() == null ? "" : entry.getValue();
            if (value.isEmpty()) {
                changed |= trainConfigs.remove(key) != null;
            } else {
                changed |= !value.equals(trainConfigs.put(key, value));
            }
        }
        if (changed) {
            train.setCustomConfigs(trainConfigs);
            train.isConfigsChanged(true);
        }
    }
}
