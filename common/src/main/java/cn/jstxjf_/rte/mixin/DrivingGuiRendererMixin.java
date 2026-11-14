package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTEConfig;
import org.mtr.mod.render.DrivingGuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = DrivingGuiRenderer.class, remap = false)
public abstract class DrivingGuiRendererMixin {

    @ModifyConstant(method = "render", constant = @Constant(intValue = -7), require = 0)
    private static int rte$emergencyDisplayThreshold(int value) {
        return -7 * RTEConfig.manualNotchMultiplier;
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = 5), require = 0)
    private static int rte$percentRatio(int value) {
        return 5 * RTEConfig.manualNotchMultiplier;
    }
}
