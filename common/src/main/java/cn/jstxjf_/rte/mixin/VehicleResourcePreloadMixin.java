package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.mod.resource.MinecraftModelResource;
import org.mtr.mod.resource.ResourceProvider;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleResource.class, remap = false)
public abstract class VehicleResourcePreloadMixin {

    @Mutable
    @Shadow
    @Final
    public boolean shouldPreload;

    @Shadow
    public abstract void writeMinecraftResource(ObjectArraySet<MinecraftModelResource> minecraftModelResources, ObjectArraySet<String> minecraftTextureResources);

    @Inject(method = "<init>(Lorg/mtr/core/serializer/ReaderBase;Lorg/mtr/mod/resource/VehicleResource$LegacyVehicleSupplier;Lorg/mtr/mod/resource/ResourceProvider;)V", at = @At("RETURN"))
    private void rte$enableBbModelPreload(ReaderBase readerBase, VehicleResource.LegacyVehicleSupplier<?> extraModelsSupplier, ResourceProvider resourceProvider, CallbackInfo ci) {
        if (shouldPreload || !(ClientConfig.enableBbModelPreload || readerBase.getBoolean("preloadBbModel", false))) {
            return;
        }

        final ObjectArraySet<MinecraftModelResource> modelResources = new ObjectArraySet<>();
        writeMinecraftResource(modelResources, new ObjectArraySet<>());
        if (modelResources.stream().anyMatch(modelResource -> ((MinecraftModelResourceAccessor) (Object) modelResource).rte$getModelResource().endsWith(".bbmodel"))) {
            if (!cn.jstxjf_.rte.render.PreloadBudget.tryReserve()) {
                return;
            }
            shouldPreload = true;
            ((VehicleResourceSchemaAccessor) this).rte$getModels().forEach(model -> ((VehicleModelAccessor) (Object) model).rte$setShouldPreload(true));
        }
    }
}
