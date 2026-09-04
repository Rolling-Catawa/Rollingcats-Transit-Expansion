package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.EyeCandyProperties;
import cn.jstxjf_.rte.data.EyeCandyRegistry;
import cn.jstxjf_.rte.render.ShadersModHandler;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.integration.BufferSourceProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    private void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer) {
        throw new AssertionError();
    }

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    private void rte$renderEyeCandyItem(
        ItemStack itemStack,
        ItemDisplayContext transformType,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        final Block block = blockItem.getBlock();
        if (!(block instanceof cn.jstxjf_.rte.block.BlockEyeCandy)) {
            return;
        }

        final CompoundTag blockEntityTag = itemStack.getTagElement("BlockEntityTag");
        if (blockEntityTag == null || !blockEntityTag.contains("prefabId")) {
            return;
        }

        final EyeCandyProperties properties = EyeCandyRegistry.ELEMENTS.get(blockEntityTag.getString("prefabId"));
        if (properties == null) {
            return;
        }

        if (properties.itemBakedModel != null) {
            final boolean directBuffer = transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.GROUND || transformType == ItemDisplayContext.FIXED;
            final RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, directBuffer);
            final VertexConsumer vertexConsumer = directBuffer
                ? ItemRenderer.getFoilBufferDirect(buffer, renderType, true, itemStack.hasFoil())
                : ItemRenderer.getFoilBuffer(buffer, renderType, true, itemStack.hasFoil());
            poseStack.pushPose();
            properties.itemBakedModel.getTransforms().getTransform(transformType).apply(leftHand, poseStack);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            renderModelLists(properties.itemBakedModel, itemStack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
            poseStack.popPose();
            ci.cancel();
            return;
        }

        if (properties.itemModel == null || properties.itemTransform == null) {
            return;
        }

        final ModelCluster cluster = properties.itemModel;
        poseStack.pushPose();
        model.getTransforms().getTransform(transformType).apply(leftHand, poseStack);
        final Matrix4f matrix = new Matrix4f(poseStack.last().pose()).copy();
        matrix.translate(0, -0.5f, 0);
        matrix.mul(properties.itemTransform);
        final boolean heldInHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            || transformType == ItemDisplayContext.HEAD;
        if (transformType == ItemDisplayContext.GUI || heldInHand) {
            final DrawContext drawContext = new DrawContext();
            drawContext.drawWithBlaze = true;
            drawContext.screenSpace = true;
            cn.jstxjf_.rte.RTEClient.drawScheduler.drawAlone(cluster, matrix, combinedLight, combinedOverlay, new BufferSourceProxy(buffer), drawContext);
        } else {
            cn.jstxjf_.rte.RTEClient.drawScheduler.enqueue(cluster, matrix, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
        ci.cancel();
    }
}
