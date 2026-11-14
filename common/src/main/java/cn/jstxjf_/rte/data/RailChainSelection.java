package cn.jstxjf_.rte.data;

import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class RailChainSelection {

    private static final int MIN_NODES = 2;

    private static List<Position> nodes;
    private static Position anchorA;
    private static Position anchorB;

    private RailChainSelection() {
    }

    public static boolean isManual() {
        return nodes != null && nodes.size() >= MIN_NODES;
    }

    public static List<Position> resolve(Position start, Position end) {
        if (!isManual() || anchorA == null || anchorB == null) {
            return null;
        }
        final boolean matches = (anchorA.equals(start) && anchorB.equals(end))
            || (anchorA.equals(end) && anchorB.equals(start));
        return matches ? new ArrayList<>(nodes) : null;
    }

    public static void reset() {
        nodes = null;
        anchorA = null;
        anchorB = null;
    }

    public static void adopt(List<Position> chain, Position start, Position end) {
        if (chain == null || chain.size() < MIN_NODES) {
            reset();
            return;
        }
        nodes = new ArrayList<>(chain);
        anchorA = start;
        anchorB = end;
    }

    public static int size() {
        return nodes == null ? 0 : nodes.size();
    }

    public static List<Position> view() {
        return nodes == null ? List.of() : List.copyOf(nodes);
    }

    public static boolean canTrim(boolean atStart) {
        if (!isManual() || nodes.size() <= MIN_NODES) {
            return false;
        }
        final List<Position> candidate = new ArrayList<>(nodes);
        candidate.remove(atStart ? 0 : candidate.size() - 1);
        return containsAnchorSegment(candidate);
    }

    public static boolean trim(boolean atStart) {
        if (!isManual() || nodes.size() <= MIN_NODES) {
            return false;
        }
        final List<Position> candidate = new ArrayList<>(nodes);
        if (atStart) {
            candidate.remove(0);
        } else {
            candidate.remove(candidate.size() - 1);
        }
        if (!containsAnchorSegment(candidate)) {
            return false;
        }
        nodes = candidate;
        return true;
    }

    public static List<Position> extensionCandidates(MinecraftClientData data, boolean atStart) {
        final List<Position> result = new ArrayList<>();
        if (!isManual() || data == null) {
            return result;
        }
        final Position tip = atStart ? nodes.get(0) : nodes.get(nodes.size() - 1);
        final Position inward = atStart ? nodes.get(1) : nodes.get(nodes.size() - 2);
        final Object2ObjectOpenHashMap<Position, Rail> connections = data.positionsToRail.get(tip);
        if (connections == null) {
            return result;
        }
        for (final Position candidate : connections.keySet()) {
            if (!candidate.equals(inward) && !nodes.contains(candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    public static boolean extend(Position next, boolean atStart) {
        if (!isManual() || next == null || nodes.contains(next)) {
            return false;
        }
        if (atStart) {
            nodes.add(0, next);
        } else {
            nodes.add(next);
        }
        return true;
    }

    private static boolean containsAnchorSegment(List<Position> candidate) {
        if (anchorA == null || anchorB == null) {
            return true;
        }
        for (int i = 0; i + 1 < candidate.size(); i++) {
            final Position a = candidate.get(i);
            final Position b = candidate.get(i + 1);
            if ((a.equals(anchorA) && b.equals(anchorB)) || (a.equals(anchorB) && b.equals(anchorA))) {
                return true;
            }
        }
        return false;
    }
}
