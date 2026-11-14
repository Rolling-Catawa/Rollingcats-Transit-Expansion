package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Light;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Type;
import org.mtr.mapping.holder.Box;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mod.generated.resource.ModelPropertiesPartSchema;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.resource.ModelProperties;
import org.mtr.mod.resource.ModelPropertiesPart;
import org.mtr.mod.resource.PartType;
import org.mtr.mod.resource.PartCondition;
import org.mtr.mod.resource.RenderStage;
import org.mtr.mod.resource.VehicleResource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public final class ModelLampFinder {

    private static final long RETRY_MILLIS = 2000;
    private static final double END_FRACTION = 0.3;
    private static final double CELL = 0.2;
    private static final double MAX_LAMP_WIDTH = 0.9;
    private static final double MAX_LAMP_HEIGHT = 0.6;
    private static final double MIN_LAMP_HEIGHT = 0.15;
    private static final double MAX_LAMP_ELEVATION = 4.5;
    private static final double MAX_LAMP_DEPTH = 0.8;
    private static final double BODY_SURFACE_INSET = 0.05;
    private static final int MAX_BODY_BOXES = 2000;

    private static final Map<String, Entry> CACHE = new HashMap<>();
    private static Field conditionField;
    private static Field renderStageField;
    private static Field partDetailsField;
    private static Field boxField;
    private static Field namesField;
    private static Field typeField;
    private static Field rawMeshesField;
    private static Field detailXField;
    private static Field detailYField;
    private static Field detailZField;
    private static Field detailFlippedField;
    private static final Map<DynamicVehicleModel, List<Point>> CAPTURED = new WeakHashMap<>();
    private static boolean reflectionFailed;

    private record Entry(List<Light> lights, long retryAt) {
    }

    private enum Role {HEAD_WHEN_FORWARDS, HEAD_WHEN_BACKWARDS, EITHER}

    private record Point(double x, double y, double z, Role role) {
    }

    private ModelLampFinder() {
    }

    static void reset() {
        CACHE.clear();
    }

    static List<Light> find(VehicleResource vehicleResource, int carNumber, int carCount) {
        final String key = vehicleResource.getId() + "|" + carNumber + "|" + carCount;
        final Entry cached = CACHE.get(key);
        final long now = System.currentTimeMillis();
        if (cached != null && (cached.lights != null || now < cached.retryAt)) {
            return cached.lights;
        }
        final List<Light> lights = scan(vehicleResource, carNumber, carCount);
        CACHE.put(key, new Entry(lights, now + RETRY_MILLIS));
        return lights;
    }

    private static List<Light> scan(VehicleResource vehicleResource, int carNumber, int carCount) {
        if (!prepareReflection()) {
            return List.of();
        }
        final double halfLength = vehicleResource.getLength() / 2;
        final double halfWidth = Math.max(0.8, vehicleResource.getWidth() / 2);
        final List<Point> points = new ArrayList<>();
        final double[] bodyExtent = {0, 0};
        final boolean[] loaded = {false};
        vehicleResource.iterateModels(carNumber, carCount, (index, model) -> {
            loaded[0] = true;
            model.modelProperties.iterateParts(part -> collectBodyExtent(part, bodyExtent));
            final List<Point> captured;
            synchronized (CAPTURED) {
                captured = CAPTURED.get(model);
            }
            if (captured != null) {
                for (final Point point : captured) {
                    if (Math.abs(point.z) >= halfLength * (1 - END_FRACTION)) {
                        points.add(point);
                    }
                }
            } else {
                model.modelProperties.iterateParts(part -> collectPartBoxes(part, points, halfLength));
            }
        });
        if (!loaded[0]) {
            return null;
        }
        final List<Light> lights = new ArrayList<>();
        boolean directional = false;
        for (final Point point : points) {
            directional |= point.role != Role.EITHER;
        }
        for (final boolean front : new boolean[]{true, false}) {
            for (final Role role : Role.values()) {
                if (!directional || role != Role.EITHER) {
                    cluster(points, front, role, lights);
                }
            }
        }
        lights.removeIf(light -> Math.abs(light.x()) > halfWidth + 0.2 || light.y() < 0.2 || light.y() > MAX_LAMP_ELEVATION);
        lights.replaceAll(light -> toBodySurface(light, bodyExtent));
        final StringBuilder summary = new StringBuilder();
        for (final Light light : lights) {
            summary.append(String.format(Locale.ROOT, " %s(%.2f,%.2f,%.2f)", light.type(), light.x(), light.y(), light.z()));
        }
        final String message = "[VehicleLights] {} car {}/{} half {}x{} body {}..{} points {} lamps:{}";
        final Object[] arguments = {vehicleResource.getId(), carNumber, carCount,
            String.format(Locale.ROOT, "%.2f", halfLength), String.format(Locale.ROOT, "%.2f", halfWidth),
            String.format(Locale.ROOT, "%.2f", bodyExtent[1]), String.format(Locale.ROOT, "%.2f", bodyExtent[0]),
            points.size(), summary.length() == 0 ? " none" : summary.toString()};
        if (cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            RTE.LOGGER.info(message, arguments);
        } else {
            RTE.LOGGER.debug(message, arguments);
        }
        return List.copyOf(lights);
    }

    public static void captureObjModel(DynamicVehicleModel model, Map<String, OptimizedModel.ObjModel> nameToObjModels, ModelProperties modelProperties) {
        if (!prepareReflection() || nameToObjModels == null || modelProperties == null) {
            return;
        }
        final List<Point> points = new ArrayList<>();
        final double modelYOffset = modelProperties.getModelYOffset();
        modelProperties.iterateParts(part -> {
            try {
                final Object stage = renderStageField.get(part);
                if ((stage != RenderStage.LIGHT && stage != RenderStage.ALWAYS_ON_LIGHT) || typeField.get(part) != PartType.NORMAL) {
                    return;
                }
                final Role role = roleOf((PartCondition) conditionField.get(part));
                if (role == null) {
                    return;
                }
                final List<?> names = (List<?>) namesField.get(part);
                for (final Object details : (List<?>) partDetailsField.get(part)) {
                    final double tx = detailXField.getDouble(details) / 16;
                    final double ty = detailYField.getDouble(details) / 16 - modelYOffset;
                    final double tz = detailZField.getDouble(details) / 16;
                    final boolean flipped = detailFlippedField.getBoolean(details);
                    for (final Object name : names) {
                        final OptimizedModel.ObjModel objModel = nameToObjModels.get(String.valueOf(name));
                        if (objModel == null) {
                            continue;
                        }
                        for (final Object mesh : (List<?>) rawMeshesField.get(objModel)) {
                            for (final Vertex vertex : ((RawMesh) mesh).vertices) {
                                double x = vertex.position.getX() + tx;
                                final double y = vertex.position.getY() + ty;
                                double z = vertex.position.getZ() + tz;
                                if (flipped) {
                                    x = -x;
                                    z = -z;
                                }
                                points.add(new Point(x, -y, -z, role));
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
        synchronized (CAPTURED) {
            CAPTURED.put(model, List.copyOf(points));
        }
    }

    private static void collectPartBoxes(ModelPropertiesPart part, List<Point> points, double halfLength) {
        try {
            final Object stage = renderStageField.get(part);
            if (stage != RenderStage.LIGHT && stage != RenderStage.ALWAYS_ON_LIGHT) {
                return;
            }
            final Role role = roleOf((PartCondition) conditionField.get(part));
            if (role == null) {
                return;
            }
            for (final Object details : (List<?>) partDetailsField.get(part)) {
                if (boxField.get(details) instanceof Box box) {
                    addPoint(points, box.getMinXMapped(), box.getMinYMapped(), box.getMinZMapped(), role, halfLength);
                    addPoint(points, box.getMaxXMapped(), box.getMaxYMapped(), box.getMaxZMapped(), role, halfLength);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static List<float[]> bodyBoxes(VehicleResource vehicleResource, int carNumber, int carCount) {
        final List<float[]> boxes = new ArrayList<>();
        if (vehicleResource == null || !prepareReflection()) {
            return boxes;
        }
        vehicleResource.iterateModels(carNumber, carCount, (index, model) -> model.modelProperties.iterateParts(part -> {
            try {
                if (renderStageField.get(part) != RenderStage.EXTERIOR) {
                    return;
                }
                for (final Object details : (List<?>) partDetailsField.get(part)) {
                    if (boxField.get(details) instanceof Box box && boxes.size() < MAX_BODY_BOXES) {
                        boxes.add(new float[]{
                            (float) Math.min(box.getMinXMapped(), box.getMaxXMapped()), (float) Math.max(box.getMinXMapped(), box.getMaxXMapped()),
                            (float) -Math.max(box.getMinYMapped(), box.getMaxYMapped()), (float) -Math.min(box.getMinYMapped(), box.getMaxYMapped()),
                            (float) -Math.max(box.getMinZMapped(), box.getMaxZMapped()), (float) -Math.min(box.getMinZMapped(), box.getMaxZMapped())});
                    }
                }
            } catch (Exception ignored) {
            }
        }));
        return boxes;
    }

    private static void collectBodyExtent(ModelPropertiesPart part, double[] extent) {
        try {
            if (renderStageField.get(part) != RenderStage.EXTERIOR) {
                return;
            }
            for (final Object details : (List<?>) partDetailsField.get(part)) {
                if (boxField.get(details) instanceof Box box) {
                    final double a = -box.getMinZMapped();
                    final double b = -box.getMaxZMapped();
                    extent[0] = Math.max(extent[0], Math.max(a, b));
                    extent[1] = Math.min(extent[1], Math.min(a, b));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static Light toBodySurface(Light light, double[] extent) {
        final double surface = light.z() >= 0 ? extent[0] - BODY_SURFACE_INSET : extent[1] + BODY_SURFACE_INSET;
        final boolean outward = light.z() >= 0 ? surface > light.z() : surface < light.z();
        if (!outward || Math.abs(surface) < 1) {
            return light;
        }
        return new Light(light.type(), light.rgb(), light.brightness(), light.x(), light.y(), (float) surface,
            light.axisX(), light.axisY(), light.axisZ(), light.flareWidth(), light.flareHeight(),
            light.beamDiameter(), light.beamLength(), light.blinkOnMillis(), light.blinkOffMillis());
    }

    private static Role roleOf(PartCondition condition) {
        if (condition == PartCondition.ON_ROUTE_FORWARDS) {
            return Role.HEAD_WHEN_FORWARDS;
        }
        if (condition == PartCondition.ON_ROUTE_BACKWARDS) {
            return Role.HEAD_WHEN_BACKWARDS;
        }
        if (condition == PartCondition.NORMAL || condition == PartCondition.AT_DEPOT) {
            return Role.EITHER;
        }
        return null;
    }

    private static void addPoint(List<Point> points, double localX, double localY, double localZ, Role role, double halfLength) {
        if (Math.abs(localZ) < halfLength * (1 - END_FRACTION)) {
            return;
        }
        points.add(new Point(localX, -localY, -localZ, role));
    }

    private static void cluster(List<Point> points, boolean front, Role role, List<Light> lights) {
        final Map<Long, List<Point>> cells = new HashMap<>();
        for (final Point point : points) {
            if (point.role != role || (point.z > 0) != front) {
                continue;
            }
            cells.computeIfAbsent(cellKey(point.x, point.y), key -> new ArrayList<>()).add(point);
        }
        final Map<Long, Boolean> visited = new HashMap<>();
        for (final Long start : cells.keySet()) {
            if (visited.containsKey(start)) {
                continue;
            }
            final List<Point> group = new ArrayList<>();
            final List<Long> queue = new ArrayList<>();
            queue.add(start);
            visited.put(start, true);
            for (int i = 0; i < queue.size(); i++) {
                final long cell = queue.get(i);
                group.addAll(cells.get(cell));
                final int cx = (int) (cell >> 32);
                final int cy = (int) cell;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        final long next = ((long) (cx + dx) << 32) | ((cy + dy) & 0xFFFFFFFFL);
                        if (cells.containsKey(next) && !visited.containsKey(next)) {
                            visited.put(next, true);
                            queue.add(next);
                        }
                    }
                }
            }
            addLamp(group, front, role, lights);
        }
    }

    private static long cellKey(double x, double y) {
        return ((long) (int) Math.floor(x / CELL) << 32) | ((int) Math.floor(y / CELL) & 0xFFFFFFFFL);
    }

    private static void addLamp(List<Point> group, boolean front, Role role, List<Light> lights) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double outerZ = front ? -Double.MAX_VALUE : Double.MAX_VALUE;
        double innerZ = front ? Double.MAX_VALUE : -Double.MAX_VALUE;
        for (final Point point : group) {
            innerZ = front ? Math.min(innerZ, point.z) : Math.max(innerZ, point.z);
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
            outerZ = front ? Math.max(outerZ, point.z) : Math.min(outerZ, point.z);
        }
        final double width = maxX - minX;
        final double height = maxY - minY;
        if (width > MAX_LAMP_WIDTH || height > MAX_LAMP_HEIGHT || Math.abs(outerZ - innerZ) > MAX_LAMP_DEPTH) {
            return;
        }
        final float x = (float) ((minX + maxX) / 2);
        final float y = (float) ((minY + maxY) / 2);
        final float z = (float) outerZ;
        final float size = (float) Math.max(MIN_LAMP_HEIGHT, Math.max(width, height));
        final int direction = front ? 1 : -1;
        final boolean headWhenLeading = role == Role.EITHER || (role == Role.HEAD_WHEN_FORWARDS) == front;
        final boolean tailWhenTrailing = role == Role.EITHER || !headWhenLeading;
        if (headWhenLeading) {
            final float flare = Math.max(0.8F, size * 4);
            lights.add(new Light(Type.HEADLIGHT, 0xFFF3DC, 1, x, y, z, 0, -0.035F, direction, flare, flare, 3.2F, 20, 0, 0));
        }
        if (tailWhenTrailing) {
            final float flare = Math.max(0.6F, size * 3);
            lights.add(new Light(Type.TAILLIGHT, 0xFF2A1A, 0.9F, x, y, z, 0, 0, direction, flare, flare, 0, 0, 0, 0));
        }
    }

    private static boolean prepareReflection() {
        if (reflectionFailed) {
            return false;
        }
        if (detailFlippedField != null) {
            return true;
        }
        try {
            conditionField = accessible(ModelPropertiesPartSchema.class.getDeclaredField("condition"));
            renderStageField = accessible(ModelPropertiesPartSchema.class.getDeclaredField("renderStage"));
            namesField = accessible(ModelPropertiesPartSchema.class.getDeclaredField("names"));
            typeField = accessible(ModelPropertiesPartSchema.class.getDeclaredField("type"));
            partDetailsField = accessible(ModelPropertiesPart.class.getDeclaredField("partDetailsList"));
            final Class<?> details = Class.forName(ModelPropertiesPart.class.getName() + "$PartDetails");
            boxField = accessible(details.getDeclaredField("box"));
            detailXField = accessible(details.getDeclaredField("x"));
            detailYField = accessible(details.getDeclaredField("y"));
            detailZField = accessible(details.getDeclaredField("z"));
            rawMeshesField = accessible(OptimizedModel.ObjModel.class.getDeclaredField("rawMeshes"));
            detailFlippedField = accessible(details.getDeclaredField("flipped"));
            return true;
        } catch (Exception e) {
            RTE.LOGGER.warn("[VehicleLights] Model lamp lookup unavailable: {}", e.toString());
            reflectionFailed = true;
            return false;
        }
    }

    private static Field accessible(Field field) {
        field.setAccessible(true);
        return field;
    }
}
