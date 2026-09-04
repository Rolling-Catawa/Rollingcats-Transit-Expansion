package cn.jstxjf_.rte.mixin;

import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyPlugDoorFixMixin {

    private static boolean rte$hookReported = false;
    private static int rte$fixedParts = 0;

    @Redirect(
        method = "lambda$processModel$6",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/libraries/com/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V"
        ),
        require = 0
    )
    private void rte$flipPlugDoorX(JsonObject target, String key, Number value) {
        Number finalValue = value;
        try {
            if (cn.jstxjf_.rte.ClientConfig.fixLegacyPlugDoors
                    && "doorXMultiplier".equals(key)
                    && value != null
                    && value.doubleValue() != 0) {
                finalValue = -value.doubleValue();
                if (++rte$fixedParts <= 5) {
                    cn.jstxjf_.rte.RTE.LOGGER.info(
                        "[LegacyPlugDoor] doorXMultiplier {} -> {}", value.doubleValue(), finalValue.doubleValue());
                }
            }
            if (!rte$hookReported) {
                rte$hookReported = true;
                cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyPlugDoor] hook active");
            }
        } catch (Throwable ignored) {
            finalValue = value;
        }
        target.addProperty(key, finalValue);
    }
}
