package cn.jstxjf_.rte.compat;

import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class RailFactory {

    private RailFactory() {
    }

    public static Rail newRail(Position position1, Angle angle1, Position position2, Angle angle2, Rail.Shape shape, double verticalRadius, ObjectArrayList<String> styles, long speedLimit1, long speedLimit2, boolean isPlatform, boolean isSiding, boolean canAccelerate, boolean canConnectRemotely, boolean canHaveSignal, TransportMode transportMode) {
        //#if MC>=12101
        return Rail.newRail(position1, angle1, position2, angle2, shape, verticalRadius, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, styles, speedLimit1, speedLimit2, isPlatform, isSiding, canAccelerate, canConnectRemotely, canHaveSignal, transportMode);
        //#else
        return Rail.newRail(position1, angle1, position2, angle2, shape, verticalRadius, styles, speedLimit1, speedLimit2, isPlatform, isSiding, canAccelerate, canConnectRemotely, canHaveSignal, transportMode);
        //#endif
    }

    public static Rail newTurnBackRail(Position position1, Angle angle1, Position position2, Angle angle2, Rail.Shape shape, double verticalRadius, ObjectArrayList<String> styles, TransportMode transportMode) {
        //#if MC>=12101
        return Rail.newTurnBackRail(position1, angle1, position2, angle2, shape, verticalRadius, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, styles, transportMode);
        //#else
        return Rail.newTurnBackRail(position1, angle1, position2, angle2, shape, verticalRadius, styles, transportMode);
        //#endif
    }

    public static Rail newPlatformRail(Position position1, Angle angle1, Position position2, Angle angle2, Rail.Shape shape, double verticalRadius, ObjectArrayList<String> styles, TransportMode transportMode) {
        //#if MC>=12101
        return Rail.newPlatformRail(position1, angle1, position2, angle2, shape, verticalRadius, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, styles, transportMode);
        //#else
        return Rail.newPlatformRail(position1, angle1, position2, angle2, shape, verticalRadius, styles, transportMode);
        //#endif
    }

    public static Rail newSidingRail(Position position1, Angle angle1, Position position2, Angle angle2, Rail.Shape shape, double verticalRadius, ObjectArrayList<String> styles, TransportMode transportMode) {
        //#if MC>=12101
        return Rail.newSidingRail(position1, angle1, position2, angle2, shape, verticalRadius, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, styles, transportMode);
        //#else
        return Rail.newSidingRail(position1, angle1, position2, angle2, shape, verticalRadius, styles, transportMode);
        //#endif
    }
}
