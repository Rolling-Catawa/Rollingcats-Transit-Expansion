package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderDiagnostics;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.resource.OptimizedModelWrapper;
import org.mtr.mod.resource.OptimizedRendererWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OptimizedRendererWrapper.class, remap = false)
public abstract class QueueDiagMixin {

    @Inject(method = "queue", at = @At("HEAD"), require = 0)
    private void rte$diagQueue(OptimizedModelWrapper model, GraphicsHolder graphicsHolder, int light, CallbackInfo ci) {
        if (!RenderDiagnostics.allow()) return;
        RenderDiagnostics.log("QUEUE modelMatrix={}", RenderDiagnostics.translationOf(graphicsHolder));
    }
}
