package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.GangwayPolicy;
import cn.jstxjf_.rte.util.JcmCompat;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.Reader;
import java.util.Map;

/**
 * MTR 3 inline vehicle scripts need RTE's legacy train/door/path wrappers.
 * JCM may still parse them so its compatibility API can validate the files,
 * but it must not also create and render a second script instance for the same
 * legacy vehicle.
 */
@Pseudo
@Mixin(targets = "com.lx862.mtrscripting.mod.resource.MTRContentResourceManager", remap = false)
public abstract class JcmMtrContentResourceManagerMixin {

    /**
     * RTE owns MTR 3 inline vehicle scripts. Hiding only their inline payload
     * from JCM prevents duplicate renderers and lets RTE repair legacy resource
     * paths, while preserving JCM's modern vehicle and explicit script_id path.
     */
    @Redirect(
        method = "lambda$readMtrCustomResources$3",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/gson/JsonParser;parse(Ljava/io/Reader;)Lcom/google/gson/JsonElement;"
        ),
        require = 0
    )
    private static JsonElement rte$leaveLegacyInlineScriptsToRte(JsonParser parser, Reader reader) {
        final JsonElement root = parser.parse(reader);
        if (JcmCompat.shouldDisableRteJs() || !root.isJsonObject()) {
            return root;
        }

        final JsonElement customTrains = root.getAsJsonObject().get("custom_trains");
        if (customTrains == null || !customTrains.isJsonObject()) {
            return root;
        }

        for (final Map.Entry<String, JsonElement> entry : customTrains.getAsJsonObject().entrySet()) {
            final JsonElement value = entry.getValue();
            if (value == null || !value.isJsonObject()) {
                continue;
            }
            final JsonObject train = value.getAsJsonObject();
            if (!train.has("script_id")) {
                train.remove("script_files");
                train.remove("script_texts");
                train.remove("script_input");
            }
        }
        return root;
    }

    @Inject(method = "getVehicleScriptEntryId", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$preferRteForLegacyVehicle(
        String vehicleId,
        CallbackInfoReturnable<String> cir
    ) {
        if (!JcmCompat.shouldDisableRteJs() && GangwayPolicy.isLegacyFormat(vehicleId)) {
            cir.setReturnValue(null);
        }
    }
}
