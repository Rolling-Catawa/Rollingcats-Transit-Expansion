package cn.jstxjf_.rte.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.resource.ModelProperties;
import org.mtr.mod.resource.PositionDefinitions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicVehicleModel.class, remap = false)
public abstract class DynamicVehicleModelLampMixin {

    @Inject(
        method = "<init>(Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectAVLTreeMap;Lorg/mtr/mapping/holder/Identifier;Lorg/mtr/mod/resource/ModelProperties;Lorg/mtr/mod/resource/PositionDefinitions;Ljava/lang/String;)V",
        at = @At("TAIL"),
        require = 0
    )
    private void rte$captureLampGeometry(Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> nameToObjModels, Identifier texture,
                                         ModelProperties modelProperties, PositionDefinitions positionDefinitions, String id, CallbackInfo ci) {
        cn.jstxjf_.rte.render.train.ModelLampFinder.captureObjModel((DynamicVehicleModel) (Object) this, nameToObjModels, modelProperties);
    }
}
