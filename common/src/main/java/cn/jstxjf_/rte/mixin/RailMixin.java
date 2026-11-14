package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.BezierCurve;
import static cn.jstxjf_.rte.data.RailExtraSupplier.usesBezierCurve;
import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailCalculator;
import cn.jstxjf_.rte.data.RailSpatialContext;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.data.TransportMode;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.core.tool.Angle;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
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

    @Inject(
            method = "copy(Lorg/mtr/core/data/Rail;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;)Lorg/mtr/core/data/Rail;",
            at = @At("RETURN"),
            remap = false
    )
    private static void rte$copyStyleExtras(Rail source, ObjectArrayList<String> styles, CallbackInfoReturnable<Rail> cir) {
        final Rail result = cir.getReturnValue();
        if (source != null && result != null) {
            RailRebuildHelper.copyExtraState(source, result);
            ((RailExtraSupplier) (Object) result).setRollAngleMap(
                    new HashMap<>(((RailExtraSupplier) (Object) source).getRollAngleMap()));
        }
    }

    @Inject(
            //#if MC>=12101
            method = "copy(Lorg/mtr/core/data/Rail;Lorg/mtr/core/data/Rail$Shape;DJDDDDDDDDDDD)Lorg/mtr/core/data/Rail;",
            //#else
            method = "copy(Lorg/mtr/core/data/Rail;Lorg/mtr/core/data/Rail$Shape;D)Lorg/mtr/core/data/Rail;",
            //#endif
            at = @At("RETURN"),
            remap = false
    )
    //#if MC>=12101
    private static void rte$copyShapeExtras(Rail source, Rail.Shape shape, double verticalRadius, long extra0, double extra1, double extra2, double extra3, double extra4, double extra5, double extra6, double extra7, double extra8, double extra9, double extra10, double extra11, CallbackInfoReturnable<Rail> cir) {
    //#else
    private static void rte$copyShapeExtras(Rail source, Rail.Shape shape, double verticalRadius, CallbackInfoReturnable<Rail> cir) {
    //#endif
        final Rail result = cir.getReturnValue();
        if (source != null && result != null) {
            RailRebuildHelper.copyExtraState(source, result);
            ((RailExtraSupplier) (Object) result).setRollAngleMap(
                    new HashMap<>(((RailExtraSupplier) (Object) source).getRollAngleMap()));
        }
    }

    @Inject(method = "isValid", at = @At("RETURN"), cancellable = true, remap = false)
    private void rte$allowSpatialCarrier(CallbackInfoReturnable<Boolean> cir) {
        final boolean vanillaValid = cir.getReturnValue();
        if (!RailSpatialContext.apply(this) && vanillaValid) return;
        if (RailExtraSupplier.usesBezierCurve(rte$pathMode) && rte$bezier != null
                && Double.isFinite(rte$bezier.getLength()) && rte$bezier.getLength() > ACCEPT_THRESHOLD) {
            if (vanillaValid) return;
            cn.jstxjf_.rte.data.RailAngleExtra.ensureCarrierRailMath(
                    (Rail) (Object) this, rte$bezier.getLength(),
                    Double.isFinite(rte$angle1Rad) ? rte$angle1Rad : 0);
            cir.setReturnValue(true);
        }
    }

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
    @Unique private boolean rte$collisionEnabled = false;
    @Unique private float rte$collisionWidth = 3F;
    @Unique private float rte$collisionHeight = 0.25F;
    @Unique private float rte$collisionLength = 0.75F;
    @Unique private float rte$collisionSpacing = 0.5F;
    @Unique private float rte$collisionYOffset = -0.25F;
    @Unique private float rte$collisionSideOffset = 0F;
    @Unique private BezierCurve rte$bezier = null;
    @Unique private double rte$angle1Rad = Double.NaN;
    @Unique private double rte$angle2Rad = Double.NaN;
    @Unique private double rte$nodeXOffset1 = 0;
    @Unique private double rte$nodeYOffset1 = 0;
    @Unique private double rte$nodeZOffset1 = 0;
    @Unique private double rte$nodeXOffset2 = 0;
    @Unique private double rte$nodeYOffset2 = 0;
    @Unique private double rte$nodeZOffset2 = 0;
    @Unique private double rte$nodePitch1 = 0;
    @Unique private double rte$nodePitch2 = 0;
    @Unique private String rte$mechanismGroup = "";
    @Unique private String rte$railScriptId = "";
    @Unique private transient volatile cn.jstxjf_.rte.mechanism.MechanismTransform rte$mechanismTransform = cn.jstxjf_.rte.mechanism.MechanismTransform.IDENTITY;
    @Unique private transient volatile boolean rte$mechanismHidden = false;
    @Unique private transient double rte$effectiveLength = Double.NaN;
    @Unique private transient double rte$effectiveLengthBase = Double.NaN;
    @Unique private transient long rte$effectiveLengthTransformHash;
    @Unique private transient Object rte$effectiveLengthMath;
    @Unique private transient Object rte$effectiveLengthBezier;

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
    @Override public void setVerticalCurveRadius(float value) { this.rte$verticalCurveRadius = value; this.rte$vTheta = 0; rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }

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
    @Override public void setRollAngleMap(Map<Double, Float> rollAngleMap) { this.rte$rollAngleMap = rollAngleMap; rte$markCollisionDirty(); }
    @Override public Map<String, String> getCustomConfigs() { return rte$customConfigs; }
    @Override public void setCustomConfigs(Map<String, String> customConfigs) { this.rte$customConfigs = customConfigs == null ? new HashMap<>() : customConfigs; rte$markCollisionDirty(); }
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
    @Override public boolean isCollisionEnabled() { return rte$collisionEnabled; }
    @Override public void setCollisionEnabled(boolean value) { this.rte$collisionEnabled = value; cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionWidth() { return rte$collisionWidth; }
    @Override public void setCollisionWidth(float value) { this.rte$collisionWidth = rte$collisionValue(value, 0.05F, 32F, 3F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionHeight() { return rte$collisionHeight; }
    @Override public void setCollisionHeight(float value) { this.rte$collisionHeight = rte$collisionValue(value, 0.05F, 16F, 0.25F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionLength() { return rte$collisionLength; }
    @Override public void setCollisionLength(float value) { this.rte$collisionLength = rte$collisionValue(value, 0.05F, 16F, 0.75F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionSpacing() { return rte$collisionSpacing; }
    @Override public void setCollisionSpacing(float value) { this.rte$collisionSpacing = rte$collisionValue(value, cn.jstxjf_.rte.util.RailCollisionManager.MIN_SPACING, 16F, 0.5F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionYOffset() { return rte$collisionYOffset; }
    @Override public void setCollisionYOffset(float value) { this.rte$collisionYOffset = rte$collisionValue(value, -32F, 32F, -0.25F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public float getCollisionSideOffset() { return rte$collisionSideOffset; }
    @Override public void setCollisionSideOffset(float value) { this.rte$collisionSideOffset = rte$collisionValue(value, -32F, 32F, 0F); cn.jstxjf_.rte.util.RailCollisionManager.markDirty(); }
    @Override public void setRollingOffset(float rollingOffset) { this.rte$rollingOffset = rollingOffset; rte$markCollisionDirty(); }
    @Override public float getRollingOffset() { return rte$rollingOffset; }
    @Override public double getNodeXOffset1() { return rte$nodeXOffset1; }
    @Override public void setNodeXOffset1(double value) { rte$nodeXOffset1 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodeYOffset1() { return rte$nodeYOffset1; }
    @Override public void setNodeYOffset1(double value) { rte$nodeYOffset1 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodeZOffset1() { return rte$nodeZOffset1; }
    @Override public void setNodeZOffset1(double value) { rte$nodeZOffset1 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodeXOffset2() { return rte$nodeXOffset2; }
    @Override public void setNodeXOffset2(double value) { rte$nodeXOffset2 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodeYOffset2() { return rte$nodeYOffset2; }
    @Override public void setNodeYOffset2(double value) { rte$nodeYOffset2 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodeZOffset2() { return rte$nodeZOffset2; }
    @Override public void setNodeZOffset2(double value) { rte$nodeZOffset2 = rte$sanitizeNodeOffset(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodePitch1() { return rte$nodePitch1; }
    @Override public void setNodePitch1(double value) { rte$nodePitch1 = rte$sanitizePitch(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public double getNodePitch2() { return rte$nodePitch2; }
    @Override public void setNodePitch2(double value) { rte$nodePitch2 = rte$sanitizePitch(value); rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }
    @Override public String getMechanismGroup() { return rte$mechanismGroup; }
    @Override public void setMechanismGroup(String group) { rte$mechanismGroup = group == null ? "" : group; }
    @Override public String getRailScriptId() { return rte$railScriptId; }
    @Override public void setRailScriptId(String scriptId) { rte$railScriptId = scriptId == null ? "" : scriptId; }
    @Override public cn.jstxjf_.rte.mechanism.MechanismTransform getMechanismTransform() { return rte$mechanismTransform; }
    @Override public void setMechanismTransform(cn.jstxjf_.rte.mechanism.MechanismTransform transform) {
        final cn.jstxjf_.rte.mechanism.MechanismTransform next = transform == null ? cn.jstxjf_.rte.mechanism.MechanismTransform.IDENTITY : transform;
        if (!next.equals(rte$mechanismTransform)) {
            rte$mechanismTransform = next;
            rte$markCollisionDirty();
        }
    }
    @Override public boolean isMechanismHidden() { return rte$mechanismHidden; }
    @Override public void setMechanismHidden(boolean hidden) { rte$mechanismHidden = hidden; }
    @Override public boolean hasNodeYOffset() { return Math.abs(rte$nodeYOffset1) > 1E-6 || Math.abs(rte$nodeYOffset2) > 1E-6; }
    @Override public boolean hasNodeOffset() {
        return hasNodeYOffset() || Math.abs(rte$nodeXOffset1) > 1E-6 || Math.abs(rte$nodeZOffset1) > 1E-6
                || Math.abs(rte$nodeXOffset2) > 1E-6 || Math.abs(rte$nodeZOffset2) > 1E-6;
    }

    @Override
    public double getLength() {
        final double baseLength = rte$getBaseLength();
        if (!hasNodeOffset() || !Double.isFinite(baseLength) || baseLength <= ACCEPT_THRESHOLD) {
            return baseLength;
        }
        final long transformHash = rte$nodeTransformHash();
        if (rte$effectiveLengthMath == railMath
                && rte$effectiveLengthBezier == rte$bezier
                && Math.abs(rte$effectiveLengthBase - baseLength) <= 1E-6
                && rte$effectiveLengthTransformHash == transformHash
                && Double.isFinite(rte$effectiveLength)) {
            return rte$effectiveLength;
        }
        final int samples = Math.max(16, Math.min(128, (int) Math.ceil(baseLength / 2)));
        double length = 0;
        Vec3 previous = rte$getBaseCenterPosition(0);
        for (int i = 1; i <= samples; i++) {
            final double value = baseLength * i / samples;
            final Vec3 current = rte$getBaseCenterPosition(value);
            final double ratio = value / baseLength;
            final double previousRatio = (i - 1D) / samples;
            final double dx = current.x + rte$nodeOffsetAt(rte$nodeXOffset1, rte$nodeXOffset2, ratio)
                    - previous.x - rte$nodeOffsetAt(rte$nodeXOffset1, rte$nodeXOffset2, previousRatio);
            final double dy = current.y + rte$nodeYOffsetAt(ratio)
                    - previous.y - rte$nodeYOffsetAt(previousRatio);
            final double dz = current.z + rte$nodeOffsetAt(rte$nodeZOffset1, rte$nodeZOffset2, ratio)
                    - previous.z - rte$nodeOffsetAt(rte$nodeZOffset1, rte$nodeZOffset2, previousRatio);
            length += Math.sqrt(dx * dx + dy * dy + dz * dz);
            previous = current;
        }
        rte$effectiveLength = length;
        rte$effectiveLengthBase = baseLength;
        rte$effectiveLengthTransformHash = transformHash;
        rte$effectiveLengthMath = railMath;
        rte$effectiveLengthBezier = rte$bezier;
        return length;
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
                final double d1 = Math.pow(v0.x() - (p1.getX() + 0.5), 2) + Math.pow(v0.z() - (p1.getZ() + 0.5), 2);
                final double d2 = Math.pow(v0.x() - (p2.getX() + 0.5), 2) + Math.pow(v0.z() - (p2.getZ() + 0.5), 2);
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
    public Vec3 getPosition(double value) {
        final double baseLength = rte$getBaseLength();
        final double effectiveLength = getLength();
        final double ratio = effectiveLength <= ACCEPT_THRESHOLD
                ? 0 : Mth.clamp(value / effectiveLength, 0, 1);
        final double baseValue = baseLength * ratio;
        final Vec3 center = rte$getBaseCenterPosition(baseValue);
        final double x = center.x + rte$nodeOffsetAt(rte$nodeXOffset1, rte$nodeXOffset2, ratio);
        final double y = center.y + rte$nodeYOffsetAt(ratio) + rte$rollingYOffset(baseValue);
        final double z = center.z + rte$nodeOffsetAt(rte$nodeZOffset1, rte$nodeZOffset2, ratio);
        final cn.jstxjf_.rte.mechanism.MechanismTransform transform = rte$mechanismTransform;
        if (transform.isIdentity() || cn.jstxjf_.rte.mechanism.MechanismTransform.SUPPRESS.get()) {
            return new Vec3(x, y, z);
        }
        final double[] moved = transform.apply(x, y, z);
        return new Vec3(moved[0], moved[1], moved[2]);
    }

    @Unique
    private double rte$getBaseLength() {
        return usesBezierCurve(rte$pathMode) && rte$bezier != null
                ? rte$bezier.getLength() : ((Rail)(Object)this).railMath.getLength();
    }

    @Unique
    private Vec3 rte$getBaseCenterPosition(double baseValue) {
        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            final BezierCurve.Vec3 vec = rte$bezier.getPosition(baseValue);
            return new Vec3(vec.x + 0.5, vec.y, vec.z + 0.5);
        }
        final org.mtr.core.tool.Vector vec = railMathPositionFromP1(baseValue);
        final double y = rte$verticalCurveRadius == 0 ? vec.y() : rte$getPositionYBase(baseValue);
        return new Vec3(vec.x(), y, vec.z());
    }

    @Unique
    private double rte$nodeYOffsetAt(double ratio) {
        return rte$nodeOffsetAt(rte$nodeYOffset1, rte$nodeYOffset2, ratio);
    }

    @Unique
    private static double rte$nodeOffsetAt(double first, double second, double ratio) {
        return first + (second - first) * Mth.clamp(ratio, 0, 1);
    }

    @Unique
    private static double rte$sanitizeNodeOffset(double value) {
        return Double.isFinite(value) ? Mth.clamp(value, -64, 64) : 0;
    }

    @Unique
    private static double rte$sanitizePitch(double value) {
        if (!Double.isFinite(value)) return 0;
        value %= 360;
        if (value > 180) value -= 360;
        if (value <= -180) value += 360;
        return value;
    }

    @Unique
    private long rte$nodeTransformHash() {
        long hash = Double.doubleToLongBits(rte$nodeXOffset1);
        hash = 31 * hash + Double.doubleToLongBits(rte$nodeYOffset1);
        hash = 31 * hash + Double.doubleToLongBits(rte$nodeZOffset1);
        hash = 31 * hash + Double.doubleToLongBits(rte$nodeXOffset2);
        hash = 31 * hash + Double.doubleToLongBits(rte$nodeYOffset2);
        return 31 * hash + Double.doubleToLongBits(rte$nodeZOffset2);
    }

    @Unique
    private void rte$invalidateEffectiveLength() {
        rte$effectiveLength = Double.NaN;
        rte$effectiveLengthMath = null;
        rte$effectiveLengthBezier = null;
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
        final Vec3 pos1 = getPosition(start);
        final Vec3 pos2 = getPosition(end);
        final double dx = pos2.x - pos1.x;
        final double dz = pos2.z - pos1.z;
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
        double dx1 = p1.x() - p0.x(), dz1 = p1.z() - p0.z();
        double dx2 = p3.x() - p2.x(), dz2 = p3.z() - p2.z();
        double dot = dx1 * dx2 + dz1 * dz2;
        double len1 = Math.sqrt(dx1*dx1 + dz1*dz1);
        double len2 = Math.sqrt(dx2*dx2 + dz2*dz2);
        if (len1 < 1e-6 || len2 < 1e-6) return true;
        return (dot / (len1 * len2)) > 0.999;
    }

    @Override public int getPathMode() { return rte$pathMode; }
    @Override public void setPathModeRaw(int mode) { this.rte$pathMode = mode; rte$invalidateEffectiveLength(); rte$markCollisionDirty(); }

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
        rte$invalidateEffectiveLength();
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

        final Vec3 posStart = new Vec3(p1.getX(), p1.getY(), p1.getZ());
        final Vec3 posEnd = new Vec3(p2.getX(), p2.getY(), p2.getZ());
        final Vec3 offsetStart = new Vec3(rte$nodeXOffset1, rte$nodeYOffset1, rte$nodeZOffset1);
        final Vec3 offsetEnd = new Vec3(rte$nodeXOffset2, rte$nodeYOffset2, rte$nodeZOffset2);
        final Vec3 offsetDelta = offsetEnd.subtract(offsetStart);
        final Vec3 actualStart = posStart.add(offsetStart);
        final Vec3 actualEnd = posEnd.add(offsetEnd);
        final double dx = actualEnd.x - actualStart.x;
        final double dy = actualEnd.y - actualStart.y;
        final double dz = actualEnd.z - actualStart.z;
        final double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.01) return;

        double straightAngle = Math.atan2(dz, dx);
        final boolean hasRad1 = Double.isFinite(rte$angle1Rad);
        final boolean hasRad2 = Double.isFinite(rte$angle2Rad);
        double rad1 = hasRad1 ? rte$angle1Rad : straightAngle;
        double rad2 = hasRad2 ? rte$angle2Rad : straightAngle + Math.PI;

        double rmLen = railMath.getLength();
        if (Double.isFinite(rmLen) && rmLen > 0.01 && rmLen < length * 5) {
            double eps = Math.min(0.5, rmLen * 0.01);
            if (eps < 0.001) eps = 0.001;

            org.mtr.core.tool.Vector rm0 = railMathPositionFromP1(0);
            org.mtr.core.tool.Vector rm0e = railMathPositionFromP1(eps);
            if (!hasRad1 && rm0 != null && rm0e != null) {
                double tdx = rm0e.x() - rm0.x();
                double tdz = rm0e.z() - rm0.z();
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
            if (!hasRad2 && rmN != null && rmNe != null) {
                double tdx = rmNe.x() - rmN.x();
                double tdz = rmNe.z() - rmN.z();
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

        final double handle = Math.max(0.05, Math.min(length / 2, 64));
        final Vec3 direction1 = rte$direction(rad1, Math.toRadians(rte$nodePitch1));
        final Vec3 direction2 = rte$direction(rad2, Math.toRadians(rte$nodePitch2));
        final Vec3 ctrl1 = posStart.add(direction1.scale(handle)).subtract(offsetDelta.scale(1D / 3D));
        final Vec3 ctrl2 = posEnd.add(direction2.scale(handle)).add(offsetDelta.scale(1D / 3D));

        rte$bezier = new BezierCurve(0.1,
            posStart.x, posStart.y, posStart.z,
            ctrl1.x, ctrl1.y, ctrl1.z,
            ctrl2.x, ctrl2.y, ctrl2.z,
            posEnd.x, posEnd.y, posEnd.z
        );
        rte$pathMode = 1;
        rte$invalidateEffectiveLength();
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
                rte$markCollisionDirty();
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
                    rte$markCollisionDirty();
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
        rte$invalidateEffectiveLength();
        rte$markCollisionDirty();
    }

    @Override
    public BezierCurve getBezierCurve() { return rte$bezier; }

    @Override
    public boolean isBetween(double x, double y, double z, double radius) {
        if (usesBezierCurve(rte$pathMode) && rte$bezier != null) {
            if (!hasNodeOffset()) {
                return rte$bezier.isBetween(x, y, z, radius);
            }
            final double length = getLength();
            final int samples = Math.max(8, Math.min(512, (int) Math.ceil(length * 2)));
            final double radiusSquared = radius * radius;
            for (int i = 0; i <= samples; i++) {
                final Vec3 point = getPosition(length * i / samples);
                final double dx = point.x - x;
                final double dy = point.y - y;
                final double dz = point.z - z;
                if (dx * dx + dy * dy + dz * dz <= radiusSquared) {
                    return true;
                }
            }
            return false;
        }
        double minX = railMath.minX + Math.min(rte$nodeXOffset1, rte$nodeXOffset2) - radius;
        double maxX = railMath.maxX + Math.max(rte$nodeXOffset1, rte$nodeXOffset2) + radius;
        double minY = railMath.minY + Math.min(rte$nodeYOffset1, rte$nodeYOffset2) - radius;
        double maxY = railMath.maxY + Math.max(rte$nodeYOffset1, rte$nodeYOffset2) + radius;
        double minZ = railMath.minZ + Math.min(rte$nodeZOffset1, rte$nodeZOffset2) - radius;
        double maxZ = railMath.maxZ + Math.max(rte$nodeZOffset1, rte$nodeZOffset2) + radius;
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
        setNodeXOffset1(oth.getNodeXOffset1());
        setNodeYOffset1(oth.getNodeYOffset1());
        setNodeZOffset1(oth.getNodeZOffset1());
        setNodeXOffset2(oth.getNodeXOffset2());
        setNodeYOffset2(oth.getNodeYOffset2());
        setNodeZOffset2(oth.getNodeZOffset2());
        setNodePitch1(oth.getNodePitch1());
        setNodePitch2(oth.getNodePitch2());

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
        setCollisionEnabled(oth.isCollisionEnabled());
        setCollisionWidth(oth.getCollisionWidth());
        setCollisionHeight(oth.getCollisionHeight());
        setCollisionLength(oth.getCollisionLength());
        setCollisionSpacing(oth.getCollisionSpacing());
        setCollisionYOffset(oth.getCollisionYOffset());
        setCollisionSideOffset(oth.getCollisionSideOffset());
        setMechanismGroup(oth.getMechanismGroup());
        setRailScriptId(oth.getRailScriptId());
    }

    @Override
    public Rail getTransposition() {
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        Angle a1 = getExtraAngle1();
        Angle a2 = getExtraAngle2();
        if (p1 == null || p2 == null || a1 == null || a2 == null) return (Rail)(Object)this;
        final double newRad1 = Double.isFinite(rte$angle2Rad) ? rte$angle2Rad : a2.angleRadians;
        final double newRad2 = Double.isFinite(rte$angle1Rad) ? rte$angle1Rad : a1.angleRadians;
        Rail result = RailRebuildHelper.rebuildWithEndpoints((Rail)(Object)this, p2, a2, p1, a1);
        long sl1 = rte$accessor().rte$getSpeedLimit1();
        long sl2 = rte$accessor().rte$getSpeedLimit2();
        if (sl1 != sl2) {
            RailAccessor resultAcc = (RailAccessor)(Object) result;
            resultAcc.rte$setSpeedLimit1(sl2);
            resultAcc.rte$setSpeedLimit2(sl1);
        }
        RailExtraSupplier extra = (RailExtraSupplier)(Object)result;
        extra.setRenderReversed(!rte$isSecondaryDir);
        final cn.jstxjf_.rte.data.BezierCurve sourceBezier = getBezierCurve();
        if (sourceBezier != null) {
            extra.setBezier(sourceBezier.getTransposition());
        }
        extra.setCollisionSideOffset(-getCollisionSideOffset());
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
        extra.setExtraAngle1Rad(newRad1);
        extra.setExtraAngle2Rad(newRad2);
        RailAngleExtra.correctStraightRailMathIfApplicable(result, newRad1, newRad2);
        return result;
    }

    @Unique private float rte$vTheta;

    @Unique
    private static float rte$collisionValue(float value, float min, float max, float fallback) {
        return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    @Unique
    private void rte$markCollisionDirty() {
        if (rte$collisionEnabled) {
            cn.jstxjf_.rte.util.RailCollisionManager.markDirty();
        }
    }

    @Override
    public double getPositionY(double rawValue) {
        final double baseLength = rte$getBaseLength();
        final double effectiveLength = getLength();
        final double ratio = effectiveLength <= ACCEPT_THRESHOLD
                ? 0 : Mth.clamp(rawValue / effectiveLength, 0, 1);
        final double baseValue = baseLength * ratio;
        final double baseY = usesBezierCurve(rte$pathMode) && rte$bezier != null
                ? rte$bezier.getPosition(baseValue).y : rte$getPositionYBase(baseValue);
        return baseY + rte$nodeYOffsetAt(ratio) + rte$rollingYOffset(baseValue);
    }

    @Unique
    private static Vec3 rte$direction(double yaw, double pitch) {
        final double horizontal = Math.cos(pitch);
        return new Vec3(Math.cos(yaw) * horizontal, Math.sin(pitch), Math.sin(yaw) * horizontal);
    }

    @Unique
    private double rte$getPositionYBase(double rawValue) {
        double length = rte$getBaseLength();
        if (length == 0) return 0;
        Position p1 = rte$accessor().rte$getPosition1();
        Position p2 = rte$accessor().rte$getPosition2();
        if (p1 == null || p2 == null) return 0;
        int yStart = (int)p1.getY();
        int yEnd = (int)p2.getY();

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
        return result;
    }

    @Override public void setExtraAngle1Rad(double rad) { rte$angle1Rad = rad; rte$markCollisionDirty(); }
    @Override public void setExtraAngle2Rad(double rad) { rte$angle2Rad = rad; rte$markCollisionDirty(); }
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
