package cn.jstxjf_.rte.mapping.render.vertex;

import org.lwjgl.opengl.GL33;
import cn.jstxjf_.rte.mapping.render.tool.GlStateTracker;

public enum VertexAttributeType {

	POSITION(0, GL33.GL_FLOAT, 3, 1, false, false),
	COLOR(1, GL33.GL_UNSIGNED_BYTE, 4, 1, true, false),
	UV_TEXTURE(2, GL33.GL_FLOAT, 2, 1, false, false),
	UV_OVERLAY(3, GL33.GL_SHORT, 2, 1, false, true),
	UV_LIGHTMAP(4, GL33.GL_SHORT, 2, 1, false, true),
	NORMAL(5, GL33.GL_BYTE, 3, 1, true, false),
	MATRIX_MODEL(6, GL33.GL_FLOAT, 4, 4, false, false);

	public final int location;
	public final int type;
	public final int size;
	public final int span;
	public final int byteSize;
	public final boolean normalized;
	public final boolean iPointer;

	VertexAttributeType(int location, int type, int size, int span, boolean normalized, boolean iPointer) {
		this.location = location;
		this.type = type;
		this.size = size;
		this.span = span;
		this.normalized = normalized;
		this.iPointer = iPointer;

		final int singleSize;
		switch (type) {
			case GL33.GL_FLOAT:
				singleSize = 4;
				break;
			case GL33.GL_UNSIGNED_BYTE:
			case GL33.GL_BYTE:
				singleSize = 1;
				break;
			case GL33.GL_SHORT:
				singleSize = 2;
				break;
			default:
				singleSize = 0;
				break;
		}

		byteSize = singleSize * size * span;
	}

	public void toggleAttributeArray(boolean enable) {
		for (int i = 0; i < span; i++) {
			if (enable) {
				GL33.glEnableVertexAttribArray(location + i);
			} else {
				GL33.glDisableVertexAttribArray(location + i);
			}
		}
	}

	public void setupAttributePointer(int stride, int pointer) {
		for (int i = 0; i < span; i++) {
			final int attributePointer = pointer + (i * byteSize / span);
			if (iPointer) {
				if (!GlStateTracker.isGl4ES()) {
					GL33.glVertexAttribIPointer(location + i, size, type, stride, attributePointer);
				} else {
					GL33.glVertexAttribPointer(location + i, size, type, false, stride, attributePointer);
				}
			} else {
				GL33.glVertexAttribPointer(location + i, size, type, normalized, stride, attributePointer);
			}
		}
	}

	public void setAttributeDivisor(int divisor) {
		if (GlStateTracker.supportVertexAttributeDivisor()) {
			for (int i = 0; i < span; i++) {
				GL33.glVertexAttribDivisor(location + i, divisor);
			}
		}
	}
}
