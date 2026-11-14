package cn.jstxjf_.rte.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.*;
import cn.jstxjf_.rte.mapping.tool.ColorHelper;
import cn.jstxjf_.rte.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GraphicsHolder extends DummyClass {

	VertexConsumer vertexConsumer;
	private int matrixPushes;

	final PoseStack matrixStack;
	final MultiBufferSource vertexConsumerProvider;
	final GuiGraphics drawContext;

	public static GraphicsHolder of(PoseStack matrixStack, VertexConsumer vertexConsumer) {
		final GraphicsHolder graphicsHolder = new GraphicsHolder(matrixStack, null);
		graphicsHolder.vertexConsumer = vertexConsumer;
		return graphicsHolder;
	}

	public PoseStack matrixStack() {
		return matrixStack;
	}

	public VertexConsumer vertexConsumer() {
		return vertexConsumer;
	}

	@MappedMethod
	public static int getDefaultLight() {
		return 0xF000F0;
	}

	@Deprecated
	public static void createInstanceSafe(@Nullable PoseStack matrixStack, @Nullable MultiBufferSource vertexConsumerProvider, Consumer<GraphicsHolder> consumer) {
		createInstanceSafe(new GraphicsHolder(matrixStack, vertexConsumerProvider), consumer);
	}

	@Deprecated
	public static void createInstanceSafe(GuiGraphics drawContext, Consumer<GraphicsHolder> consumer) {
		createInstanceSafe(new GraphicsHolder(drawContext), consumer);
	}

	private static void createInstanceSafe(GraphicsHolder graphicsHolder, Consumer<GraphicsHolder> consumer) {
		try {
			consumer.accept(graphicsHolder);
		} catch (Exception e) {
			logException(e);
		}

		while (graphicsHolder.matrixPushes > 0) {
			graphicsHolder.pop();
		}
	}

	private GraphicsHolder(@Nullable PoseStack matrixStack, @Nullable MultiBufferSource vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		drawContext = null;
		push();
	}

	private GraphicsHolder(GuiGraphics drawContext) {
		this.matrixStack = drawContext.pose();
		this.vertexConsumerProvider = drawContext.bufferSource();
		this.drawContext = drawContext;
		push();
	}

	@MappedMethod
	public void push() {
		if (matrixStack != null) {
			matrixStack.pushPose();
			matrixPushes++;
		}
	}

	@MappedMethod
	public void pop() {
		if (matrixStack != null && matrixPushes > 0) {
			matrixStack.popPose();
			matrixPushes--;
		}
	}

	@MappedMethod
	public void translate(double x, double y, double z) {
		if (matrixStack != null) {
			matrixStack.translate(x, y, z);
		}
	}

	@MappedMethod
	public void scale(float x, float y, float z) {
		if (matrixStack != null) {
			matrixStack.scale(x, y, z);
		}
	}

	@MappedMethod
	public void rotateXRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.XP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateYRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.YP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateZRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.ZP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateXDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.XP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateYDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.YP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateZDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.ZP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public cn.jstxjf_.rte.mapping.holder.Matrix4f copyPositionMatrix() {
		return matrixStack == null ? new cn.jstxjf_.rte.mapping.holder.Matrix4f() : new cn.jstxjf_.rte.mapping.holder.Matrix4f(matrixStack.last().pose());
	}

	@MappedMethod
	public void drawText(MutableText mutableText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final MultiBufferSource.BufferSource immediate = drawContext == null ? getInstance().renderBuffers().bufferSource() : drawContext.bufferSource();
			getInstance().font.drawInBatch(mutableText.data, x, y, color, shadow, matrixStack.last().pose(), immediate, Font.DisplayMode.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.endBatch();
			} else {
				drawContext.flush();
			}
		}
	}

	@MappedMethod
	public void drawText(OrderedText orderedText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final MultiBufferSource.BufferSource immediate = drawContext == null ? getInstance().renderBuffers().bufferSource() : drawContext.bufferSource();
			getInstance().font.drawInBatch(orderedText.data, x, y, color, shadow, matrixStack.last().pose(), immediate, Font.DisplayMode.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.endBatch();
			} else {
				drawContext.flush();
			}
		}
	}

	@MappedMethod
	public void drawText(String text, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final MultiBufferSource.BufferSource immediate = drawContext == null ? getInstance().renderBuffers().bufferSource() : drawContext.bufferSource();
			getInstance().font.drawInBatch(text, x, y, color, shadow, matrixStack.last().pose(), immediate, Font.DisplayMode.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.endBatch();
			} else {
				drawContext.flush();
			}
		}
	}

	@MappedMethod
	public void drawCenteredText(String text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.drawCenteredString(getInstance().font, text, centerX, y, color);
		}
	}

	@MappedMethod
	public void drawCenteredText(MutableText text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.drawCenteredString(getInstance().font, text.data, centerX, y, color);
		}
	}

	@MappedMethod
	public static int getTextWidth(MutableText mutableText) {
		return getInstance().font.width(mutableText.data);
	}

	@MappedMethod
	public static int getTextWidth(OrderedText orderedText) {
		return getInstance().font.width(orderedText.data);
	}

	@MappedMethod
	public static int getTextWidth(String text) {
		return getInstance().font.width(text);
	}

	@MappedMethod
	public static List<OrderedText> wrapLines(MutableText mutableText, int width) {
		return getInstance().font.split(mutableText.data, width).stream().map(OrderedText::new).collect(Collectors.toList());
	}

	private static Minecraft getInstance() {
		return Minecraft.getInstance();
	}

	@MappedMethod
	public void createVertexConsumer(RenderLayer renderLayer) {
		if (vertexConsumerProvider != null) {
			vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer.data);
		}
	}

	@MappedMethod
	public void drawLineInWorld(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				final PoseStack.Pose entry = matrixStack.last();
				final Matrix4f matrix4f = entry.pose();
				final Matrix3f matrix3f = entry.normal();

				vertexConsumer.addVertex(matrix4f, x1, y1, z1).setColor(r, g, b, a).setNormal(entry, 0, 1, 0);
				vertexConsumer.addVertex(matrix4f, x2, y2, z2).setColor(r, g, b, a).setNormal(entry, 0, 1, 0);
			});
		}
	}

	@MappedMethod
	public void drawTextureInWorld(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				final Vector3i vector3i = facing.getVector();
				final int x = vector3i.getX();
				final int y = vector3i.getY();
				final int z = vector3i.getZ();

				final PoseStack.Pose entry = matrixStack.last();
				final Matrix4f matrix4f = entry.pose();
				final Matrix3f matrix3f = entry.normal();

				vertexConsumer.addVertex(matrix4f, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, x, y, z);
				vertexConsumer.addVertex(matrix4f, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, x, y, z);
				vertexConsumer.addVertex(matrix4f, x3, y3, z3).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, x, y, z);
				vertexConsumer.addVertex(matrix4f, x4, y4, z4).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, x, y, z);
			});
		}
	}

	@MappedMethod
	public void renderEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta, int light) {
		if (matrixStack != null && vertexConsumerProvider != null) {
			getInstance().getEntityRenderDispatcher().render(entity.data, x, y, z, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
		}
	}
}
