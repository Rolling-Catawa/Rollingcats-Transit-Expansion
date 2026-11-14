package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderUtil;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MainRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainRenderer.class, remap = false)
public abstract class MainRendererMixin {

    @Inject(method = "render(Lorg/mtr/mapping/mapper/GraphicsHolder;Lorg/mtr/mapping/holder/Vector3d;)V", at = @At("HEAD"), require = 0)
    private static void rte$captureRenderOffset(GraphicsHolder graphicsHolder, Vector3d offset, CallbackInfo ci) {
        RenderUtil.mtrRenderOffset = offset;
    }
}
