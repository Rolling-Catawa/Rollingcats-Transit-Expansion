package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.Box;
import org.mtr.mod.render.RenderVehicleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RenderVehicleHelper.class, remap = false)
public class DoorwayMirrorMixin {

    @org.spongepowered.asm.mixin.injection.ModifyVariable(
        method = "canOpenDoors", at = @At("HEAD"), argsOnly = true, index = 0)
    private static Box rte$mirrorDoorwayForReversed(Box box) {
        try {
            if (!cn.jstxjf_.rte.ClientConfig.mirrorReversedDoorways) {
                return box;
            }
            final org.mtr.mod.data.VehicleExtension vehicle = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.get();
            if (vehicle == null || !((cn.jstxjf_.rte.data.TrainExtraSupplier) vehicle).getVehicleReversed()) {
                return box;
            }
            return new Box(
                -box.getMaxXMapped(), box.getMinYMapped(), box.getMinZMapped(),
                -box.getMinXMapped(), box.getMaxYMapped(), box.getMaxZMapped());
        } catch (Throwable e) {
            return box;
        }
    }
}
