package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.Rolling;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.joml.Matrix4f;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final Minecraft minecraft;
    @Unique private Boolean hideGuiOptionCache = null;

    @Inject(method = "getProjectionMatrix", at = @At("TAIL"), cancellable = true)
    void getProjectionMatrixTail(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (RailRenderDispatcher.isPreviewingModel) {
            Matrix4f result = new Matrix4f();
            result.translation(0.5f, 0f, 0f);
            result.scale(0.8f, 0.8f, 1f);
            result.mul(cir.getReturnValue());
            cir.setReturnValue(result);
        }

        if (RailRenderDispatcher.isPreviewingModel && hideGuiOptionCache == null) {
            hideGuiOptionCache = minecraft.options.hideGui;
            minecraft.options.hideGui = true;
        } else if (!RailRenderDispatcher.isPreviewingModel && hideGuiOptionCache != null) {
            minecraft.options.hideGui = hideGuiOptionCache;
            hideGuiOptionCache = null;
        }
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;normal()Lorg/joml/Matrix3f;",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void injectCustomRotation(float partialTick, long finishNanoTime, PoseStack poseStack, CallbackInfo ci) {
        Rolling.applyRolling(poseStack);
        cn.jstxjf_.rte.render.CameraPose.capture(poseStack);
    }
}
