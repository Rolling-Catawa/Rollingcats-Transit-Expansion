package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.mapping.holder.Vector3d;
import cn.jstxjf_.rte.render.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.mtr.render.MainRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainRenderer.class, remap = false)
public abstract class MainRendererMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), require = 0)
    private static void rte$captureRenderOffset(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Vec3 offset, CallbackInfo ci) {
        RenderUtil.mtrRenderOffset = new Vector3d(offset);
    }
}
