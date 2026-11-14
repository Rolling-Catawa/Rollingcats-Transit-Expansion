package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.data.VehicleLightOverrides;
import cn.jstxjf_.rte.data.VehicleLightRegistry;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Light;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VehicleLightEditor {

    private static final long STALE_FRAMES = 40;
    private static final float MARKER_SIZE = 0.09F;
    private static final float AXIS_WIDTH = 0.025F;

    public static final class EditableLight {
        public Type type = Type.HEADLIGHT;
        public final float[] position = {0, 1.2F, 10};
        public final float[] axis = {0, 0, 1};
        public final float[] color = {1, 0.95F, 0.86F};
        public final float[] brightness = {1};
        public final float[] flareSize = {1.1F};
        public final int[] blink = {0, 0};
        public boolean mirrorX;

        public EditableLight copy() {
            return fromJson(toJson());
        }

        public JsonObject toJson() {
            final JsonObject object = new JsonObject();
            object.addProperty("type", type.name().toLowerCase(Locale.ROOT));
            object.add("pos", vector(position));
            object.add("axis", vector(axis));
            object.addProperty("color", String.format(Locale.ROOT, "#%06X", rgb()));
            object.addProperty("brightness", brightness[0]);
            object.addProperty("flareSize", flareSize[0]);
            if (blink[0] > 0 && blink[1] > 0) {
                final JsonArray array = new JsonArray();
                array.add(blink[0]);
                array.add(blink[1]);
                array.add(0);
                object.add("blink", array);
            }
            if (mirrorX) {
                object.addProperty("mirrorX", true);
            }
            return object;
        }

        public int rgb() {
            return (channel(color[0]) << 16) | (channel(color[1]) << 8) | channel(color[2]);
        }

        static EditableLight fromJson(JsonObject object) {
            return from(VehicleLightRegistry.readLight(object), object.has("mirrorX") && object.get("mirrorX").getAsBoolean());
        }

        static EditableLight from(Light light, boolean mirrorX) {
            final EditableLight editable = new EditableLight();
            editable.type = light.type();
            editable.position[0] = light.x();
            editable.position[1] = light.y();
            editable.position[2] = light.z();
            editable.axis[0] = light.axisX();
            editable.axis[1] = light.axisY();
            editable.axis[2] = light.axisZ();
            editable.color[0] = ((light.rgb() >> 16) & 0xFF) / 255F;
            editable.color[1] = ((light.rgb() >> 8) & 0xFF) / 255F;
            editable.color[2] = (light.rgb() & 0xFF) / 255F;
            editable.brightness[0] = light.brightness();
            editable.flareSize[0] = Math.max(light.flareWidth(), light.flareHeight());
            editable.blink[0] = light.blinkOnMillis();
            editable.blink[1] = light.blinkOffMillis();
            editable.mirrorX = mirrorX;
            return editable;
        }

        private static JsonArray vector(float[] values) {
            final JsonArray array = new JsonArray();
            for (final float value : values) {
                array.add(Math.round(value * 1000) / 1000.0);
            }
            return array;
        }

        private static int channel(float value) {
            return Math.max(0, Math.min(255, Math.round(value * 255)));
        }
    }

    private record CarSnapshot(long vehicleId, int car, int carCount, org.mtr.mod.resource.VehicleResource resource, String resourceId, Matrix4f pose, List<Light> lights, long frame) {
    }

    private static final Map<Long, CarSnapshot> CARS = new ConcurrentHashMap<>();
    private static final List<EditableLight> LIGHTS = new ArrayList<>();
    private static final List<float[]> BODY = new ArrayList<>();
    private static final float[] BOUNDS = {-1.5F, 1.5F, 0, 4, -10, 10};
    private static long frame;
    private static boolean active;
    private static long targetKey;
    private static String resourceId = "";
    private static int selected = -1;
    private static volatile List<Light> preview;
    private static final float AXIS_LENGTH = 0.8F;
    private static final float PICK_RADIUS = 0.2F;
    private static final float AXIS_PICK_RADIUS = 0.09F;
    private static final int[] AXIS_COLORS = {0xFFE04040, 0xFF40D040, 0xFF4080FF};
    private static final float[] orbitTarget = {0, 1.6F, 0};
    private static float orbitYaw = 210;
    private static float orbitPitch = 18;
    private static float orbitDistance = 14;
    private static int dragAxis = -1;
    private static float dragStart;
    private static float[] dragOrigin;

    private VehicleLightEditor() {
    }

    public static void record(long vehicleId, int car, int carCount, org.mtr.mod.resource.VehicleResource resource, Matrix4f pose, List<Light> lights) {
        CARS.put(key(vehicleId, car), new CarSnapshot(vehicleId, car, carCount, resource, resource.getId(), new Matrix4f(pose), lights == null ? List.of() : lights, frame));
    }

    public static List<float[]> bodyBoxes() {
        return BODY;
    }

    public static float[] bodyBounds() {
        return BOUNDS.clone();
    }

    public static void beginFrame() {
        frame++;
        if ((frame & 255) == 0) {
            CARS.values().removeIf(snapshot -> frame - snapshot.frame > STALE_FRAMES * 8);
        }
    }

    public static List<Light> previewFor(String resource) {
        return active && resource != null && resource.equalsIgnoreCase(resourceId) ? preview : null;
    }

    public static boolean isActive() {
        return active;
    }

    public static String resourceId() {
        return resourceId;
    }

    public static List<EditableLight> lights() {
        return LIGHTS;
    }

    public static int selected() {
        return selected;
    }

    public static void select(int index) {
        selected = index >= 0 && index < LIGHTS.size() ? index : -1;
    }

    public static boolean open(Vec3 camera, long preferredVehicle) {
        final List<CarSnapshot> cars = sortedCars(camera, preferredVehicle);
        if (cars.isEmpty()) {
            return false;
        }
        target(cars.get(0));
        active = true;
        return true;
    }

    public static void cycle(Vec3 camera, int direction) {
        final List<CarSnapshot> cars = sortedCars(camera, 0);
        if (cars.isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < cars.size(); i++) {
            if (key(cars.get(i).vehicleId, cars.get(i).car) == targetKey) {
                index = i;
            }
        }
        target(cars.get(Math.floorMod(index + direction, cars.size())));
    }

    public static void close() {
        active = false;
        preview = null;
        LIGHTS.clear();
        selected = -1;
        dragAxis = -1;
    }

    public static boolean isOrbiting() {
        return active && CARS.containsKey(targetKey);
    }

    public static Vec3 orbitEye() {
        final Vector3f centre = orbitCentre();
        final float[] forward = forward();
        return new Vec3(centre.x - forward[0] * orbitDistance, centre.y - forward[1] * orbitDistance, centre.z - forward[2] * orbitDistance);
    }

    public static float orbitYaw() {
        return orbitYaw;
    }

    public static float orbitPitch() {
        return orbitPitch;
    }

    public static void orbit(float deltaX, float deltaY) {
        orbitYaw += deltaX * 0.4F;
        orbitPitch = Math.max(-89, Math.min(89, orbitPitch + deltaY * 0.4F));
    }

    public static void zoom(float wheel) {
        orbitDistance = Math.max(1.2F, Math.min(80, orbitDistance * (1 - wheel * 0.12F)));
    }

    public static void pan(float deltaX, float deltaY) {
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (snapshot == null) {
            return;
        }
        final float[] forward = forward();
        final Vector3f right = new Vector3f(forward[0], forward[1], forward[2]).cross(new Vector3f(0, 1, 0));
        if (right.lengthSquared() < 1e-6F) {
            right.set(1, 0, 0);
        }
        right.normalize();
        final Vector3f up = new Vector3f(right).cross(new Vector3f(forward[0], forward[1], forward[2])).normalize();
        final float speed = orbitDistance * 0.0018F;
        final Vector3f worldDelta = right.mul(-deltaX * speed).add(up.mul(deltaY * speed));
        final Vector3f local = new Matrix4f(snapshot.pose).invert().transformDirection(worldDelta);
        orbitTarget[0] += local.x;
        orbitTarget[1] -= local.y;
        orbitTarget[2] -= local.z;
    }

    public static void moveFocus(float forwardAmount, float rightAmount, float upAmount, float seconds) {
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (snapshot == null || seconds <= 0) {
            return;
        }
        final float[] forward = forward();
        final Vector3f flat = new Vector3f(forward[0], 0, forward[2]);
        if (flat.lengthSquared() < 1e-6F) {
            flat.set(0, 0, 1);
        }
        flat.normalize();
        final Vector3f right = new Vector3f(flat).cross(new Vector3f(0, 1, 0)).normalize();
        final float speed = Math.max(2, orbitDistance * 0.8F) * seconds;
        final Vector3f worldDelta = flat.mul(forwardAmount * speed).add(right.mul(rightAmount * speed)).add(0, upAmount * speed, 0);
        final Vector3f local = new Matrix4f(snapshot.pose).invert().transformDirection(worldDelta);
        orbitTarget[0] += local.x;
        orbitTarget[1] -= local.y;
        orbitTarget[2] -= local.z;
    }

    public static void focusSelected() {
        if (selected >= 0) {
            System.arraycopy(LIGHTS.get(selected).position, 0, orbitTarget, 0, 3);
            orbitDistance = Math.min(orbitDistance, 4);
        }
    }

    public static void focusCar() {
        orbitTarget[0] = 0;
        orbitTarget[1] = 1.6F;
        orbitTarget[2] = 0;
        orbitDistance = 14;
    }

    public static boolean press(double ndcX, double ndcY) {
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (!active || snapshot == null) {
            return false;
        }
        final Vector3f direction = ray(ndcX, ndcY);
        if (direction == null) {
            return false;
        }
        final Vec3 camera = orbitEye();
        final Vector3f eye = new Vector3f((float) camera.x, (float) camera.y, (float) camera.z);
        if (selected >= 0) {
            final EditableLight light = LIGHTS.get(selected);
            final Vector3f position = world(snapshot.pose, light.position[0], light.position[1], light.position[2]);
            for (int a = 0; a < 3; a++) {
                final float[] closest = closest(eye, direction, position, axisDirection(snapshot.pose, a));
                if (closest != null && closest[0] > 0 && closest[1] >= -0.05F && closest[1] <= AXIS_LENGTH + 0.1F && closest[2] <= AXIS_PICK_RADIUS * Math.max(1, closest[0] / 6)) {
                    dragAxis = a;
                    dragStart = closest[1];
                    dragOrigin = light.position.clone();
                    return true;
                }
            }
        }
        int best = -1;
        float bestAlong = Float.MAX_VALUE;
        for (int i = 0; i < LIGHTS.size(); i++) {
            final EditableLight light = LIGHTS.get(i);
            for (final float side : light.mirrorX ? new float[]{1, -1} : new float[]{1}) {
                final Vector3f position = world(snapshot.pose, light.position[0] * side, light.position[1], light.position[2]);
                final Vector3f toPoint = new Vector3f(position).sub(eye);
                final float along = toPoint.dot(direction);
                if (along <= 0) {
                    continue;
                }
                final float distance = new Vector3f(direction).mul(along).sub(toPoint).length();
                if (distance <= PICK_RADIUS * Math.max(1, along / 6) && along < bestAlong) {
                    best = i;
                    bestAlong = along;
                }
            }
        }
        if (best >= 0) {
            selected = best;
            return true;
        }
        return false;
    }

    public static boolean isDraggingAxis() {
        return dragAxis >= 0;
    }

    public static void drag(double ndcX, double ndcY) {
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (dragAxis < 0 || snapshot == null || selected < 0 || selected >= LIGHTS.size()) {
            return;
        }
        final Vector3f direction = ray(ndcX, ndcY);
        if (direction == null) {
            return;
        }
        final Vec3 camera = orbitEye();
        final Vector3f eye = new Vector3f((float) camera.x, (float) camera.y, (float) camera.z);
        final Vector3f origin = world(snapshot.pose, dragOrigin[0], dragOrigin[1], dragOrigin[2]);
        final float[] closest = closest(eye, direction, origin, axisDirection(snapshot.pose, dragAxis));
        if (closest == null) {
            return;
        }
        final EditableLight light = LIGHTS.get(selected);
        System.arraycopy(dragOrigin, 0, light.position, 0, 3);
        light.position[dragAxis] = Math.round((dragOrigin[dragAxis] + closest[1] - dragStart) * 100) / 100F;
        changed();
    }

    public static void release() {
        dragAxis = -1;
    }

    private static Vector3f orbitCentre() {
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (snapshot == null) {
            return new Vector3f();
        }
        return world(snapshot.pose, orbitTarget[0], orbitTarget[1], orbitTarget[2]);
    }

    private static float[] forward() {
        final double yaw = Math.toRadians(orbitYaw);
        final double pitch = Math.toRadians(orbitPitch);
        return new float[]{(float) (-Math.sin(yaw) * Math.cos(pitch)), (float) -Math.sin(pitch), (float) (Math.cos(yaw) * Math.cos(pitch))};
    }

    private static Vector3f ray(double ndcX, double ndcY) {
        final Matrix4f inverse = VehicleLightRenderer.projection().mul(VehicleLightRenderer.view()).invert();
        final org.joml.Vector4f near = inverse.transform(new org.joml.Vector4f((float) ndcX, (float) ndcY, -1, 1));
        final org.joml.Vector4f far = inverse.transform(new org.joml.Vector4f((float) ndcX, (float) ndcY, 1, 1));
        if (Math.abs(near.w) < 1e-9F || Math.abs(far.w) < 1e-9F) {
            return null;
        }
        final Vector3f nearPoint = new Vector3f(near.x / near.w, near.y / near.w, near.z / near.w);
        final Vector3f farPoint = new Vector3f(far.x / far.w, far.y / far.w, far.z / far.w);
        final Vector3f direction = farPoint.sub(nearPoint);
        return direction.lengthSquared() < 1e-12F ? null : direction.normalize();
    }

    private static float[] closest(Vector3f rayOrigin, Vector3f rayDirection, Vector3f lineOrigin, Vector3f lineDirection) {
        final Vector3f w = new Vector3f(rayOrigin).sub(lineOrigin);
        final float b = rayDirection.dot(lineDirection);
        final float d = rayDirection.dot(w);
        final float e = lineDirection.dot(w);
        final float denominator = 1 - b * b;
        if (Math.abs(denominator) < 1e-6F) {
            return null;
        }
        final float s = (b * e - d) / denominator;
        final float t = (e - b * d) / denominator;
        final Vector3f onRay = new Vector3f(rayDirection).mul(s).add(rayOrigin);
        final Vector3f onLine = new Vector3f(lineDirection).mul(t).add(lineOrigin);
        return new float[]{s, t, onRay.distance(onLine)};
    }

    private static Vector3f axisDirection(Matrix4f pose, int axis) {
        return direction(pose, axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0).normalize();
    }

    public static void changed() {
        final List<Light> expanded = new ArrayList<>();
        for (final EditableLight light : LIGHTS) {
            final Light parsed = VehicleLightRegistry.readLight(light.toJson());
            expanded.add(parsed);
            if (light.mirrorX) {
                expanded.add(new Light(parsed.type(), parsed.rgb(), parsed.brightness(), -parsed.x(), parsed.y(), parsed.z(),
                    -parsed.axisX(), parsed.axisY(), parsed.axisZ(), parsed.flareWidth(), parsed.flareHeight(),
                    parsed.beamDiameter(), parsed.beamLength(), parsed.blinkOnMillis(), parsed.blinkOffMillis()));
            }
        }
        preview = List.copyOf(expanded);
    }

    public static void save() throws java.io.IOException {
        VehicleLightOverrides.save(resourceId, toJson());
    }

    public static void resetToAutomatic() throws java.io.IOException {
        VehicleLightOverrides.remove(resourceId);
        VehicleLightRenderer.reload();
        final CarSnapshot snapshot = CARS.get(targetKey);
        LIGHTS.clear();
        selected = -1;
        preview = null;
        if (snapshot != null) {
            seedFrom(snapshot.resourceId, VehicleLightRegistry.lightsFor(snapshot.resourceId));
        }
    }

    public static java.nio.file.Path export() throws java.io.IOException {
        save();
        return VehicleLightOverrides.export();
    }

    public static void add() {
        final EditableLight light = selected >= 0 ? LIGHTS.get(selected).copy() : new EditableLight();
        LIGHTS.add(light);
        selected = LIGHTS.size() - 1;
        changed();
    }

    public static void remove(int index) {
        if (index >= 0 && index < LIGHTS.size()) {
            LIGHTS.remove(index);
            selected = Math.min(selected, LIGHTS.size() - 1);
            changed();
        }
    }

    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource buffers, Vec3 camera) {
        if (!active) {
            return;
        }
        final CarSnapshot snapshot = CARS.get(targetKey);
        if (snapshot == null) {
            return;
        }
        final Matrix4f view = cn.jstxjf_.rte.compat.MatrixCompat.pose(poseStack);
        final VertexConsumer consumer = buffers.getBuffer(VehicleLightRenderType.editor());
        final Vector3f eye = new Vector3f((float) camera.x, (float) camera.y, (float) camera.z);
        for (int i = 0; i < LIGHTS.size(); i++) {
            final EditableLight light = LIGHTS.get(i);
            final boolean isSelected = i == selected;
            final int color = 0xFF000000 | light.rgb();
            for (final float side : light.mirrorX ? new float[]{1, -1} : new float[]{1}) {
                final Vector3f position = world(snapshot.pose, light.position[0] * side, light.position[1], light.position[2]);
                final Vector3f axis = direction(snapshot.pose, light.axis[0] * side, light.axis[1], light.axis[2]);
                square(consumer, view, camera, position, eye, isSelected && side > 0 ? MARKER_SIZE * 1.6F : MARKER_SIZE, isSelected && side > 0 ? 0xFFFFE040 : color);
                bar(consumer, view, camera, position, new Vector3f(position).add(new Vector3f(axis).normalize().mul(0.5F)), eye, AXIS_WIDTH * 0.6F, (color & 0x00FFFFFF) | 0xC0000000);
                if (isSelected && side > 0) {
                    for (int a = 0; a < 3; a++) {
                        final Vector3f end = new Vector3f(position).add(axisDirection(snapshot.pose, a).mul(AXIS_LENGTH));
                        bar(consumer, view, camera, position, end, eye, a == dragAxis ? AXIS_WIDTH * 1.8F : AXIS_WIDTH * 1.2F, AXIS_COLORS[a]);
                        square(consumer, view, camera, end, eye, MARKER_SIZE * 0.7F, AXIS_COLORS[a]);
                    }
                }
            }
        }
        buffers.endBatch(VehicleLightRenderType.editor());
    }

    private static void target(CarSnapshot snapshot) {
        targetKey = key(snapshot.vehicleId, snapshot.car);
        resourceId = snapshot.resourceId;
        focusCar();
        BODY.clear();
        BODY.addAll(ModelLampFinder.bodyBoxes(snapshot.resource, snapshot.car, snapshot.carCount));
        final float halfLength = (float) snapshot.resource.getLength() / 2;
        final float halfWidth = (float) Math.max(1, snapshot.resource.getWidth() / 2);
        BOUNDS[0] = -halfWidth;
        BOUNDS[1] = halfWidth;
        BOUNDS[2] = 0;
        BOUNDS[3] = 4;
        BOUNDS[4] = -halfLength;
        BOUNDS[5] = halfLength;
        for (final float[] box : BODY) {
            BOUNDS[0] = Math.min(BOUNDS[0], box[0]);
            BOUNDS[1] = Math.max(BOUNDS[1], box[1]);
            BOUNDS[2] = Math.min(BOUNDS[2], box[2]);
            BOUNDS[3] = Math.max(BOUNDS[3], box[3]);
            BOUNDS[4] = Math.min(BOUNDS[4], box[4]);
            BOUNDS[5] = Math.max(BOUNDS[5], box[5]);
        }
        LIGHTS.clear();
        selected = -1;
        final JsonArray raw = VehicleLightOverrides.rawFor(resourceId);
        if (raw != null) {
            for (final JsonElement element : raw) {
                if (element.isJsonObject()) {
                    LIGHTS.add(EditableLight.fromJson(element.getAsJsonObject()));
                }
            }
            changed();
        } else {
            seedFrom(resourceId, snapshot.lights);
        }
    }

    private static void seedFrom(String resource, List<Light> lights) {
        LIGHTS.clear();
        if (lights != null) {
            for (final Light light : lights) {
                LIGHTS.add(EditableLight.from(light, false));
            }
        }
        changed();
    }

    private static JsonArray toJson() {
        final JsonArray array = new JsonArray();
        for (final EditableLight light : LIGHTS) {
            array.add(light.toJson());
        }
        return array;
    }

    private static List<CarSnapshot> sortedCars(Vec3 camera, long preferredVehicle) {
        final List<CarSnapshot> cars = new ArrayList<>();
        for (final CarSnapshot snapshot : CARS.values()) {
            if (frame - snapshot.frame <= STALE_FRAMES) {
                cars.add(snapshot);
            }
        }
        final Vector3f eye = new Vector3f((float) camera.x, (float) camera.y, (float) camera.z);
        cars.sort(Comparator.<CarSnapshot>comparingInt(snapshot -> preferredVehicle != 0 && snapshot.vehicleId == preferredVehicle ? 0 : 1)
            .thenComparingDouble(snapshot -> snapshot.pose.getTranslation(new Vector3f()).distanceSquared(eye)));
        return cars;
    }

    private static Vector3f world(Matrix4f pose, float x, float y, float z) {
        return pose.transformPosition(VehicleLightRenderer.lampToCar(x, y, z));
    }

    private static Vector3f direction(Matrix4f pose, float x, float y, float z) {
        return pose.transformDirection(VehicleLightRenderer.lampToCar(x, y, z));
    }

    private static void square(VertexConsumer consumer, Matrix4f view, Vec3 camera, Vector3f centre, Vector3f eye, float size, int color) {
        final Vector3f toEye = new Vector3f(eye).sub(centre).normalize();
        final Vector3f up = Math.abs(toEye.y) > 0.9F ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        final Vector3f u = new Vector3f(up).cross(toEye).normalize().mul(size);
        final Vector3f v = new Vector3f(toEye).cross(u).normalize().mul(size);
        quad(consumer, view, camera, new Vector3f(centre).sub(u).add(v), new Vector3f(centre).add(u).add(v),
            new Vector3f(centre).add(u).sub(v), new Vector3f(centre).sub(u).sub(v), color);
    }

    private static void bar(VertexConsumer consumer, Matrix4f view, Vec3 camera, Vector3f from, Vector3f to, Vector3f eye, float width, int color) {
        final Vector3f along = new Vector3f(to).sub(from);
        if (along.lengthSquared() < 1e-8F) {
            return;
        }
        final Vector3f toEye = new Vector3f(eye).sub(from);
        final Vector3f side = new Vector3f(along).cross(toEye);
        if (side.lengthSquared() < 1e-8F) {
            return;
        }
        side.normalize().mul(width);
        quad(consumer, view, camera, new Vector3f(from).add(side), new Vector3f(to).add(side),
            new Vector3f(to).sub(side), new Vector3f(from).sub(side), color);
    }

    private static void quad(VertexConsumer consumer, Matrix4f view, Vec3 camera, Vector3f a, Vector3f b, Vector3f c, Vector3f d, int color) {
        for (final Vector3f point : new Vector3f[]{a, b, c, d}) {
            cn.jstxjf_.rte.compat.VertexCompat.of(consumer).vertex(view, (float) (point.x - camera.x), (float) (point.y - camera.y), (float) (point.z - camera.z))
                .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >>> 24) & 0xFF)
                .endVertex();
        }
    }

    private static long key(long vehicleId, int car) {
        return vehicleId * 64 + car;
    }
}
