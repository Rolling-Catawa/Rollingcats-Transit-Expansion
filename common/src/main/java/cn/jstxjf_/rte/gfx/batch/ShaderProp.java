package cn.jstxjf_.rte.gfx.batch;

import cn.jstxjf_.rte.gfx.math.Matrix4f;

import java.util.Objects;

public class ShaderProp {

    public Matrix4f viewMatrix = null;

    public static ShaderProp DEFAULT = new ShaderProp();

    public ShaderProp() {

    }

    public ShaderProp setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShaderProp that = (ShaderProp) o;
        return Objects.equals(viewMatrix, that.viewMatrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(viewMatrix);
    }
}
