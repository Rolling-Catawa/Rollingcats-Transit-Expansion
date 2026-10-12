package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.DoubleFloatMapSerializer;
import cn.jstxjf_.rte.util.StringMapSerializer;
import org.mtr.core.data.Rail;
import org.mtr.core.generated.data.RailSchema;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.core.serializer.WriterBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(value = RailSchema.class, remap = false)
public abstract class RailSchemaMixin {

    @Inject(method = "updateData", at = @At("TAIL"))
    private void rte$updateDataMixin(ReaderBase reader, CallbackInfo ci) {
        if (!(this instanceof RailExtraSupplier)) return;
        RailExtraSupplier self = (RailExtraSupplier) this;

        final int oldPathMode = self.getPathMode();
        final BezierCurve oldBezier = self.getBezierCurve();
        final double oldBezierLength = oldBezier != null ? oldBezier.getLength() : Double.NaN;

        reader.unpackString("model_key", self::setModelKey);
        reader.unpackBoolean("is_secondary_dir", self::setRenderReversed);
        reader.unpackDouble("vertical_curve_radius", value -> self.setVerticalCurveRadius((float) value));
        reader.unpackString("custom_configs", value -> {
            try { self.setCustomConfigs(StringMapSerializer.deserialize(value)); }
            catch (Exception e) { self.setCustomConfigs(new HashMap<>()); }
        });
        reader.unpackString("roll_angle_map", value -> {
            try { self.setRollAngleMap(DoubleFloatMapSerializer.deserialize(value)); }
            catch (Exception e) { self.setRollAngleMap(new HashMap<>()); }
        });
        reader.unpackInt("opening_direction", self::setOpeningDirectionRaw);
        reader.unpackDouble("rolling_offset", value -> self.setRollingOffset((float) value));
        reader.unpackInt("path_mode", self::setPathModeRaw);
        final List<Double> legacyBezierValues = new ArrayList<>();
        reader.iterateDoubleArray("bezier_curve", legacyBezierValues::clear, legacyBezierValues::add);
        final BezierCurve legacyBezier = BezierCurve.fromLegacyValues(legacyBezierValues);
        if (legacyBezier != null) {
            self.setBezier(legacyBezier);
        }

        reader.unpackString("rte_model_key", self::setModelKey);
        reader.unpackString("rte_model_keys", value -> {
            final java.util.List<String> keys = rte$splitModelKeys(value);
            if (keys.size() > 1) {
                cn.jstxjf_.rte.RTE.LOGGER.info("[RailModels] deserialised {} models: {}", keys.size(), keys);
            }
            self.setModelKeys(keys);
        });
        reader.unpackBoolean("rte_is_secondary_dir", self::setRenderReversed);
        reader.unpackDouble("rte_vertical_curve_radius", value -> self.setVerticalCurveRadius((float) value));
        reader.unpackString("rte_rail_type", self::setRailType);
        reader.unpackInt("rte_speed_limit_kph", self::setSpeedLimitKph);
        reader.unpackBoolean("rte_collision_enabled", self::setCollisionEnabled);
        reader.unpackDouble("rte_collision_width", value -> self.setCollisionWidth((float) value));
        reader.unpackDouble("rte_collision_height", value -> self.setCollisionHeight((float) value));
        reader.unpackDouble("rte_collision_length", value -> self.setCollisionLength((float) value));
        reader.unpackDouble("rte_collision_spacing", value -> self.setCollisionSpacing((float) value));
        reader.unpackDouble("rte_collision_y_offset", value -> self.setCollisionYOffset((float) value));
        reader.unpackDouble("rte_collision_side_offset", value -> self.setCollisionSideOffset((float) value));
        reader.unpackString("rte_custom_configs", value -> {
            try { self.setCustomConfigs(StringMapSerializer.deserialize(value)); }
            catch (Exception e) { self.setCustomConfigs(new HashMap<>()); }
        });
        reader.unpackString("rte_roll_angle_map", value -> {
            try { self.setRollAngleMap(DoubleFloatMapSerializer.deserialize(value)); }
            catch (Exception e) { self.setRollAngleMap(new HashMap<>()); }
        });
        reader.unpackInt("rte_opening_direction", self::setOpeningDirectionRaw);
        reader.unpackDouble("rte_rolling_offset", value -> self.setRollingOffset((float) value));
        reader.unpackDouble("rte_node_x_offset_1", self::setNodeXOffset1);
        reader.unpackDouble("rte_node_y_offset_1", self::setNodeYOffset1);
        reader.unpackDouble("rte_node_z_offset_1", self::setNodeZOffset1);
        reader.unpackDouble("rte_node_x_offset_2", self::setNodeXOffset2);
        reader.unpackDouble("rte_node_y_offset_2", self::setNodeYOffset2);
        reader.unpackDouble("rte_node_z_offset_2", self::setNodeZOffset2);
        reader.unpackDouble("rte_node_pitch_1", self::setNodePitch1);
        reader.unpackDouble("rte_node_pitch_2", self::setNodePitch2);
        reader.unpackInt("rte_path_mode", self::setPathModeRaw);
        reader.unpackChild("rte_bezier", value -> self.setBezier(new BezierCurve(value)));

        final double[] a1 = {Double.NaN};
        final double[] a2 = {Double.NaN};
        reader.unpackDouble("facing_start", v -> a1[0] = v);
        reader.unpackDouble("facing_end", v -> a2[0] = v);
        reader.unpackDouble("rte_angle1_rad", v -> a1[0] = v);
        reader.unpackDouble("rte_angle2_rad", v -> a2[0] = v);

        if (Double.isFinite(a1[0])) self.setExtraAngle1Rad(a1[0]);
        if (Double.isFinite(a2[0])) self.setExtraAngle2Rad(a2[0]);

        final BezierCurve newBezier = self.getBezierCurve();
        final double newBezierLength = newBezier != null ? newBezier.getLength() : Double.NaN;
        final boolean bezierChanged = (oldBezier == null) != (newBezier == null)
            || (oldBezier != null && Math.abs(oldBezierLength - newBezierLength) > 1e-6);
        final boolean pathModeChanged = oldPathMode != self.getPathMode();
        final boolean bezierMode = RailExtraSupplier.usesBezierCurve(self.getPathMode());

        if (bezierMode && newBezier != null) {
            final Rail thisRail = (Rail)(Object)this;
            RailAngleExtra.rebuildRailMathFromBezier(thisRail, newBezier);
            if (thisRail.railMath.getLength() <= 1E-4) {
                RailAngleExtra.ensureCarrierRailMath(thisRail, newBezier.getLength(),
                        Double.isFinite(a1[0]) ? a1[0] : 0);
            }
            if (bezierChanged || pathModeChanged) {
                rte$evictRender();
            }
        } else if (Double.isFinite(a1[0]) && Double.isFinite(a2[0])) {
            final Rail thisRail = (Rail)(Object)this;
            final double curMismatch = RailAngleExtra.currentTangentMismatch(thisRail, a1[0], a2[0]);
            final double curLen = thisRail.railMath.getLength();
            final double expLen = RailAngleExtra.probeRebuildLength(thisRail, a1[0], a2[0]);
            final boolean railMathAlreadyMatches = !pathModeChanged
                && Double.isFinite(curMismatch) && curMismatch < Math.toRadians(1)
                && Double.isFinite(curLen) && Double.isFinite(expLen) && Math.abs(curLen - expLen) < 0.5;
            if (!railMathAlreadyMatches) {
                RailAngleExtra.rebuildRailMath(thisRail, a1[0], a2[0]);
                rte$evictRender();
            } else if (bezierChanged || pathModeChanged) {
                rte$evictRender();
            }
        } else if (bezierChanged || pathModeChanged) {
            rte$evictRender();
        }
        {
            final RailExtraSupplier extra = (RailExtraSupplier)this;
            if (RailExtraSupplier.requiresBezierCurve(extra.getPathMode()) && extra.getBezierCurve() == null) {
                cn.jstxjf_.rte.RTE.LOGGER.warn("[RailSync] pathMode={} but bezier is null after deserialization, resetting to ORIGINAL for rail {}",
                    extra.getPathMode(), ((Rail)(Object)this).getHexId());
                extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_ORIGINAL);
            }
        }
        try {
            cn.jstxjf_.rte.util.RailRebuildHelper.autoFixOneWayRenderReversed(
                (Rail)(Object)this, (RailAccessor)(Object)this);
        } catch (Exception ignored) {}
    }

    @Unique
    private static String rte$joinModelKeys(java.util.List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return "";
        }
        return String.join("", keys);
    }

    @Unique
    private static java.util.List<String> rte$splitModelKeys(String packed) {
        final java.util.List<String> keys = new java.util.ArrayList<>();
        if (packed == null || packed.isEmpty()) {
            return keys;
        }
        for (final String part : packed.split("")) {
            if (!part.isEmpty()) {
                keys.add(part);
            }
        }
        return keys;
    }

    @Inject(method = "serializeData", at = @At("TAIL"))
    private void rte$serializeDataMixin(WriterBase writer, CallbackInfo ci) {
        if (!(this instanceof RailExtraSupplier)) return;
        RailExtraSupplier self = (RailExtraSupplier) this;

        writer.writeString("model_key", self.getModelKey());
        writer.writeBoolean("is_secondary_dir", self.getRenderReversed());
        writer.writeDouble("vertical_curve_radius", self.getVerticalCurveRadius());
        writer.writeString("custom_configs", StringMapSerializer.serializeToString(self.getCustomConfigs()));
        writer.writeString("roll_angle_map", DoubleFloatMapSerializer.serializeToString(self.getRollAngleMap()));
        writer.writeInt("opening_direction", self.getOpeningDirectionRaw());
        writer.writeDouble("rolling_offset", self.getRollingOffset());
        writer.writeInt("path_mode", self.getPathMode());

        writer.writeString("rte_model_key", self.getModelKey());
        writer.writeString("rte_model_keys", rte$joinModelKeys(self.getModelKeys()));
        writer.writeBoolean("rte_is_secondary_dir", self.getRenderReversed());
        writer.writeDouble("rte_vertical_curve_radius", self.getVerticalCurveRadius());
        writer.writeString("rte_rail_type", self.getRailType());
        writer.writeInt("rte_speed_limit_kph", self.getSpeedLimitKph());
        writer.writeBoolean("rte_collision_enabled", self.isCollisionEnabled());
        writer.writeDouble("rte_collision_width", self.getCollisionWidth());
        writer.writeDouble("rte_collision_height", self.getCollisionHeight());
        writer.writeDouble("rte_collision_length", self.getCollisionLength());
        writer.writeDouble("rte_collision_spacing", self.getCollisionSpacing());
        writer.writeDouble("rte_collision_y_offset", self.getCollisionYOffset());
        writer.writeDouble("rte_collision_side_offset", self.getCollisionSideOffset());
        writer.writeString("rte_custom_configs", StringMapSerializer.serializeToString(self.getCustomConfigs()));
        writer.writeString("rte_roll_angle_map", DoubleFloatMapSerializer.serializeToString(self.getRollAngleMap()));
        writer.writeInt("rte_opening_direction", self.getOpeningDirectionRaw());
        writer.writeDouble("rte_rolling_offset", self.getRollingOffset());
        writer.writeDouble("rte_node_x_offset_1", self.getNodeXOffset1());
        writer.writeDouble("rte_node_y_offset_1", self.getNodeYOffset1());
        writer.writeDouble("rte_node_z_offset_1", self.getNodeZOffset1());
        writer.writeDouble("rte_node_x_offset_2", self.getNodeXOffset2());
        writer.writeDouble("rte_node_y_offset_2", self.getNodeYOffset2());
        writer.writeDouble("rte_node_z_offset_2", self.getNodeZOffset2());
        writer.writeDouble("rte_node_pitch_1", self.getNodePitch1());
        writer.writeDouble("rte_node_pitch_2", self.getNodePitch2());
        writer.writeInt("rte_path_mode", self.getPathMode());
        BezierCurve bezier = self.getBezierCurve();
        if (bezier != null) {
            bezier.save(writer.writeChild("rte_bezier"));
            bezier.saveLegacy(writer);
        }

        double a1 = self.getExtraAngle1Rad();
        double a2 = self.getExtraAngle2Rad();
        if (!Double.isFinite(a1) || !Double.isFinite(a2)) {
            Rail rail = (Rail)(Object)this;
            double rmLen = rail.railMath.getLength();
            if (Double.isFinite(rmLen) && rmLen > 0.01) {
                double eps = Math.min(0.5, rmLen * 0.01);
                if (eps < 0.001) eps = 0.001;
                if (!Double.isFinite(a1)) {
                    org.mtr.core.tool.Vector rm0 = self.railMathPositionFromP1(0);
                    org.mtr.core.tool.Vector rm0e = self.railMathPositionFromP1(eps);
                    if (rm0 != null && rm0e != null) {
                        double tdx = rm0e.x - rm0.x;
                        double tdz = rm0e.z - rm0.z;
                        if (Math.abs(tdx) > 1e-9 || Math.abs(tdz) > 1e-9) {
                            a1 = Math.atan2(tdz, tdx);
                        }
                    }
                }
                if (!Double.isFinite(a2)) {
                    org.mtr.core.tool.Vector rmN = self.railMathPositionFromP1(rmLen);
                    org.mtr.core.tool.Vector rmNe = self.railMathPositionFromP1(rmLen - eps);
                    if (rmN != null && rmNe != null) {
                        double tdx = rmN.x - rmNe.x;
                        double tdz = rmN.z - rmNe.z;
                        if (Math.abs(tdx) > 1e-9 || Math.abs(tdz) > 1e-9) {
                            a2 = Math.atan2(tdz, tdx);
                        }
                    }
                }
            }
            if (!Double.isFinite(a1) && self.getExtraAngle1() != null) {
                a1 = self.getExtraAngle1().angleRadians;
            }
            if (!Double.isFinite(a2) && self.getExtraAngle2() != null) {
                a2 = self.getExtraAngle2().angleRadians;
            }
        }
        final org.mtr.core.data.Position p1 = self.getExtraPosition1();
        final org.mtr.core.data.Position p2 = self.getExtraPosition2();
        if (p1 != null) writer.writeLong("pos_start", rte$asBlockPosLong(p1));
        if (p2 != null) writer.writeLong("pos_end", rte$asBlockPosLong(p2));
        if (Double.isFinite(a1)) writer.writeDouble("facing_start", a1);
        if (Double.isFinite(a2)) writer.writeDouble("facing_end", a2);
        if (Double.isFinite(a1)) writer.writeDouble("rte_angle1_rad", a1);
        if (Double.isFinite(a2)) writer.writeDouble("rte_angle2_rad", a2);

    }

    private void rte$evictRender() {
        if (dev.architectury.platform.Platform.getEnvironment() != dev.architectury.utils.Env.CLIENT) {
            return;
        }
        try {
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(((Rail) (Object) this).getHexId());
        } catch (NoClassDefFoundError | Exception ignored) {
        }
    }

    private static long rte$asBlockPosLong(org.mtr.core.data.Position position) {
        return ((position.getX() & 0x3FFFFFFL) << 38) | ((position.getZ() & 0x3FFFFFFL) << 12) | (position.getY() & 0xFFFL);
    }
}
