package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.track.RailTrackPath;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.mtr.mod.Init;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RailChainSmoother {

    private static final int MAX_NODES = 64;
    private static final double KINK_THRESHOLD_RAD = Math.toRadians(5);

    public record ChainReport(int nodeCount, boolean ring, List<Position> kinkNodes, Double minRadiusMeters, double maxGradePermille) {
    }

    static List<Position> walkChain(MinecraftClientData data, Position startNode, Position endNode) {
        final List<Position> manual = RailChainSelection.resolve(startNode, endNode);
        if (manual != null) {
            return manual;
        }
        return walkChainAuto(data, startNode, endNode);
    }

    public static List<Position> walkChainAuto(MinecraftClientData data, Position startNode, Position endNode) {
        final List<Position> chain = new ArrayList<>();
        chain.add(startNode);
        chain.add(endNode);
        extend(data, chain, true);
        if (!chain.get(0).equals(chain.get(chain.size() - 1))) {
            extend(data, chain, false);
        }
        return chain;
    }

    private static void extend(MinecraftClientData data, List<Position> chain, boolean forward) {
        while (chain.size() <= MAX_NODES) {
            final Position tip = forward ? chain.get(chain.size() - 1) : chain.get(0);
            final Position prev = forward ? chain.get(chain.size() - 2) : chain.get(1);
            final Object2ObjectOpenHashMap<Position, Rail> connections = data.positionsToRail.get(tip);
            if (connections == null || connections.size() != 2) {
                return;
            }
            Position next = null;
            for (final Position candidate : connections.keySet()) {
                if (!candidate.equals(prev)) {
                    next = candidate;
                }
            }
            if (next == null) {
                return;
            }
            if (next.equals(forward ? chain.get(0) : chain.get(chain.size() - 1))) {
                if (forward) {
                    chain.add(next);
                }
                return;
            }
            if (forward) {
                chain.add(next);
            } else {
                chain.add(0, next);
            }
        }
    }

    private static double horizontalDistanceSquared(Vec3 point, Position node) {
        final double dx = point.x - node.getX();
        final double dz = point.z - node.getZ();
        return dx * dx + dz * dz;
    }

    static Rail railBetween(MinecraftClientData data, Position from, Position to) {
        final Object2ObjectOpenHashMap<Position, Rail> connections = data.positionsToRail.get(from);
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

    private RailChainSmoother() {
    }
}
