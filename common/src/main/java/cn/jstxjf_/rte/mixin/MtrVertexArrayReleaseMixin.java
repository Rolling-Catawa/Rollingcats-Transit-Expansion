package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.MtrInstancedBatcher;
import org.mtr.mapping.render.object.VertexArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VertexArray.class, remap = false)
public class MtrVertexArrayReleaseMixin {

    @Inject(method = "close", at = @At("HEAD"), require = 0)
    private void rte$releaseInstancedVao(CallbackInfo ci) {
        MtrInstancedBatcher.release((VertexArray) (Object) this);
    }
}
