package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.SparseOcclusionCache;
import org.mtr.libraries.com.logisticscraft.occlusionculling.DataProvider;
import org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import org.mtr.mod.render.WorkerThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorkerThread.class, remap = false)
public class WorkerThreadOcclusionMixin {

    @Redirect(
        method = "updateInstance",
        at = @At(value = "NEW", target = "(ILorg/mtr/libraries/com/logisticscraft/occlusionculling/DataProvider;)Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;"),
        require = 0
    )
    private OcclusionCullingInstance rte$useSparseCache(int maxDistance, DataProvider provider) {
        return new OcclusionCullingInstance(maxDistance, provider, new SparseOcclusionCache(), 0.5);
    }
}
