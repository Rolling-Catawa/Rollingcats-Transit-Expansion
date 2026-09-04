package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.mixin.RailAccessor;
import cn.jstxjf_.rte.RTE;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.World;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.data.RailType;

import javax.annotation.Nullable;
import java.util.Locale;

public final class RailRebuildHelper {

    private RailRebuildHelper() {
    }

    public static Rail rebuildFromCurrentState(Rail rail) {
        final RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
        return rebuildWithEndpoints(rail, extra.getExtraPosition1(), extra.getExtraAngle1(), extra.getExtraPosition2(), extra.getExtraAngle2());
    }

    @Nullable
    public static Rail findClientRail(@Nullable BlockPos posStart, @Nullable BlockPos posEnd) {
        if (posStart == null || posEnd == null) {
            return null;
        }
        return findClientRail(
            new Position(posStart.getX(), posStart.getY(), posStart.getZ()),
            new Position(posEnd.getX(), posEnd.getY(), posEnd.getZ())
        );
    }

    @Nullable
    public static Rail findClientRail(@Nullable Position posStart, @Nullable Position posEnd) {
        if (posStart == null || posEnd == null) {
            return null;
        }
        final var railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(posStart);
        return railsAtPos == null ? null : railsAtPos.get(posEnd);
    }

    public static Rail resolveClientRail(@Nullable BlockPos posStart, @Nullable BlockPos posEnd, Rail fallback) {
        final Rail rail = findClientRail(posStart, posEnd);
        return rail == null ? fallback : rail;
    }

    public static void replaceClientRail(Rail rebuilt) {
        if (!(((Object) rebuilt) instanceof RailExtraSupplier)) {
            return;
        }

        final RailExtraSupplier rebuiltExtra = (RailExtraSupplier) (Object) rebuilt;
        final Position position1 = rebuiltExtra.getExtraPosition1();
        final Position position2 = rebuiltExtra.getExtraPosition2();
        if (position1 == null || position2 == null) {
            return;
        }

        final MinecraftClientData clientData = MinecraftClientData.getInstance();

        final String hexId = rebuilt.getHexId();
        final Rail existing = clientData.railIdMap.get(hexId);
        if (existing != null) {
            clientData.rails.remove(existing);
        }
        clientData.rails.add(rebuilt);
        clientData.railIdMap.put(hexId, rebuilt);

        clientData.railWrapperList.remove(hexId);

        try {
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(hexId);
            final String transHexId = rebuiltExtra.getTransposition().getHexId();
            if (!transHexId.equals(hexId)) {
                cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(transHexId);
            }
        } catch (Exception ignored) {}

        clientData.sync();
    }

    public static void removeClientRail(Rail rail) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        final Position position1 = extra.getExtraPosition1();
        final Position position2 = extra.getExtraPosition2();
        if (position1 == null || position2 == null) {
            return;
        }
        final MinecraftClientData clientData = MinecraftClientData.getInstance();
        final String hexId = rail.getHexId();

        final Rail existing = clientData.railIdMap.get(hexId);
        if (existing != null) {
            clientData.rails.remove(existing);
        }
        clientData.railIdMap.remove(hexId);
        clientData.railWrapperList.remove(hexId);

        try {
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(hexId);
            final String transHexId = extra.getTransposition().getHexId();
            if (!transHexId.equals(hexId)) {
                cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(transHexId);
            }
        } catch (Exception ignored) {}

