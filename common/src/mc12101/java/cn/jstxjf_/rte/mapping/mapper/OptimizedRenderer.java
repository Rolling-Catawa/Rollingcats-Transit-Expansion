package cn.jstxjf_.rte.mapping.mapper;

import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.Matrix4f;
import cn.jstxjf_.rte.mapping.render.batch.BatchManager;
import cn.jstxjf_.rte.mapping.render.shader.ModShaderHandler;
import cn.jstxjf_.rte.mapping.render.shader.ShaderManager;
import cn.jstxjf_.rte.mapping.render.tool.GlStateTracker;
import cn.jstxjf_.rte.mapping.render.tool.Utilities;
import cn.jstxjf_.rte.mapping.render.vertex.VertexAttributeState;
import cn.jstxjf_.rte.mapping.tool.DummyClass;

import java.util.function.Supplier;

public final class OptimizedRenderer extends DummyClass {

	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();
	private int protectedDepth;
	private int reloadDepth;

	@MappedMethod
	public void beginReload() {
		if (reloadDepth++ == 0) {
			shaderManager.reloadShaders();
		}
		beginProtectedState();
	}

	@MappedMethod
	public void finishReload() {
		if (reloadDepth > 0) {
			reloadDepth--;
			finishProtectedState();
		}
	}

	@MappedMethod
	public void runWithProtectedState(Runnable runnable) {
		beginProtectedState();
		try {
			runnable.run();
		} finally {
			finishProtectedState();
		}
	}

	@MappedMethod
	public <T> T runWithProtectedState(Supplier<T> supplier) {
		beginProtectedState();
		try {
			return supplier.get();
		} finally {
			finishProtectedState();
		}
	}

	@MappedMethod
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
		if (graphicsHolder.matrixStack != null) {
			batchManager.queue(optimizedModel.uploadedParts, new VertexAttributeState(color, light, Utilities.copy(new Matrix4f(graphicsHolder.matrixStack.last().pose()))));
		}
	}

	@MappedMethod
	public void render(boolean renderTranslucent) {
		if (shaderManager.isReady()) {
			runWithProtectedState(() -> batchManager.drawAll(shaderManager, renderTranslucent));
		}
	}

	@MappedMethod
	public static boolean renderingShadows() {
		return ModShaderHandler.renderingShadows();
	}

	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return true;
	}

	private void beginProtectedState() {
		if (protectedDepth++ == 0) {
			GlStateTracker.capture();
		}
	}

	private void finishProtectedState() {
		if (protectedDepth > 0 && --protectedDepth == 0) {
			GlStateTracker.restore();
		}
	}
}
