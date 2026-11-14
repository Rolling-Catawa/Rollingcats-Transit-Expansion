package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.data.VehicleLightRegistry;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Light;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Type;
import cn.jstxjf_.rte.render.ShadersModHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.resource.VehicleResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.jstxjf_.rte.compat.McCompat;

public final class VehicleLightRenderer {

    private static final ResourceLocation FLARE_TEXTURE = McCompat.id("rte", "textures/misc/vehicle_light_flare.png");
    private static final ResourceLocation GLARE_TEXTURE = McCompat.id("rte", "textures/misc/vehicle_light_glare.png");
    private static final float GLARE_ASPECT = 2;
    private static final int FRONT_LOCAL_Z = -1;
    private static final long BRAKE_HOLD_MILLIS = 400;
    private static final float FLARE_PULL = 0.9F;
    private static final float SHADER_FLARE_SCALE = 0.8F;
    private static final float GLARE_GROWTH = 1.4F;

    private static final Map<Long, BrakeState> BRAKE_STATES = new HashMap<>();
    private static final Map<String, List<Light>> AUTO_LIGHTS = new HashMap<>();
    private static final List<Job> JOBS = new ArrayList<>();
    private static final List<VehicleLightPass.Spot> SPOTS = new ArrayList<>();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static final Matrix4f VIEW = new Matrix4f();
    private static final float HEAD_RANGE = 48;
    private static final float HEAD_COS_OUTER = (float) Math.cos(Math.toRadians(26));
    private static final float HEAD_COS_INNER = (float) Math.cos(Math.toRadians(9));
    private static final float HEAD_INTENSITY = 1.8F;
    private static final float HEAD_TILT = 0.06F;
    private static final float TAIL_RANGE = 6;
    private static final float TAIL_COS_OUTER = (float) Math.cos(Math.toRadians(85));
    private static final float TAIL_COS_INNER = (float) Math.cos(Math.toRadians(40));
    private static final float TAIL_INTENSITY = 0.35F;
    private static final float PER_LAMP_SHARE = 0.6F;
    private static long frame;

    private record Job(Matrix4f pose, List<Light> lights, float flareFactor, float darkness) {
    }

    private static final class BrakeState {
        double speed;
        long frame;
        long brakingUntil;
        boolean braking;
    }

    private VehicleLightRenderer() {
    }

    public static void reload() {
        AUTO_LIGHTS.clear();
        ModelLampFinder.reset();
        ScriptedLamps.reset();
        BRAKE_STATES.clear();
    }

    public static void beginFrame() {
        JOBS.clear();
        VehicleLightEditor.beginFrame();
        frame++;
        if ((frame & 511) == 0) {
            BRAKE_STATES.values().removeIf(state -> frame - state.frame > 600);
        }
    }

