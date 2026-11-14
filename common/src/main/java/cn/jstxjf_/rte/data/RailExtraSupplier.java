package cn.jstxjf_.rte.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.jstxjf_.rte.data.ConfigResponder;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public interface RailExtraSupplier {

    int PATH_MODE_ORIGINAL = 0;
    int PATH_MODE_AUTO_BEZIER = 1;
    int PATH_MODE_CURVE_FITTING = 2;
    int PATH_MODE_MANUAL_BEZIER = 3;

    static boolean usesBezierCurve(int pathMode) {
        return pathMode == PATH_MODE_AUTO_BEZIER
            || pathMode == PATH_MODE_CURVE_FITTING
            || pathMode == PATH_MODE_MANUAL_BEZIER;
    }

    static boolean requiresBezierCurve(int pathMode) {
        return pathMode == PATH_MODE_AUTO_BEZIER
            || pathMode == PATH_MODE_MANUAL_BEZIER;
    }

    String getModelKey();

    void setModelKey(String key);

    java.util.List<String> getModelKeys();

    void setModelKeys(java.util.List<String> keys);

    boolean getRenderReversed();

    void setRenderReversed(boolean value);

    float getVerticalCurveRadius();

    void setVerticalCurveRadius(float value);

    int getHeight();

    Map<Double, Float> getRollAngleMap();

    void setRollAngleMap(Map<Double, Float> rollAngleMap);

    Map<String, String> getCustomConfigs();

    void setCustomConfigs(Map<String, String> customConfigs);

    Map<String, ConfigResponder> getCustomResponders();

    void setCustomResponders(Map<String, ConfigResponder> customResponders);

    void setOpeningDirectionRaw(int direction);

    void setOpeningDirection(int direction);

    int getOpeningDirection();

    int getOpeningDirectionRaw();

    void setRailType(String railType);

    String getRailType();

    void setSpeedLimitKph(int kph);

    int getSpeedLimitKph();

    boolean isCollisionEnabled();

    void setCollisionEnabled(boolean value);

    float getCollisionWidth();

    void setCollisionWidth(float value);

    float getCollisionHeight();

    void setCollisionHeight(float value);

    float getCollisionLength();

    void setCollisionLength(float value);

    float getCollisionSpacing();

    void setCollisionSpacing(float value);

    float getCollisionYOffset();

    void setCollisionYOffset(float value);

    float getCollisionSideOffset();

    void setCollisionSideOffset(float value);

    void partialCopyFrom(Object other);

    void setRollingOffset(float rollingOffset);

    float getRollingOffset();

    double getNodeXOffset1();

    void setNodeXOffset1(double value);

    double getNodeYOffset1();

    void setNodeYOffset1(double value);

    double getNodeYOffset2();

    void setNodeYOffset2(double value);

    double getNodeZOffset1();

    void setNodeZOffset1(double value);

    double getNodeXOffset2();

    void setNodeXOffset2(double value);

    double getNodeZOffset2();

    void setNodeZOffset2(double value);

    double getNodePitch1();

    void setNodePitch1(double value);

    double getNodePitch2();

    void setNodePitch2(double value);

    boolean hasNodeYOffset();

    boolean hasNodeOffset();

    String getMechanismGroup();

    void setMechanismGroup(String group);

    String getRailScriptId();

    void setRailScriptId(String scriptId);

    cn.jstxjf_.rte.mechanism.MechanismTransform getMechanismTransform();

    void setMechanismTransform(cn.jstxjf_.rte.mechanism.MechanismTransform transform);

    boolean isMechanismHidden();

    void setMechanismHidden(boolean hidden);

    default boolean usesExtendedGeometry() {
        return (usesBezierCurve(getPathMode()) && getBezierCurve() != null) || hasNodeOffset()
            || !getMechanismTransform().isIdentity();
    }

    boolean isStraightOnly();

    int getPathMode();

    void changePathMode(int mode);

    void regenerateAutoBezier();

    boolean railMathStartsAtPosition1();

    org.mtr.core.tool.Vector railMathPositionFromP1(double value);

    void setPathModeRaw(int mode);

    void setBezier(BezierCurve bezier);

    BezierCurve getBezierCurve();

    boolean isBetween(double x, double y, double z, double radius);

    Rail getTransposition();

    double getPositionY(double rawValue);

    boolean couldSwitchModeTo(int mode);

    void sendUpdateC2S();

    void setExtraAngle1Rad(double rad);

    void setExtraAngle2Rad(double rad);

    double getExtraAngle1Rad();

    double getExtraAngle2Rad();

    Position getExtraPosition1();

    Position getExtraPosition2();

    Angle getExtraAngle1();

    Angle getExtraAngle2();

    Angle getOriginalAngle1();

    Angle getOriginalAngle2();

    Rail.Shape getExtraShape();

    double getExtraVerticalRadius();

    TransportMode getExtraTransportMode();

    boolean getExtraCanTurnBack();

    boolean getExtraCanHaveSignal();

    ObjectArrayList<String> getExtraStyles();

    double getLength();

    Vec3 getPosition(double value);

    java.util.concurrent.atomic.AtomicReference<java.util.function.BiFunction<Object, Position, Object>> ROLL_NEIGHBOR_RESOLVER = new java.util.concurrent.atomic.AtomicReference<>();

    java.util.concurrent.ConcurrentHashMap<Integer, Long> ROLL_DEBUG_THROTTLE = new java.util.concurrent.ConcurrentHashMap<>();

    static float getRollAngle(Object rail, double value) {
        if (!(rail instanceof RailExtraSupplier supplier))
            return 0;
        Map<Double, Float> rollAngleMap = supplier.getRollAngleMap();
        if (rollAngleMap == null || rollAngleMap.isEmpty()) {
            return 0;
        }

        final boolean reversed = supplier.getRenderReversed();
        final float k = reversed ? -1F : 1F;
        final double queryValue = reversed ? supplier.getLength() - value : value;

        List<Double> keys = new ArrayList<>(rollAngleMap.keySet());
        keys.sort(Double::compareTo);
        double firstKey = keys.get(0);
        double lastKey = keys.get(keys.size() - 1);

        final float result;
        if (queryValue <= firstKey) {
            result = k * rollAngleMap.get(firstKey);
        } else if (queryValue >= lastKey) {
            result = k * rollAngleMap.get(lastKey);
        } else {
            result = interpolateRoll(rollAngleMap, keys, queryValue, k);
        }
        return result;
    }

    private static float sampleRollMap(RailExtraSupplier supplier, double fromP1) {
        final Map<Double, Float> rollAngleMap = supplier.getRollAngleMap();
        if (rollAngleMap == null || rollAngleMap.isEmpty()) {
            return 0;
        }
        final List<Double> keys = new ArrayList<>(rollAngleMap.keySet());
        keys.sort(Double::compareTo);
        final double firstKey = keys.get(0);
        final double lastKey = keys.get(keys.size() - 1);
        if (fromP1 <= firstKey) {
            return rollAngleMap.get(firstKey);
        }
        if (fromP1 >= lastKey) {
            return rollAngleMap.get(lastKey);
        }
        return interpolateRoll(rollAngleMap, keys, fromP1, 1F);
    }

    static float getRollAngleForPath(Object rail, double pathDistance, boolean reversePositions) {
        if (!(rail instanceof RailExtraSupplier supplier)) {
            return 0;
        }
        final Map<Double, Float> pathMap = supplier.getRollAngleMap();
        if (pathMap == null || pathMap.isEmpty()) {
            int clientMapSize = -1;
            boolean clientFound = false;
            try {
                final String hexId = ((Rail) (Object) supplier).getHexId();
                final Object clientRail = org.mtr.mod.client.MinecraftClientData.getInstance().railIdMap.get(hexId);
                if (clientRail instanceof RailExtraSupplier clientExtra && clientExtra != supplier) {
                    clientFound = true;
                    final Map<Double, Float> clientMap = clientExtra.getRollAngleMap();
                    clientMapSize = clientMap == null ? 0 : clientMap.size();
                    if (clientMap != null && !clientMap.isEmpty()) {
                        supplier.setRollAngleMap(new java.util.HashMap<>(clientMap));
                        supplier.setRollingOffset(clientExtra.getRollingOffset());
                    }
                }
            } catch (Exception ignored) {
            }
        }
        final double length = supplier.getLength();
        boolean startsAtP1;
        try {
            startsAtP1 = supplier.railMathStartsAtPosition1();
        } catch (Exception e) {
            startsAtP1 = true;
        }
        final boolean travelingP1ToP2 = reversePositions != startsAtP1;
        final double fromP1 = travelingP1ToP2 ? pathDistance : length - pathDistance;
        final float raw = sampleRollMap(supplier, fromP1);
        final float result = travelingP1ToP2 ? raw : -raw;
        return result;
    }

    static float getRollAngleRaw(Object rail, double value) {
        return getRollAngle(rail, value);
    }

    private static float interpolateRoll(Map<Double, Float> rollAngleMap, List<Double> keys, double value, float k) {
        double prevKey = keys.get(0);
        for (int i = 1; i < keys.size(); i++) {
            double currentKey = keys.get(i);
            if (prevKey <= value && value < currentKey) {
                double t = (value - prevKey) / (currentKey - prevKey);
                double cosFactor = (1 - Math.cos(t * Math.PI)) / 2.0;
                float interpolated = (float) (rollAngleMap.get(prevKey) * (1 - cosFactor)
                        + rollAngleMap.get(currentKey) * cosFactor);
                return k * interpolated;
            }
            prevKey = currentKey;
        }
        return 0;
    }

    static Float neighborEdgeRollForAlignment(Object selfRail, boolean atP1) {
        try {
            if (!(selfRail instanceof RailExtraSupplier self)) return null;
            java.util.function.BiFunction<Object, Position, Object> resolver = ROLL_NEIGHBOR_RESOLVER.get();
            if (resolver == null) return null;
            final Position node = atP1 ? self.getExtraPosition1() : self.getExtraPosition2();
            if (node == null) return null;
            final Object neighbor = resolver.apply(selfRail, node);
            if (!(neighbor instanceof RailExtraSupplier neighborExtra)) return null;
            final Position np1 = neighborExtra.getExtraPosition1();
            if (np1 == null) return null;
            final Map<Double, Float> neighborMap = neighborExtra.getRollAngleMap();
            if (neighborMap == null || neighborMap.isEmpty()) return null;
            final List<Double> keys = new ArrayList<>(neighborMap.keySet());
            keys.sort(Double::compareTo);
            final boolean atNp1 = node.equals(np1);
            final float edge = atNp1 ? neighborMap.get(keys.get(0)) : neighborMap.get(keys.get(keys.size() - 1));
            final boolean sameDirection = atP1 != atNp1;
            return sameDirection ? edge : -edge;
        } catch (Exception e) {
            return null;
        }
    }

    static float getVTheta(Object rail, double verticalCurveRadius) {
        if (!(rail instanceof RailExtraSupplier))
            return 0;
        RailExtraSupplier supplier = (RailExtraSupplier) rail;
        double H = Math.abs(supplier.getHeight());
        double L = supplier.getLength();
        double R = verticalCurveRadius;
        if (R == 0 || L == 0)
            return 0;
        double discriminant = H * H - 4 * R * H + L * L;
        if (discriminant < 0)
            return 0;
        return 2 * (float) Mth.atan2(Math.sqrt(discriminant) - L, H - 4 * R);
    }
}
