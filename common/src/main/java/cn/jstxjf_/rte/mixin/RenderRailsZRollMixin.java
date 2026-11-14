package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Rail;
import org.mtr.mod.render.RenderRails;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.lang.reflect.Field;
import java.util.function.Consumer;

@Mixin(value = RenderRails.class, remap = false)
public abstract class RenderRailsZRollMixin {

    private static final ThreadLocal<Rail> RTE_CURRENT_RAIL = new ThreadLocal<>();

    @Unique private static Field rte$smtXField;
    @Unique private static Field rte$smtZField;

    static {
        try {
            rte$smtXField = StoredMatrixTransformations.class.getDeclaredField("initialTranslateX");
            rte$smtZField = StoredMatrixTransformations.class.getDeclaredField("initialTranslateZ");
            rte$smtXField.setAccessible(true);
            rte$smtZField.setAccessible(true);
        } catch (Exception ignored) {
            rte$smtXField = null;
            rte$smtZField = null;
        }
    }

    @ModifyArg(
            method = "renderRailStandard(Lorg/mtr/mapping/holder/ClientWorld;Lorg/mtr/core/data/Rail;FLorg/mtr/mod/render/RenderRails$RenderState;FLorg/mtr/mapping/holder/Identifier;FFFF)V",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/RenderRails;renderWithinRenderDistance(Lorg/mtr/core/data/Rail;Lorg/mtr/mod/render/RenderRails$RenderRailWithBlockPos;DFF)V"),
            index = 0,
            require = 0
    )
    private static Rail rte$captureRailInLong(Rail rail) {
        RTE_CURRENT_RAIL.set(rail);
        return rail;
    }

    @ModifyArg(method = "renderRailStandard",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/resource/RailResource;render(Lorg/mtr/mapping/holder/StoredMatrixTransformations;I)V"),
            index = 0, require = 0)
    private static Object rte$applySuperelevation(Object smt) {
        try {
            if (smt == null) return smt;
            final Rail rail = RTE_CURRENT_RAIL.get();
            if (rail == null || !(((Object) rail) instanceof RailExtraSupplier supplier)) return smt;

            final java.util.Map<Double, Float> map = supplier.getRollAngleMap();
            if (map == null || map.isEmpty()) return smt;

            float rollRad = 0;
            if (rte$smtXField != null && rte$smtZField != null) {
                double segX = rte$smtXField.getDouble(smt);
                double segZ = rte$smtZField.getDouble(smt);
                org.mtr.core.data.Position p1 = supplier.getExtraPosition1();
                if (p1 != null) {
                    double dx = segX - p1.getX() - 0.5;
                    double dz = segZ - p1.getZ() - 0.5;
                    double approxDist = Math.sqrt(dx * dx + dz * dz);
                    rollRad = RailExtraSupplier.getRollAngle(rail, approxDist);
                }
            }

            if (rollRad == 0) return smt;

            final float finalRoll = rollRad;
            final float finalAngleDeg = (float) Math.toDegrees(finalRoll);
            final Consumer<Object> consumer = gh -> {
                try {
                    gh.getClass().getMethod("rotateZDegrees", float.class).invoke(gh, finalAngleDeg);
                } catch (Throwable ignored) {
                }
            };
            smt.getClass().getMethod("add", Consumer.class).invoke(smt, consumer);
        } catch (Throwable ignored) {
        }
        return smt;
    }
}
