package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class RteSidingPathHelper {

    private static final Field POSITION_FIELD;
    private static final Field ANGLE_FIELD;
    private static final Constructor<?> POSITION_AND_ANGLE_CONSTRUCTOR;
    private static final Constructor<?> CONNECTION_DETAILS_CONSTRUCTOR;

    static {
        try {
            final Class<?> nodeClass = Class.forName("org.mtr.core.path.SidingPathFinder$PositionAndAngle");
            POSITION_FIELD = nodeClass.getDeclaredField("position");
            ANGLE_FIELD = nodeClass.getDeclaredField("angle");
            POSITION_AND_ANGLE_CONSTRUCTOR = nodeClass.getDeclaredConstructor(Position.class, Angle.class);
            POSITION_FIELD.setAccessible(true);
            ANGLE_FIELD.setAccessible(true);
            POSITION_AND_ANGLE_CONSTRUCTOR.setAccessible(true);
            CONNECTION_DETAILS_CONSTRUCTOR = Class.forName("org.mtr.core.path.PathFinder$ConnectionDetails")
                .getDeclaredConstructor(Object.class, long.class, long.class, long.class);
            CONNECTION_DETAILS_CONSTRUCTOR.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RteSidingPathHelper() {
    }

    private static final int VIA_COST_DIVISOR = 16;

    private static String railIdOf(Rail rail) {
        try {
            return rail.getHexId();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ObjectArrayList getConnections(
        TransportMode transportMode,
        Object2ObjectOpenHashMap<Position, Object2ObjectOpenHashMap<Position, Rail>> positionsToRail,
        Object2ObjectOpenHashMap<Position, Rail> runwaysInbound,
        ObjectOpenHashSet<Position> runwaysOutbound,
        Object nodeObject,
        long depotId
    ) {
        final ObjectArrayList connections = new ObjectArrayList<>();
        final Position positionObject = getPosition(nodeObject);
        final Object2ObjectOpenHashMap<Position, Rail> railConnections = positionsToRail.get(positionObject);

        if (railConnections != null) {
            railConnections.forEach((position, rail) -> {
                final Position nodePosition = positionObject;
                final Angle nodeAngle = getAngle(nodeObject);
                final Angle railAngle = rail.getStartAngle(nodePosition);
                final Angle nextAngle = rail.getStartAngle(position);
                final double speedLimit = rail.getSpeedLimitMetersPerMillisecond(nodePosition);
                if (speedLimit > 0 && nextAngle != null && (nodeAngle == null || rail.canTurnBack() || (railAngle != null && Angle.similarFacing(nodeAngle.angleDegrees, railAngle.angleDegrees)))) {
                    final cn.jstxjf_.rte.data.DepotRailRules.Rule rule = depotId == 0
                        ? cn.jstxjf_.rte.data.DepotRailRules.Rule.NONE
                        : cn.jstxjf_.rte.data.DepotRailRules.get(depotId, railIdOf(rail));
                    if (rule == cn.jstxjf_.rte.data.DepotRailRules.Rule.BANNED) {
                        return;
                    }
                    final double length = ((Object) rail) instanceof RailExtraSupplier supplier ? supplier.getLength() : rail.railMath.getLength();
                    final long cost = Math.round(length / speedLimit);
                    connections.add(newConnectionDetails(newNode(position, nextAngle.getOpposite()),
                        rule == cn.jstxjf_.rte.data.DepotRailRules.Rule.VIA ? Math.max(1, cost / VIA_COST_DIVISOR) : cost));
                }
            });
        }

        if (transportMode == TransportMode.AIRPLANE && runwaysOutbound.contains(positionObject)) {
            runwaysInbound.forEach((position, rail) -> connections.add(newConnectionDetails(newNode(position, rail.getStartAngle(position)), 1)));
        }

        return connections;
    }

    private static Object newConnectionDetails(Object node, long cost) {
        try {
            return CONNECTION_DETAILS_CONSTRUCTOR.newInstance(node, cost, 0L, 0L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Position getPosition(Object nodeObject) {
        try {
            return (Position) POSITION_FIELD.get(nodeObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Angle getAngle(Object nodeObject) {
        try {
            return (Angle) ANGLE_FIELD.get(nodeObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object newNode(Position position, Angle angle) {
        try {
            return POSITION_AND_ANGLE_CONSTRUCTOR.newInstance(position, angle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
