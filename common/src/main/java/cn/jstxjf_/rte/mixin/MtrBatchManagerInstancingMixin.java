package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.MtrInstancedBatcher;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.shader.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(value = BatchManager.class, remap = false)
public class MtrBatchManagerInstancingMixin {

    @Inject(method = "drawBatch", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$drawBatchInstanced(Map<MaterialProperties, List<?>> batches, ShaderManager shaderManager, CallbackInfo ci) {
        if (!MtrInstancedBatcher.isActive()) {
            return;
        }
        MtrInstancedBatcher.drawBatches(batches, shaderManager);
        batches.clear();
        ci.cancel();
    }
}
