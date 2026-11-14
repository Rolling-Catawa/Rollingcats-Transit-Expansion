package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.LegacyReverseFallback;
import org.mtr.legacy.resource.LegacyVehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public abstract class LegacyProcessModelReverseMixin {

    @ModifyVariable(method = "processModel", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private boolean rte$fallbackReversed(boolean reversed) {
        return reversed || LegacyReverseFallback.active();
    }
}