    public static void queue(RenderVehicleCarFrameState state, VehicleResource vehicleResource, float rollDegrees) {
        if (!ClientConfig.vehicleLights || ClientConfig.vanillaVehicleMode || state == null || state.vehicle == null || vehicleResource == null
            || state.absolutePositionAndRotation == null || ShadersModHandler.isRenderingShadowPass()) {
            return;
        }
        final VehicleExtension vehicle = state.vehicle;
        final int carCount;
        try {
            carCount = vehicle.vehicleExtraData.immutableVehicleCars.size();
        } catch (Exception e) {
            return;
        }
        final List<Light> configured = VehicleLightRegistry.lightsFor(vehicleResource.getId());
        final List<Light> resolved = configured != null ? configured
            : ClientConfig.vehicleLightsAuto ? automaticLights(vehicle.getId(), vehicleResource, state.carNumber, carCount) : null;
        final PositionAndRotation pose = state.absolutePositionAndRotation;
        final Matrix4f carPose = carToWorld(pose);
        if (Math.abs(rollDegrees) > 1e-4F) {
            carPose.translate(0, 1, 0).rotateZ((float) Math.toRadians(rollDegrees)).translate(0, -1, 0);
        }
        VehicleLightEditor.record(vehicle.getId(), state.carNumber, carCount, vehicleResource, carPose, resolved);
        final List<Light> preview = VehicleLightEditor.previewFor(vehicleResource.getId());
        final List<Light> lights = preview != null ? preview : resolved;
        if (lights == null || lights.isEmpty()) {
            return;
        }

        final boolean onRoute = vehicle.getIsOnRoute() || vehicle.getSpeed() > 1e-6;
        final boolean reversed = vehicle.getReversed();
        final boolean braking = isBraking(vehicle, onRoute);
        final boolean doorsOpen = vehicle.persistentVehicleData.getDoorValue() > 0;
        final long now = System.currentTimeMillis();
        final int carNumber = state.carNumber;
        final boolean frontLeads = !reversed && carNumber == 0;
        final boolean rearLeads = reversed && carNumber == carCount - 1;
        final boolean frontTrails = reversed && carNumber == 0;
        final boolean rearTrails = !reversed && carNumber == carCount - 1;

        final List<Light> active = new ArrayList<>();
        for (final Light light : lights) {
            final boolean front = light.atFrontEnd();
            final boolean leading = front ? frontLeads : rearLeads;
            final boolean trailing = front ? frontTrails : rearTrails;
            final boolean on = switch (light.type()) {
                case HEADLIGHT -> onRoute && leading;
                case TAILLIGHT -> onRoute && trailing;
                case BRAKE -> onRoute && trailing && braking;
                case DOOR -> doorsOpen;
                case RUNNING -> onRoute;
                case ALWAYS -> true;
            };
            if ((preview != null || on) && blinkVisible(light, now)) {
                active.add(light);
            }
        }
        if (active.isEmpty()) {
            return;
        }

        final float darkness = darkness(pose.light);
        final boolean shaderPack = ShadersModHandler.isShaderPackInUse();
        final float flareFactor = (0.6F + 0.4F * darkness) * (shaderPack ? SHADER_FLARE_SCALE : 1);
        JOBS.add(new Job(carPose, List.copyOf(active), flareFactor, darkness));
    }

    public static void render(PoseStack poseStack, Matrix4f projection, MultiBufferSource.BufferSource buffers, Vec3 camera) {
        SPOTS.clear();
        final Matrix4f view = cn.jstxjf_.rte.compat.MatrixCompat.pose(poseStack);
        PROJECTION.set(projection);
        VIEW.set(view);
        if (JOBS.isEmpty()) {
            return;
        }
        collectSpots(camera);
        final RenderType flareType = VehicleLightRenderType.glow(FLARE_TEXTURE);
        final VertexConsumer flares = buffers.getBuffer(flareType);
        for (final Job job : JOBS) {
            final Emitter emitter = new Emitter(flares, view, job.pose, camera);
            final float[] eye = emitter.cameraLocal();
            for (final Light light : job.lights) {
                if (light.flareWidth() > 0 && light.flareHeight() > 0) {
                    drawFlare(emitter, light, color(light, job.flareFactor), eye);
                }
            }
        }
        buffers.endBatch(flareType);
        final RenderType glareType = VehicleLightRenderType.glow(GLARE_TEXTURE);
        final VertexConsumer glares = buffers.getBuffer(glareType);
        for (final Job job : JOBS) {
            final Emitter emitter = new Emitter(glares, view, job.pose, camera);
            final float[] eye = emitter.cameraLocal();
            for (final Light light : job.lights) {
                if (light.type() == VehicleLightRegistry.Type.HEADLIGHT && light.flareWidth() > 0 && light.flareHeight() > 0) {
                    drawGlare(emitter, light, color(light, job.flareFactor), eye);
                }
            }
        }
        buffers.endBatch(glareType);
        JOBS.clear();
    }

    public static void renderLighting() {
        if (SPOTS.isEmpty()) {
            return;
        }
        VehicleLightPass.render(SPOTS, PROJECTION, VIEW);
        SPOTS.clear();
    }

    private static void collectSpots(Vec3 camera) {
        final List<VehicleLightPass.Spot> candidates = new ArrayList<>();
        for (final Job job : JOBS) {
            for (final Light light : job.lights) {
                addSpot(candidates, job, light, camera);
            }
        }
        candidates.sort((a, b) -> Float.compare(a.x() * a.x() + a.y() * a.y() + a.z() * a.z(), b.x() * b.x() + b.y() * b.y() + b.z() * b.z()));
        for (int i = 0; i < Math.min(VehicleLightPass.MAX_LIGHTS, candidates.size()); i++) {
            SPOTS.add(candidates.get(i));
        }
    }

