package cn.jstxjf_.rte.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(Pack.class)
public abstract class PackMixin {

    @Unique
    private static final String[] rte$customResourceNamespaces = {"mtr", cn.jstxjf_.rte.RTE.LEGACY_RESOURCE_NAMESPACE, "rte"};

    @Shadow
    @Final
    private Pack.ResourcesSupplier resources;

    @Shadow
    @Final
    private String id;

    @Unique
    private Boolean rte$isMtrCustomResourcesPack;

    @Inject(method = "getCompatibility", at = @At("HEAD"), cancellable = true)
    private void rte$markMtrCustomResourcesPackCompatible(CallbackInfoReturnable<PackCompatibility> cir) {
        if (rte$isMtrCustomResourcesPack == null) {
            //#if MC>=12002
            try (PackResources packResources = resources.openPrimary(id)) {
            //#else
            try (PackResources packResources = resources.open(id)) {
            //#endif
                rte$isMtrCustomResourcesPack = false;
                for (String namespace : rte$customResourceNamespaces) {
                    final IoSupplier<InputStream> ioSupplier = packResources.getResource(
                        PackType.CLIENT_RESOURCES,
                        new ResourceLocation(namespace, "mtr_custom_resources.json")
                    );
                    if (ioSupplier != null) {
                        rte$isMtrCustomResourcesPack = true;
                        break;
                    }
                }
            } catch (Exception ignored) {
                rte$isMtrCustomResourcesPack = false;
            }
        }

        if (Boolean.TRUE.equals(rte$isMtrCustomResourcesPack)) {
            cir.setReturnValue(PackCompatibility.COMPATIBLE);
        }
    }
}
