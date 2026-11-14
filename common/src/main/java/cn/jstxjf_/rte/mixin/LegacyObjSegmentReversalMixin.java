package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.LegacyModelSegments;
import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.resource.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyObjSegmentReversalMixin {

    @Inject(method = "lambda$convert$4", at = @At("HEAD"), require = 0)
    private void rte$beginCarSegments(JsonObject propertiesObject, double doorMax, String id, ResourceProvider resourceProvider,
                                      int currentCar, int totalCars, CallbackInfoReturnable<ObjectArrayList<?>> cir) {
        LegacyModelSegments.begin(((LegacyVehicleSchemaAccessor) (Object) this).rte$getLegacyModel(), currentCar, totalCars);
    }

    @Inject(method = "lambda$convert$4", at = @At("RETURN"), require = 0)
    private void rte$endCarSegments(JsonObject propertiesObject, double doorMax, String id, ResourceProvider resourceProvider,
                                    int currentCar, int totalCars, CallbackInfoReturnable<ObjectArrayList<?>> cir) {
        LegacyModelSegments.end();
    }

    @Inject(method = "processPositions", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$objPositionsInModelSpace(JsonObject propertiesPartsObject, String property, boolean reversed,
                                                    JsonArray positionDefinitionPositionsArray, CallbackInfo ci) {
        if (!LegacyModelSegments.isObjCar() || !"positions".equals(property)
                || propertiesPartsObject.has("mirror") && propertiesPartsObject.get("mirror").getAsBoolean()) {
            return;
        }
        ci.cancel();
        try {
            propertiesPartsObject.getAsJsonArray(property).forEach(jsonElement -> {
                final JsonObject positionObject = new JsonObject();
                positionObject.addProperty("x", jsonElement.getAsJsonArray().get(0).getAsDouble());
                positionObject.addProperty("z", -jsonElement.getAsJsonArray().get(1).getAsDouble() * (reversed ? -1 : 1));
                positionDefinitionPositionsArray.add(positionObject);
            });
        } catch (Exception ignored) {
        }
    }

    @ModifyVariable(method = "lambda$convert$3", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private static JsonObject rte$scopePartsToModel(JsonObject modelPropertiesObject, JsonObject sameModelPropertiesObject, JsonObject positionDefinitionsObject,
                                                    String id, ResourceProvider resourceProvider, JsonObject modelObject) {
        return LegacyModelSegments.filterPartsForModel(modelPropertiesObject, modelObject);
    }

    @ModifyVariable(method = "lambda$convert$3", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private static JsonObject rte$applySegmentReversal(JsonObject positionDefinitionsObject) {
        return LegacyModelSegments.nextPositionDefinitions(positionDefinitionsObject);
    }
}
