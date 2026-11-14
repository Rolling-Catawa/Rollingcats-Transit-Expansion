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
    //#if MC>=11903
    void getProjectionMatrixTail(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (RailRenderDispatcher.isPreviewingModel) {
            Matrix4f result = new Matrix4f();
            result.translation(0.5f, 0f, 0f);
            result.scale(0.8f, 0.8f, 1f);
            result.mul(cir.getReturnValue());
            cir.setReturnValue(result);
        }
    //#else
    void getProjectionMatrixTail(double fov, CallbackInfoReturnable<com.mojang.math.Matrix4f> cir) {
        if (RailRenderDispatcher.isPreviewingModel) {
            final com.mojang.math.Matrix4f result = com.mojang.math.Matrix4f.createTranslateMatrix(0.5f, 0f, 0f);
            result.multiply(com.mojang.math.Matrix4f.createScaleMatrix(0.8f, 0.8f, 1f));
            result.multiply(cir.getReturnValue());
            cir.setReturnValue(result);
        }
    //#endif

        if (RailRenderDispatcher.isPreviewingModel && hideGuiOptionCache == null) {
            hideGuiOptionCache = minecraft.options.hideGui;
            minecraft.options.hideGui = true;
        } else if (!RailRenderDispatcher.isPreviewingModel && hideGuiOptionCache != null) {
            minecraft.options.hideGui = hideGuiOptionCache;
            hideGuiOptionCache = null;
        }
    }

    //#if MC>=12101
    @org.spongepowered.asm.mixin.injection.ModifyArg(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
        ),
        index = 5
    )
    private Matrix4f injectCustomRotation(Matrix4f frustumMatrix) {
        final PoseStack poseStack = new PoseStack();
        poseStack.mulPose(frustumMatrix);
        Rolling.applyRolling(poseStack);
        cn.jstxjf_.rte.render.CameraPose.capture(poseStack);
        return poseStack.last().pose();
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            shift = At.Shift.AFTER
        ),
        require = 0
    )
    private void rte$renderVehicleLighting(net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        cn.jstxjf_.rte.render.train.VehicleLightRenderer.renderLighting();
    }
    //#else
    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            //#if MC>=11903
            target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;normal()Lorg/joml/Matrix3f;",
            //#else
            target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;normal()Lcom/mojang/math/Matrix3f;",
            //#endif
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void injectCustomRotation(float partialTick, long finishNanoTime, PoseStack poseStack, CallbackInfo ci) {
        Rolling.applyRolling(poseStack);
        cn.jstxjf_.rte.render.CameraPose.capture(poseStack);
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            //#if MC>=11903
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            //#else
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
            //#endif
            shift = At.Shift.AFTER
        ),
        require = 0
    )
    private void rte$renderVehicleLighting(float partialTick, long finishNanoTime, PoseStack poseStack, CallbackInfo ci) {
        cn.jstxjf_.rte.render.train.VehicleLightRenderer.renderLighting();
    }
    //#endif
}
