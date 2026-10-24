package cn.jstxjf_.rte.gfx.vertex;

import cn.jstxjf_.rte.gfx.ContextCapability;
import org.lwjgl.opengl.GL33;

public enum VertAttrType {

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

    VertAttrType(int location, int type, int size, int span, boolean normalized, boolean iPointer) {
        this.location = location;
        this.type = type;
        this.size = size;
        this.span = span;
        this.normalized = normalized;
        this.iPointer = iPointer;

        int singleSize;
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
        };
        this.byteSize = singleSize * size * span;
    }

    public void toggleAttrArray(boolean enable) {
        for (int i = 0; i < span; ++i) {
            if (enable) {
                GL33.glEnableVertexAttribArray(location + i);
            } else {
                GL33.glDisableVertexAttribArray(location + i);
            }
        }
    }

    public void setupAttrPtr(int stride, int pointer) {
        for (int i = 0; i < span; ++i) {
            int attrPtr = pointer + (i * byteSize / span);
            if (iPointer) {
                if (!ContextCapability.isGL4ES) {
                    GL33.glVertexAttribIPointer(location + i, size, type, stride, attrPtr);
                } else {
                    GL33.glVertexAttribPointer(location + i, size, type, false, stride, attrPtr);
                }
            } else {
                GL33.glVertexAttribPointer(location + i, size, type, normalized, stride, attrPtr);
            }
        }
    }

    public void setAttrDivisor(int divisor) {
        if (!ContextCapability.supportVertexAttribDivisor) return;
        for (int i = 0; i < span; ++i) {
            GL33.glVertexAttribDivisor(location + i, divisor);
        }
    }

}
