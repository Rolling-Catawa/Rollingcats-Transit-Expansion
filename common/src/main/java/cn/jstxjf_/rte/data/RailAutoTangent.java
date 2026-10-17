package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;
import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.track.RailTrackPath;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.simulation.Simulator;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.mtr.mod.packet.PacketUpdateData;

import java.util.Map;

public final class RailAutoTangent {

    private static final double MAX_REFINE_RAD = Math.toRadians(15);
    private static final double MIN_REFINE_RAD = Math.toRadians(0.5);
    private static final double MIN_CONTINUATION_SCORE = 0.3;


    public static void apply(Level world, BlockPos posStart, BlockPos posEnd) {
        if (!RTEConfig.enableAutoTangent) {
            return;
        }
        try {
            final Simulator simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(world);
            if (simulator == null) {
                return;
            }
            final Position p1 = cn.jstxjf_.rte.bridge.MtrBridge.position(posStart);
            final Position p2 = cn.jstxjf_.rte.bridge.MtrBridge.position(posEnd);
            final Rail rail = railBetween(simulator, p1, p2);
            if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
                return;
            }

            final double currentRad1 = extra.getExtraAngle1Rad();
            final double currentRad2 = extra.getExtraAngle2Rad();

            final Double geoForward1 = departureRad(rail, p1);
            final Double geoDeparture2 = departureRad(rail, p2);
            final Double geoForwardOut2 = geoDeparture2 == null ? null : wrap(geoDeparture2 + Math.PI);

            final Double delta1 = continuationDelta(simulator, p1, p2, rail, geoForward1, true);
            final Double delta2 = continuationDelta(simulator, p2, p1, rail, geoForwardOut2, false);
            if (delta1 == null && delta2 == null) {
                return;
            }

            final double newRad1 = delta1 != null ? wrap(currentRad1 + delta1) : currentRad1;
            final double newRad2 = delta2 != null ? wrap(currentRad2 + delta2) : currentRad2;

            extra.setExtraAngle1Rad(newRad1);
            extra.setExtraAngle2Rad(newRad2);
            RailAngleExtra.rebuildRailMath(rail, newRad1, newRad2);
            extra.regenerateAutoBezier();

            final Rail reverse = railBetween(simulator, p2, p1);
            if (reverse != null && reverse != rail && ((Object) reverse) instanceof RailExtraSupplier reverseExtra) {
                final double reverseRad1 = wrap(newRad2 + Math.PI);
                final double reverseRad2 = wrap(newRad1 + Math.PI);
                reverseExtra.setExtraAngle1Rad(reverseRad1);
                reverseExtra.setExtraAngle2Rad(reverseRad2);
                RailAngleExtra.rebuildRailMath(reverse, reverseRad1, reverseRad2);
                reverseExtra.regenerateAutoBezier();
            }

            if (!(world instanceof ServerLevel serverLevel)) {
                return;
            }
            cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(serverLevel, rail);
            if (reverse != null && reverse != rail && !reverse.getHexId().equals(rail.getHexId())) {
                cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(serverLevel, reverse);
            }

        } catch (Exception e) {
            RTE.LOGGER.error("[AutoTangent] failed", e);
        }
    }

    private static Double continuationDelta(Simulator simulator, Position node, Position otherEnd, Rail newRail, Double geoForward, boolean isStartEnd) {
        if (geoForward == null) {
            return null;
        }
        final Object2ObjectOpenHashMap<Position, Rail> connections = simulator.positionsToRail.get(node);
        if (connections == null || connections.isEmpty()) {
            return null;
        }

        Double bestDelta = null;
        double bestScore = MIN_CONTINUATION_SCORE;
        for (final Map.Entry<Position, Rail> entry : connections.entrySet()) {
            if (entry.getKey().equals(otherEnd)) {
                continue;
            }
            final Rail neighbor = entry.getValue();
            if (neighbor == null || neighbor == newRail) {
                continue;
            }
            final Double neighborDeparture = departureRad(neighbor, node);
            if (neighborDeparture == null) {
                continue;
            }
            final double desired = isStartEnd ? wrap(neighborDeparture + Math.PI) : neighborDeparture;
            final double score = Math.cos(desired - geoForward);
            if (score > bestScore) {
                bestScore = score;
                bestDelta = wrap(desired - geoForward);
            }
        }
        if (bestDelta == null) {
            return null;
        }
        final double magnitude = Math.abs(bestDelta);
        if (magnitude < MIN_REFINE_RAD || magnitude > MAX_REFINE_RAD) {
            return null;
        }
        return bestDelta;
    }

    static Double departureRad(Rail rail, Position node) {
        try {
            final RailTrackPath path = new RailTrackPath(rail);
            final double length = path.length();
            if (!Double.isFinite(length) || length < 1) {
                return null;
            }
            final Vec3 startPoint = path.sampleByDistance(0).position();
            final Vec3 endPoint = path.sampleByDistance(length).position();
            final double startDistance = horizontalDistanceSquared(startPoint, node);
            final double endDistance = horizontalDistanceSquared(endPoint, node);
            if (Math.min(startDistance, endDistance) > 9) {
                return null;
            }
            final boolean fromStart = startDistance <= endDistance;
            final double step = Math.min(2, length / 2);
            final Vec3 near = fromStart ? startPoint : endPoint;
            final Vec3 inner = fromStart ? path.sampleByDistance(step).position() : path.sampleByDistance(length - step).position();
            final double dx = inner.x - near.x;
            final double dz = inner.z - near.z;
            if (dx * dx + dz * dz < 1e-6) {
                return null;
            }
            return Math.atan2(dz, dx);
        } catch (Exception e) {
            return null;
        }
    }

    private static double horizontalDistanceSquared(Vec3 point, Position node) {
        final double dx = point.x - node.getX();
        final double dz = point.z - node.getZ();
        return dx * dx + dz * dz;
    }

    private static Rail railBetween(Simulator simulator, Position from, Position to) {
        final Object2ObjectOpenHashMap<Position, Rail> connections = simulator.positionsToRail.get(from);
        return connections == null ? null : connections.get(to);
    }

    private static double wrap(double rad) {
        rad = rad % (2 * Math.PI);
        if (rad <= -Math.PI) {
            rad += 2 * Math.PI;
        } else if (rad > Math.PI) {
            rad -= 2 * Math.PI;
        }
        return rad;
    }

    private RailAutoTangent() {
    }
}
