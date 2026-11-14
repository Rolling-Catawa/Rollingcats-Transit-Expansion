package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.gfx.ContextCapability;
import cn.jstxjf_.rte.mixin.MtrRenderCallAccessor;
import cn.jstxjf_.rte.mixin.MtrVertexArrayAccessor;
import cn.jstxjf_.rte.mixin.MtrVertexAttributeStateAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class MtrInstancedBatcher {

    private static final int MIN_INSTANCES = 2;
    private static final int LIGHTMAP_LOCATION = 4;
    private static final int MATRIX_LOCATION = 6;
    private static final int MATRIX_COLUMNS = 4;
    private static final int MAX_ATTRIBUTES = 16;
    private static final int INSTANCE_STRIDE = 4 + 16 * Float.BYTES;
    private static final int UNSUPPORTED_VAO = -1;

    private static final Object LOCK = new Object();
    private static final Map<VertexArray, Integer> INSTANCED_VAOS = new WeakHashMap<>();
    private static final FloatBuffer MATRIX_SCRATCH = MemoryUtil.memAllocFloat(16);
    private static ByteBuffer instanceData = MemoryUtil.memAlloc(INSTANCE_STRIDE * 64);
    private static int instanceBuffer;

    private static long instancedDraws;
    private static long mergedCalls;

    private MtrInstancedBatcher() {
    }

    public static boolean isActive() {
        return ClientConfig.instancedVehicleRendering
            && ContextCapability.supportVertexAttribDivisor
            && !ContextCapability.isGL4ES
            && Utilities.canUseCustomShader();
    }

    public static long instancedDraws() {
        return instancedDraws;
    }

    public static long mergedCalls() {
        return mergedCalls;
    }

    public static void drawBatches(Map<MaterialProperties, ? extends List<?>> batches, org.mtr.mapping.render.shader.ShaderManager shaderManager) {
        for (final Map.Entry<MaterialProperties, ? extends List<?>> entry : batches.entrySet()) {
            final MaterialProperties material = entry.getKey();
            final List<?> calls = entry.getValue();
            if (material == null || calls == null) {
                continue;
            }
            shaderManager.setupShaderBatchState(material);
            try {
                if (material.translucent || calls.size() < MIN_INSTANCES) {
                    drawEach(calls);
                } else {
                    drawGrouped(material, calls);
                }
            } finally {
                shaderManager.cleanupShaderBatchState();
            }
        }
    }

    private static void drawEach(List<?> calls) {
        for (final Object call : calls) {
            ((MtrRenderCallAccessor) call).rte$draw();
        }
    }

    private static void drawGrouped(MaterialProperties material, List<?> calls) {
        final Map<VertexArray, List<MtrRenderCallAccessor>> groups = new LinkedHashMap<>();
        for (final Object call : calls) {
            final MtrRenderCallAccessor accessor = (MtrRenderCallAccessor) call;
            groups.computeIfAbsent(accessor.rte$getVertexArray(), key -> new ArrayList<>()).add(accessor);
        }
        for (final Map.Entry<VertexArray, List<MtrRenderCallAccessor>> group : groups.entrySet()) {
            final List<MtrRenderCallAccessor> members = group.getValue();
            if (members.size() < MIN_INSTANCES || !tryDrawInstanced(material, group.getKey(), members)) {
                for (final MtrRenderCallAccessor member : members) {
                    member.rte$draw();
                }
            }
        }
    }

    private static boolean tryDrawInstanced(MaterialProperties material, VertexArray vertexArray, List<MtrRenderCallAccessor> members) {
        final VertexAttributeState materialState = material.vertexAttributeState;
        final MtrVertexAttributeStateAccessor materialAccessor = (MtrVertexAttributeStateAccessor) (Object) materialState;
        if (materialAccessor.rte$getMatrix() != null) {
            return false;
        }
        final Integer materialLightmap = materialAccessor.rte$getLightmapUV();
        final VertexAttributeState firstState = members.get(0).rte$getVertexAttributeState();
        final MtrVertexAttributeStateAccessor first = (MtrVertexAttributeStateAccessor) (Object) firstState;
        for (final MtrRenderCallAccessor member : members) {
            final MtrVertexAttributeStateAccessor state = (MtrVertexAttributeStateAccessor) (Object) member.rte$getVertexAttributeState();
            if (!sameSharedState(first, state)
                || state.rte$getMatrix() == null
                || (materialLightmap == null && state.rte$getLightmapUV() == null)) {
                return false;
            }
        }
        final int vao = instancedVaoFor(vertexArray);
        if (vao == UNSUPPORTED_VAO) {
            return false;
        }

        ensureCapacity(members.size() * INSTANCE_STRIDE);
        instanceData.clear();
        for (final MtrRenderCallAccessor member : members) {
            final MtrVertexAttributeStateAccessor state = (MtrVertexAttributeStateAccessor) (Object) member.rte$getVertexAttributeState();
            final int lightmap = materialLightmap != null ? materialLightmap : state.rte$getLightmapUV();
            instanceData.putShort((short) (lightmap >>> 16));
            instanceData.putShort((short) lightmap);
            final Matrix4f matrix = state.rte$getMatrix();
            MATRIX_SCRATCH.clear();
            Utilities.store(matrix, MATRIX_SCRATCH);
            for (int i = 0; i < 16; i++) {
                instanceData.putFloat(MATRIX_SCRATCH.get(i));
            }
        }
        instanceData.flip();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceData, GL15.GL_STREAM_DRAW);
        firstState.apply();
        materialState.apply();
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, vertexArray.indexBuffer.getVertexCount(), vertexArray.indexBuffer.indexType, 0L, members.size());
        instancedDraws++;
        mergedCalls += members.size();
        return true;
    }

    private static boolean sameSharedState(MtrVertexAttributeStateAccessor a, MtrVertexAttributeStateAccessor b) {
        return Objects.equals(a.rte$getColor(), b.rte$getColor())
            && Objects.equals(a.rte$getOverlayUV(), b.rte$getOverlayUV())
            && Objects.equals(a.rte$getTextureU(), b.rte$getTextureU())
            && Objects.equals(a.rte$getTextureV(), b.rte$getTextureV())
            && a.rte$getPosition() == b.rte$getPosition()
            && a.rte$getNormal() == b.rte$getNormal();
    }

    private static void ensureCapacity(int bytes) {
        if (instanceData.capacity() < bytes) {
            instanceData = MemoryUtil.memRealloc(instanceData, Math.max(bytes, instanceData.capacity() * 2));
        }
    }

    private static int instancedVaoFor(VertexArray vertexArray) {
        synchronized (LOCK) {
            final Integer cached = INSTANCED_VAOS.get(vertexArray);
            if (cached != null) {
                return cached;
            }
        }
        final int created = createInstancedVao(((MtrVertexArrayAccessor) (Object) vertexArray).rte$getId());
        synchronized (LOCK) {
            INSTANCED_VAOS.put(vertexArray, created);
        }
        return created;
    }

    private static int createInstancedVao(int sourceVao) {
        if (sourceVao <= 0) {
            return UNSUPPORTED_VAO;
        }
        GL30.glBindVertexArray(sourceVao);
        final int elementBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        final int[][] attributes = new int[MAX_ATTRIBUTES][];
        final long[] pointers = new long[MAX_ATTRIBUTES];
        for (int i = 0; i < MAX_ATTRIBUTES; i++) {
            if (GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED) == 0) {
                continue;
            }
            if (i == LIGHTMAP_LOCATION || (i >= MATRIX_LOCATION && i < MATRIX_LOCATION + MATRIX_COLUMNS)) {
                GL30.glBindVertexArray(0);
                return UNSUPPORTED_VAO;
            }
            attributes[i] = new int[]{
                GL20.glGetVertexAttribi(i, GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING),
                GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_SIZE),
                GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_TYPE),
                GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED),
                GL20.glGetVertexAttribi(i, GL30.GL_VERTEX_ATTRIB_ARRAY_INTEGER),
                GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_STRIDE),
                GL20.glGetVertexAttribi(i, GL33.GL_VERTEX_ATTRIB_ARRAY_DIVISOR)
            };
            pointers[i] = GL20.glGetVertexAttribPointer(i, GL20.GL_VERTEX_ATTRIB_ARRAY_POINTER);
        }
        GL30.glBindVertexArray(0);
        if (elementBuffer == 0 || attributes[0] == null) {
            return UNSUPPORTED_VAO;
        }
        if (instanceBuffer == 0) {
            instanceBuffer = GL15.glGenBuffers();
        }

        final int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        for (int i = 0; i < MAX_ATTRIBUTES; i++) {
            final int[] attribute = attributes[i];
            if (attribute == null) {
                continue;
            }
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attribute[0]);
            GL20.glEnableVertexAttribArray(i);
            if (attribute[4] != 0) {
                GL30.glVertexAttribIPointer(i, attribute[1], attribute[2], attribute[5], pointers[i]);
            } else {
                GL20.glVertexAttribPointer(i, attribute[1], attribute[2], attribute[3] != 0, attribute[5], pointers[i]);
            }
            GL33.glVertexAttribDivisor(i, attribute[6]);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceBuffer);
        GL20.glEnableVertexAttribArray(LIGHTMAP_LOCATION);
        GL30.glVertexAttribIPointer(LIGHTMAP_LOCATION, 2, GL11.GL_SHORT, INSTANCE_STRIDE, 0L);
        GL33.glVertexAttribDivisor(LIGHTMAP_LOCATION, 1);
        for (int column = 0; column < MATRIX_COLUMNS; column++) {
            final int location = MATRIX_LOCATION + column;
            GL20.glEnableVertexAttribArray(location);
            GL20.glVertexAttribPointer(location, 4, GL11.GL_FLOAT, false, INSTANCE_STRIDE, 4L + (long) column * 4 * Float.BYTES);
            GL33.glVertexAttribDivisor(location, 1);
        }
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
        GL30.glBindVertexArray(0);
        return vao;
    }

    public static void release(VertexArray vertexArray) {
        final Integer vao;
        synchronized (LOCK) {
            vao = INSTANCED_VAOS.remove(vertexArray);
        }
        if (vao == null || vao <= 0) {
            return;
        }
        if (RenderSystem.isOnRenderThread()) {
            GL30.glDeleteVertexArrays(vao);
        } else {
            RenderSystem.recordRenderCall(() -> GL30.glDeleteVertexArrays(vao));
        }
    }
}
