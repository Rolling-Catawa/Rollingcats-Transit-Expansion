package cn.jstxjf_.rte.gfx.vertex;

import cn.jstxjf_.rte.render.ShadersModHandler;
import cn.jstxjf_.rte.gfx.ContextCapability;
import cn.jstxjf_.rte.gfx.util.AttrUtil;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.lwjgl.opengl.GL33;

import java.util.Objects;
import java.util.function.Function;

public class VertAttrState {

    public static final Function<Matrix4f, Matrix4f> BILLBOARD = (matrix) -> {
        Vector3f pos = matrix.getTranslationPart();
        Matrix4f result = new Matrix4f();
        result.translate(pos);
        return result;
    };
    public Vector3f position;
    public Integer color;
    public Float texU, texV;
    public Integer overlayUV;
    public Integer lightmapUV;
    public Vector3f normal;
    public Function<Matrix4f, Matrix4f> matrixModel;
    public boolean useMatixProcess = false;
    public static Matrix4f nowMatrix = new Matrix4f();

    private static int cachedProgramId = -1;
    private static int cachedModelViewMatLoc = -1;
    private static final float[] MATRIX_BUF = new float[16];

    public static void invalidateUniformCache() {
        cachedProgramId = -1;
        cachedModelViewMatLoc = -1;
    }

    private static int getModelViewMatLocation() {
        int prog = GL33.glGetInteger(GL33.GL_CURRENT_PROGRAM);
        if (prog != cachedProgramId) {
            cachedProgramId = prog;
            cachedModelViewMatLoc = GL33.glGetUniformLocation(prog, "ModelViewMat");
        }
        return cachedModelViewMatLoc;
    }

