package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderDiagnostics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "top.mcmtr.mod.render.RenderCatenaryModel", remap = false)
public abstract class CatenaryDiagMixin {

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void rte$diagCatenary(@Coerce Object blockEntity, float tickDelta,
                                  org.mtr.mapping.mapper.GraphicsHolder graphicsHolder,
                                  int light, int overlay, CallbackInfo ci) {
        if (!RenderDiagnostics.allow()) return;
        try {
            final Object pos = blockEntity.getClass().getMethod("getPos2").invoke(blockEntity);
            final int px = (Integer) pos.getClass().getMethod("getX").invoke(pos);
            final int py = (Integer) pos.getClass().getMethod("getY").invoke(pos);
            final int pz = (Integer) pos.getClass().getMethod("getZ").invoke(pos);
            final net.minecraft.world.phys.Vec3 c =
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            final double d = Math.sqrt(Math.pow(px + 0.5 - c.x, 2) + Math.pow(py + 0.5 - c.y, 2) + Math.pow(pz + 0.5 - c.z, 2));
            RenderDiagnostics.log("CATENARY pole=({}, {}, {}) realDist={} incomingGh={}",
                px, py, pz, String.format("%.2f", d), RenderDiagnostics.translationOf(graphicsHolder));
        } catch (Throwable t) {
            RenderDiagnostics.log("CATENARY probe failed: {}", t.toString());
        }
    }
}
