package cn.jstxjf_.rte.scripting.rail;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.render.rail.BakedRail;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Rail;

public final class RailScriptRail {

    private static final double TANGENT_STEP = 0.1;

    public final Rail rail;
    public final String modelKey;
    private final RailExtraSupplier supplier;
    private final double length;
    private final Vec3 origin;

    RailScriptRail(Rail rail, String modelKey) {
        this.rail = rail;
        this.modelKey = modelKey;
        this.supplier = (RailExtraSupplier) (Object) rail;
        this.length = supplier.getLength();
        this.origin = supplier.getPosition(0);
    }

    public String getId() {
        return rail.getHexId();
    }

    public String getModelKey() {
        return modelKey;
    }

    public Rail getRail() {
        return rail;
    }

    public double getLength() {
        return length;
    }

    public boolean isReversed() {
        return supplier.getRenderReversed();
    }

    public boolean getRenderReversed() {
        return supplier.getRenderReversed();
    }

    public java.util.List<String> getModelKeys() {
        return supplier.getModelKeys();
    }

    public float getVerticalCurveRadius() {
        return supplier.getVerticalCurveRadius();
    }

    public int getHeight() {
        return supplier.getHeight();
    }

    public java.util.Map<Double, Float> getRollAngleMap() {
        return supplier.getRollAngleMap();
    }

    public java.util.Map<String, String> getCustomConfigs() {
        return supplier.getCustomConfigs();
    }

    public int getOpeningDirection() {
        return supplier.getOpeningDirection();
    }

    public String getRailType() {
        return supplier.getRailType();
    }

    public int getSpeedLimitKph() {
        return supplier.getSpeedLimitKph();
    }

    public float getYOffset() {
        final RailModelProperties properties = RailModelRegistry.getProperty(modelKey);
        return properties == null ? 0 : properties.yOffset;
    }

    public Vector3f getOrigin() {
        return new Vector3f((float) origin.x, (float) origin.y, (float) origin.z);
    }

    public Vector3f getPosition(double distance) {
        final Vec3 position = relative(clamp(distance));
        return new Vector3f((float) position.x, (float) position.y, (float) position.z);
    }

    public Vector3f getWorldPosition(double distance) {
        final Vec3 position = supplier.getPosition(clamp(distance));
        return new Vector3f((float) position.x, (float) position.y, (float) position.z);
    }

    public float getRollAngle(double distance) {
        return RailExtraSupplier.getRollAngle(rail, clamp(distance));
    }

    public Matrices getPose(double distance) {
        final double at = clamp(distance);
        final double before = Math.max(0, at - TANGENT_STEP);
        final double after = Math.min(length, at + TANGENT_STEP);
        return new Matrices(BakedRail.getLookAtMat(relative(at), relative(before), relative(after),
            getRollAngle(at), getYOffset(), isReversed()));
    }

    double originX() {
        return origin.x;
    }

    double originY() {
        return origin.y;
    }

    double originZ() {
        return origin.z;
    }

    boolean isValid() {
        return Double.isFinite(length) && length > 0
            && Double.isFinite(origin.x) && Double.isFinite(origin.y) && Double.isFinite(origin.z);
    }

    private Vec3 relative(double distance) {
        return supplier.getPosition(distance).subtract(origin);
    }

    private double clamp(double distance) {
        if (!Double.isFinite(distance)) {
            return 0;
        }
        return Math.max(0, Math.min(length, distance));
    }
}
