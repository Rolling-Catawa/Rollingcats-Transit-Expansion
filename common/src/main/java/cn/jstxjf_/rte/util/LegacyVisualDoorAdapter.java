package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.data.GangwayPolicy;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectDoubleImmutablePair;
import org.mtr.mapping.holder.Box;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.resource.ModelProperties;

public final class LegacyVisualDoorAdapter {

    public static final Box LEFT = new Box(-0.125, 0, 0, -0.0625, 0.0625, 0.0625);
    public static final Box RIGHT = new Box(0.0625, 0, 0, 0.125, 0.0625, 0.0625);
    private static final java.util.Map<String, Boolean> MANAGED = new java.util.concurrent.ConcurrentHashMap<>();

    private LegacyVisualDoorAdapter() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void prepare(ModelProperties properties, VehicleExtension vehicle, int car,
                               ObjectArrayList openDoorways) {
        if (properties == null || vehicle == null || openDoorways == null) {
            return;
        }
        try {
            if (!managesDoors(properties, vehicle, car)) {
                return;
            }
            openDoorways.removeIf(entry -> !(entry instanceof ObjectDoubleImmutablePair<?> pair)
                || !LEFT.equals(pair.left()) && !RIGHT.equals(pair.left()));
            if (vehicle.persistentVehicleData.getDoorValue() <= 0) {
                return;
            }
            final org.mtr.mod.render.PositionAndRotation position =
                ScriptedTrainRenderers.getCarPositionAndRotation(vehicle, car);
            if (position == null) {
                return;
            }
            final boolean[] needed = neededSides(properties);
            final boolean[] sides = ScriptedTrainRenderers.doorSidesOf(vehicle, car, position);
            if (needed[0] && sides[0] && !contains(openDoorways, LEFT)) {
                openDoorways.add(new ObjectDoubleImmutablePair<>(LEFT, 0));
            }
            if (needed[1] && sides[1] && !contains(openDoorways, RIGHT)) {
                openDoorways.add(new ObjectDoubleImmutablePair<>(RIGHT, 0));
            }
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyDoorSide] render marker injection failed", e);
        }
    }

    public static boolean managesDoors(ModelProperties properties, VehicleExtension vehicle, int car) {
        if (properties == null || vehicle == null) {
            return false;
        }
        final String vehicleId = vehicle.vehicleExtraData.immutableVehicleCars.get(car).getVehicleId();
        if (!GangwayPolicy.isLegacyFormat(vehicleId)) {
            return false;
        }
        final Boolean known = MANAGED.get(vehicleId);
        if (known != null) {
            return known;
        }
        final boolean[] needed = neededSides(properties);
        final boolean managed = needed[0] || needed[1];
        MANAGED.put(vehicleId, managed);
        return managed;
    }

    public static boolean isManaged(String vehicleId) {
        return Boolean.TRUE.equals(MANAGED.get(vehicleId));
    }

    private static boolean[] neededSides(ModelProperties properties) {
        final boolean[] needed = {false, false};
        properties.iterateParts(part -> {
            if ((Object) part instanceof LegacyDoorMetadata metadata) {
                needed[0] |= "LEFT".equals(metadata.rte$getLegacyDoorSide());
                needed[1] |= "RIGHT".equals(metadata.rte$getLegacyDoorSide());
            }
        });
        return needed;
    }

    private static boolean contains(ObjectArrayList<?> values, Box marker) {
        for (final Object value : values) {
            if (value instanceof ObjectDoubleImmutablePair<?> pair && marker.equals(pair.left())) {
                return true;
            }
        }
        return false;
    }
}
