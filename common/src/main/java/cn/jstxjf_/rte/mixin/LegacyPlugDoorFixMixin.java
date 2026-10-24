package cn.jstxjf_.rte.mixin;

import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyPlugDoorFixMixin {

    private static boolean rte$hookReported = false;
    private static int rte$fixedParts = 0;
    private static int rte$fixedConditions = 0;
    private static final ThreadLocal<Integer> RTE_OUTPUT_SIZE = new ThreadLocal<>();
    private static final ThreadLocal<String> RTE_DOOR_SIDE = new ThreadLocal<>();
    private static final ThreadLocal<String> RTE_RENDER_CONDITION = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> RTE_DROP_PART = new ThreadLocal<>();
    private static final ThreadLocal<String> RTE_RENDER_STAGE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> RTE_MIRROR = new ThreadLocal<>();

    @Inject(
        method = {"lambda$processModel$6", "lambda$processModel$4"},
        at = @At("HEAD"),
        require = 0
    )
    private void rte$normaliseDoorCondition(
            String unused1, String unused2, int unused3, int unused4,
            JsonArray outputParts, String unused6, double unused7,
            boolean unused8, boolean unused9, JsonArray unused10,
            JsonElement sourceElement, CallbackInfo ci) {
        try {
            RTE_OUTPUT_SIZE.set(outputParts == null ? 0 : outputParts.size());
            RTE_DOOR_SIDE.set("");
            RTE_RENDER_CONDITION.set("");
            RTE_MIRROR.set(false);
            if (sourceElement == null || !sourceElement.isJsonObject()) {
                return;
            }
            final JsonObject source = sourceElement.getAsJsonObject();
            RTE_MIRROR.set(source.has("mirror") && source.get("mirror").isJsonPrimitive() && source.get("mirror").getAsBoolean());
            RTE_DROP_PART.set(false);
            RTE_RENDER_STAGE.set("");
            if (source.has("stage") && source.get("stage").isJsonPrimitive()) {
                final String stage = source.get("stage").getAsString().toUpperCase(java.util.Locale.ROOT);
                switch (stage) {
                    case "LIGHTS" -> RTE_RENDER_STAGE.set("LIGHT");
                    case "ALWAYS_ON_LIGHTS" -> RTE_RENDER_STAGE.set("ALWAYS_ON_LIGHT");
                    case "INTERIOR", "INTERIOR_TRANSLUCENT", "EXTERIOR" -> RTE_RENDER_STAGE.set(stage);
                    default -> RTE_DROP_PART.set(true);
                }
            }
            if (source.has("door_offset") && source.get("door_offset").isJsonPrimitive()) {
                final String offset = source.get("door_offset").getAsString()
                    .toUpperCase(java.util.Locale.ROOT);
                RTE_DOOR_SIDE.set(offset.startsWith("LEFT_") ? "LEFT"
                    : offset.startsWith("RIGHT_") ? "RIGHT" : "");
            }
            if (!source.has("render_condition") || !source.get("render_condition").isJsonPrimitive()) {
                return;
            }
            final String raw = source.get("render_condition").getAsString();
            final String normalised = switch (raw.toUpperCase(java.util.Locale.ROOT)) {
                case "DOOR_OPEN" -> "DOORS_OPEN";
                case "DOOR_LEFT_OPEN" -> "DOORS_LEFT_OPEN";
                case "DOOR_RIGHT_OPEN" -> "DOORS_RIGHT_OPEN";
                case "DOOR_CLOSED" -> "DOORS_CLOSED";
                case "DOOR_LEFT_CLOSED" -> "DOORS_LEFT_CLOSED";
                case "DOOR_RIGHT_CLOSED" -> "DOORS_RIGHT_CLOSED";
                default -> raw;
            };
            if (!normalised.equals(raw)) {
                source.addProperty("render_condition", normalised);
                if (++rte$fixedConditions <= 12) {
                    cn.jstxjf_.rte.RTE.LOGGER.info(
                        "[LegacyDoorCondition] {} -> {}", raw, normalised);
                }
            }
            RTE_RENDER_CONDITION.set(normalised.toUpperCase(java.util.Locale.ROOT));
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyDoorCondition] normalisation failed", e);
        }
    }

    @Inject(
        method = {"lambda$processModel$6", "lambda$processModel$4"},
        at = @At("RETURN"), require = 0
    )
    private void rte$retainLegacyDoorMetadata(
            String unused1, String unused2, int unused3, int unused4,
            JsonArray outputParts, String unused6, double unused7,
            boolean unused8, boolean unused9, JsonArray unused10,
            JsonElement sourceElement, CallbackInfo ci) {
        try {
            if (outputParts == null) {
                return;
            }
            final Integer capturedSize = RTE_OUTPUT_SIZE.get();
            final int start = Math.max(0, capturedSize == null ? outputParts.size() : capturedSize);
            if (Boolean.TRUE.equals(RTE_DROP_PART.get())) {
                for (int i = outputParts.size() - 1; i >= start; i--) {
                    outputParts.remove(i);
                }
                return;
            }
            final String renderStage = RTE_RENDER_STAGE.get() == null ? "" : RTE_RENDER_STAGE.get();
            final String side = RTE_DOOR_SIDE.get() == null ? "" : RTE_DOOR_SIDE.get();
            final String condition = RTE_RENDER_CONDITION.get() == null
                ? "" : RTE_RENDER_CONDITION.get();
            for (int i = start; i < outputParts.size(); i++) {
                if (!outputParts.get(i).isJsonObject()) {
                    continue;
                }
                final JsonObject converted = outputParts.get(i).getAsJsonObject();
                if (!renderStage.isEmpty()) {
                    converted.addProperty("renderStage", renderStage);
                }
                if (!side.isEmpty()) {
                    converted.addProperty("rteLegacyDoorSide", side);
                }
                if (!condition.isEmpty()) {
                    converted.addProperty("rteLegacyRenderCondition", condition);
                    if ((condition.contains("LEFT") || condition.contains("RIGHT")) && condition.endsWith("_CLOSED")) {
                        converted.addProperty("condition", "NORMAL");
                    }
                }
            }
        } finally {
            RTE_OUTPUT_SIZE.remove();
            RTE_DOOR_SIDE.remove();
            RTE_RENDER_CONDITION.remove();
            RTE_DROP_PART.remove();
            RTE_RENDER_STAGE.remove();
            RTE_MIRROR.remove();
        }
    }

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
            if ("doorZMultiplier".equals(key) && value != null && value.doubleValue() != 0
                    && Boolean.TRUE.equals(RTE_MIRROR.get())) {
                finalValue = -value.doubleValue();
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
