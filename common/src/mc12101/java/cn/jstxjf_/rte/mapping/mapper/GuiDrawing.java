package cn.jstxjf_.rte.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.Identifier;
import cn.jstxjf_.rte.mapping.tool.ColorHelper;
import cn.jstxjf_.rte.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private VertexConsumer vertexConsumer;
	private GuiGraphics drawContext;
	private BufferBuilder bufferBuilder;
	private Matrix4f matrix;
	private final GraphicsHolder graphicsHolder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		this.graphicsHolder = graphicsHolder;
	}

	@MappedMethod
	public void beginDrawingRectangle() {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		vertexConsumer = graphicsHolder.vertexConsumerProvider == null ? null : graphicsHolder.vertexConsumerProvider.getBuffer(RenderType.gui());
		drawContext = graphicsHolder.drawContext;
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (matrix != null && vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.addVertex(matrix, (float) x1, (float) y1, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x1, (float) y2, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x2, (float) y2, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x2, (float) y1, 0).setColor(r, g, b, a);
			});
		}
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int color) {
		if (matrix != null && vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.addVertex(matrix, (float) x1, (float) y1, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x2, (float) y2, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x3, (float) y3, 0).setColor(r, g, b, a);
				vertexConsumer.addVertex(matrix, (float) x4, (float) y4, 0).setColor(r, g, b, a);
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		if (vertexConsumer != null && drawContext != null) {
			drawContext.flush();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (matrix != null && bufferBuilder != null) {
			bufferBuilder.addVertex(matrix, (float) x1, (float) y1, 0).setUv(u1, v1);
			bufferBuilder.addVertex(matrix, (float) x1, (float) y2, 0).setUv(u1, v2);
			bufferBuilder.addVertex(matrix, (float) x2, (float) y2, 0).setUv(u2, v2);
			bufferBuilder.addVertex(matrix, (float) x2, (float) y1, 0).setUv(u2, v1);
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
		if (bufferBuilder != null) {
			final com.mojang.blaze3d.vertex.MeshData meshData = bufferBuilder.build();
			if (meshData != null) {
				BufferUploader.drawWithShader(meshData);
			}
			bufferBuilder = null;
		}
	}
}
