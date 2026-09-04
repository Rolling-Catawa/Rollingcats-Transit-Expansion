package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.TransportMode;
import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import org.mtr.mod.client.CustomResourceLoader;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyVehicleResourceMixin {

    @Redirect(
        method = "convert",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/mod/client/CustomResourceLoader;getVehicleById(Lorg/mtr/core/data/TransportMode;Ljava/lang/String;Ljava/util/function/Consumer;)V"
        )
    )
    private void rte$getVehicleByIdWithMtr4LegacyAliases(
        TransportMode transportMode,
        String vehicleId,
        Consumer<ObjectBooleanImmutablePair<VehicleResource>> ifPresent
    ) {
        if (tryGetVehicleById(transportMode, vehicleId, ifPresent)) {
            return;
        }
        for (String alias : getMtr4DefinitionAliases(vehicleId)) {
            if (tryGetVehicleById(transportMode, alias, ifPresent)) {
                return;
            }
        }
    }

    private static boolean tryGetVehicleById(
        TransportMode transportMode,
        String vehicleId,
        Consumer<ObjectBooleanImmutablePair<VehicleResource>> ifPresent
    ) {
        final AtomicBoolean found = new AtomicBoolean(false);
        CustomResourceLoader.getVehicleById(transportMode, vehicleId, vehicleResourceDetails -> {
            found.set(true);
            ifPresent.accept(vehicleResourceDetails);
        });
        return found.get();
    }

    private static Set<String> getMtr4DefinitionAliases(String vehicleId) {
        final Set<String> aliases = new LinkedHashSet<>();
        if (vehicleId == null || vehicleId.contains("@definition@")) {
            return aliases;
        }
        for (String variation : new String[]{"trailer", "cab_1", "cab_2", "cab_3"}) {
            final String suffix = "_" + variation;
            if (vehicleId.endsWith(suffix)) {
                final String base = vehicleId.substring(0, vehicleId.length() - suffix.length());
                aliases.add(base + "@definition@" + suffix);
                if (base.endsWith("_small")) {
                    aliases.add(base.substring(0, base.length() - 6) + "@definition@" + suffix);
                }
                if (base.endsWith("_mini")) {
                    aliases.add(base.substring(0, base.length() - 5) + "@definition@" + suffix);
                }
            }
        }
        return aliases;
    }
}
