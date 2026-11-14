package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.render.model.Face;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.vertex.Vertex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RawMesh.class, remap = false)
public class MtrRawMeshDedupeMixin {

    @Shadow(remap = false)
    public List<Vertex> vertices;

    @Shadow(remap = false)
    public List<Face> faces;

    @Inject(method = "distinct", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$skipLargeMeshDedupe(CallbackInfo ci) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.MESH_OPTIMIZE)) {
            return;
        }
        try {
            final int limit = cn.jstxjf_.rte.ClientConfig.mtrMeshDedupeFaceLimit;
            if (limit > 0 && faces != null && faces.size() > limit) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }
}
