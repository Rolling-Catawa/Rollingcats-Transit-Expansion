package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.LightType;
import org.mtr.mapping.holder.LightmapTextureManager;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;

public final class BuiltinGangwayRenderer {

    private static final Identifier TEXTURE = new Identifier("rte", "textures/entity/gangway.png");

    private static final java.util.Map<String, Identifier> TEXTURE_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();
    private static final Identifier MISSING = new Identifier("rte", "textures/entity/gangway.png");

    private static Identifier rte$packTexture(String part) {
        final String connectionId = cn.jstxjf_.rte.data.GangwayPolicy.connectionIdOf(
            cn.jstxjf_.rte.render.train.CurrentVehicleTracker.currentVehicleId());
        if (connectionId == null || connectionId.isEmpty()) {
            return null;
        }
        if (rte$isBlankConnection(connectionId)) {
            return null;
        }
        final Identifier result = TEXTURE_CACHE.computeIfAbsent(connectionId + '|' + part, key -> rte$loadPackTexture(connectionId, part));
        return result == MISSING ? null : result;
    }

    private static Identifier rte$loadPackTexture(String connectionId, String part) {
        Identifier result = MISSING;
        try {
            final int colon = connectionId.indexOf(':');
            final String namespace = colon < 0 ? "minecraft" : connectionId.substring(0, colon);
            final String path = colon < 0 ? connectionId : connectionId.substring(colon + 1);
            final String rawPath = path + "_connector_" + part + ".png";
            final Identifier rawId = new Identifier(namespace, rawPath);
            final net.minecraft.resources.ResourceLocation bindable =
                new net.minecraft.resources.ResourceLocation(namespace, "rte_gangway/" + namespace + "/" + rawPath);
            if (rte$registerRawTexture(rawId, bindable)) {
                result = new Identifier(bindable);
            }
        } catch (Throwable ignored) {
        }
        if (cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            cn.jstxjf_.rte.RTE.LOGGER.info("[Gangway] {} part={} -> {}",
                connectionId, part, result == MISSING ? "not found, falling back to RTE built-in texture" : result.toString());
        }
        return result;
    }

    public static boolean rte$suppressForCurrentVehicle() {
        final String connectionId = cn.jstxjf_.rte.data.GangwayPolicy.connectionIdOf(
            cn.jstxjf_.rte.render.train.CurrentVehicleTracker.currentVehicleId());
        return connectionId != null && !connectionId.isEmpty() && rte$isBlankConnection(connectionId);
    }

    private static boolean rte$isBlankConnection(String connectionId) {
        final int colon = connectionId.indexOf(':');
        final String path = colon < 0 ? connectionId : connectionId.substring(colon + 1);
        return path.equals("air") || path.endsWith("/air");
    }

