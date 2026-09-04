package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.BezierCurve;
import static cn.jstxjf_.rte.data.RailExtraSupplier.usesBezierCurve;
import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailCalculator;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.data.TransportMode;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.core.tool.Angle;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Vector3d;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = Rail.class, remap = false)
public abstract class RailMixin implements RailExtraSupplier {

    private static final double ACCEPT_THRESHOLD = 1E-4;
    private static final double HALF_ACCEPT_THRESHOLD = ACCEPT_THRESHOLD / 2;

    @Shadow public RailMath railMath;

    @Unique private java.util.List<String> rte$modelKeys = new java.util.ArrayList<>();
    @Unique private boolean rte$isSecondaryDir = false;
    @Unique private float rte$verticalCurveRadius = 0f;
    @Unique private Map<String, String> rte$customConfigs = new HashMap<>();
    @Unique private Map<String, ConfigResponder> rte$customResponders = new HashMap<>();
    @Unique private Map<Double, Float> rte$rollAngleMap = new HashMap<>();
    @Unique private int rte$openingDirection = 0;
    @Unique private float rte$rollingOffset = 1.435F / 2F;
    @Unique private int rte$pathMode = 0;
    @Unique private String rte$railType = "";
    @Unique private int rte$speedLimitKph = -1;
    @Unique private BezierCurve rte$bezier = null;
    @Unique private BlockPos rte$posStartCache = null;
    @Unique private BlockPos rte$posEndCache = null;
    @Unique private double rte$angle1Rad = Double.NaN;
    @Unique private double rte$angle2Rad = Double.NaN;

    @Override public String getModelKey() {
        return rte$modelKeys == null || rte$modelKeys.isEmpty() ? "" : rte$modelKeys.get(0);
    }

    @Override public void setModelKey(String key) {
        if (rte$modelKeys == null) {
            rte$modelKeys = new java.util.ArrayList<>();
        }
        rte$modelKeys.clear();
        if (key != null && !key.isEmpty()) {
            rte$modelKeys.add(key);
        }
    }

    @Override public java.util.List<String> getModelKeys() {
        return rte$modelKeys == null ? java.util.Collections.emptyList()
            : java.util.Collections.unmodifiableList(rte$modelKeys);
    }

    @Override public void setModelKeys(java.util.List<String> keys) {
        final java.util.List<String> next = new java.util.ArrayList<>();
        if (keys != null) {
            for (final String key : keys) {
                if (key != null && !key.isEmpty() && !next.contains(key)) {
                    next.add(key);
                }
            }
        }
        this.rte$modelKeys = next;
    }
    @Override public boolean getRenderReversed() { return rte$isSecondaryDir; }
    @Override public void setRenderReversed(boolean value) { this.rte$isSecondaryDir = value; }
    @Override public float getVerticalCurveRadius() { return rte$verticalCurveRadius; }
    @Override public void setVerticalCurveRadius(float value) { this.rte$verticalCurveRadius = value; this.rte$vTheta = 0; }

    @Unique
    private RailAccessor rte$accessor() { return (RailAccessor)(Object)this; }

    @Override
    public int getHeight() {
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        if (p1 != null && p2 != null) {
            return (int)(p2.getY() - p1.getY());
        }
        return (int)(railMath.maxY - railMath.minY);
    }

    @Override public Map<Double, Float> getRollAngleMap() { return rte$rollAngleMap; }
    @Override public void setRollAngleMap(Map<Double, Float> rollAngleMap) { this.rte$rollAngleMap = rollAngleMap; }
    @Override public Map<String, String> getCustomConfigs() { return rte$customConfigs; }
    @Override public void setCustomConfigs(Map<String, String> customConfigs) { this.rte$customConfigs = customConfigs; }
    @Override public Map<String, ConfigResponder> getCustomResponders() { return rte$customResponders; }
    @Override public void setCustomResponders(Map<String, ConfigResponder> customResponders) { this.rte$customResponders = customResponders; }
    @Override public void setOpeningDirectionRaw(int direction) { this.rte$openingDirection = direction; }

