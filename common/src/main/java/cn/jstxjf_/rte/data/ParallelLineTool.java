package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.network.PacketPlaceParallelNodes;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.track.RailTrackPath;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mod.Init;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParallelLineTool {

    public static int copyParallel(BlockPos startBlockPos, BlockPos endBlockPos, double spacing, boolean rightSide) {
        return copyParallel(startBlockPos, endBlockPos, spacing, rightSide, false);
    }

    public static int copyParallel(BlockPos startBlockPos, BlockPos endBlockPos, double spacing, boolean rightSide, boolean mirrorOneWay) {
        final MinecraftClientData data = MinecraftClientData.getInstance();
        final Position startNode = Init.blockPosToPosition(startBlockPos);
        final Position endNode = Init.blockPosToPosition(endBlockPos);
        final List<Position> manualChain = RailChainSelection.resolve(startNode, endNode);
        List<Position> chain;
        if (manualChain != null && manualChain.size() >= 2) {
            chain = manualChain;
        } else {
            chain = findPath(data, startNode, endNode);
            if (chain == null) {
                chain = RailChainSmoother.walkChain(data, startNode, endNode);
            } else {
                extendOutwards(data, chain);
            }
        }
        if (chain.size() < 2) {
            return 0;
        }
        for (int i = 0; i < chain.size(); i++) {
            final Position n = chain.get(i);
            final var conn = data.positionsToRail.get(n);
        }
        final boolean ring = chain.size() > 3 && chain.get(0).equals(chain.get(chain.size() - 1));
        final int nodeCount = ring ? chain.size() - 1 : chain.size();
        final double side = rightSide ? 1 : -1;

        final Map<Position, double[]> rights = new LinkedHashMap<>();
        final Map<Position, Position> offsetNodes = new LinkedHashMap<>();
        final List<long[]> nodePlacements = new ArrayList<>();
        double[] lastRight = null;
        long[] lastDelta = null;
        for (int i = 0; i < nodeCount; i++) {
            final Position node = chain.get(i);
            final Position prev = i == 0 ? (ring ? chain.get(nodeCount - 1) : node) : chain.get(i - 1);
            final Position next = i == nodeCount - 1 && !ring ? node : chain.get((i + 1) % chain.size() == 0 ? 1 : i + 1);
            double tx;
            double tz;
            final double[] fwd = node.equals(next) ? null : tangentAtNode(data, node, next);
            final double[] bwd = node.equals(prev) ? null : tangentAtNode(data, node, prev);
            if (fwd != null && bwd != null) {
                tx = fwd[0] - bwd[0];
                tz = fwd[1] - bwd[1];
            } else if (fwd != null) {
                tx = fwd[0];
                tz = fwd[1];
            } else if (bwd != null) {
                tx = -bwd[0];
                tz = -bwd[1];
            } else {
                tx = next.getX() - prev.getX();
                tz = next.getZ() - prev.getZ();
            }
            double horizontal = Math.sqrt(tx * tx + tz * tz);
            if (horizontal < 1e-6) {
                tx = next.getX() - prev.getX();
                tz = next.getZ() - prev.getZ();
                horizontal = Math.sqrt(tx * tx + tz * tz);
            }
            if (horizontal < 1e-6) {
                continue;
            }
            tx /= horizontal;
            tz /= horizontal;
            final double rx = -tz * side;
            final double rz = tx * side;
            rights.put(node, new double[]{rx, rz});

            final double exactX = node.getX() + rx * spacing;
            final double exactZ = node.getZ() + rz * spacing;
            long offX;
            long offZ;
            if (lastRight != null && Math.abs(rx - lastRight[0]) < 1e-4 && Math.abs(rz - lastRight[1]) < 1e-4
                && lastDelta != null) {
                offX = node.getX() + lastDelta[0];
                offZ = node.getZ() + lastDelta[1];
            } else {
                offX = Math.round(exactX);
                offZ = Math.round(exactZ);
                double bestError = Double.MAX_VALUE;
                for (final long cx : new long[]{(long) Math.floor(exactX), (long) Math.ceil(exactX)}) {
                    for (final long cz : new long[]{(long) Math.floor(exactZ), (long) Math.ceil(exactZ)}) {
                        final double vx = cx - node.getX();
                        final double vz = cz - node.getZ();
                        final double dist = Math.sqrt(vx * vx + vz * vz);
                        if (dist < 1e-6) {
                            continue;
                        }
                        final double distError = Math.abs(dist - spacing);
                        final double dirError = 1 - (vx * rx + vz * rz) / dist;
                        final double error = distError + dirError * spacing * 4;
                        if (error < bestError) {
                            bestError = error;
                            offX = cx;
                            offZ = cz;
                        }
                    }
                }
                lastDelta = new long[]{offX - node.getX(), offZ - node.getZ()};
            }
            lastRight = new double[]{rx, rz};
            final Position offsetPosition = new Position(offX, node.getY(), offZ);
            offsetNodes.put(node, offsetPosition);
            nodePlacements.add(new long[]{
                new BlockPos((int) offX, (int) node.getY(), (int) offZ).asLong(),
                Init.positionToBlockPos(node).asLong()
            });
        }
        if (offsetNodes.size() < 2) {
            return 0;
        }
        PacketPlaceParallelNodes.sendPlaceC2S(nodePlacements);

        for (final Map.Entry<Position, Position> e : offsetNodes.entrySet()) {
            final double d = Math.hypot(e.getValue().getX() - e.getKey().getX(),
                e.getValue().getZ() - e.getKey().getZ());
        }
        int created = 0;
        for (int i = 0; i < chain.size() - 1; i++) {
            final boolean ok = createOffsetSegment(data, chain.get(i), chain.get(i + 1), rights, offsetNodes, spacing, side, mirrorOneWay);
            if (ok) {
                created++;
            }
        }
        return created;
    }

    private static void extendOutwards(MinecraftClientData data, List<Position> chain) {
        for (int direction = 0; direction < 2; direction++) {
            final boolean forward = direction == 0;
            while (chain.size() < 512) {
                final Position tip = forward ? chain.get(chain.size() - 1) : chain.get(0);
                final Position inner = forward ? chain.get(chain.size() - 2) : chain.get(1);
                final var connections = data.positionsToRail.get(tip);
                if (connections == null || connections.size() != 2) {
                    break;
                }
                Position next = null;
                for (final Position candidate : connections.keySet()) {
                    if (!candidate.equals(inner)) {
                        next = candidate;
                    }
                }
                if (next == null || chain.contains(next)) {
                    break;
                }
                if (forward) {
                    chain.add(next);
                } else {
                    chain.add(0, next);
                }
            }
        }
    }

    private static List<Position> findPath(MinecraftClientData data, Position start, Position end) {
        if (start.equals(end)) {
            return null;
        }
        final Map<Position, Position> cameFrom = new LinkedHashMap<>();
        final java.util.ArrayDeque<Position> queue = new java.util.ArrayDeque<>();
        queue.add(start);
        cameFrom.put(start, start);
        int visited = 0;
        while (!queue.isEmpty() && visited < 4096) {
            final Position current = queue.poll();
            visited++;
            if (current.equals(end)) {
                final List<Position> path = new ArrayList<>();
                Position node = end;
                while (!node.equals(start)) {
                    path.add(0, node);
                    node = cameFrom.get(node);
                    if (node == null) {
                        return null;
                    }
                }
                path.add(0, start);
                return path.size() >= 2 ? path : null;
            }
            final var connections = data.positionsToRail.get(current);
            if (connections == null) {
                continue;
            }
            for (final Position neighbour : connections.keySet()) {
                if (!cameFrom.containsKey(neighbour)) {
                    cameFrom.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }
        return null;
    }

    private static double[] tangentAtNode(MinecraftClientData data, Position node, Position neighbour) {
        try {
            final Rail rail = RailChainSmoother.railBetween(data, node, neighbour);
            if (rail != null && ((Object) rail) instanceof RailExtraSupplier extra) {
                final RailTrackPath path = new RailTrackPath(rail);
                final double len = path.length();
                if (Double.isFinite(len) && len > 0.5) {
                    final Vec3 a = path.sampleByDistance(0).position();
                    final Vec3 b = path.sampleByDistance(len).position();
                    final double probe = Math.min(2, len / 4);
                    final boolean startIsNode =
                        sq(a.x - node.getX(), a.z - node.getZ()) <= sq(b.x - node.getX(), b.z - node.getZ());
                    final Vec3 p0 = startIsNode ? a : b;
                    final Vec3 p1 = path.sampleByDistance(startIsNode ? probe : len - probe).position();
                    final double dx = p1.x - p0.x;
                    final double dz = p1.z - p0.z;
                    final double h = Math.sqrt(dx * dx + dz * dz);
                    if (h > 1e-6) {
                        return new double[]{dx / h, dz / h};
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static double sq(double a, double b) {
        return a * a + b * b;
    }

    private static boolean createOffsetSegment(MinecraftClientData data, Position nodeA, Position nodeB,
                                               Map<Position, double[]> rights, Map<Position, Position> offsetNodes,
                                               double spacing, double side, boolean mirrorOneWay) {
        final Rail source = RailChainSmoother.railBetween(data, nodeA, nodeB);
        if (source == null || !(((Object) source) instanceof RailExtraSupplier extra)) {
            return false;
        }
        if (source.getTransportMode() != org.mtr.core.data.TransportMode.TRAIN) {
            return false;
        }
        final Position pos1 = extra.getExtraPosition1();
        final Position pos2 = extra.getExtraPosition2();
        if (pos1 == null || pos2 == null) {
            return false;
        }
        final Position firstNode = pos1.equals(nodeA) || pos1.equals(nodeB) ? pos1 : null;
        final Position secondNode = pos2.equals(nodeA) || pos2.equals(nodeB) ? pos2 : null;
        if (firstNode == null || secondNode == null) {
            return false;
        }
        final Position offsetFirst = offsetNodes.get(firstNode);
        final Position offsetSecond = offsetNodes.get(secondNode);
        final double[] rightFirst = rights.get(firstNode);
        final double[] rightSecond = rights.get(secondNode);
        if (offsetFirst == null || offsetSecond == null || rightFirst == null || rightSecond == null) {
            return false;
        }

        final RailTrackPath path = new RailTrackPath(source);
        final double length = path.length();
        if (!Double.isFinite(length) || length < 1) {
            return false;
        }
        final Vec3 sample0 = path.sampleByDistance(0).position();
        final Vec3 sampleEnd = path.sampleByDistance(length).position();
        final boolean startsAtFirst = distanceSquared(sample0, firstNode) <= distanceSquared(sampleEnd, firstNode);
        final Vec3 startPoint = startsAtFirst ? sample0 : sampleEnd;
        final Vec3 endPoint = startsAtFirst ? sampleEnd : sample0;

        final double step = Math.min(2, length / 2);
        final Vec3 startInner = path.sampleByDistance(startsAtFirst ? step : length - step).position();
        final Vec3 endInner = path.sampleByDistance(startsAtFirst ? length - step : step).position();
        final double[] depStart = horizontalDirection(startPoint, startInner);
        final double[] depEnd = horizontalDirection(endInner, endPoint);
        if (depStart == null || depEnd == null) {
            return false;
        }

        final Vec3 offStart = new Vec3(offsetFirst.getX(), offsetFirst.getY(), offsetFirst.getZ());
        final Vec3 offEnd = new Vec3(offsetSecond.getX(), offsetSecond.getY(), offsetSecond.getZ());
        final double chordX = offEnd.x - offStart.x;
        final double chordZ = offEnd.z - offStart.z;
        final double chordLength = Math.sqrt(chordX * chordX + chordZ * chordZ);
        if (chordLength < 1) {
            return false;
        }

        final BezierCurve sourceBezier = extra.getBezierCurve();
        final boolean sourceIsCurved = sourceBezier != null;

        final double angleStart;
        final double angleEnd;
        final BezierCurve curve;

        if (!sourceIsCurved) {
            final double srcA1 = extra.getExtraAngle1Rad();
            final double srcA2 = extra.getExtraAngle2Rad();
            final boolean sameOrder = pos1.equals(firstNode);
            double a1 = sameOrder ? srcA1 : srcA2;
            double a2 = sameOrder ? srcA2 : srcA1;
            if (!Double.isFinite(a1) || !Double.isFinite(a2)) {
                a1 = Math.atan2(chordZ, chordX);
                a2 = Math.atan2(-chordZ, -chordX);
            }
            angleStart = a1;
            angleEnd = a2;
            curve = null;
        } else {
            final List<Vec3> src = sourceBezier.getPoints();
            if (src == null || src.size() < 2) {
                return false;
            }
            final Vec3 srcFirst = src.get(0);
            final Vec3 srcLast = src.get(src.size() - 1);
            final boolean bezierStartsAtFirst =
                sq(srcFirst.x - offStart.x, srcFirst.z - offStart.z)
                    <= sq(srcLast.x - offStart.x, srcLast.z - offStart.z);
            final double shiftFirstX = offStart.x - (bezierStartsAtFirst ? srcFirst.x : srcLast.x);
            final double shiftFirstZ = offStart.z - (bezierStartsAtFirst ? srcFirst.z : srcLast.z);
            final double shiftLastX = offEnd.x - (bezierStartsAtFirst ? srcLast.x : srcFirst.x);
            final double shiftLastZ = offEnd.z - (bezierStartsAtFirst ? srcLast.z : srcFirst.z);
            final List<Vec3> controls = new ArrayList<>(src.size());
            final int last = src.size() - 1;
            for (int i = 0; i <= last; i++) {
                final Vec3 point = src.get(bezierStartsAtFirst ? i : last - i);
                final double t = last == 0 ? 0 : (double) i / last;
                controls.add(new Vec3(
                    point.x + shiftFirstX + (shiftLastX - shiftFirstX) * t,
                    point.y,
                    point.z + shiftFirstZ + (shiftLastZ - shiftFirstZ) * t));
            }
            curve = new BezierCurve(0.1, controls);
            if (!Double.isFinite(curve.getLength()) || curve.getLength() < 1) {
                return false;
            }
            final Vec3 headNext = controls.get(1);
            final Vec3 tailPrev = controls.get(controls.size() - 2);
            angleStart = Math.atan2(headNext.z - offStart.z, headNext.x - offStart.x);
            angleEnd = Math.atan2(tailPrev.z - offEnd.z, tailPrev.x - offEnd.x);
        }

        final Rail[] rebuiltHolder = new Rail[1];
        RailAngleExtra.withAngles(angleStart, angleEnd, (a1, a2) ->
            rebuiltHolder[0] = RailRebuildHelper.rebuildWithEndpoints(source, offsetFirst, a1, offsetSecond, a2));
        final Rail rebuilt = rebuiltHolder[0];
        if (rebuilt == null || rebuilt == source || !(((Object) rebuilt) instanceof RailExtraSupplier rebuiltExtra)) {
            return false;
        }
        rebuiltExtra.setExtraAngle1Rad(angleStart);
        rebuiltExtra.setExtraAngle2Rad(angleEnd);
        if (curve == null) {
            rebuiltExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_ORIGINAL);
            RailAngleExtra.rebuildRailMath(rebuilt, angleStart, angleEnd);
        } else {
            rebuiltExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_MANUAL_BEZIER);
            rebuiltExtra.setBezier(curve);
            RailAngleExtra.rebuildRailMathFromBezier(rebuilt, curve);
        }

        if (mirrorOneWay) {
            final cn.jstxjf_.rte.mixin.RailAccessor acc = (cn.jstxjf_.rte.mixin.RailAccessor) (Object) rebuilt;
            final long s1 = acc.rte$getSpeedLimit1();
            final long s2 = acc.rte$getSpeedLimit2();
            if ((s1 <= 0) != (s2 <= 0)) {
                acc.rte$setSpeedLimit1(s2);
                acc.rte$setSpeedLimit2(s1);
                rebuiltExtra.setRenderReversed(!rebuiltExtra.getRenderReversed());
            }
        }

        try {
            final RailTrackPath newPath = new RailTrackPath(rebuilt);
            final double nLen = newPath.length();
            final StringBuilder gaps = new StringBuilder();
            for (final double f : new double[]{0, 0.25, 0.5, 0.75, 1}) {
                final Vec3 a = path.sampleByDistance(length * f).position();
                final Vec3 b = newPath.sampleByDistance(nLen * f).position();
                gaps.append(String.format(java.util.Locale.ROOT, "%.2f ",
                    Math.hypot(b.x - a.x, b.z - a.z)));
            }
            final StringBuilder renderGaps = new StringBuilder();
            for (final double f : new double[]{0, 0.25, 0.5, 0.75, 1}) {
                final org.mtr.mapping.holder.Vector3d ra = extra.getPosition(length * f);
                final org.mtr.mapping.holder.Vector3d rb = rebuiltExtra.getPosition(
                    rebuiltExtra.getLength() * f);
                renderGaps.append(String.format(java.util.Locale.ROOT, "%.2f ",
                    Math.hypot(rb.getXMapped() - ra.getXMapped(), rb.getZMapped() - ra.getZMapped())));
            }
        } catch (Exception ignored) {
        }

        PacketUpdateRail.sendUpdateC2S(rebuilt, rebuilt);
        return true;
    }

    private static boolean isPathCurved(RailTrackPath path, double length) {
        try {
            final Vec3 a = path.sampleByDistance(0).position();
            final Vec3 b = path.sampleByDistance(length / 2).position();
            final Vec3 c = path.sampleByDistance(length).position();
            final double chord = Math.hypot(c.x - a.x, c.z - a.z);
            if (chord < 1e-6) {
                return true;
            }
            final double cross = Math.abs((b.x - a.x) * (c.z - a.z) - (b.z - a.z) * (c.x - a.x));
            return cross / chord > 0.25;
        } catch (Exception e) {
            return true;
        }
    }

    private static double solveControlArm(RailTrackPath path, double length, boolean startsAtFirst,
                                          Vec3 offStart, Vec3 offEnd,
                                          double[] depStart, double[] depEnd, double spacing, double side) {
        final double fallback = Math.hypot(offEnd.x - offStart.x, offEnd.z - offStart.z) / 3;
        try {
            final double mid = length / 2;
            final Vec3 pm = path.sampleByDistance(mid).position();
            final double probe = Math.min(0.5, length / 8);
            final Vec3 pa = path.sampleByDistance(Math.max(0, mid - probe)).position();
            final Vec3 pb = path.sampleByDistance(Math.min(length, mid + probe)).position();
            double tx = pb.x - pa.x;
            double tz = pb.z - pa.z;
            final double h = Math.hypot(tx, tz);
            if (h < 1e-6) {
                return fallback;
            }
            tx /= h;
            tz /= h;
            final double dir = startsAtFirst ? 1 : -1;
            final double targetX = pm.x - tz * side * spacing * dir;
            final double targetZ = pm.z + tx * side * spacing * dir;

            final double baseX = (offStart.x + offEnd.x) / 2;
            final double baseZ = (offStart.z + offEnd.z) / 2;
            final double vx = depStart[0] - depEnd[0];
            final double vz = depStart[1] - depEnd[1];
            final double vLenSq = vx * vx + vz * vz;
            if (vLenSq < 1e-6) {
                return fallback;
            }
            final double arm = (8.0 / 3.0) * ((targetX - baseX) * vx + (targetZ - baseZ) * vz) / vLenSq;
            if (!Double.isFinite(arm) || arm <= 0) {
                return fallback;
            }
            return Math.min(arm, length);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static double[] horizontalDirection(Vec3 from, Vec3 to) {
        final double dx = to.x - from.x;
        final double dz = to.z - from.z;
        final double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 1e-6) {
            return null;
        }
        return new double[]{dx / horizontal, dz / horizontal};
    }

    private static double distanceSquared(Vec3 point, Position node) {
        final double dx = point.x - node.getX();
        final double dz = point.z - node.getZ();
        return dx * dx + dz * dz;
    }

    private ParallelLineTool() {
    }
}
