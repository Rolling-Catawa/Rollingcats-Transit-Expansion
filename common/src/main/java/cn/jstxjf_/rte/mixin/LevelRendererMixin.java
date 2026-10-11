package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.render.RailDistanceRenderer;
import cn.jstxjf_.rte.render.RailPicker;
import cn.jstxjf_.rte.render.RenderUtil;
import cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer;
import cn.jstxjf_.rte.render.block.BlockEntityEyeCandyRenderer;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import cn.jstxjf_.rte.gfx.util.GlStateTracker;
import cn.jstxjf_.rte.gfxext.model.integration.BufferSourceProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    private int rte$lvLogCooldown = 0;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void afterRenderChunkLayer(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, org.joml.Matrix4f projectionMatrix, CallbackInfo ci) {
        rte$worldDrawnEarly = false;
        if (!cn.jstxjf_.rte.render.ShadersModHandler.isShaderPackGpuPathActive()) {
            return;
        }
        final Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }
        rte$drawWorldContent(client, poseStack, camera);
        rte$worldDrawnEarly = true;
    }

    private boolean rte$worldDrawnEarly;

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderLevelTail(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, org.joml.Matrix4f projectionMatrix, CallbackInfo ci) {

         Minecraft client = Minecraft.getInstance();
         if (client.level == null) return;
         MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();
         net.minecraft.world.phys.Vec3 camPos3 = camera.getPosition();
         if (!rte$worldDrawnEarly) {
             rte$drawWorldContent(client, poseStack, camera);
         }
         rte$worldDrawnEarly = false;
         rte$drawOverlays(client, poseStack, camera, bufferSource, camPos3);
    }

    private void rte$drawWorldContent(Minecraft client, PoseStack poseStack, Camera camera) {
         MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

         BlockEntityDirectNodeRenderer.exchange();
         BlockEntityEyeCandyRenderer.exchange();
         cn.jstxjf_.rte.scripting.util.client.GraphicsTexture.flushPendingUploads();

         RenderUtil.commonVertexConsumers = bufferSource;
         RenderUtil.commonPoseStack = poseStack;
         RenderUtil.updateElapsedTicks();
         if (rte$lvLogCooldown <= 0) {
             rte$lvLogCooldown = 200;
         } else { rte$lvLogCooldown--; }
         cn.jstxjf_.rte.data.Rolling.update();

         net.minecraft.world.phys.Vec3 camPos3 = camera.getPosition();
         cn.jstxjf_.rte.gfx.math.Matrix4f viewMatrix = cn.jstxjf_.rte.gfx.math.Matrix4f.translation(
                 -(float) camPos3.x, -(float) camPos3.y, -(float) camPos3.z);

         RTEClient.railRenderDispatcher.prepareDraw();

         org.mtr.mapping.holder.World worldWrapper = new org.mtr.mapping.holder.World(client.level);

         if (ClientConfig.getRailRenderLevel() >= 2) {
            RTEClient.railRenderDispatcher.drawRails(worldWrapper, RTEClient.drawScheduler, viewMatrix);
            if (RTEClient.drawScheduler.shaderManager.isReady()) {
                GlStateTracker.capture();
                try {
                    RTEClient.drawScheduler.commitRaw(RTEClient.drawContext);
                } finally {
                    GlStateTracker.restore();
                }
            } else {
                RTEClient.drawScheduler.batchManager.clearAll();
            }

            if (client.getEntityRenderDispatcher().shouldRenderHitBoxes() && !client.showOnlyReducedInfo()) {
                poseStack.pushPose();
                poseStack.translate(-camPos3.x, -camPos3.y, -camPos3.z);
                RTEClient.railRenderDispatcher.drawBoundingBoxes(poseStack, bufferSource.getBuffer(RenderType.lines()));
                poseStack.popPose();
            }

        }

        RTEClient.drawContext.drawWithBlaze = !ClientConfig.useRenderOptimization();
        RTEClient.drawContext.sortTranslucentFaces = ClientConfig.translucentSort;

        BufferSourceProxy vertexConsumersProxy = new BufferSourceProxy(bufferSource);
        RTEClient.drawScheduler.commit(vertexConsumersProxy, RTEClient.drawContext);
        vertexConsumersProxy.commit();

        BlockEntityDirectNodeRenderer.commit(poseStack, bufferSource);
        BlockEntityEyeCandyRenderer.commit(poseStack, bufferSource);

        poseStack.pushPose();
        poseStack.translate(-camPos3.x, -camPos3.y, -camPos3.z);
        cn.jstxjf_.rte.render.FreeNodeEditor.render(poseStack);
        poseStack.popPose();

        vertexConsumersProxy = new BufferSourceProxy(bufferSource);
        RTEClient.drawScheduler.commit(vertexConsumersProxy, RTEClient.drawContext);
        vertexConsumersProxy.commit();
    }

    private void rte$drawOverlays(Minecraft client, PoseStack poseStack, Camera camera, MultiBufferSource.BufferSource bufferSource, net.minecraft.world.phys.Vec3 camPos3) {
        if (client.player != null && RailRenderDispatcher.isHoldingBrush) {
            RailPicker.pick();
            net.minecraft.world.phys.Vec3 camPos = camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
            RailPicker.render(poseStack, bufferSource);
            RailDistanceRenderer.render(poseStack, bufferSource);
            cn.jstxjf_.rte.render.RailCollisionPreviewRenderer.render(poseStack, bufferSource);
            poseStack.popPose();
        } else {
            RailPicker.pickedRail = null;
        }

        if (client.getEntityRenderDispatcher().shouldRenderHitBoxes() && !client.showOnlyReducedInfo()) {
            poseStack.pushPose();
            poseStack.translate(-camPos3.x, -camPos3.y, -camPos3.z);
            cn.jstxjf_.rte.render.RailCollisionPreviewRenderer.renderDebug(client.level, poseStack, bufferSource, camPos3);
            poseStack.popPose();
        }

        if (cn.jstxjf_.rte.render.SliceGhostRenderer.isActive()) {
            final net.minecraft.world.phys.Vec3 ghostCam = camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-ghostCam.x, -ghostCam.y, -ghostCam.z);
            cn.jstxjf_.rte.render.SliceGhostRenderer.render(poseStack, bufferSource);
            poseStack.popPose();
        }

        if (cn.jstxjf_.rte.render.DepotRailMarking.isActive()) {
            cn.jstxjf_.rte.render.DepotRailMarking.updateHover();
            final net.minecraft.world.phys.Vec3 markCam = camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-markCam.x, -markCam.y, -markCam.z);
            cn.jstxjf_.rte.render.DepotRailMarkingRenderer.render(poseStack, bufferSource);
            poseStack.popPose();
        }

        if (cn.jstxjf_.rte.render.RailChainHandles.isActive()) {
            cn.jstxjf_.rte.render.RailChainHandles.updateDrag();
            final net.minecraft.world.phys.Vec3 handleCam = camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-handleCam.x, -handleCam.y, -handleCam.z);
            cn.jstxjf_.rte.render.RailChainHandleRenderer.render(poseStack, bufferSource);
            poseStack.popPose();
        }

        bufferSource.endBatch();

         cn.jstxjf_.rte.render.CameraPose.invalidate();
    }

    @Inject(method = "setSectionDirtyWithNeighbors", at = @At("HEAD"))
    private void rte$setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        for (int i = sectionZ - 1; i <= sectionZ + 1; ++i) {
            for (int j = sectionX - 1; j <= sectionX + 1; ++j) {
                RTEClient.railRenderDispatcher.registerLightUpdate(j, sectionY - 1, sectionY + 1, i);
            }
        }
    }

    @Inject(method = "setSectionDirty(IIIZ)V", at = @At("HEAD"))
    private void rte$setSectionDirty4(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread, CallbackInfo ci) {
        RTEClient.railRenderDispatcher.registerLightUpdate(sectionX, sectionY, sectionY, sectionZ);
    }

    @Inject(method = "setSectionDirty(III)V", at = @At("HEAD"))
    private void rte$setSectionDirty3(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        RTEClient.railRenderDispatcher.registerLightUpdate(sectionX, sectionY, sectionY, sectionZ);
    }
}
