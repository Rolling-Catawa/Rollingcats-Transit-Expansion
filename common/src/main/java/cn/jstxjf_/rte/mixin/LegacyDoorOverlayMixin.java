package cn.jstxjf_.rte.mixin;

import org.mtr.core.serializer.ReaderBase;
import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;
import java.util.function.Consumer;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyDoorOverlayMixin {

    @Unique
    private boolean rte$renderDoorOverlay = true;

    @Inject(method = "<init>(Lorg/mtr/core/serializer/ReaderBase;)V", at = @At("RETURN"), require = 0)
    private void rte$readDoorOverlayFlag(ReaderBase readerBase, CallbackInfo ci) {
        try {
            rte$renderDoorOverlay = readerBase.getBoolean("render_door_overlay", true);
        } catch (Exception ignored) {
            rte$renderDoorOverlay = true;
        }
    }

    @Redirect(
        method = "convert",
        at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/com/google/gson/JsonArray;forEach(Ljava/util/function/Consumer;)V"),
        require = 0
    )
    private void rte$dropDoorOverlayModels(JsonArray models, Consumer<? super JsonElement> action) {
        if (!rte$renderDoorOverlay) {
            for (int i = models.size() - 1; i >= 0; i--) {
                final JsonElement element = models.get(i);
                if (element.isJsonObject() && element.getAsJsonObject().has("textureResource")
                        && element.getAsJsonObject().get("textureResource").getAsString().toLowerCase(Locale.ROOT).contains("overlay")) {
                    models.remove(i);
                }
            }
        }
        models.forEach(action);
    }
}
