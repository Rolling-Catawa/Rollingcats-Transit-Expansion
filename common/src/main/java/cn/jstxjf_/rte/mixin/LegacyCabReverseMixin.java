package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.LegacyReverseFallback;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.resource.ResourceProvider;
import org.mtr.mod.resource.VehicleModel;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleResource.class, remap = false)
public abstract class LegacyCabReverseMixin {

    @Shadow
    @Final
    @Mutable
    private VehicleResource.LegacyVehicleSupplier<ObjectArrayList<VehicleModel>> extraModelsSupplier;

    @Shadow
    public abstract String getId();

    @Inject(method = "<init>(Lorg/mtr/core/serializer/ReaderBase;Lorg/mtr/mod/resource/VehicleResource$LegacyVehicleSupplier;Lorg/mtr/mod/resource/ResourceProvider;)V", at = @At("TAIL"))
    private void rte$wrapBackwardsCab(ReaderBase readerBase, VehicleResource.LegacyVehicleSupplier<ObjectArrayList<VehicleModel>> supplier, ResourceProvider resourceProvider, CallbackInfo ci) {
        final String id = getId();
        if (extraModelsSupplier != null && id != null && id.endsWith("_cab_2")) {
            extraModelsSupplier = LegacyReverseFallback.wrap(id, extraModelsSupplier);
        }
    }
}
