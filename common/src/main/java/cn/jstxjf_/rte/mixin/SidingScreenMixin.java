package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Siding;
import org.mtr.mod.screen.SidingScreen;
import org.mtr.mod.screen.WidgetShorterSlider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SidingScreen.class, remap = false)
public abstract class SidingScreenMixin {

    private static final int RTE_MAX_MANUAL_SPEED_KPH = 10000;

    @Shadow
    @Final
    private WidgetShorterSlider sliderMaxManualSpeed;

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/mod/screen/WidgetShorterSlider;<init>(IIILjava/util/function/IntFunction;Ljava/util/function/IntConsumer;)V",
            ordinal = 4
        ),
        index = 2
    )
    private int rte$expandManualSpeedSlider(int originalMaxValue) {
        return Math.max(originalMaxValue, RTE_MAX_MANUAL_SPEED_KPH);
    }

    @Inject(method = "init2", at = @At("TAIL"))
    private void rte$restoreManualSpeedFromKph(CallbackInfo ci) {
        sliderMaxManualSpeed.setValue(rte$metersPerMillisecondToKilometersPerHour(rte$getSavedRailBase().getMaxManualSpeed()));
    }

    @Inject(
        method = "onClose2",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/core/data/Siding;setMaxManualSpeed(D)V",
            shift = At.Shift.AFTER
        )
    )
    private void rte$storeManualSpeedAsDirectKph(CallbackInfo ci) {
        rte$getSavedRailBase().setMaxManualSpeed(rte$kilometersPerHourToMetersPerMillisecond(sliderMaxManualSpeed.getIntValue()));
    }

    @Inject(method = "speedSliderFormatter", at = @At("HEAD"), cancellable = true)
    private static void rte$formatManualSpeedAsDirectKph(int value, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Math.max(0, value) + " km/h");
    }

    private static int rte$metersPerMillisecondToKilometersPerHour(double value) {
        return (int) Math.round(value * 3600D);
    }

    private static double rte$kilometersPerHourToMetersPerMillisecond(int value) {
        return Math.max(0, value) / 3600D;
    }

    private Siding rte$getSavedRailBase() {
        return (Siding) ((SavedRailScreenBaseAccessor) this).rte$getSavedRailBase();
    }
}