    private static boolean rte$isFullyTransparent(com.mojang.blaze3d.platform.NativeImage image) {
        if (image.format() != com.mojang.blaze3d.platform.NativeImage.Format.RGBA) {
            return false;
        }
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getPixelRGBA(x, y) >>> 24) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean rte$registerRawTexture(Identifier rawId, net.minecraft.resources.ResourceLocation bindable) {
        final java.util.concurrent.atomic.AtomicBoolean ok =
            new java.util.concurrent.atomic.AtomicBoolean();
        try {
            org.mtr.mapping.mapper.ResourceManagerHelper.readResource(rawId, stream -> {
                try {
                    final com.mojang.blaze3d.platform.NativeImage image =
                        com.mojang.blaze3d.platform.NativeImage.read(stream);
                    if (rte$isFullyTransparent(image)) {
                        image.close();
                        cn.jstxjf_.rte.RTE.LOGGER.info(
                            "[Gangway] {} is a blank placeholder, using RTE built-in texture", rawId);
                        return;
                    }
                    com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() ->
                        net.minecraft.client.Minecraft.getInstance().getTextureManager()
                            .register(bindable, new net.minecraft.client.renderer.texture.DynamicTexture(image)));
                    ok.set(true);
                } catch (Exception e) {
                    cn.jstxjf_.rte.RTE.LOGGER.warn("[Gangway] failed to decode {}: {}", rawId, e.toString());
                }
            });
        } catch (Throwable ignored) {
        }
        return ok.get();
    }

    public static void clearTextureCache() {
        TEXTURE_CACHE.clear();
    }

    private static final float SIDE_INSET = 0.28F;
    private static final float FLOOR_HEIGHT = 0.12F;
    private static final float ROOF_HEIGHT = 2.35F;
    private static final double MAX_GAP = 6;

    private BuiltinGangwayRenderer() {
    }

    private static long rte$nextTraceMillis = 0;

    private static void rte$trace(String reason, double centreDistance, double carLength, double faceGap) {
        if (!ClientConfig.traceDoorSides) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextTraceMillis) {
            return;
        }
        rte$nextTraceMillis = now + 2000;
        cn.jstxjf_.rte.RTE.LOGGER.info(
            "[GangwayTrace] {} centreDistance={} carLength={} faceGap={} (limits: centre-len<={}, faceGap<={})",
            reason, String.format("%.2f", centreDistance), String.format("%.2f", carLength),
            String.format("%.2f", faceGap), MAX_GAP, String.format("%.2f", Math.max(2, carLength * 0.4)));
    }

    public static void render(Matrix4f prevCarPose, Matrix4f thisCarPose, double carLength, double carWidth) {
        if (!ClientConfig.builtinGangway || rte$suppressForCurrentVehicle()
                || cn.jstxjf_.rte.data.GangwayPolicy.currentVehicleSelfHandled()
                || prevCarPose == null || thisCarPose == null) {
            return;
        }
        final Vector3f prevCentre = prevCarPose.getTranslationPart();
        final Vector3f thisCentre = thisCarPose.getTranslationPart();
        final double dx = thisCentre.x() - prevCentre.x();
        final double dy = thisCentre.y() - prevCentre.y();
        final double dz = thisCentre.z() - prevCentre.z();
        final double centreDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!Double.isFinite(centreDistance) || centreDistance <= 0 || centreDistance - carLength > MAX_GAP) {
            rte$trace("centreDistance", centreDistance, carLength, 0);
            return;
        }

        final float halfWidth = (float) (carWidth / 2) - SIDE_INSET;
        final float halfLength = (float) (carLength / 2);
        if (halfWidth <= 0) {
            return;
        }

        final Vector3f[] prevFace = faceCorners(prevCarPose, halfWidth, -halfLength);
        final Vector3f[] thisFace = faceCorners(thisCarPose, halfWidth, halfLength);
        if (prevFace == null || thisFace == null) {
            return;
        }
        final double fx = thisFace[0].x() - prevFace[0].x();
        final double fy = thisFace[0].y() - prevFace[0].y();
        final double fz = thisFace[0].z() - prevFace[0].z();
        final double faceGap = Math.sqrt(fx * fx + fy * fy + fz * fz);
        if (!Double.isFinite(faceGap) || faceGap > Math.max(2, carLength * 0.4)) {
            rte$trace("faceGap", centreDistance, carLength, faceGap);
            return;
        }
        rte$trace("ok", centreDistance, carLength, faceGap);

        final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
        if (clientWorld == null) {
            return;
        }
        final BlockPos lightPos = Init.newBlockPos(
            (prevCentre.x() + thisCentre.x()) / 2,
            (prevCentre.y() + thisCentre.y()) / 2 + 1,
            (prevCentre.z() + thisCentre.z()) / 2);
        final int exteriorLight = LightmapTextureManager.pack(
            clientWorld.getLightLevel(LightType.getBlockMapped(), lightPos),
            clientWorld.getLightLevel(LightType.getSkyMapped(), lightPos));
        final int interiorLight = GraphicsHolder.getDefaultLight();

        final Identifier packExterior = rte$packTexture("exterior");
        if (packExterior != null) {
            final Identifier packSide = rte$packTexture("side");
            final Identifier packRoof = rte$packTexture("roof");
            final Identifier packFloor = rte$packTexture("floor");
            MainRenderer.scheduleRender(packExterior, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
                quad(graphicsHolder, offset, prevFace[1], thisFace[1], thisFace[0], prevFace[0], 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, offset, prevFace[3], thisFace[3], thisFace[2], prevFace[2], 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, offset, prevFace[2], thisFace[2], thisFace[1], prevFace[1], 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, offset, prevFace[0], thisFace[0], thisFace[3], prevFace[3], 0, 0, 1F, 1F, exteriorLight);
            });
            if (packSide != null) {
                MainRenderer.scheduleRender(packSide, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) -> {
                    quad(graphicsHolder, offset, thisFace[0], thisFace[1], prevFace[1], prevFace[0], 0, 0, 1F, 1F, interiorLight);
                    quad(graphicsHolder, offset, thisFace[2], thisFace[3], prevFace[3], prevFace[2], 0, 0, 1F, 1F, interiorLight);
                });
            }
            if (packRoof != null) {
                MainRenderer.scheduleRender(packRoof, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) ->
                    quad(graphicsHolder, offset, thisFace[1], thisFace[2], prevFace[2], prevFace[1], 0, 0, 1F, 1F, interiorLight));
            }
            if (packFloor != null) {
                MainRenderer.scheduleRender(packFloor, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) ->
                    quad(graphicsHolder, offset, thisFace[3], thisFace[0], prevFace[0], prevFace[3], 0, 0, 1F, 1F, interiorLight));
            }
            return;
        }

        MainRenderer.scheduleRender(TEXTURE, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
            quad(graphicsHolder, offset, prevFace[1], thisFace[1], thisFace[0], prevFace[0], 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, offset, prevFace[3], thisFace[3], thisFace[2], prevFace[2], 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, offset, prevFace[2], thisFace[2], thisFace[1], prevFace[1], 0, 0.5F, 0.5F, 1F, exteriorLight);
            quad(graphicsHolder, offset, prevFace[0], thisFace[0], thisFace[3], prevFace[3], 0.5F, 0.5F, 1F, 1F, exteriorLight);

            quad(graphicsHolder, offset, thisFace[0], thisFace[1], prevFace[1], prevFace[0], 0.5F, 0, 1F, 0.5F, interiorLight);
            quad(graphicsHolder, offset, thisFace[2], thisFace[3], prevFace[3], prevFace[2], 0.5F, 0, 1F, 0.5F, interiorLight);
            quad(graphicsHolder, offset, thisFace[1], thisFace[2], prevFace[2], prevFace[1], 0.5F, 0.5F, 1F, 1F, interiorLight);
            quad(graphicsHolder, offset, thisFace[3], thisFace[0], prevFace[0], prevFace[3], 0, 0.5F, 0.5F, 1F, interiorLight);
        });
    }

    public static void renderFromCorners(
            org.mtr.core.tool.Vector prevPos1, org.mtr.core.tool.Vector prevPos2,
            org.mtr.core.tool.Vector prevPos3, org.mtr.core.tool.Vector prevPos4,
            org.mtr.core.tool.Vector thisPos1, org.mtr.core.tool.Vector thisPos2,
            org.mtr.core.tool.Vector thisPos3, org.mtr.core.tool.Vector thisPos4,
            double vehicleLength, double prevRoll, double thisRoll,
            boolean useOffset, int exteriorLight, int interiorLight) {
        if (!ClientConfig.builtinGangway || rte$suppressForCurrentVehicle()
                || cn.jstxjf_.rte.data.GangwayPolicy.currentVehicleSelfHandled()
                || prevPos1 == null || thisPos1 == null) {
            return;
        }
        final double dx = thisPos1.x - prevPos1.x;
        final double dy = thisPos1.y - prevPos1.y;
        final double dz = thisPos1.z - prevPos1.z;
        final double faceGap = Math.sqrt(dx * dx + dy * dy + dz * dz);
        final double gapLimit = Math.max(2, Math.min(MAX_GAP, vehicleLength * 0.4));
        if (!Double.isFinite(faceGap) || faceGap > gapLimit) {
            return;
        }
        final org.mtr.core.tool.Vector axis = rte$axis(prevPos1, prevPos2, prevPos3, prevPos4,
            thisPos1, thisPos2, thisPos3, thisPos4);
        final org.mtr.core.tool.Vector p1 = rte$roll(prevPos1, prevPos1, prevPos2, prevPos3, prevPos4, axis, prevRoll);
        final org.mtr.core.tool.Vector p2 = rte$roll(prevPos2, prevPos1, prevPos2, prevPos3, prevPos4, axis, prevRoll);
        final org.mtr.core.tool.Vector p3 = rte$roll(prevPos3, prevPos1, prevPos2, prevPos3, prevPos4, axis, prevRoll);
        final org.mtr.core.tool.Vector p4 = rte$roll(prevPos4, prevPos1, prevPos2, prevPos3, prevPos4, axis, prevRoll);
        final org.mtr.core.tool.Vector t1 = rte$roll(thisPos1, thisPos1, thisPos2, thisPos3, thisPos4, axis, thisRoll);
        final org.mtr.core.tool.Vector t2 = rte$roll(thisPos2, thisPos1, thisPos2, thisPos3, thisPos4, axis, thisRoll);
        final org.mtr.core.tool.Vector t3 = rte$roll(thisPos3, thisPos1, thisPos2, thisPos3, thisPos4, axis, thisRoll);
        final org.mtr.core.tool.Vector t4 = rte$roll(thisPos4, thisPos1, thisPos2, thisPos3, thisPos4, axis, thisRoll);
        final Identifier cornerExterior = rte$packTexture("exterior");
        if (cornerExterior != null) {
            final Identifier cornerSide = rte$packTexture("side");
            final Identifier cornerRoof = rte$packTexture("roof");
            final Identifier cornerFloor = rte$packTexture("floor");
            MainRenderer.scheduleRender(cornerExterior, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
                final org.mtr.mapping.holder.Vector3d o =
                    useOffset ? offset : org.mtr.mapping.holder.Vector3d.getZeroMapped();
                quad(graphicsHolder, o, t2, p3, p4, t1, 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, o, p2, t3, t4, p1, 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, o, p3, t2, t3, p2, 0, 0, 1F, 1F, exteriorLight);
                quad(graphicsHolder, o, p1, t4, t1, p4, 0, 0, 1F, 1F, exteriorLight);
            });
            if (cornerSide != null) {
                MainRenderer.scheduleRender(cornerSide, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) -> {
                    final org.mtr.mapping.holder.Vector3d o =
                        useOffset ? offset : org.mtr.mapping.holder.Vector3d.getZeroMapped();
                    quad(graphicsHolder, o, t3, p2, p1, t4, 0, 0, 1F, 1F, interiorLight);
                    quad(graphicsHolder, o, p3, t2, t1, p4, 0, 0, 1F, 1F, interiorLight);
                });
            }
            if (cornerRoof != null) {
                MainRenderer.scheduleRender(cornerRoof, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) -> {
                    final org.mtr.mapping.holder.Vector3d o =
                        useOffset ? offset : org.mtr.mapping.holder.Vector3d.getZeroMapped();
                    quad(graphicsHolder, o, p2, t3, t2, p3, 0, 0, 1F, 1F, interiorLight);
                });
            }
            if (cornerFloor != null) {
                MainRenderer.scheduleRender(cornerFloor, false, QueuedRenderLayer.INTERIOR, (graphicsHolder, offset) -> {
                    final org.mtr.mapping.holder.Vector3d o =
                        useOffset ? offset : org.mtr.mapping.holder.Vector3d.getZeroMapped();
                    quad(graphicsHolder, o, p4, t1, t4, p1, 0, 0, 1F, 1F, interiorLight);
                });
            }
            return;
        }

        MainRenderer.scheduleRender(TEXTURE, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
            final org.mtr.mapping.holder.Vector3d renderOffset =
                useOffset ? offset : org.mtr.mapping.holder.Vector3d.getZeroMapped();
            quad(graphicsHolder, renderOffset, t2, p3, p4, t1, 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, renderOffset, p2, t3, t4, p1, 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, renderOffset, p3, t2, t3, p2, 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, renderOffset, p1, t4, t1, p4, 0, 0, 0.5F, 0.5F, exteriorLight);
            quad(graphicsHolder, renderOffset, t3, p2, p1, t4, 0.5F, 0, 1F, 0.5F, interiorLight);
            quad(graphicsHolder, renderOffset, p3, t2, t1, p4, 0.5F, 0, 1F, 0.5F, interiorLight);
            quad(graphicsHolder, renderOffset, p2, t3, t2, p3, 0, 0.5F, 0.5F, 1F, interiorLight);
            quad(graphicsHolder, renderOffset, p4, t1, t4, p1, 0.5F, 0.5F, 1F, 1F, interiorLight);
        });
    }

    private static org.mtr.core.tool.Vector rte$axis(
            org.mtr.core.tool.Vector a1, org.mtr.core.tool.Vector a2,
            org.mtr.core.tool.Vector a3, org.mtr.core.tool.Vector a4,
            org.mtr.core.tool.Vector b1, org.mtr.core.tool.Vector b2,
            org.mtr.core.tool.Vector b3, org.mtr.core.tool.Vector b4) {
        final double ax = (a1.x + a2.x + a3.x + a4.x) / 4;
        final double ay = (a1.y + a2.y + a3.y + a4.y) / 4;
        final double az = (a1.z + a2.z + a3.z + a4.z) / 4;
        final double bx = (b1.x + b2.x + b3.x + b4.x) / 4;
        final double by = (b1.y + b2.y + b3.y + b4.y) / 4;
        final double bz = (b1.z + b2.z + b3.z + b4.z) / 4;
        final double dx = bx - ax;
        final double dy = by - ay;
        final double dz = bz - az;
        final double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!Double.isFinite(length) || length < 1e-6) {
            return new org.mtr.core.tool.Vector(0, 0, 1);
        }
        return new org.mtr.core.tool.Vector(dx / length, dy / length, dz / length);
    }

    private static org.mtr.core.tool.Vector rte$roll(
            org.mtr.core.tool.Vector point,
            org.mtr.core.tool.Vector c1, org.mtr.core.tool.Vector c2,
            org.mtr.core.tool.Vector c3, org.mtr.core.tool.Vector c4,
            org.mtr.core.tool.Vector axis, double roll) {
        if (Math.abs(roll) < 1e-5) {
            return point;
        }
        final double cx = (c1.x + c2.x + c3.x + c4.x) / 4;
        final double cy = (c1.y + c2.y + c3.y + c4.y) / 4;
        final double cz = (c1.z + c2.z + c3.z + c4.z) / 4;
        final double px = point.x - cx;
        final double py = point.y - cy;
        final double pz = point.z - cz;
        final double cos = Math.cos(roll);
        final double sin = Math.sin(roll);
        final double dot = axis.x * px + axis.y * py + axis.z * pz;
        final double crossX = axis.y * pz - axis.z * py;
        final double crossY = axis.z * px - axis.x * pz;
        final double crossZ = axis.x * py - axis.y * px;
        return new org.mtr.core.tool.Vector(
            cx + px * cos + crossX * sin + axis.x * dot * (1 - cos),
            cy + py * cos + crossY * sin + axis.y * dot * (1 - cos),
            cz + pz * cos + crossZ * sin + axis.z * dot * (1 - cos));
    }

    private static void quad(GraphicsHolder graphicsHolder, org.mtr.mapping.holder.Vector3d offset,
                             org.mtr.core.tool.Vector pos1, org.mtr.core.tool.Vector pos2,
                             org.mtr.core.tool.Vector pos3, org.mtr.core.tool.Vector pos4,
                             float u1, float v1, float u2, float v2, int light) {
        org.mtr.mod.client.IDrawing.drawTexture(
            graphicsHolder,
            pos1.x, pos1.y, pos1.z,
            pos2.x, pos2.y, pos2.z,
            pos3.x, pos3.y, pos3.z,
            pos4.x, pos4.y, pos4.z,
            offset, u1, v1, u2, v2,
            org.mtr.mapping.holder.Direction.UP,
            org.mtr.mod.data.IGui.ARGB_WHITE, light);
    }

    private static Vector3f[] faceCorners(Matrix4f carPose, float halfWidth, float zOffset) {
        try {
            return new Vector3f[]{
                carPose.transform(new Vector3f(-halfWidth, FLOOR_HEIGHT, zOffset)),
                carPose.transform(new Vector3f(-halfWidth, ROOF_HEIGHT, zOffset)),
                carPose.transform(new Vector3f(halfWidth, ROOF_HEIGHT, zOffset)),
                carPose.transform(new Vector3f(halfWidth, FLOOR_HEIGHT, zOffset))
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static void quad(GraphicsHolder graphicsHolder, org.mtr.mapping.holder.Vector3d offset,
                             Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f pos4,
                             float u1, float v1, float u2, float v2, int light) {
        org.mtr.mod.client.IDrawing.drawTexture(
            graphicsHolder,
            pos1.x(), pos1.y(), pos1.z(),
            pos2.x(), pos2.y(), pos2.z(),
            pos3.x(), pos3.y(), pos3.z(),
            pos4.x(), pos4.y(), pos4.z(),
            offset, u1, v1, u2, v2,
            org.mtr.mapping.holder.Direction.UP,
            org.mtr.mod.data.IGui.ARGB_WHITE, light);
    }
}
