package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.render.RenderUtil;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderRails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderRails.class, remap = false)
public class RenderRailsMixin {

    private static final Identifier ARROW_TEXTURE = new Identifier("mtr", "textures/block/one_way_rail_arrow.png");

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private static void renderHead(CallbackInfo ci) {
        RenderUtil.commonVertexConsumers = null;
        RTEClient.railRenderDispatcher.prepareDraw();
        cn.jstxjf_.rte.render.DashboardMapCache.frameTick();
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/Rail;getAngles(Lorg/mtr/core/data/Position;FLorg/mtr/core/data/Position;F)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;"),
        remap = false
    )
    private static ObjectObjectImmutablePair<Angle, Angle> rte$redirectPreviewGetAngles(Position posStart, float angle1, Position posEnd, float angle2) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_PREVIEW_ANGLE)) {
            return Rail.getAngles(posStart, angle1, posEnd, angle2);
        }
        ClientWorld cw = MinecraftClient.getInstance().getWorldMapped();
        if (cw == null) {
            return Rail.getAngles(posStart, angle1, posEnd, angle2);
        }

        BlockPos bpStart = Init.positionToBlockPos(posStart);
        BlockPos bpEnd = Init.positionToBlockPos(posEnd);
        BlockDirectNode.BlockEntityDirectNode dnStart = rte$directNode(cw, bpStart);
        BlockDirectNode.BlockEntityDirectNode dnEnd = rte$directNode(cw, bpEnd);

        if (dnStart == null && dnEnd == null) {
            final ObjectObjectImmutablePair<Angle, Angle> vanilla = Rail.getAngles(posStart, angle1, posEnd, angle2);
            cn.jstxjf_.rte.render.PreviewMetrics.report(posStart, posEnd, vanilla.left(), vanilla.right(), rte$previewTransportMode(cw, bpStart));
            return vanilla;
        }

        TransportMode transportMode = TransportMode.TRAIN;
        try {
            BlockState stateStart = cw.getBlockState(bpStart);
            if (stateStart.getBlock().data instanceof BlockNode blockNode) {
                transportMode = blockNode.transportMode;
            }
        } catch (Exception ignored) {
        }

        double chordRad = Math.atan2(posEnd.getZ() - posStart.getZ(), posEnd.getX() - posStart.getX());
        double radStart = Math.toRadians(angle1);
        double radEnd = Math.toRadians(angle2);
        if (dnStart != null && dnStart.isBound()) {
            radStart = dnStart.getAngleRadians();
        }
        if (dnEnd != null && dnEnd.isBound()) {
            radEnd = dnEnd.getAngleRadians();
        }

        boolean s1 = dnStart == null || dnStart.isBound();
        boolean s2 = dnEnd == null || dnEnd.isBound();
        if (s1 && !s2) {
            Double deg = cn.jstxjf_.rte.data.RailCalculator.calculateMaxRadiusAngle(
                posStart.getX(), posStart.getZ(), posEnd.getX(), posEnd.getZ(), radStart);
            radEnd = deg == null ? chordRad : Math.toRadians(deg);
        } else if (s2 && !s1) {
            Double deg = cn.jstxjf_.rte.data.RailCalculator.calculateMaxRadiusAngle(
                posEnd.getX(), posEnd.getZ(), posStart.getX(), posStart.getZ(), radEnd);
            radStart = deg == null ? chordRad + Math.PI : Math.toRadians(deg);
        } else if (!s1 && !s2) {
            radStart = chordRad;
            radEnd = chordRad + Math.PI;
        }

        radStart = rte$faceToward(chordRad, radStart);
        radEnd = rte$faceToward(chordRad, radEnd);
        double[] best = rte$selectPreviewAngles(posStart, radStart, posEnd, radEnd, transportMode);
        final ObjectObjectImmutablePair<Angle, Angle> result = new ObjectObjectImmutablePair<>(RailAngleExtra.fromRadians(best[0]), RailAngleExtra.fromRadians(best[1]));
        cn.jstxjf_.rte.render.PreviewMetrics.report(posStart, posEnd, result.left(), result.right(), transportMode);
        return result;
    }

    private static TransportMode rte$previewTransportMode(ClientWorld cw, BlockPos bpStart) {
        try {
            BlockState stateStart = cw.getBlockState(bpStart);
            if (stateStart.getBlock().data instanceof BlockNode blockNode) {
                return blockNode.transportMode;
            }
        } catch (Exception ignored) {
        }
        return TransportMode.TRAIN;
    }

    private static BlockDirectNode.BlockEntityDirectNode rte$directNode(ClientWorld cw, BlockPos pos) {
        BlockEntity entity = cw.getBlockEntity(pos);
        if (entity != null && entity.data instanceof BlockDirectNode.BlockEntityDirectNode directNode) {
            return directNode;
        }
        return null;
    }

    private static double[] rte$selectPreviewAngles(Position posStart, double radStart, Position posEnd, double radEnd, TransportMode transportMode) {
        double dx = posEnd.getX() - posStart.getX();
        double dz = posEnd.getZ() - posStart.getZ();
        double chordDist = Math.sqrt(dx * dx + dz * dz);
        double maxReasonable = chordDist * 4 + 32;
        double[][] order = {
            {radStart, radEnd},
            {radStart, rte$wrap(radEnd + Math.PI)},
            {rte$wrap(radStart + Math.PI), radEnd},
            {rte$wrap(radStart + Math.PI), rte$wrap(radEnd + Math.PI)}
        };
        for (double[] candidate : order) {
            double length = rte$testRailLength(posStart, candidate[0], posEnd, candidate[1], transportMode);
            if (Double.isFinite(length) && length > 0.01 && length <= maxReasonable) {
                return candidate;
            }
        }
        return new double[]{radStart, radEnd};
    }

    private static double rte$testRailLength(Position p1, double rad1, Position p2, double rad2, TransportMode transportMode) {
        final double[] result = {Double.NaN};
        RailAngleExtra.withAngles(rad1, rad2, (a1, a2) -> {
            try {
                Rail testRail = Rail.newRail(p1, a1, p2, a2, Rail.Shape.QUADRATIC, 0,
                    new ObjectArrayList<>(), 20, 20, false, false, true, false, true, transportMode);
                if (testRail.isValid()) {
                    double length = testRail.railMath.getLength();
                    if (Double.isFinite(length) && length > 0.01) {
                        result[0] = length;
                    }
                }
            } catch (Exception ignored) {
            }
        });
        return result[0];
    }

    private static double rte$faceToward(double chordRad, double rad) {
        double diff = Math.atan2(Math.sin(rad - chordRad), Math.cos(rad - chordRad));
        return Math.abs(diff) < Math.PI / 2 ? rad : rte$wrap(rad + Math.PI);
    }

    private static double rte$wrap(double r) {
        r = r % (2 * Math.PI);
        if (r <= -Math.PI) {
            r += 2 * Math.PI;
        } else if (r > Math.PI) {
            r -= 2 * Math.PI;
        }
        return r;
    }

    @Inject(method = "renderRailStandard(Lorg/mtr/mapping/holder/ClientWorld;Lorg/mtr/core/data/Rail;FLorg/mtr/mod/render/RenderRails$RenderState;FLorg/mtr/mapping/holder/Identifier;FFFF)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void renderRailStandard(ClientWorld world, Rail rail, float yOffset, @Coerce Object renderState, float railWidth, Identifier texture, float u1, float v1, float u2, float v2, CallbackInfo ci) {
        if (ClientConfig.getRailRenderLevel() == 0) {
            ci.cancel();
            return;
        }
        if (ClientConfig.getRailRenderLevel() >= 2) {
            if (!(((Object) rail) instanceof RailExtraSupplier)) {
                return;
            }
            final Rail carrier = RailRenderDispatcher.resolveRenderCarrier(rail);
            String modelKey = RailRenderDispatcher.getModelKeyForRender(carrier);
            boolean schedulerReady = RTEClient.drawScheduler != null && RTEClient.drawScheduler.shaderManager != null && RTEClient.drawScheduler.shaderManager.isReady();
            if (!modelKey.isEmpty() && schedulerReady) {
                cn.jstxjf_.rte.data.RailModelProperties prop = cn.jstxjf_.rte.data.RailModelRegistry.getProperty(modelKey);
                if (prop != null && prop.rawModel != null) {
                    RTEClient.railRenderDispatcher.registerRail(carrier);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "renderRailOneWayArrows", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rte$renderBezierArrows(Rail rail, float yOffset, CallbackInfo ci) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.RAIL_ONEWAY_ARROW)) {
            return;
        }
        final Rail mtrRail = rail;
        rail = RailRenderDispatcher.resolveRenderCarrier(rail);
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
        if (!RailExtraSupplier.usesBezierCurve(extra.getPathMode())) return;
        BezierCurve bezier = extra.getBezierCurve();
        if (bezier == null) return;

        ci.cancel();

        long speed1 = mtrRail.getSpeedLimitKilometersPerHour(false);
        long speed2 = mtrRail.getSpeedLimitKilometersPerHour(true);
        if (speed1 != 0 && speed2 != 0) return;

        double length = extra.getLength();
        if (!Double.isFinite(length) || length <= 0) return;

        try {
            final org.mtr.core.tool.Vector mtrStart = mtrRail.railMath.getPosition(0, false);
            final org.mtr.core.tool.Vector mtrEnd = mtrRail.railMath.getPosition(mtrRail.railMath.getLength(), false);
            final Vector3d stripStart = extra.getPosition(0);
            final double toStart = Math.pow(stripStart.getXMapped() - mtrStart.x, 2) + Math.pow(stripStart.getZMapped() - mtrStart.z, 2);
            final double toEnd = Math.pow(stripStart.getXMapped() - mtrEnd.x, 2) + Math.pow(stripStart.getZMapped() - mtrEnd.z, 2);
            if (toEnd < toStart) {
                final long swap = speed1;
                speed1 = speed2;
                speed2 = swap;
            }
        } catch (Exception ignored) {
        }

        float v1Arrow = speed1 == 0 ? 0.25f : 0.75f;
        float v2Arrow = speed1 == 0 ? 0.75f : 0.25f;
        int light = GraphicsHolder.getDefaultLight();

        int segments = Math.max(1, (int) Math.round(length));
        double increment = length / segments;

        Vector3d prevCenter = null;
        double prevPerpX = 0, prevPerpZ = 0;

        for (int i = 0; i <= segments; i++) {
            double t = Math.min(i * increment, length);
            Vector3d center = extra.getPosition(t);

            double eps = Math.min(0.1, increment * 0.25);
            Vector3d pA = extra.getPosition(Math.max(0, t - eps));
            Vector3d pB = extra.getPosition(Math.min(length, t + eps));
            double dx = pB.getXMapped() - pA.getXMapped();
            double dz = pB.getZMapped() - pA.getZMapped();
            double hLen = Math.sqrt(dx * dx + dz * dz);
            double perpX = 0, perpZ = 1;
            if (hLen > 1E-9) {
                perpX = -dz / hLen;
                perpZ = dx / hLen;
            }

            if (prevCenter != null) {
                final double cx = center.getXMapped();
                final double cy = center.getYMapped();
                final double cz = center.getZMapped();
                final double pcx = prevCenter.getXMapped();
                final double pcy = prevCenter.getYMapped();
                final double pcz = prevCenter.getZMapped();

                final double ax1 = pcx + prevPerpX;
                final double az1 = pcz + prevPerpZ;
                final double ax2 = pcx - prevPerpX;
                final double az2 = pcz - prevPerpZ;
                final double ax3 = cx + perpX;
                final double az3 = cz + perpZ;
                final double ax4 = cx - perpX;
                final double az4 = cz - perpZ;

                final double ay1 = pcy + yOffset + 0.125;
                final double ay2 = cy + yOffset + 0.125;

                MainRenderer.scheduleRender(ARROW_TEXTURE, false, QueuedRenderLayer.EXTERIOR, (gh, offset) -> {
                    IDrawing.drawTexture(gh,
                            ax1, ay1, az1,
                            ax2, ay1 + 0.003125, az2,
                            ax4, ay2 + 0.003125, az4,
                            ax3, ay2, az3,
                            offset, 0, v1Arrow, 1, v2Arrow, Direction.UP, -1, light);
                    IDrawing.drawTexture(gh,
                            ax2, ay1 + 0.003125, az2,
                            ax1, ay1, az1,
                            ax3, ay2, az3,
                            ax4, ay2 + 0.003125, az4,
                            offset, 0, v1Arrow, 1, v2Arrow, Direction.UP, -1, light);
                });
            }

            prevCenter = center;
            prevPerpX = perpX;
            prevPerpZ = perpZ;
        }
    }
}
