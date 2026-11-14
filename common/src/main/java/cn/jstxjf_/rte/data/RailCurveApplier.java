package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.network.PacketUpdateRail;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class RailCurveApplier {

    private RailCurveApplier() {
    }

    public static void apply(Rail rail, BezierCurve curve, double angleStart, double angleEnd) {
        if (rail == null || curve == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_MANUAL_BEZIER);
        extra.setBezier(curve);
        extra.setExtraAngle1Rad(angleStart);
        extra.setExtraAngle2Rad(angleEnd);
        RailAngleExtra.rebuildRailMathFromBezier(rail, curve);
        PacketUpdateRail.sendUpdateC2S(rail);
        applyReverse(rail, extra, curve, angleStart, angleEnd);
    }

    private static void applyReverse(Rail rail, RailExtraSupplier extra, BezierCurve curve,
                                     double angleStart, double angleEnd) {
        try {
            final Position position1 = extra.getExtraPosition1();
            final Position position2 = extra.getExtraPosition2();
            if (position1 == null || position2 == null) {
                return;
            }
            final var connections = MinecraftClientData.getInstance().positionsToRail.get(position2);
            final Rail reverse = connections == null ? null : connections.get(position1);
            if (reverse == null || reverse == rail || !(((Object) reverse) instanceof RailExtraSupplier reverseExtra)) {
                return;
            }
            final List<BezierCurve.Vec3> points = curve.getPoints();
            if (points == null || points.size() < 2) {
                return;
            }
            final List<BezierCurve.Vec3> reversed = new ArrayList<>(points.size());
            for (int i = points.size() - 1; i >= 0; i--) {
                reversed.add(points.get(i));
            }
            final BezierCurve reverseCurve = new BezierCurve(0.1, reversed);
            if (!Double.isFinite(reverseCurve.getLength()) || reverseCurve.getLength() < 1) {
                return;
            }
            reverseExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_MANUAL_BEZIER);
            reverseExtra.setBezier(reverseCurve);
            reverseExtra.setExtraAngle1Rad(wrap(angleEnd + Math.PI));
            reverseExtra.setExtraAngle2Rad(wrap(angleStart + Math.PI));
            RailAngleExtra.rebuildRailMathFromBezier(reverse, reverseCurve);
            PacketUpdateRail.sendUpdateC2S(reverse);
        } catch (Exception ignored) {
        }
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
}
