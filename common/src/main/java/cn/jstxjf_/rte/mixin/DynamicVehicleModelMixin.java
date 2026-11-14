package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderUtil;
import cn.jstxjf_.rte.render.train.BuiltinTrainRenderers;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectDoubleImmutablePair;
import org.mtr.mapping.holder.Box;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicVehicleModel.class, remap = false)
public class DynamicVehicleModelMixin {
    @org.spongepowered.asm.mixin.Shadow
    private org.mtr.mod.resource.ModelProperties modelProperties;

    @Inject(method = "render(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V", at = @At("HEAD"), cancellable = true)
    private void rte$renderCustomTrain(
        StoredMatrixTransformations storedMatrixTransformations,
        VehicleExtension vehicle,
        int carNumber,
        int[] scrollingDisplayIndexTracker,
        int light,
        ObjectArrayList<?> openDoorways,
        boolean fromResourcePackCreator,
        CallbackInfo ci
    ) {
        if (fromResourcePackCreator || vehicle == null) {
            return;
        }
        cn.jstxjf_.rte.util.LegacyVisualDoorAdapter.prepare(
            modelProperties, vehicle, carNumber, (ObjectArrayList) openDoorways);
        if (RenderUtil.shouldSkipRenderTrain(vehicle)) {
            ci.cancel();
            return;
        }
        ScriptedTrainRenderers.renderIfScripted(vehicle, carNumber, light);
        if (BuiltinTrainRenderers.renderIfBuiltin(vehicle, carNumber, light, (ObjectArrayList<ObjectDoubleImmutablePair<Box>>) openDoorways)) {
            ci.cancel();
            return;
        }
    }
}
