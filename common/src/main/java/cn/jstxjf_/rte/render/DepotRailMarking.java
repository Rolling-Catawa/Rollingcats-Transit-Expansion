package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.DepotRailRules;
import cn.jstxjf_.rte.network.PacketSetDepotRailRule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.mod.InitClient;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class DepotRailMarking {

    private static final double PICK_ANGLE = Math.toRadians(8);
    private static final double MAX_PICK_DISTANCE = 128;
    private static final int SAMPLES_PER_RAIL = 12;

    private static boolean active;
    private static long depotId;
    private static String depotName = "";
    private static String hoveredRailId;

    private DepotRailMarking() {
    }

    public static boolean isActive() {
        return active && depotId != 0;
    }

    public static void enter(long depot, String name) {
        active = true;
        depotId = depot;
        depotName = name == null ? "" : name;
        hoveredRailId = null;
        final int visible = segments().size();
        cn.jstxjf_.rte.RTE.LOGGER.info("[DepotRailMarking] entered depot={} name={} visibleRails={} marked={}",
            depot, depotName, visible, DepotRailRules.rulesOf(depot).size());
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "[RTE] " + depotName + " · " + visible + " rails in range · left-click to mark, right-click to exit"), false);
        }
    }

    public static void exit() {
        active = false;
        depotId = 0;
        hoveredRailId = null;
    }

    public static long depotId() {
        return depotId;
    }

    public static String depotName() {
        return depotName;
    }

    public static String hoveredRailId() {
        return hoveredRailId;
    }

    public static DepotRailRules.Rule ruleOf(String railId) {
        return DepotRailRules.get(depotId, railId);
    }

    public static void updateHover() {
        if (!isActive()) {
            hoveredRailId = null;
            return;
        }
        String best = null;
        double bestAngle = PICK_ANGLE;
        for (final RailSegment segment : segments()) {
            for (final double[] point : segment.points) {
                final Double angle = aimOffset(point);
                if (angle != null && angle < bestAngle) {
                    bestAngle = angle;
                    best = segment.railId;
                }
            }
        }
        hoveredRailId = best;
    }

    public static boolean cycleHovered() {
        if (!isActive() || hoveredRailId == null) {
            return false;
        }
        final DepotRailRules.Rule next = DepotRailRules.cycle(depotId, hoveredRailId);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketSetDepotRailRule(depotId, hoveredRailId, next));
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "[RTE] " + hoveredRailId.substring(0, Math.min(8, hoveredRailId.length())) + " -> " + next), true);
        }
        return true;
    }

    public static List<RailSegment> segments() {
        final List<RailSegment> result = new ArrayList<>();
        final MinecraftClientData data = MinecraftClientData.getInstance();
        if (data == null || data.positionsToRail == null) {
            return result;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return result;
        }
        final Vec3 eye = minecraft.player.getEyePosition(1);
        final java.util.Set<String> seen = new java.util.HashSet<>();
        for (final var entry : data.positionsToRail.entrySet()) {
            final Object2ObjectOpenHashMap<Position, Rail> neighbours = entry.getValue();
            if (neighbours == null) {
                continue;
            }
            for (final var neighbour : neighbours.entrySet()) {
                final Rail rail = neighbour.getValue();
                final String railId;
                try {
                    railId = rail.getHexId();
                } catch (Exception e) {
                    continue;
                }
                if (!seen.add(railId)) {
                    continue;
                }
                final Position from = entry.getKey();
                if (Math.abs(from.getX() + 0.5 - eye.x) > MAX_PICK_DISTANCE
                    || Math.abs(from.getZ() + 0.5 - eye.z) > MAX_PICK_DISTANCE) {
                    continue;
                }
                result.add(buildSegment(railId, rail, from, neighbour.getKey()));
            }
        }
        return result;
    }

    private static RailSegment buildSegment(String railId, Rail rail, Position from, Position to) {
        final List<double[]> points = new ArrayList<>();
        double length = 0;
        try {
            length = rail.railMath.getLength();
        } catch (Exception ignored) {
        }
        if (length > 0.01) {
            for (int i = 0; i <= SAMPLES_PER_RAIL; i++) {
                try {
                    final org.mtr.core.tool.Vector vector =
                        rail.railMath.getPosition(length * i / SAMPLES_PER_RAIL, false);
                    points.add(new double[]{vector.x(), vector.y(), vector.z()});
                } catch (Exception ignored) {
                }
            }
        }
        if (points.isEmpty()) {
            points.add(new double[]{from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5});
            points.add(new double[]{to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5});
        }
        final double[] mid = points.get(points.size() / 2);
        return new RailSegment(railId, points, mid);
    }

    private static Double aimOffset(double[] point) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return null;
        }
        final Vec3 eye = minecraft.player.getEyePosition(1);
        final Vec3 look = minecraft.player.getLookAngle();
        final Vec3 delta = new Vec3(point[0] - eye.x, point[1] - eye.y, point[2] - eye.z);
        final double distance = delta.length();
        if (distance < 1e-3 || distance > MAX_PICK_DISTANCE) {
            return null;
        }
        final double cos = delta.scale(1 / distance).dot(look);
        if (cos <= 0) {
            return null;
        }
        return Math.acos(Math.min(1, cos));
    }

    public static final class RailSegment {
        public final String railId;
        public final List<double[]> points;
        public final double[] midpoint;

        RailSegment(String railId, List<double[]> points, double[] midpoint) {
            this.railId = railId;
            this.points = points;
            this.midpoint = midpoint;
        }
    }
}