    private static void addSpot(List<VehicleLightPass.Spot> spots, Job job, Light light, Vec3 camera) {
        final boolean head = light.type() == Type.HEADLIGHT;
        final float intensity = (head ? HEAD_INTENSITY : TAIL_INTENSITY) * PER_LAMP_SHARE * job.darkness * light.brightness();
        if (intensity <= 0.01F) {
            return;
        }
        final int rgb = light.rgb();
        final float[] origin = local(light.x(), light.y(), light.z());
        final float[] axis = normalise(local(light.axisX(), light.axisY() - (head ? HEAD_TILT : 0), light.axisZ()));
        final Vector3f worldOrigin = job.pose.transformPosition(new Vector3f(origin[0], origin[1], origin[2]));
        final Vector3f worldAxis = job.pose.transformDirection(new Vector3f(axis[0], axis[1], axis[2])).normalize();
        spots.add(new VehicleLightPass.Spot(
            (float) (worldOrigin.x - camera.x), (float) (worldOrigin.y - camera.y), (float) (worldOrigin.z - camera.z), head ? HEAD_RANGE : TAIL_RANGE,
            worldAxis.x, worldAxis.y, worldAxis.z, head ? HEAD_COS_OUTER : TAIL_COS_OUTER,
            ((rgb >> 16) & 0xFF) / 255F * intensity, ((rgb >> 8) & 0xFF) / 255F * intensity, (rgb & 0xFF) / 255F * intensity,
            head ? HEAD_COS_INNER : TAIL_COS_INNER));
    }

    private static final class Emitter {

        private final VertexConsumer consumer;
        private final Matrix4f view;
        private final Matrix4f pose;
        private final Vec3 camera;
        private final Vector3f scratch = new Vector3f();

        Emitter(VertexConsumer consumer, Matrix4f view, Matrix4f pose, Vec3 camera) {
            this.consumer = consumer;
            this.view = view;
            this.pose = pose;
            this.camera = camera;
        }

        float[] cameraLocal() {
            final Vector3f local = new Matrix4f(pose).invert().transformPosition(new Vector3f((float) camera.x, (float) camera.y, (float) camera.z));
            return new float[]{local.x, local.y, local.z};
        }