    @Override
    public void setOpeningDirection(int direction) {
        this.rte$openingDirection = direction;
    }

    @Override
    public int getOpeningDirection() {
        int direction = rte$openingDirection;
        if (getRenderReversed()) {
            if (direction == 1) direction = 2;
            else if (direction == 2) direction = 1;
        }
        return direction;
    }

    @Override public int getOpeningDirectionRaw() { return rte$openingDirection; }
    @Override public void setRailType(String railType) { this.rte$railType = railType; }
    @Override public String getRailType() { return rte$railType; }
    @Override public void setSpeedLimitKph(int kph) { this.rte$speedLimitKph = kph; }
    @Override public int getSpeedLimitKph() { return rte$speedLimitKph; }
    @Override public void setRollingOffset(float rollingOffset) { this.rte$rollingOffset = rollingOffset; }
    @Override public float getRollingOffset() { return rte$rollingOffset; }

    @Override
    public BlockPos getPosStart() {
        if (rte$posStartCache != null) return rte$posStartCache;
        Position p1 = rte$accessor().rte$getPosition1();
        if (p1 != null) {
            rte$posStartCache = new BlockPos((int)p1.getX(), (int)p1.getY(), (int)p1.getZ());
            return rte$posStartCache;
        }
        org.mtr.core.tool.Vector pos = railMath.getPosition(0, false);
        rte$posStartCache = new BlockPos((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
        return rte$posStartCache;
    }

    @Override
    public BlockPos getPosEnd() {
        if (rte$posEndCache != null) return rte$posEndCache;
        Position p2 = rte$accessor().rte$getPosition2();
        if (p2 != null) {
            rte$posEndCache = new BlockPos((int)p2.getX(), (int)p2.getY(), (int)p2.getZ());
            return rte$posEndCache;
        }
        org.mtr.core.tool.Vector pos = railMath.getPosition(railMath.getLength(), false);
        rte$posEndCache = new BlockPos((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
        return rte$posEndCache;
    }

    @Override
    public double getLength() {
        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            return rte$bezier.getLength();
        }
        return ((Rail)(Object)this).railMath.getLength();
    }

    @Unique private transient Object rte$orientCheckedMath;
    @Unique private transient boolean rte$mathFromP1 = true;

    @Override
    public boolean railMathStartsAtPosition1() {
        final org.mtr.core.data.RailMath rm = ((Rail)(Object)this).railMath;
        if (rm == null) return true;
        if (rm == rte$orientCheckedMath) return rte$mathFromP1;
        final Position p1 = rte$accessor().rte$getPosition1();
        final Position p2 = rte$accessor().rte$getPosition2();
        boolean fromP1 = true;
        if (p1 != null && p2 != null && !p1.equals(p2)) {
            try {
                final org.mtr.core.tool.Vector v0 = rm.getPosition(0, false);
                final double d1 = Math.pow(v0.x - (p1.getX() + 0.5), 2) + Math.pow(v0.z - (p1.getZ() + 0.5), 2);
                final double d2 = Math.pow(v0.x - (p2.getX() + 0.5), 2) + Math.pow(v0.z - (p2.getZ() + 0.5), 2);
                fromP1 = d1 <= d2;
            } catch (Exception ignored) {
            }
        }
        rte$orientCheckedMath = rm;
        rte$mathFromP1 = fromP1;
        return fromP1;
    }

    @Override
    public org.mtr.core.tool.Vector railMathPositionFromP1(double value) {
        return ((Rail)(Object)this).railMath.getPosition(value, !railMathStartsAtPosition1());
    }

    @Override
    public Vector3d getPosition(double value) {
        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            BezierCurve.Vec3 vec = rte$bezier.getPosition(value);
            return new Vector3d(vec.x + 0.5, vec.y + rte$rollingYOffset(value), vec.z + 0.5);
        }
        org.mtr.core.tool.Vector vec = railMathPositionFromP1(value);
        if (rte$verticalCurveRadius != 0) {
            double y = getPositionY(value);
            return new Vector3d(vec.x, y, vec.z);
        }
        return new Vector3d(vec.x, vec.y + rte$rollingYOffset(value), vec.z);
    }

    @Inject(method = "getStartAngle", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$getSampledStartAngle(boolean getEnd, CallbackInfoReturnable<Angle> cir) {
        final Angle angle = rte$getSampledAngle(getEnd);
        if (angle != null) {
            cir.setReturnValue(angle);
        }
    }

    @Inject(method = "getStartAngle(Lorg/mtr/core/data/Position;)Lorg/mtr/core/tool/Angle;", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$getSampledStartAngle(Position startPosition, CallbackInfoReturnable<Angle> cir) {
        if (startPosition == null) {
            return;
        }
        final Position p1 = rte$accessor().rte$getPosition1();
        final Position p2 = rte$accessor().rte$getPosition2();
        final boolean getEnd;
        if (startPosition.equals(p1)) {
            getEnd = false;
        } else if (startPosition.equals(p2)) {
            getEnd = true;
        } else {
            return;
        }
        final Angle angle = rte$getSampledAngle(getEnd);
        if (angle != null) {
            cir.setReturnValue(angle);
        }
    }

    @Unique
    private Angle rte$getSampledAngle(boolean getEnd) {
        final double length = getLength();
        if (!Double.isFinite(length) || length <= ACCEPT_THRESHOLD) {
            return null;
        }

        final double start = getEnd ? length : 0;
        final double end = getEnd ? Math.max(0, length - ACCEPT_THRESHOLD) : Math.min(length, ACCEPT_THRESHOLD);
        final Vector3d pos1 = getPosition(start);
        final Vector3d pos2 = getPosition(end);
        final double dx = pos2.getXMapped() - pos1.getXMapped();
        final double dz = pos2.getZMapped() - pos1.getZMapped();
        if (Math.abs(dx) < 1E-9 && Math.abs(dz) < 1E-9) {
            return null;
        }

        return RailAngleExtra.fromRadians(Math.atan2(dz, dx));
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true, remap = false)
    private void rte$isValid(CallbackInfoReturnable<Boolean> cir) {
        if (!usesBezierCurve(rte$pathMode) || rte$bezier == null) {
            return;
        }
        final Rail rail = (Rail) (Object) this;
        final boolean hasGeometry = Double.isFinite(rte$bezier.getLength()) && rte$bezier.getLength() > ACCEPT_THRESHOLD;
        final boolean speedValid = rail.getSpeedLimitKilometersPerHour(false) > 0 || rail.getSpeedLimitKilometersPerHour(true) > 0;
        final boolean typeValid = !rail.isPlatform() || !rail.isSiding();
        cir.setReturnValue(speedValid && hasGeometry && typeValid);
    }

    @Unique
    private double rte$rollingYOffset(double rawValue) {
        float r = RailExtraSupplier.getRollAngle((Rail)(Object)this, rawValue);
        float offset = getRollingOffset();
        if (r == 0) return 0;
        RailCalculator.Vec2 rotated = new RailCalculator.Vec2(offset, 0).rotateRad(r);
        return Math.abs(rotated.z);
    }

    @Override
    public boolean isStraightOnly() {
        double length = railMath.getLength();
        if (length < 0.1) return true;
        org.mtr.core.tool.Vector p0 = railMath.getPosition(0, false);
        org.mtr.core.tool.Vector p1 = railMath.getPosition(Math.min(1, length / 4), false);
        org.mtr.core.tool.Vector p2 = railMath.getPosition(length - Math.min(1, length / 4), false);
        org.mtr.core.tool.Vector p3 = railMath.getPosition(length, false);
        double dx1 = p1.x - p0.x, dz1 = p1.z - p0.z;
        double dx2 = p3.x - p2.x, dz2 = p3.z - p2.z;
        double dot = dx1 * dx2 + dz1 * dz2;
        double len1 = Math.sqrt(dx1*dx1 + dz1*dz1);
        double len2 = Math.sqrt(dx2*dx2 + dz2*dz2);
        if (len1 < 1e-6 || len2 < 1e-6) return true;
        return (dot / (len1 * len2)) > 0.999;
    }

    @Override public int getPathMode() { return rte$pathMode; }
    @Override public void setPathModeRaw(int mode) { this.rte$pathMode = mode; }

    @Override
    public void changePathMode(int mode) {
        if (rte$pathMode == mode) return;
        switch (mode) {
            case RailExtraSupplier.PATH_MODE_ORIGINAL:
                rte$pathMode = 0;
                break;
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER:
                rte$genForBezier();
                break;
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER:
                if (rte$bezier == null) {
                    rte$genForBezier();
                }
                rte$pathMode = RailExtraSupplier.PATH_MODE_MANUAL_BEZIER;
                break;
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING:
                rte$bezier = null;
                rte$pathMode = RailExtraSupplier.PATH_MODE_CURVE_FITTING;
                break;
        }
    }

    @Override
    public void regenerateAutoBezier() {
        if (rte$pathMode != RailExtraSupplier.PATH_MODE_AUTO_BEZIER) return;
        rte$genForBezier();
    }

    @Unique
    private void rte$genForBezier() {
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        if (p1 == null || p2 == null) return;

        Vec3 posStart = new Vec3(p1.getX(), p1.getY(), p1.getZ());
        Vec3 posEnd = new Vec3(p2.getX(), p2.getY(), p2.getZ());
        double dx = posEnd.x - posStart.x;
        double dz = posEnd.z - posStart.z;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 0.01) return;

        double straightAngle = Math.atan2(dz, dx);
        double rad1 = straightAngle;
        double rad2 = straightAngle + Math.PI;

        double rmLen = railMath.getLength();
        if (Double.isFinite(rmLen) && rmLen > 0.01 && rmLen < length * 5) {
            double eps = Math.min(0.5, rmLen * 0.01);
            if (eps < 0.001) eps = 0.001;

            org.mtr.core.tool.Vector rm0 = railMathPositionFromP1(0);
            org.mtr.core.tool.Vector rm0e = railMathPositionFromP1(eps);
            if (rm0 != null && rm0e != null) {
                double tdx = rm0e.x - rm0.x;
                double tdz = rm0e.z - rm0.z;
                if (Math.abs(tdx) > 1e-9 || Math.abs(tdz) > 1e-9) {
                    double sampled = Math.atan2(tdz, tdx);
                    double dot = Math.cos(sampled) * Math.cos(straightAngle) + Math.sin(sampled) * Math.sin(straightAngle);
                    if (dot > -0.3) {
                        rad1 = sampled;
                    }
                }
            }

            org.mtr.core.tool.Vector rmN = railMathPositionFromP1(rmLen);
            org.mtr.core.tool.Vector rmNe = railMathPositionFromP1(rmLen - eps);
            if (rmN != null && rmNe != null) {
                double tdx = rmNe.x - rmN.x;
                double tdz = rmNe.z - rmN.z;
                if (Math.abs(tdx) > 1e-9 || Math.abs(tdz) > 1e-9) {
                    double sampled = Math.atan2(tdz, tdx);
                    double revAngle = straightAngle + Math.PI;
                    double dot = Math.cos(sampled) * Math.cos(revAngle) + Math.sin(sampled) * Math.sin(revAngle);
                    if (dot > -0.3) {
                        rad2 = sampled;
                    }
                }
            }
        }

        Vec3 ctrl1 = rte$getCtrlPoint(rad1, posStart, length / 2, 0);
        Vec3 ctrl2 = rte$getCtrlPoint(rad2, posEnd, length / 2, 0);

        rte$bezier = new BezierCurve(0.1,
            posStart.x, posStart.y, posStart.z,
            ctrl1.x, ctrl1.y, ctrl1.z,
            ctrl2.x, ctrl2.y, ctrl2.z,
            posEnd.x, posEnd.y, posEnd.z
        );
        rte$pathMode = 1;
    }

    @Unique
    private Vec3 rte$getCtrlPoint(double rad, Vec3 base, double t, float elevationAngle) {
        double dx = Math.cos(rad) * t;
        double dz = Math.sin(rad) * t;
        if (elevationAngle != 0) {
            Vec3 offset = new Vec3(dx, 0, dz).xRot(elevationAngle);
            return offset.add(base);
        }
        return new Vec3(base.x + dx, base.y, base.z + dz);
    }

    @Override
    public void setBezier(BezierCurve bezier) {
        if (bezier != null) {
            double bezierLen = bezier.getLength();
            if (!Double.isFinite(bezierLen) || bezierLen <= 0) {
                cn.jstxjf_.rte.RTE.LOGGER.warn("[setBezier] Rejected: invalid length={}", bezierLen);
                this.rte$bezier = null;
                return;
            }
            Position p1 = rte$accessor().rte$getPosition1();
            Position p2 = rte$accessor().rte$getPosition2();
            if (p1 != null && p2 != null) {
                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();
                double dz = p2.getZ() - p1.getZ();
                double straightDist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double maxAllowed = Math.max(100, straightDist * 10);
                if (bezierLen > maxAllowed) {
                    cn.jstxjf_.rte.RTE.LOGGER.warn("[setBezier] Rejected: length={} exceeds {}x straight distance={}",
                        String.format("%.1f", bezierLen), 10, String.format("%.1f", straightDist));
                    this.rte$bezier = null;
                    return;
                }
            }
        }
        this.rte$bezier = bezier;
        if (bezier != null
            && rte$pathMode != RailExtraSupplier.PATH_MODE_CURVE_FITTING
            && rte$pathMode != RailExtraSupplier.PATH_MODE_MANUAL_BEZIER) {
            rte$pathMode = RailExtraSupplier.PATH_MODE_AUTO_BEZIER;
        }
    }

    @Override
    public BezierCurve getBezierCurve() { return rte$bezier; }

    @Override
    public boolean isBetween(double x, double y, double z, double radius) {
        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            return rte$bezier.isBetween(x, y, z, radius);
        }
        double minX = railMath.minX - radius;
        double maxX = railMath.maxX + radius;
        double minY = railMath.minY - radius;
        double maxY = railMath.maxY + radius;
        double minZ = railMath.minZ - radius;
        double maxZ = railMath.maxZ + radius;
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    @Override
    public boolean couldSwitchModeTo(int mode) {
        switch (mode) {
            case RailExtraSupplier.PATH_MODE_ORIGINAL:
                return railMath != null && railMath.getLength() > 0;
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER:
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER:
                return rte$accessor().rte$getPosition1() != null && rte$accessor().rte$getPosition2() != null
                    && getExtraAngle1() != null && getExtraAngle2() != null;
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING:
                return rte$accessor().rte$getPosition1() != null && rte$accessor().rte$getPosition2() != null;
            default: return false;
        }
    }

    @Override
    public void sendUpdateC2S() {
        PacketUpdateRail.sendUpdateC2S((Rail)(Object)this);
    }

    @Override
    public void partialCopyFrom(Object other) {
        if (!(other instanceof RailExtraSupplier)) return;
        RailExtraSupplier oth = (RailExtraSupplier) other;
        setModelKeys(oth.getModelKeys());
        setVerticalCurveRadius(oth.getVerticalCurveRadius());
        setCustomConfigs(new HashMap<>(oth.getCustomConfigs()));

        double length0 = oth.getLength();
        double length1 = this.getLength();
        double k = (length0 == 0) ? 1 : length1 / length0;
        Map<Double, Float> src = new HashMap<>(oth.getRollAngleMap());
        Map<Double, Float> dst = new HashMap<>();
        for (Map.Entry<Double, Float> entry : src.entrySet()) {
            dst.put(entry.getKey() * k, entry.getValue());
        }
        setRollAngleMap(dst);
        setOpeningDirectionRaw(oth.getOpeningDirectionRaw());
        setRollingOffset(oth.getRollingOffset());
        setRailType(oth.getRailType());
        setSpeedLimitKph(oth.getSpeedLimitKph());
    }

    @Override
    public Rail getTransposition() {
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        Angle a1 = getExtraAngle1();
        Angle a2 = getExtraAngle2();
        if (p1 == null || p2 == null || a1 == null || a2 == null) return (Rail)(Object)this;
        final Angle ra2 = a2.getOpposite();
        final Angle ra1 = a1.getOpposite();
        Rail result = RailRebuildHelper.rebuildWithEndpoints((Rail)(Object)this, p2, ra2, p1, ra1);
        long sl1 = rte$accessor().rte$getSpeedLimit1();
        long sl2 = rte$accessor().rte$getSpeedLimit2();
        if (sl1 != sl2) {
            RailAccessor resultAcc = (RailAccessor)(Object) result;
            resultAcc.rte$setSpeedLimit1(sl2);
            resultAcc.rte$setSpeedLimit2(sl1);
        }
        RailExtraSupplier extra = (RailExtraSupplier)(Object)result;
        if ((sl1 <= 0) != (sl2 <= 0)) {
            extra.setRenderReversed(getRenderReversed());
        } else {
            extra.setRenderReversed(!rte$isSecondaryDir);
        }
        final cn.jstxjf_.rte.data.BezierCurve sourceBezier = getBezierCurve();
        if (sourceBezier != null) {
            extra.setBezier(sourceBezier.getTransposition());
        }
        cn.jstxjf_.rte.RTE.LOGGER.info(
            "[RailDir] pathMode={} bezier={} vRadius={} rollPts={} p1y={} p2y={} oneWay={}",
            rte$pathMode, sourceBezier != null, rte$verticalCurveRadius,
            getRollAngleMap() == null ? 0 : getRollAngleMap().size(),
            p1.getY(), p2.getY(), (sl1 <= 0) != (sl2 <= 0));
        final Map<Double, Float> sourceRoll = getRollAngleMap();
        if (sourceRoll != null && !sourceRoll.isEmpty()) {
            final double totalLength = getLength();
            final Map<Double, Float> flipped = new HashMap<>(sourceRoll.size());
            sourceRoll.forEach((distance, angle) -> flipped.put(totalLength - distance, -angle));
            extra.setRollAngleMap(flipped);
        }
        extra.setExtraAngle1Rad(ra2.angleRadians);
        extra.setExtraAngle2Rad(ra1.angleRadians);
        RailAngleExtra.correctStraightRailMathIfApplicable(result, ra2.angleRadians, ra1.angleRadians);
        return result;
    }

    @Unique private float rte$vTheta;

    @Override
    public double getPositionY(double rawValue) {
        double length = getLength();
        if (length == 0) return 0;
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        if (p1 == null || p2 == null) return 0;
        int yStart = (int)p1.getY();
        int yEnd = (int)p2.getY();

        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            return rte$bezier.getPosition(rawValue).y + rte$rollingYOffset(rawValue);
        }

        double H = Math.abs(yEnd - yStart);
        double L = length;
        int sign = yStart < yEnd ? 1 : -1;
        double result;

        if (rte$verticalCurveRadius < 0) {
            result = sign * (rawValue / L) * H + yStart;
        } else if (rte$verticalCurveRadius == 0) {
            double intercept = L / 2;
            double yChange, yInitial, offsetValue;
            if (rawValue < intercept) {
                yChange = (yEnd - yStart) / 2D;
                yInitial = yStart;
                offsetValue = rawValue;
            } else {
                yChange = (yStart - yEnd) / 2D;
                yInitial = yEnd;
                offsetValue = L - rawValue;
            }
            result = yChange * offsetValue * offsetValue / (intercept * intercept) + yInitial;
        } else {
            double maxRadius = (H == 0) ? 0 : Math.abs((H * H + L * L) / (H * 4));
            if (rte$verticalCurveRadius > maxRadius) {
                double intercept = L / 2;
                double yChange, yInitial, offsetValue;
                if (rawValue < intercept) {
                    yChange = (yEnd - yStart) / 2D;
                    yInitial = yStart;
                    offsetValue = rawValue;
                } else {
                    yChange = (yStart - yEnd) / 2D;
                    yInitial = yEnd;
                    offsetValue = L - rawValue;
                }
                result = yChange * offsetValue * offsetValue / (intercept * intercept) + yInitial;
            } else {
                if (rte$vTheta == 0) rte$vTheta = RailExtraSupplier.getVTheta(this, rte$verticalCurveRadius);
                if (!Float.isFinite(rte$vTheta)) {
                    double intercept = L / 2;
                    double yChange, yInitial, offsetValue;
                    if (rawValue < intercept) {
                        yChange = (yEnd - yStart) / 2D;
                        yInitial = yStart;
                        offsetValue = rawValue;
                    } else {
                        yChange = (yStart - yEnd) / 2D;
                        yInitial = yEnd;
                        offsetValue = L - rawValue;
                    }
                    result = yChange * offsetValue * offsetValue / (intercept * intercept) + yInitial;
                } else {
                    float curveL = Mth.sin(rte$vTheta) * rte$verticalCurveRadius;
                    float curveH = (1 - Mth.cos(rte$vTheta)) * rte$verticalCurveRadius;
                    if (rawValue < curveL) {
                        float r = (float)rawValue;
                        result = sign * (rte$verticalCurveRadius - Math.sqrt(rte$verticalCurveRadius * rte$verticalCurveRadius - r * r)) + yStart;
                    } else if (rawValue > L - curveL) {
                        float r = (float)(L - rawValue);
                        result = -sign * (rte$verticalCurveRadius - Math.sqrt(rte$verticalCurveRadius * rte$verticalCurveRadius - r * r)) + yEnd;
                    } else {
                        result = sign * (((rawValue - curveL) / (L - 2 * curveL)) * (H - 2 * curveH) + curveH) + yStart;
                    }
                }
            }
        }
        result += rte$rollingYOffset(rawValue);
        return result;
    }

    @Override public void setExtraAngle1Rad(double rad) { rte$angle1Rad = rad; }
    @Override public void setExtraAngle2Rad(double rad) { rte$angle2Rad = rad; }
    @Override public double getExtraAngle1Rad() { return rte$angle1Rad; }
    @Override public double getExtraAngle2Rad() { return rte$angle2Rad; }

    @Override public Position getExtraPosition1() { return rte$accessor().rte$getPosition1(); }
    @Override public Position getExtraPosition2() { return rte$accessor().rte$getPosition2(); }
    @Override public Angle getExtraAngle1() { return Double.isFinite(rte$angle1Rad) ? RailAngleExtra.fromRadians(rte$angle1Rad) : rte$accessor().rte$getAngle1(); }
    @Override public Angle getExtraAngle2() { return Double.isFinite(rte$angle2Rad) ? RailAngleExtra.fromRadians(rte$angle2Rad) : rte$accessor().rte$getAngle2(); }
    @Override public Angle getOriginalAngle1() { return rte$accessor().rte$getAngle1(); }
    @Override public Angle getOriginalAngle2() { return rte$accessor().rte$getAngle2(); }
    @Override public Rail.Shape getExtraShape() { return rte$accessor().rte$getShape(); }
    @Override public double getExtraVerticalRadius() { return rte$accessor().rte$getVerticalRadius(); }
    @Override public TransportMode getExtraTransportMode() { return rte$accessor().rte$getTransportMode(); }
    @Override public boolean getExtraCanTurnBack() { return rte$accessor().rte$getCanTurnBack(); }
    @Override public boolean getExtraCanHaveSignal() { return rte$accessor().rte$getCanHaveSignal(); }
    @Override public ObjectArrayList<String> getExtraStyles() { return rte$accessor().rte$getStyles(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rail self = (Rail)(Object)this;
        Rail other = (Rail)o;
        return self.getHexId().equals(other.getHexId());
    }

    @Override
    public int hashCode() {
        return ((Rail)(Object)this).getHexId().hashCode();
    }

}
