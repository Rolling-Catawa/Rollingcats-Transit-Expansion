package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.render.DashboardMapCache;
import net.minecraft.client.gui.components.AbstractWidget;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.mapping.holder.Identifier;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.screen.WidgetMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetMap.class, remap = false)
public abstract class WidgetMapMixin {

    @Shadow(remap = false)
    private double scale;

    @Shadow(remap = false)
    private double centerX;

    @Shadow(remap = false)
    private double centerY;

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/mapper/GuiDrawing;finishDrawingRectangle()V", ordinal = 0, shift = At.Shift.AFTER),
        remap = false,
        require = 0
    )
    private void rte$drawCachedTiles(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final AbstractWidget widget = (AbstractWidget) (Object) this;
        final int x = cn.jstxjf_.rte.compat.WidgetCompat.widgetX(widget);
        final int y = cn.jstxjf_.rte.compat.WidgetCompat.widgetY(widget);
        final int w = widget.getWidth();
        final int h = widget.getHeight();
        if (w <= 0 || h <= 0 || scale <= 0) return;

        final double minWorldX = centerX - w / 2.0 / scale;
        final double minWorldZ = centerY - h / 2.0 / scale;
        final double maxWorldX = centerX + w / 2.0 / scale;
        final double maxWorldZ = centerY + h / 2.0 / scale;
        final double regionBlocks = 128;
        final boolean wantTexture = regionBlocks * scale >= 4;

        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        if (!wantTexture) {
            guiDrawing.beginDrawingRectangle();
        }
        DashboardMapCache.forEachRegion(minWorldX, minWorldZ, maxWorldX, maxWorldZ, wantTexture, (regionX, regionZ, texture, averageArgb) -> {
            final double x1 = (regionX * regionBlocks - centerX) * scale + w / 2.0;
            final double z1 = (regionZ * regionBlocks - centerY) * scale + h / 2.0;
            final double x2 = x1 + regionBlocks * scale;
            final double z2 = z1 + regionBlocks * scale;
            if (x2 < 0 || z2 < 0 || x1 > w || z1 > h) return;
            if (wantTexture && texture != null) {
                final double cx1 = Math.max(0, x1);
                final double cz1 = Math.max(0, z1);
                final double cx2 = Math.min(w, x2);
                final double cz2 = Math.min(h, z2);
                final float u1 = (float) ((cx1 - x1) / (x2 - x1));
                final float v1 = (float) ((cz1 - z1) / (z2 - z1));
                final float u2 = (float) ((cx2 - x1) / (x2 - x1));
                final float v2 = (float) ((cz2 - z1) / (z2 - z1));
                guiDrawing.beginDrawingTexture(new Identifier(texture.getNamespace(), texture.getPath()));
                guiDrawing.drawTexture(x + cx1, y + cz1, x + cx2, y + cz2, u1, v1, u2, v2);
                guiDrawing.finishDrawingTexture();
            } else if (!wantTexture) {
                guiDrawing.drawRectangle(x + Math.max(0, x1), y + Math.max(0, z1), x + Math.min(w, x2), y + Math.min(h, z2), averageArgb);
            }
        });
        if (!wantTexture) {
            guiDrawing.finishDrawingRectangle();
        }
    }

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/mapper/GuiDrawing;beginDrawingRectangle()V", ordinal = 1),
        remap = false,
        require = 0
    )
    private void rte$drawRails(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final AbstractWidget widget = (AbstractWidget) (Object) this;
        final int x = cn.jstxjf_.rte.compat.WidgetCompat.widgetX(widget);
        final int y = cn.jstxjf_.rte.compat.WidgetCompat.widgetY(widget);
        final int w = widget.getWidth();
        final int h = widget.getHeight();
        if (w <= 0 || h <= 0 || scale <= 0) return;

        final double minWorldX = centerX - w / 2.0 / scale;
        final double minWorldZ = centerY - h / 2.0 / scale;
        final double maxWorldX = centerX + w / 2.0 / scale;
        final double maxWorldZ = centerY + h / 2.0 / scale;

        final ObjectArraySet<Rail> rails = new ObjectArraySet<>(MinecraftClientData.getDashboardInstance().rails);
        try {
            rails.addAll(MinecraftClientData.getInstance().rails);
        } catch (Exception ignored) {
        }

        cn.jstxjf_.rte.render.MapRailArchive.bind(rte$worldKey());
        cn.jstxjf_.rte.render.MapRailArchive.requestFullSyncIfDue();
        final java.util.Set<String> rte$liveIds = new java.util.HashSet<>();

        cn.jstxjf_.rte.data.RouteRailColors.refreshIfStale();
        final java.util.Map<Integer, String> colorToRouteName = new java.util.HashMap<>();
        try {
            for (final org.mtr.core.data.SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {
                if (route != null && route.getName() != null && !route.getName().isEmpty()) {
                    colorToRouteName.putIfAbsent(route.getColor() & 0xFFFFFF, route.getName().split("\\|")[0]);
                }
            }
        } catch (Exception ignored) {
        }
        final java.util.Map<Integer, double[]> routeLabelAnchors = new java.util.HashMap<>();

        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        final double dot = 2.0;
        for (final Rail dashboardRail : rails) {
            final Rail rail = cn.jstxjf_.rte.render.rail.RailRenderDispatcher.resolveRenderCarrier(dashboardRail);
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) continue;
            final Position p1 = extra.getExtraPosition1();
            final Position p2 = extra.getExtraPosition2();
            if (p1 == null || p2 == null) continue;

            final double pad = Math.abs(p2.getX() - p1.getX()) * 0.3 + Math.abs(p2.getZ() - p1.getZ()) * 0.3 + 16;
            final double railMinX = Math.min(p1.getX(), p2.getX()) - pad;
            final double railMaxX = Math.max(p1.getX(), p2.getX()) + pad;
            final double railMinZ = Math.min(p1.getZ(), p2.getZ()) - pad;
            final double railMaxZ = Math.max(p1.getZ(), p2.getZ()) + pad;
            if (railMaxX < minWorldX || railMinX > maxWorldX || railMaxZ < minWorldZ || railMinZ > maxWorldZ) continue;

            final double length = extra.getLength();
            final boolean straightFallback = !Double.isFinite(length) || length <= 0;

            int lineColor = 0xBBC8C8C8;
            double lineDot = dot;
            int routeColorKey = -1;
            final Integer routeColor = cn.jstxjf_.rte.data.RouteRailColors.colorOf(dashboardRail);
            if (routeColor != null) {
                routeColorKey = routeColor & 0xFFFFFF;
                lineColor = routeColor;
                lineDot = dot + 1;
            } else {
                try {
                    var signalColors = rail.getSignalColors();
                    if (signalColors != null && !signalColors.isEmpty()) {
                        routeColorKey = signalColors.firstInt() & 0xFFFFFF;
                        lineColor = 0xFF000000 | routeColorKey;
                        lineDot = dot + 1;
                    }
                } catch (Exception ignored) {
                }
            }

            try {
                rte$liveIds.add(rail.getHexId());
            } catch (Exception ignored) {
            }
            cn.jstxjf_.rte.render.MapRailArchive.record(rail, routeColor);

            final int segments = straightFallback ? 1 : (int) Math.max(1, Math.min(256, Math.ceil(length)));
            double prevX = Double.NaN, prevY = Double.NaN;
            for (int i = 0; i <= segments; i++) {
                Vec3 pos;
                if (straightFallback) {
                    pos = i == 0
                        ? new Vec3(p1.getX() + 0.5, p1.getY(), p1.getZ() + 0.5)
                        : new Vec3(p2.getX() + 0.5, p2.getY(), p2.getZ() + 0.5);
                } else {
                    try {
                        pos = extra.getPosition(length * i / segments);
                    } catch (Exception e) {
                        final double ratio = (double) i / segments;
                        pos = new Vec3(
                            p1.getX() + 0.5 + (p2.getX() - p1.getX()) * ratio,
                            p1.getY() + (p2.getY() - p1.getY()) * ratio,
                            p1.getZ() + 0.5 + (p2.getZ() - p1.getZ()) * ratio);
                    }
                }
                if (!Double.isFinite(pos.x) || !Double.isFinite(pos.z)) {
                    prevX = Double.NaN;
                    prevY = Double.NaN;
                    continue;
                }
                final double mapX = (pos.x - centerX) * scale + w / 2.0;
                final double mapY = (pos.z - centerY) * scale + h / 2.0;
                if (!Double.isNaN(prevX)) {
                    final double dist = Math.max(Math.abs(mapX - prevX), Math.abs(mapY - prevY));
                    final double stride = Math.max(0.35, lineDot * 0.5);
                    final int steps = (int) Math.max(1, Math.min(4096, Math.ceil(dist / stride)));
                    for (int s = 0; s <= steps; s++) {
                        final double px = prevX + (mapX - prevX) * s / steps;
                        final double py = prevY + (mapY - prevY) * s / steps;
                        if (px < 0 || py < 0 || px > w || py > h) continue;
                        guiDrawing.drawRectangle(x + px - lineDot / 2, y + py - lineDot / 2, x + px + lineDot / 2, y + py + lineDot / 2, lineColor);
                    }
                }
                if (routeColorKey >= 0 && i == segments / 2 && mapX > 20 && mapY > 8 && mapX < w - 20 && mapY < h - 8) {
                    routeLabelAnchors.putIfAbsent(routeColorKey, new double[]{mapX, mapY});
                }
                prevX = mapX;
                prevY = mapY;
            }
        }

        cn.jstxjf_.rte.render.MapRailArchive.forEachOutside(rte$liveIds, minWorldX, minWorldZ, maxWorldX, maxWorldZ, entry -> {
            final int lineColor = entry.color == 0 ? 0x66C8C8C8 : (entry.color & 0x00FFFFFF) | 0xAA000000;
            final double lineDot = entry.color == 0 ? dot : dot + 1;
            double prevX = Double.NaN;
            double prevY = Double.NaN;
            for (int i = 0; i < entry.xs.length; i++) {
                final double mapX = (entry.xs[i] - centerX) * scale + w / 2.0;
                final double mapY = (entry.zs[i] - centerY) * scale + h / 2.0;
                if (!Double.isNaN(prevX)) {
                    final double dist = Math.max(Math.abs(mapX - prevX), Math.abs(mapY - prevY));
                    final double stride = Math.max(0.35, lineDot * 0.5);
                    final int steps = (int) Math.max(1, Math.min(4096, Math.ceil(dist / stride)));
                    for (int step = 0; step <= steps; step++) {
                        final double px = prevX + (mapX - prevX) * step / steps;
                        final double py = prevY + (mapY - prevY) * step / steps;
                        if (px < 0 || py < 0 || px > w || py > h) continue;
                        guiDrawing.drawRectangle(x + px - lineDot / 2, y + py - lineDot / 2,
                            x + px + lineDot / 2, y + py + lineDot / 2, lineColor);
                    }
                }
                prevX = mapX;
                prevY = mapY;
            }
        });
        cn.jstxjf_.rte.render.MapRailArchive.tickSave();

        if (cn.jstxjf_.rte.data.RouteRailColors.isRunning()) {
            final int activeColor = cn.jstxjf_.rte.data.RouteRailColors.getActiveColor();
            final int closedColor = (activeColor & 0x00FFFFFF) | 0x50000000;
            final double nodeSize = Math.max(1.5, Math.min(6, scale * 1.5));
            cn.jstxjf_.rte.data.RouteRailColors.forEachExploredNode((position, frontier) -> {
                final double mapX = (position.getX() + 0.5 - centerX) * scale + w / 2.0;
                final double mapY = (position.getZ() + 0.5 - centerY) * scale + h / 2.0;
                if (mapX < 0 || mapY < 0 || mapX > w || mapY > h) return;
                final double size = frontier ? nodeSize * 1.6 : nodeSize;
                guiDrawing.drawRectangle(
                    x + mapX - size / 2, y + mapY - size / 2,
                    x + mapX + size / 2, y + mapY + size / 2,
                    frontier ? 0xFFFFFF66 : closedColor);
            });
        }

        guiDrawing.finishDrawingRectangle();

        if (scale >= 0.08 && !routeLabelAnchors.isEmpty()) {
            for (final java.util.Map.Entry<Integer, double[]> entry : routeLabelAnchors.entrySet()) {
                final String routeName = colorToRouteName.get(entry.getKey());
                if (routeName == null || routeName.isEmpty()) {
                    continue;
                }
                final double[] anchor = entry.getValue();
                graphicsHolder.drawText(routeName,
                    (int) (x + anchor[0] + 4), (int) (y + anchor[1] - 4),
                    0xFF000000 | entry.getKey(), true, GraphicsHolder.getDefaultLight());
            }
        }
    }

    @Unique
    private static String rte$worldKey() {
        try {
            final net.minecraft.client.multiplayer.ServerData serverData = net.minecraft.client.Minecraft.getInstance().getCurrentServer();
            if (serverData != null && serverData.ip != null && !serverData.ip.isEmpty()) {
                return "server_" + serverData.ip;
            }
            final var integratedServer = net.minecraft.client.Minecraft.getInstance().getSingleplayerServer();
            if (integratedServer != null) {
                return "local_" + integratedServer.getWorldData().getLevelName();
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    @Inject(method = "render", at = @At("TAIL"), remap = false, require = 0)
    private void rte$drawVehicles(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final AbstractWidget widget = (AbstractWidget) (Object) this;
        final int x = cn.jstxjf_.rte.compat.WidgetCompat.widgetX(widget);
        final int y = cn.jstxjf_.rte.compat.WidgetCompat.widgetY(widget);
        final int w = widget.getWidth();
        final int h = widget.getHeight();
        if (w <= 0 || h <= 0 || scale <= 0) return;

        final double minWorldX = centerX - w / 2.0 / scale;
        final double minWorldZ = centerY - h / 2.0 / scale;
        final double maxWorldX = centerX + w / 2.0 / scale;
        final double maxWorldZ = centerY + h / 2.0 / scale;
        cn.jstxjf_.rte.render.VehicleMarkerCache.requestIfDue(minWorldX, minWorldZ, maxWorldX, maxWorldZ);

        final java.util.Set<Long> drawn = new java.util.HashSet<>();
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            Vector head;
            try {
                head = cn.jstxjf_.rte.compat.VehicleCompat.headPosition(vehicle);
            } catch (Exception e) {
                continue;
            }
            if (head == null) continue;
            drawn.add(vehicle.getId());
            final double mapX = (head.x() - centerX) * scale + w / 2.0;
            final double mapY = (head.z() - centerY) * scale + h / 2.0;
            if (mapX < 3 || mapY < 3 || mapX > w - 3 || mapY > h - 3) continue;
            guiDrawing.drawRectangle(x + mapX - 3, y + mapY - 3, x + mapX + 3, y + mapY + 3, 0xFFFFFFFF);
            guiDrawing.drawRectangle(x + mapX - 2, y + mapY - 2, x + mapX + 2, y + mapY + 2, 0xFFFF6600);
        }
        for (final cn.jstxjf_.rte.network.PacketVehicleMarkers.Marker marker
                : cn.jstxjf_.rte.render.VehicleMarkerCache.current()) {
            if (drawn.contains(marker.id)) continue;
            final double mapX = (marker.x - centerX) * scale + w / 2.0;
            final double mapY = (marker.z - centerY) * scale + h / 2.0;
            if (mapX < 3 || mapY < 3 || mapX > w - 3 || mapY > h - 3) continue;
            guiDrawing.drawRectangle(x + mapX - 3, y + mapY - 3, x + mapX + 3, y + mapY + 3, 0xFFFFFFFF);
            guiDrawing.drawRectangle(x + mapX - 2, y + mapY - 2, x + mapX + 2, y + mapY + 2, marker.color);
        }
        guiDrawing.finishDrawingRectangle();
    }
}