        void vertex(float[] p, float u, float v, int color) {
            pose.transformPosition(p[0], p[1], p[2], scratch);
            cn.jstxjf_.rte.compat.VertexCompat.of(consumer).vertex(view, (float) (scratch.x - camera.x), (float) (scratch.y - camera.y), (float) (scratch.z - camera.z))
                .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >>> 24) & 0xFF)
                .uv(u, v)
                .endVertex();
        }

        void quad(float[] p1, float[] p2, float[] p3, float[] p4, int color) {
            vertex(p1, 0, 1, color);
            vertex(p2, 1, 1, color);
            vertex(p3, 1, 0, color);
            vertex(p4, 0, 0, color);
        }
    }

    private static Matrix4f carToWorld(PositionAndRotation pose) {
        return new Matrix4f()
            .translate((float) pose.position.x(), (float) pose.position.y(), (float) pose.position.z())
            .rotateY((float) (pose.yaw + Math.PI))
            .rotateX((float) (pose.pitch + Math.PI));
    }

    private static List<Light> scriptedLights(List<float[]> lamps, VehicleResource vehicleResource) {
        final float endThreshold = (float) (vehicleResource.getLength() / 2 * 0.6);
        final List<Light> lights = new ArrayList<>(lamps.size() * 2);
        for (final float[] lamp : lamps) {
            if (Math.abs(lamp[2]) < endThreshold) {
                continue;
            }
            final int direction = lamp[2] > 0 ? 1 : -1;
            final float head = Math.max(0.8F, lamp[3] * 4);
            final float tail = Math.max(0.6F, lamp[3] * 3);
            lights.add(new Light(Type.HEADLIGHT, 0xFFF3DC, 1, lamp[0], lamp[1], lamp[2], 0, -0.035F, direction, head, head, 3.2F, 20, 0, 0));
            lights.add(new Light(Type.TAILLIGHT, 0xFF2A1A, 0.9F, lamp[0], lamp[1], lamp[2], 0, 0, direction, tail, tail, 0, 0, 0, 0));
        }
        return lights;
    }

    private static List<Light> automaticLights(long vehicleId, VehicleResource vehicleResource, int carNumber, int carCount) {
        final List<Light> fromModel = ModelLampFinder.find(vehicleResource, carNumber, carCount);
        if (fromModel != null && !fromModel.isEmpty()) {
            return fromModel;
        }
        final List<Light> fromDefinition = LegacyLampReader.lightsFor(vehicleResource.getId(), carNumber, carCount);
        if (fromDefinition != null && !fromDefinition.isEmpty()) {
            return fromDefinition;
        }
        final List<float[]> scripted = ScriptedLamps.lampsFor(vehicleId, carNumber);
        if (scripted != null && !scripted.isEmpty()) {
            final List<Light> fromScript = scriptedLights(scripted, vehicleResource);
            if (!fromScript.isEmpty()) {
                return fromScript;
            }
        }
        return fromModel == null ? null : AUTO_LIGHTS.computeIfAbsent(vehicleResource.getId(), id -> autoLights(vehicleResource));
    }

    private static List<Light> autoLights(VehicleResource vehicleResource) {
        final float halfLength = (float) Math.max(1, vehicleResource.getLength() / 2);
        final float halfWidth = (float) Math.max(0.6, vehicleResource.getWidth() / 2);
        final float headX = halfWidth * 0.62F;
        final float tailX = halfWidth * 0.55F;
        final float end = halfLength + 0.02F;
        final List<Light> lights = new ArrayList<>(8);
        for (final int side : new int[]{-1, 1}) {
            for (final int direction : new int[]{-1, 1}) {
                lights.add(new Light(Type.HEADLIGHT, 0xFFF3DC, 1, side * headX, 1.15F, direction * end,
                    0, -0.035F, direction, 1.1F, 1.1F, 3.2F, 20, 0, 0));
                lights.add(new Light(Type.TAILLIGHT, 0xFF2A1A, 0.9F, side * tailX, 1.0F, direction * end,
                    0, 0, direction, 0.8F, 0.8F, 0, 0, 0, 0));
            }
        }
        return List.copyOf(lights);
    }

    private static boolean isBraking(VehicleExtension vehicle, boolean onRoute) {
        final double speed = vehicle.getSpeed();
        final BrakeState brake = BRAKE_STATES.computeIfAbsent(vehicle.getId(), id -> new BrakeState());
        if (brake.frame != frame) {
            final long now = System.currentTimeMillis();
            if (speed < brake.speed - 1e-7) {
                brake.brakingUntil = now + BRAKE_HOLD_MILLIS;
            }
            brake.braking = onRoute && (speed < 1e-6 || now < brake.brakingUntil);
            brake.speed = speed;
            brake.frame = frame;
        }
        return brake.braking;
    }

    private static boolean blinkVisible(Light light, long now) {
        final int period = light.blinkOnMillis() + light.blinkOffMillis();
        return light.blinkOnMillis() <= 0 || light.blinkOffMillis() <= 0 || now % period < light.blinkOnMillis();
    }

    private static float darkness(int packedLight) {
        final int block = (packedLight >> 4) & 0xF;
        final int sky = (packedLight >> 20) & 0xF;
        float daylight = 1;
        final ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            daylight = clamp((level.getSkyDarken(1) - 0.2F) / 0.8F);
        }
        final float ambient = Math.max(block / 15F * 0.6F, sky / 15F * daylight);
        return clamp(1 - ambient);
    }

    private static int color(Light light, float factor) {
        final int alpha = Math.round(clamp(light.brightness() * factor) * 255);
        return (alpha << 24) | light.rgb();
    }

    public static Matrix4f projection() {
        return new Matrix4f(PROJECTION);
    }

    public static Matrix4f view() {
        return new Matrix4f(VIEW);
    }

    public static Vector3f lampToCar(float x, float y, float z) {
        final float[] local = local(x, y, z);
        return new Vector3f(local[0], local[1], local[2]);
    }

    private static float[] local(float x, float y, float z) {
        return new float[]{-FRONT_LOCAL_Z * x, -y, FRONT_LOCAL_Z * z};
    }

    private static void drawFlare(Emitter emitter, Light light, int color, float[] camera) {
        final float[] centre = local(light.x(), light.y(), light.z());
        final float[] axis = normalise(local(light.axisX(), light.axisY(), light.axisZ()));
        float cx = centre[0] + axis[0] * 0.01F;
        float cy = centre[1] + axis[1] * 0.01F;
        float cz = centre[2] + axis[2] * 0.01F;
        final float dx = camera[0] - cx;
        final float dy = camera[1] - cy;
        final float dz = camera[2] - cz;
        final float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1e-3F) {
            return;
        }
        final float[] view = {dx / distance, dy / distance, dz / distance};
        final float facing = Math.max(0, view[0] * axis[0] + view[1] * axis[1] + view[2] * axis[2]);
        if (facing <= 0.02F) {
            return;
        }
        final float glare = facing * facing * facing;
        final float pull = VehicleLightEditor.isActive() ? 0 : Math.min(FLARE_PULL, distance * 0.5F) / distance;
        final float shrink = 1 - pull;
        cx += dx * pull;
        cy += dy * pull;
        cz += dz * pull;
        final float[][] basis = basis(view);
        final float sizeScale = (0.45F + GLARE_GROWTH * glare) * shrink;
        final float hw = light.flareWidth() / 2 * sizeScale;
        final float hh = light.flareHeight() / 2 * sizeScale;
        final int alpha = Math.round(((color >>> 24) & 0xFF) * (0.25F + 0.45F * glare));
        final int faded = (alpha << 24) | (color & 0xFFFFFF);
        emitter.quad(
            offset(cx, cy, cz, basis[0], -hw, basis[1], hh),
            offset(cx, cy, cz, basis[0], hw, basis[1], hh),
            offset(cx, cy, cz, basis[0], hw, basis[1], -hh),
            offset(cx, cy, cz, basis[0], -hw, basis[1], -hh),
            faded);
    }

    private static void drawGlare(Emitter emitter, Light light, int color, float[] camera) {
        final float[] centre = local(light.x(), light.y(), light.z());
        final float[] axis = normalise(local(light.axisX(), light.axisY(), light.axisZ()));
        float cx = centre[0] + axis[0] * 0.02F;
        float cy = centre[1] + axis[1] * 0.02F;
        float cz = centre[2] + axis[2] * 0.02F;
        final float dx = camera[0] - cx;
        final float dy = camera[1] - cy;
        final float dz = camera[2] - cz;
        final float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1e-3F) {
            return;
        }
        final float[] view = {dx / distance, dy / distance, dz / distance};
        final float facing = Math.max(0, view[0] * axis[0] + view[1] * axis[1] + view[2] * axis[2]);
        if (facing <= 0.2F) {
            return;
        }
        final float glare = (facing - 0.2F) / 0.8F;
        final float strength = glare * glare;
        final float pull = VehicleLightEditor.isActive() ? 0 : Math.min(FLARE_PULL, distance * 0.5F) / distance;
        cx += dx * pull;
        cy += dy * pull;
        cz += dz * pull;
        final float[][] basis = basis(view);
        final float height = light.flareHeight() * (0.5F + 0.9F * strength) * (1 - pull);
        final float hh = height / 2;
        final float hw = hh * GLARE_ASPECT * (1 + 1.5F * strength);
        final int alpha = Math.round(((color >>> 24) & 0xFF) * strength);
        if (alpha <= 0) {
            return;
        }
        final int faded = (alpha << 24) | (color & 0xFFFFFF);
        emitter.quad(
            offset(cx, cy, cz, basis[0], -hw, basis[1], hh),
            offset(cx, cy, cz, basis[0], hw, basis[1], hh),
            offset(cx, cy, cz, basis[0], hw, basis[1], -hh),
            offset(cx, cy, cz, basis[0], -hw, basis[1], -hh),
            faded);
    }

    private static float[] offset(float x, float y, float z, float[] u, float du, float[] v, float dv) {
        return new float[]{x + u[0] * du + v[0] * dv, y + u[1] * du + v[1] * dv, z + u[2] * du + v[2] * dv};
    }

    private static float[][] basis(float[] axis) {
        final float[] reference = Math.abs(axis[1]) > 0.9F ? new float[]{1, 0, 0} : new float[]{0, 1, 0};
        final float[] u = normalise(cross(reference, axis));
        final float[] v = normalise(cross(axis, u));
        return new float[][]{u, v};
    }

    private static float[] cross(float[] a, float[] b) {
        return new float[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    private static float[] normalise(float[] v) {
        final float length = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (length < 1e-6F) {
            return new float[]{0, 0, FRONT_LOCAL_Z};
        }
        return new float[]{v[0] / length, v[1] / length, v[2] / length};
    }

    private static float clamp(float value) {
        return Math.max(0, Math.min(1, value));
    }
}
