package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.RailChainSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Position;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class RailChainHandles {

    private static final double HANDLE_PICK_ANGLE = Math.toRadians(6);
    private static final double CANDIDATE_PICK_ANGLE = Math.toRadians(10);
    private static final double MAX_PICK_DISTANCE = 96;

    private static boolean active;
    private static boolean draggingStart;
    private static boolean dragging;
    private static Position hoveredCandidate;
    private static Position justLeft;

    private RailChainHandles() {
    }

    public static boolean isActive() {
        return active && RailChainSelection.isManual();
    }

    public static void enter() {
        active = true;
        dragging = false;
        hoveredCandidate = null;
    }

    public static void exit() {
        active = false;
        dragging = false;
        hoveredCandidate = null;
    }

    public static boolean isDragging() {
        return dragging;
    }

    public static boolean isDraggingStart() {
        return draggingStart;
    }

    public static Position hoveredCandidate() {
        return hoveredCandidate;
    }

    public static Position tip(boolean atStart) {
        final List<Position> nodes = RailChainSelection.view();
        if (nodes.size() < 2) {
            return null;
        }
        return atStart ? nodes.get(0) : nodes.get(nodes.size() - 1);
    }

    public static List<Position> candidates() {
        if (!dragging) {
            return List.of();
        }
        final List<Position> result = new ArrayList<>(
            RailChainSelection.extensionCandidates(MinecraftClientData.getInstance(), draggingStart));
        final Position trim = trimTarget();
        if (trim != null) {
            result.add(trim);
        }
        return result;
    }

    public static Position trimTarget() {
        final List<Position> nodes = RailChainSelection.view();
        if (nodes.size() <= 2) {
            return null;
        }
        return draggingStart ? nodes.get(1) : nodes.get(nodes.size() - 2);
    }

    public static boolean isTrimTarget(Position position) {
        return position != null && position.equals(trimTarget());
    }

    public static boolean isCommittable(Position position) {
        if (position == null) {
            return false;
        }
        return isTrimTarget(position)
            ? RailChainSelection.canTrim(draggingStart)
            : !RailChainSelection.view().contains(position);
    }

    public static boolean beginDrag() {
        if (!isActive()) {
            return false;
        }
        final Position start = tip(true);
        final Position end = tip(false);
        final Double startAngle = aimOffset(start);
        final Double endAngle = aimOffset(end);
        Boolean pickStart = null;
        double best = HANDLE_PICK_ANGLE;
        if (startAngle != null && startAngle < best) {
            best = startAngle;
            pickStart = Boolean.TRUE;
        }
        if (endAngle != null && endAngle < best) {
            pickStart = Boolean.FALSE;
        }
        if (pickStart == null) {
            return false;
        }
        draggingStart = pickStart;
        dragging = true;
        hoveredCandidate = null;
        return true;
    }

    public static void updateDrag() {
        if (!dragging) {
            return;
        }
        if (justLeft != null) {
            final Double away = aimOffset(justLeft);
            if (away == null || away > CANDIDATE_PICK_ANGLE) {
                justLeft = null;
            }
        }
        Position best = null;
        double bestAngle = CANDIDATE_PICK_ANGLE;
        for (final Position candidate : candidates()) {
            if (candidate.equals(justLeft)) {
                continue;
            }
            final Double angle = aimOffset(candidate);
            if (angle != null && angle < bestAngle) {
                bestAngle = angle;
                best = candidate;
            }
        }
        hoveredCandidate = best;
        if (best == null || !isCommittable(best)) {
            return;
        }
        final Position previousTip = tip(draggingStart);
        if (apply(best)) {
            justLeft = previousTip;
            hoveredCandidate = null;
        }
    }

    private static boolean apply(Position target) {
        return isTrimTarget(target)
            ? RailChainSelection.trim(draggingStart)
            : RailChainSelection.extend(target, draggingStart);
    }

    public static boolean commitDrag() {
        if (!dragging) {
            return false;
        }
        final Position target = hoveredCandidate;
        final boolean applied = target != null && isCommittable(target) && apply(target);
        dragging = false;
        hoveredCandidate = null;
        justLeft = null;
        return applied;
    }

    public static void cancelDrag() {
        dragging = false;
        hoveredCandidate = null;
        justLeft = null;
    }

    public static List<double[]> chainPoints() {
        final List<double[]> points = new ArrayList<>();
        for (final Position position : RailChainSelection.view()) {
            points.add(new double[]{position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5});
        }
        return points;
    }

    private static Double aimOffset(Position position) {
        if (position == null) {
            return null;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return null;
        }
        final Vec3 eye = minecraft.player.getEyePosition(1);
        final Vec3 look = minecraft.player.getLookAngle();
        final Vec3 delta = new Vec3(
            position.getX() + 0.5 - eye.x,
            position.getY() + 0.5 - eye.y,
            position.getZ() + 0.5 - eye.z);
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
}
