package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.AsyncVehicleModelLoader;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mod.generated.resource.VehicleModelSchema;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.resource.CustomResourceTools;
import org.mtr.mod.resource.ModelProperties;
import org.mtr.mod.resource.ModelResourceLoader;
import org.mtr.mod.resource.PositionDefinitions;
import org.mtr.mod.resource.ResourceProvider;
import org.mtr.mod.resource.VehicleModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleModel.class, remap = false)
public abstract class VehicleModelAsyncLoadMixin extends VehicleModelSchema {

    protected VehicleModelAsyncLoadMixin(ReaderBase readerBase, ResourceProvider resourceProvider) {
        super(readerBase, resourceProvider);
    }

    @Inject(method = "createModel", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$deferUntilLoaded(ModelProperties modelProperties, PositionDefinitions positionDefinitions, String id,
                                      CallbackInfoReturnable<DynamicVehicleModel> cir) {
        final Identifier textureId = CustomResourceTools.formatIdentifierWithDefault(textureResource, "png");
        if (!AsyncVehicleModelLoader.isReady(this, modelResource, textureId, flipTextureV)) {
            cir.setReturnValue(null);
        }
    }

    @Redirect(
        method = "createModel",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/mod/resource/ModelResourceLoader;loadModel(Ljava/lang/String;Lorg/mtr/mapping/holder/Identifier;ZLorg/mtr/mod/resource/ResourceProvider;)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectAVLTreeMap;"
        ),
        require = 0
    )
    private Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> rte$usePreloadedModel(
            String modelResource, Identifier textureId, boolean flipTextureV, ResourceProvider resourceProvider) {
        final Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> preloaded = AsyncVehicleModelLoader.take(this);
        return preloaded != null ? preloaded : ModelResourceLoader.loadModel(modelResource, textureId, flipTextureV, resourceProvider);
    }
}
