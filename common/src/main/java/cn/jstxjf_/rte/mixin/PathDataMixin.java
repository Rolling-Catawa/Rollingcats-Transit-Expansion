package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Data;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.data.Vehicle;
import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PathData.class, remap = false)
public abstract class PathDataMixin {

    private static String rte$lastPathDataSignature = "";

    @Shadow
    public abstract Rail getRail();

    @Shadow
    @Final
    public boolean reversePositions;

    @Redirect(
        method = {"getPosition", "getPositionAndTiltAngle"},
        at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;getPosition(DZ)Lorg/mtr/core/tool/Vector;"),
        require = 1
    )
    private Vector rte$redirectGetPosition(RailMath railMath, double value, boolean reverse) {
        final Rail rail = rte$resolveGeometryCarrier(getRail());
        final boolean useExtendedGeometry = rail != null && ((Object) rail) instanceof RailExtraSupplier supplier
                && ((RailExtraSupplier.usesBezierCurve(supplier.getPathMode())
                && supplier.getBezierCurve() != null) || supplier.hasNodeOffset());

        final Vector result = rte$sample(railMath, rail, useExtendedGeometry, value, reverse);

        return result;
    }

    private static Rail rte$resolveGeometryCarrier(Rail rail) {
        if (rail == null) {
            return null;
        }
        if (((Object) rail) instanceof RailExtraSupplier supplier
                && ((RailExtraSupplier.usesBezierCurve(supplier.getPathMode())
                && supplier.getBezierCurve() != null) || supplier.hasNodeOffset())) {
            return rail;
        }
        try {
            final Rail clientRail = org.mtr.mod.client.MinecraftClientData.getInstance().railIdMap.get(rail.getHexId());
            if (clientRail != null && ((Object) clientRail) instanceof RailExtraSupplier clientSupplier
                    && ((RailExtraSupplier.usesBezierCurve(clientSupplier.getPathMode())
                    && clientSupplier.getBezierCurve() != null) || clientSupplier.hasNodeOffset())) {
                return clientRail;
            }
        } catch (NoClassDefFoundError | Exception ignored) {
        }
        return rail;
    }

    private Vector rte$sample(RailMath railMath, Rail rail, boolean useExtendedGeometry, double value, boolean reverse) {
        if (useExtendedGeometry) {
            final RailExtraSupplier supplier = (RailExtraSupplier) (Object) rail;
            final Position rp1 = supplier.getExtraPosition1();
            final Position rp2 = supplier.getExtraPosition2();
            final boolean railReversed = rp1 != null && rp2 != null && rp1.compareTo(rp2) > 0;
            final boolean effectiveReverse = reverse != railReversed;
            final double actualValue = effectiveReverse ? supplier.getLength() - value : value;
            final Vector3d vec = supplier.getPosition(actualValue);
            return new Vector(vec.getXMapped(), vec.getYMapped(), vec.getZMapped());
        }
        return railMath.getPosition(value, reverse);
    }

    @Redirect(
        method = "getRailLength",
        at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;getLength()D")
    )
    private double rte$redirectGetLength(RailMath railMath) {
        final Rail rail = rte$resolveGeometryCarrier(getRail());
        if (rail != null && ((Object) rail) instanceof RailExtraSupplier supplier
                && ((RailExtraSupplier.usesBezierCurve(supplier.getPathMode())
                && supplier.getBezierCurve() != null) || supplier.hasNodeOffset())) {
            return supplier.getLength();
        }
        return railMath.getLength();
    }
}
