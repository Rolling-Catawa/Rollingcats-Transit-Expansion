package cn.jstxjf_.rte.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import cn.jstxjf_.rte.compat.McCompat;


@Mixin(Pack.class)
public abstract class PackMixin {

    @Unique
    private static final String[] rte$customResourceNamespaces = {"mtr", cn.jstxjf_.rte.RTE.LEGACY_RESOURCE_NAMESPACE, "rte"};

    //#if MC>=11903
    @Shadow
    @Final
    private Pack.ResourcesSupplier resources;
    //#else
    @Shadow
    @Final
    private java.util.function.Supplier<PackResources> supplier;
    //#endif

    //#if MC>=12101
    @Shadow
    @Final
    private net.minecraft.server.packs.PackLocationInfo location;
    //#else
    @Shadow
    @Final
    private String id;
    //#endif

    @Unique
    private Boolean rte$isMtrCustomResourcesPack;

    @Inject(method = "getCompatibility", at = @At("HEAD"), cancellable = true)
    private void rte$markMtrCustomResourcesPackCompatible(CallbackInfoReturnable<PackCompatibility> cir) {
        if (rte$isMtrCustomResourcesPack == null) {
            //#if MC>=12101
            try (PackResources packResources = resources.openPrimary(location)) {
            //#elseif MC>=12002
            try (PackResources packResources = resources.openPrimary(id)) {
            //#elseif MC>=11903
            try (PackResources packResources = resources.open(id)) {
            //#else
            try (PackResources packResources = supplier.get()) {
            //#endif
                rte$isMtrCustomResourcesPack = false;
                for (String namespace : rte$customResourceNamespaces) {
                    //#if MC>=11903
                    final boolean found = packResources.getResource(PackType.CLIENT_RESOURCES, McCompat.id(namespace, "mtr_custom_resources.json")) != null;
                    //#else
                    final boolean found = packResources.hasResource(PackType.CLIENT_RESOURCES, McCompat.id(namespace, "mtr_custom_resources.json"));
                    //#endif
                    if (found) {
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