    public void applyGlobal() {
        final boolean useCustomShader = ShadersModHandler.canUseCustomShader();
        if (position != null) {
            GL33.glVertexAttrib3f(VertAttrType.POSITION.location, position.x(), position.y(), position.z());
        }
        if (color != null) {
            GL33.glVertexAttrib4f(VertAttrType.COLOR.location, ((color >>> 24) & 0xFF) / 255f, ((color >>> 16) & 0xFF) / 255f,
                    ((color >>> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
        }
        if (texU != null && texV != null) {
            GL33.glVertexAttrib2f(VertAttrType.UV_TEXTURE.location, texU, texV);
        }
        if (overlayUV != null) {
            if (!ContextCapability.isGL4ES) {
                GL33.glVertexAttribI2i(VertAttrType.UV_OVERLAY.location, (short) (overlayUV >>> 16), (short) (int) overlayUV);
            } else {
                GL33.glVertexAttrib2f(VertAttrType.UV_OVERLAY.location, (short) (overlayUV >>> 16), (short) (int) overlayUV);
            }
        }
        if (lightmapUV != null) {
            if (!ContextCapability.isGL4ES) {
                GL33.glVertexAttribI2i(VertAttrType.UV_LIGHTMAP.location, (short) (lightmapUV >>> 16), (short) (int) lightmapUV);
            } else {
                GL33.glVertexAttrib2f(VertAttrType.UV_LIGHTMAP.location, (short) (lightmapUV >>> 16), (short) (int) lightmapUV);
            }
        }
        if (normal != null) {
            GL33.glVertexAttrib3f(VertAttrType.NORMAL.location, normal.x(), normal.y(), normal.z());
        }
        if (matrixModel != null) {
            nowMatrix = matrixModel.apply(nowMatrix);
            ShaderInstance batchShader = cn.jstxjf_.rte.gfx.shader.ShaderManager.currentBatchShaderInstance;
            if (batchShader == null) batchShader = RenderSystem.getShader();
            if (batchShader != null && batchShader.MODEL_VIEW_MATRIX != null) {
                Matrix4f matToUpload;
                if (useCustomShader && cn.jstxjf_.rte.gfx.shader.ShaderManager.currentBatchCameraMatrix != null) {
                    matToUpload = cn.jstxjf_.rte.gfx.shader.ShaderManager.currentBatchCameraMatrix.copy();
                    matToUpload.multiply(nowMatrix);
                } else {
                    matToUpload = nowMatrix;
                }
                int loc = getModelViewMatLocation();
                if (loc >= 0) {
                    matToUpload.asMoj().get(MATRIX_BUF);
                    GL33.glUniformMatrix4fv(loc, false, MATRIX_BUF);
                }
            }
        }
    }

    public VertAttrState setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public VertAttrState setColor(int r, int g, int b, int a) {
        this.color = r << 24 | g << 16 | b << 8 | a;
        return this;
    }

    public VertAttrState setColor(int rgba) {
        this.color = rgba;
        return this;
    }

    public VertAttrState setTextureUV(float u, float v) {
        this.texU = u;
        this.texV = v;
        return this;
    }

    public VertAttrState setLightmapUV(short u, short v) {
        this.lightmapUV = u << 16 | v;
        return this;
    }

    public VertAttrState setOverlayUV(int uv) {
        this.overlayUV = AttrUtil.exchangeLightmapUVBits(uv);
        return this;
    }

    public VertAttrState setOverlayUVNoOverlay() {
        this.overlayUV = AttrUtil.exchangeLightmapUVBits(OverlayTexture.NO_OVERLAY);
        return this;
    }

    public VertAttrState setLightmapUV(int uv) {
        this.lightmapUV = uv;
        return this;
    }

    public VertAttrState setNormal(Vector3f position) {
        this.normal = position;
        return this;
    }

    public VertAttrState setModelMatrix(Matrix4f matrix) {
        this.matrixModel = (matrixIn) -> {
            return matrix;
        };
        return this;
    }

    public VertAttrState setMatixProcess(Function<Matrix4f, Matrix4f> matrixProcess) {
        this.matrixModel = matrixProcess;
        return this;
    }

    public boolean useMatixProcess() {
        return useMatixProcess;
    }

    public boolean hasAttr(VertAttrType attrType) {
        switch (attrType) {
            case POSITION:
                return position != null;
            case COLOR:
                return color != null;
            case NORMAL:
                return normal != null;
            case UV_OVERLAY:
                return overlayUV != null;
            case UV_TEXTURE:
                return texU != null && texV != null;
            case UV_LIGHTMAP:
                return lightmapUV != null;
            case MATRIX_MODEL:
                return matrixModel != null;
        }
        return false;
    }

    public void clearAttr(VertAttrType attrType) {
        switch (attrType) {
            case POSITION:
                position = null;
                break;
            case COLOR:
                color = null;
                break;
            case NORMAL:
                normal = null;
                break;
            case UV_OVERLAY:
                overlayUV = null;
                break;
            case UV_TEXTURE:
                texU = null;
                texV = null;
                break;
            case UV_LIGHTMAP:
                lightmapUV = null;
                break;
            case MATRIX_MODEL:
                matrixModel = null;
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertAttrState that = (VertAttrState) o;
        return Objects.equals(position, that.position) && Objects.equals(color, that.color) && Objects.equals(texU, that.texU)
                && Objects.equals(texV, that.texV) && Objects.equals(lightmapUV, that.lightmapUV) && Objects.equals(normal, that.normal)
                && Objects.equals(matrixModel, that.matrixModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, color, texU, texV, lightmapUV, normal, matrixModel);
    }

    public VertAttrState copy() {
        VertAttrState clone = new VertAttrState();
        clone.position = this.position == null ? null : this.position.copy();
        clone.color = this.color;
        clone.texU = this.texU;
        clone.texV = this.texV;
        clone.lightmapUV = this.lightmapUV;
        clone.normal = this.normal == null ? null : this.normal.copy();
        clone.matrixModel = this.matrixModel == null ? null : this.matrixModel;
        return clone;
    }
}