        clientData.sync();
    }

    public static Rail rebuildWithEndpoints(Rail source, @Nullable Position position1, @Nullable Angle angle1, @Nullable Position position2, @Nullable Angle angle2) {
        if (position1 == null || angle1 == null || position2 == null || angle2 == null) {
            return source;
        }

        final RailExtraSupplier extra = (RailExtraSupplier) (Object) source;
        final String railTypeName = getStoredOrInferredRailTypeName(source);
        final RailType railType = parseRailType(railTypeName);
        final Integer legacySpeedTypeKph = parseLegacySpeedTypeKph(railTypeName);
        final int storedSpeedLimitKph = getStoredSpeedLimitKph(source);
        final int effectiveSpeedLimitKph = storedSpeedLimitKph >= 0
            ? storedSpeedLimitKph
            : legacySpeedTypeKph != null
                ? legacySpeedTypeKph
            : railType != null
                ? railType.speedLimit
                : (int) Math.max(source.getSpeedLimitKilometersPerHour(false), source.getSpeedLimitKilometersPerHour(true));
        final RailAccessor sourceAccessor = (RailAccessor)(Object) source;
        final Position srcP1 = sourceAccessor.rte$getPosition1();
        final Position srcP2 = sourceAccessor.rte$getPosition2();
        final boolean endpointsSwapped = srcP1 != null && srcP2 != null
                && srcP1.equals(position2) && srcP2.equals(position1);
        final long rawSpeed1 = sourceAccessor.rte$getSpeedLimit1();
        final long rawSpeed2 = sourceAccessor.rte$getSpeedLimit2();
        final boolean isOneWay = rawSpeed1 <= 0 || rawSpeed2 <= 0;
        final int speedParam1;
        final int speedParam2;
        if (isOneWay) {
            final long mapped1 = endpointsSwapped ? rawSpeed2 : rawSpeed1;
            final long mapped2 = endpointsSwapped ? rawSpeed1 : rawSpeed2;
            speedParam1 = mapped1 <= 0 ? 0 : effectiveSpeedLimitKph;
            speedParam2 = mapped2 <= 0 ? 0 : effectiveSpeedLimitKph;
        } else {
            speedParam1 = effectiveSpeedLimitKph;
            speedParam2 = effectiveSpeedLimitKph;
        }
        final ObjectArrayList<String> styles = new ObjectArrayList<>(extra.getExtraStyles());

        final boolean sourceHasBezier = extra.getBezierCurve() != null;

        final org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair<Angle, Angle> mtrAngles =
            Rail.getAngles(position1, angle1.angleDegrees, position2, angle2.angleDegrees);
        final Angle mtrAngle1 = mtrAngles.left();
        final Angle mtrAngle2 = mtrAngles.right();

        final Rail rebuilt;
        if (railType != null) {
            rebuilt = createRail(position1, mtrAngle1, position2, mtrAngle2, extra, styles, railType, speedParam1, speedParam2);
        } else {
            rebuilt = createRail(position1, mtrAngle1, position2, mtrAngle2, extra, styles, source, speedParam1, speedParam2);
        }

        if (!rebuilt.isValid()) {
            RailAngleExtra.correctStraightRailMath(rebuilt, angle1.angleRadians);
        }
        if (!rebuilt.isValid()) {
            return source;
        }

        rebuilt.copySignalColors(source);
        copyExtraState(source, rebuilt);
        final RailExtraSupplier rebuiltExtra = (RailExtraSupplier) (Object) rebuilt;
        rebuiltExtra.setExtraAngle1Rad(angle1.angleRadians);
        rebuiltExtra.setExtraAngle2Rad(angle2.angleRadians);
        if (sourceHasBezier && rebuiltExtra.getBezierCurve() != null) {
            RailAngleExtra.rebuildRailMathFromBezier(rebuilt, rebuiltExtra.getBezierCurve());
            if (!rebuilt.isValid()) {
                return source;
            }
        } else if (Double.isFinite(angle1.angleRadians) && Double.isFinite(angle2.angleRadians)) {
            RailAngleExtra.rebuildRailMath(rebuilt, angle1.angleRadians, angle2.angleRadians);
            if (!rebuilt.isValid()) {
                RailAngleExtra.correctStraightRailMath(rebuilt, angle1.angleRadians);
            }
        }
        return rebuilt;
    }

    @Nullable
    public static Rail rebuildFromWorldNodeAngles(World world, @Nullable BlockPos posStart, @Nullable BlockPos posEnd, Rail source) {
        if (world == null || posStart == null || posEnd == null || source == null || !(((Object) source) instanceof RailExtraSupplier extra)) {
            return null;
        }

        final double[] angles = getWorldNodeAngles(world, extra);
        if (angles == null) {
            return null;
        }
        final Position position1 = extra.getExtraPosition1();
        final Position position2 = extra.getExtraPosition2();
        final double raw1 = angles[0];
        final double raw2 = angles[1];
        final double rad1 = angles[2];
        final double rad2 = angles[3];

        final Rail[] rebuiltHolder = {null};
        RailAngleExtra.withAngles(rad1, rad2, (angle1, angle2) ->
            rebuiltHolder[0] = rebuildWithEndpoints(source, position1, angle1, position2, angle2)
        );
        final Rail rebuilt = rebuiltHolder[0];
        if (rebuilt == null || rebuilt == source) {
            return null;
        }
        final RailExtraSupplier rebuiltExtra = (RailExtraSupplier) (Object) rebuilt;
        rebuiltExtra.setExtraAngle1Rad(rad1);
        rebuiltExtra.setExtraAngle2Rad(rad2);
        if (rebuiltExtra.getBezierCurve() == null) {
            RailAngleExtra.correctStraightRailMathIfApplicable(rebuilt, rad1, rad2);
        }
        return rebuilt.isValid() ? rebuilt : null;
    }

    public static boolean needsWorldNodeAngleRebuild(World world, @Nullable BlockPos posStart, @Nullable BlockPos posEnd, Rail source) {
        if (world == null || posStart == null || posEnd == null || source == null || !(((Object) source) instanceof RailExtraSupplier extra)) {
            return false;
        }
        if (extra.getPathMode() != cn.jstxjf_.rte.data.RailExtraSupplier.PATH_MODE_ORIGINAL) {
            return false;
        }
        final double[] angles = getWorldNodeAngles(world, extra);
        if (angles == null) {
            return false;
        }
        final double stored1 = getStoredAngle(extra.getExtraAngle1Rad(), extra.getExtraAngle1());
        final double stored2 = getStoredAngle(extra.getExtraAngle2Rad(), extra.getExtraAngle2());
        return !Double.isFinite(stored1) || !Double.isFinite(stored2)
            || angleDelta(stored1, angles[2]) > Math.toRadians(0.25)
            || angleDelta(stored2, angles[3]) > Math.toRadians(0.25);
    }

    @Nullable
    private static double[] getWorldNodeAngles(World world, RailExtraSupplier extra) {
        final Position position1 = extra.getExtraPosition1();
        final Position position2 = extra.getExtraPosition2();
        if (position1 == null || position2 == null) {
            return null;
        }

        final double raw1 = getNodeAngleRadians(world, position1, extra.getExtraAngle1Rad());
        final double raw2 = getNodeAngleRadians(world, position2, extra.getExtraAngle2Rad());
        final double direction = Math.atan2(position2.getZ() - position1.getZ(), position2.getX() - position1.getX());
        return new double[] {raw1, raw2, faceToward(direction, raw1), faceToward(direction, raw2)};
    }

    private static double getStoredAngle(double radians, @Nullable Angle angle) {
        if (Double.isFinite(radians)) {
            return radians;
        }
        return angle == null ? Double.NaN : angle.angleRadians;
    }

    private static double angleDelta(double first, double second) {
        return Math.abs(Math.atan2(Math.sin(first - second), Math.cos(first - second)));
    }

    private static double getNodeAngleRadians(World world, Position position, double fallback) {
        if (world == null || position == null) {
            return fallback;
        }
        final BlockPos blockPos = new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
        try {
            final BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity != null && blockEntity.data instanceof cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode directNode && directNode.isBound()) {
                return directNode.getAngleRadians();
            }
        } catch (Exception ignored) {
        }
        try {
            final BlockState state = world.getBlockState(blockPos);
            if (state != null && state.getBlock().data instanceof BlockNode) {
                return Math.toRadians(BlockNode.getAngle(state));
            }
        } catch (Exception ignored) {
        }
        return Double.isFinite(fallback) ? fallback : 0;
    }

    private static double faceToward(double direction, double radians) {
        double result = Angle.similarFacing((float) Math.toDegrees(direction), (float) Math.toDegrees(radians)) ? radians : radians + Math.PI;
        result = result % (2 * Math.PI);
        if (result < 0) result += 2 * Math.PI;
        return result;
    }

    private static double faceAway(double direction, double radians) {
        double result = Angle.similarFacing((float) Math.toDegrees(direction), (float) Math.toDegrees(radians)) ? radians + Math.PI : radians;
        result = result % (2 * Math.PI);
        if (result < 0) result += 2 * Math.PI;
        return result;
    }

    private static double distance(Position position1, Position position2) {
        final double dx = position2.getX() - position1.getX();
        final double dy = position2.getY() - position1.getY();
        final double dz = position2.getZ() - position1.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static String getStoredOrInferredRailTypeName(Rail rail) {
        final RailExtraSupplier extra = ((Object) rail) instanceof RailExtraSupplier ? (RailExtraSupplier) (Object) rail : null;
        final String stored = extra == null ? "" : normalizeRailTypeName(extra.getRailType());
        return stored.isEmpty() ? inferRailTypeName(rail) : stored;
    }

    public static int getStoredOrCurrentSpeedLimitKph(Rail rail) {
        final int stored = getStoredSpeedLimitKph(rail);
        return stored >= 0 ? stored : (int) Math.max(rail.getSpeedLimitKilometersPerHour(false), rail.getSpeedLimitKilometersPerHour(true));
    }

    public static int getStoredSpeedLimitKph(Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier)) {
            return -1;
        }
        return ((RailExtraSupplier) (Object) rail).getSpeedLimitKph();
    }

    public static boolean isRailTypeValid(String railTypeName) {
        return parseRailType(railTypeName) != null || parseLegacySpeedTypeKph(railTypeName) != null;
    }

    @Nullable
    public static RailType parseRailType(String railTypeName) {
        final String normalized = normalizeRailTypeName(railTypeName);
        if (normalized.isEmpty()) {
            return null;
        }

        try {
            return RailType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String inferRailTypeName(Rail rail) {
        final RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
        if (rail.isPlatform()) {
            return RailType.PLATFORM.name();
        }
        if (rail.isSiding()) {
            return RailType.SIDING.name();
        }
        if (extra.getExtraCanTurnBack()) {
            return RailType.TURN_BACK.name();
        }

        final int speedLimit = (int) Math.max(rail.getSpeedLimitKilometersPerHour(false), rail.getSpeedLimitKilometersPerHour(true));
        for (final RailType railType : RailType.values()) {
            if (railType == RailType.PLATFORM || railType == RailType.SIDING || railType == RailType.TURN_BACK) {
                continue;
            }
            if (railType.speedLimit == speedLimit
                && railType.canAccelerate == rail.canAccelerate()
                && railType.hasSignal == extra.getExtraCanHaveSignal()
                && railType.railShape == extra.getExtraShape()
                && (railType == RailType.RUNWAY) == rail.canConnectRemotely()) {
                return railType.name();
            }
        }
        return "";
    }

    private static Rail createRail(
        Position position1,
        Angle angle1,
        Position position2,
        Angle angle2,
        RailExtraSupplier extra,
        ObjectArrayList<String> styles,
        RailType railType,
        int speedParam1,
        int speedParam2
    ) {
        return switch (railType) {
            case PLATFORM -> Rail.newPlatformRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
            case SIDING -> Rail.newSidingRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
            case TURN_BACK -> Rail.newTurnBackRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
            default -> Rail.newRail(
                position1,
                angle1,
                position2,
                angle2,
                railType.railShape,
                extra.getExtraVerticalRadius(),
                styles,
                speedParam1,
                speedParam2,
                false,
                false,
                railType.canAccelerate,
                railType == RailType.RUNWAY,
                railType.hasSignal,
                extra.getExtraTransportMode()
            );
        };
    }

    private static Rail createRail(
        Position position1,
        Angle angle1,
        Position position2,
        Angle angle2,
        RailExtraSupplier extra,
        ObjectArrayList<String> styles,
        Rail source,
        int speedParam1,
        int speedParam2
    ) {
        if (source.isPlatform()) {
            return Rail.newPlatformRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
        }
        if (source.isSiding()) {
            return Rail.newSidingRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
        }
        if (extra.getExtraCanTurnBack()) {
            return Rail.newTurnBackRail(position1, angle1, position2, angle2, extra.getExtraShape(), extra.getExtraVerticalRadius(), styles, extra.getExtraTransportMode());
        }

        return Rail.newRail(
            position1,
            angle1,
            position2,
            angle2,
            extra.getExtraShape(),
            extra.getExtraVerticalRadius(),
            styles,
            speedParam1,
            speedParam2,
            false,
            false,
            source.canAccelerate(),
            source.canConnectRemotely(),
            extra.getExtraCanHaveSignal(),
            extra.getExtraTransportMode()
        );
    }

    private static void copyExtraState(Rail source, Rail target) {
        if (!(((Object) source) instanceof RailExtraSupplier) || !(((Object) target) instanceof RailExtraSupplier)) {
            return;
        }

        final RailExtraSupplier sourceExtra = (RailExtraSupplier) (Object) source;
        final RailExtraSupplier targetExtra = (RailExtraSupplier) (Object) target;

        boolean srcReversed = sourceExtra.getRenderReversed();
        int srcRaw = sourceExtra.getOpeningDirectionRaw();
        int srcLogical = sourceExtra.getOpeningDirection();

        targetExtra.setRenderReversed(srcReversed);
        targetExtra.partialCopyFrom(sourceExtra);

        int afterCopyRaw = targetExtra.getOpeningDirectionRaw();

        boolean transposed = isOrientationTransposed(source, target);
        if (transposed) {
            int raw = targetExtra.getOpeningDirectionRaw();
            if (raw == 1) targetExtra.setOpeningDirectionRaw(2);
            else if (raw == 2) targetExtra.setOpeningDirectionRaw(1);
        }

        final RailAccessor srcAcc = (RailAccessor)(Object) source;
        final RailAccessor tgtAcc = (RailAccessor)(Object) target;

        targetExtra.setRailType(sourceExtra.getRailType());
        targetExtra.setSpeedLimitKph(sourceExtra.getSpeedLimitKph());
        targetExtra.setExtraAngle1Rad(sourceExtra.getExtraAngle1Rad());
        targetExtra.setExtraAngle2Rad(sourceExtra.getExtraAngle2Rad());

        if (RailExtraSupplier.usesBezierCurve(sourceExtra.getPathMode())) {
            final BezierCurve bezier = sourceExtra.getBezierCurve();
            targetExtra.setPathModeRaw(sourceExtra.getPathMode());
            if (bezier != null) {
                targetExtra.setBezier(isOrientationTransposed(source, target) ? bezier.getTransposition() : bezier.copy());
            } else if ((sourceExtra.getPathMode() == RailExtraSupplier.PATH_MODE_AUTO_BEZIER
                    || sourceExtra.getPathMode() == RailExtraSupplier.PATH_MODE_MANUAL_BEZIER)
                    && targetExtra.couldSwitchModeTo(sourceExtra.getPathMode())) {
                targetExtra.changePathMode(sourceExtra.getPathMode());
            }
            if (RailExtraSupplier.requiresBezierCurve(targetExtra.getPathMode()) && targetExtra.getBezierCurve() == null) {
                RTE.LOGGER.warn("[CopyExtra] pathMode={} but bezier still null after copy, resetting to ORIGINAL", targetExtra.getPathMode());
                targetExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_ORIGINAL);
            }
        } else {
            targetExtra.setPathModeRaw(sourceExtra.getPathMode());
        }

        autoFixOneWayRenderReversed(target, tgtAcc);
    }

    public static void autoFixOneWayRenderReversed(Rail rail, RailAccessor acc) {
        long sl1 = acc.rte$getSpeedLimit1();
        long sl2 = acc.rte$getSpeedLimit2();
        if ((sl1 <= 0) == (sl2 <= 0)) return;
        Position p1 = acc.rte$getPosition1();
        Position p2 = acc.rte$getPosition2();
        if (p1 == null || p2 == null) return;
        RailExtraSupplier extra = (RailExtraSupplier)(Object)rail;
        boolean oldReversed = extra.getRenderReversed();
        boolean hasBezier = extra.getBezierCurve() != null;
        boolean renderReversed = rail.getSpeedLimitKilometersPerHour(false) <= 0;
        if (oldReversed == renderReversed) return;
        extra.setRenderReversed(renderReversed);
    }

    private static boolean isOrientationTransposed(Rail source, Rail target) {
        final RailExtraSupplier sourceExtra = (RailExtraSupplier) (Object) source;
        final RailExtraSupplier targetExtra = (RailExtraSupplier) (Object) target;
        final Position sourcePosition1 = sourceExtra.getExtraPosition1();
        final Position sourcePosition2 = sourceExtra.getExtraPosition2();
        final Position targetPosition1 = targetExtra.getExtraPosition1();
        final Position targetPosition2 = targetExtra.getExtraPosition2();
        return sourcePosition1 != null && sourcePosition2 != null
            && sourcePosition1.equals(targetPosition2)
            && sourcePosition2.equals(targetPosition1);
    }

    private static String normalizeRailTypeName(String railTypeName) {
        return railTypeName == null ? "" : railTypeName.trim().toUpperCase(Locale.ROOT);
    }

    @Nullable
    public static Integer parseLegacySpeedTypeKph(String railTypeName) {
        final String normalized = normalizeRailTypeName(railTypeName);
        if (!normalized.startsWith("P")) {
            return null;
        }

        try {
            final int speed = Integer.parseInt(normalized.substring(1));
            if ((speed >= -1 && speed <= 600) || (speed >= 1000 && speed <= 10000 && speed % 500 == 0)) {
                return speed;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
