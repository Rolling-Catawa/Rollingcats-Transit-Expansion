package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.render.train.AsyncOptimizedModelBuilder;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.mapper.OptimizedRenderer;
import org.mtr.mod.resource.CachedResource;
import org.mtr.mod.resource.OptimizedModelWrapper;
import org.mtr.mod.resource.PartCondition;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleResource.class, remap = false)
public class VehicleResourceAsyncBuildMixin {

    @Inject(method = "writeToOptimizedModels", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$buildOptimizedModelsInBackground(
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper>> materialGroupsModel,
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper>> objModelsModel,
            int modelLifespan,
            CallbackInfoReturnable<CachedResource<Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper>>> cir) {
        if (!ClientConfig.asyncVehicleModelLoading || !OptimizedRenderer.hasOptimizedRendering()) {
            return;
        }
        cir.setReturnValue(new CachedResource<>(AsyncOptimizedModelBuilder.supplier(materialGroupsModel, objModelsModel), modelLifespan));
    }
}
