package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.batch.EnqueueProp;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfx.model.Model;
import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfx.object.InstanceBuf;
import cn.jstxjf_.rte.gfx.object.VertBuf;
import cn.jstxjf_.rte.gfx.util.OffHeapAllocator;
import cn.jstxjf_.rte.gfx.vertex.VertAttrMapping;
import cn.jstxjf_.rte.gfx.vertex.VertAttrSrc;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import cn.jstxjf_.rte.render.ByteBufferOutputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import net.minecraft.client.renderer.LightTexture;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.LightType;
import org.mtr.mapping.holder.World;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;

public class InstancedRailChunk extends RailChunkBase {

    private final InstanceBuf instanceBuf;
    private final VertArrays vertArrays;

    private static final VertAttrMapping RAIL_MAPPING = new VertAttrMapping.Builder()
            .set(VertAttrType.POSITION, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.COLOR, VertAttrSrc.INSTANCE_BUF_OR_GLOBAL)
            .set(VertAttrType.UV_TEXTURE, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.UV_OVERLAY, VertAttrSrc.GLOBAL)
            .set(VertAttrType.UV_LIGHTMAP, VertAttrSrc.INSTANCE_BUF_OR_GLOBAL)
            .set(VertAttrType.NORMAL, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.MATRIX_MODEL, VertAttrSrc.INSTANCE_BUF)
            .build();

    public InstancedRailChunk(Long chunkId, String modelKey) {
        super(chunkId, modelKey);
        Model railModel = RailModelRegistry.getProperty(modelKey).uploadedModel;
        if (railModel != null) {
            instanceBuf = new InstanceBuf(0);
            vertArrays = VertArrays.createAll(railModel, RAIL_MAPPING, instanceBuf);
        } else {
            instanceBuf = null;
            vertArrays = null;
        }
    }

    @Override
    public void rebuildBuffer(World world) {
        super.rebuildBuffer(world);
        if (vertArrays == null) return;

        EXECUTOR.execute(() -> {
            try {
            int instanceCount = containingRails.values().stream().mapToInt(ArrayList::size).sum();
            float yMin = 256, yMax = -64;

            ByteBuffer byteBuf = OffHeapAllocator.allocate(instanceCount * RAIL_MAPPING.strideInstance);
            LittleEndianDataOutputStream oStream = new LittleEndianDataOutputStream(new ByteBufferOutputStream(byteBuf, false));

            for (Map.Entry<BakedRail, ArrayList<Matrix4f>> entry : containingRails.entrySet()) {
                ArrayList<Matrix4f> railSpan = entry.getValue();
                for (Matrix4f pieceMat : railSpan) {
                    try {
                        oStream.writeInt(entry.getKey().color);

                        final Vector3f lightPos = pieceMat.getTranslationPart();
                        yMin = Math.min(yMin, lightPos.y());
                        yMax = Math.max(yMax, lightPos.y());
                        final BlockPos lightBlockPos = new BlockPos((int)Math.floor(lightPos.x()), (int)Math.floor(lightPos.y() + 0.1), (int)Math.floor(lightPos.z()));
                        int sky = world.getLightLevel(LightType.SKY, lightBlockPos);
                        int block = world.getLightLevel(LightType.BLOCK, lightBlockPos);
                        final int light = LightTexture.pack(block, sky);
                        oStream.writeInt(light);

                        byte[] lookAtBytes = new byte[4 * 16];
                        ByteBuffer matByteBuf = ByteBuffer.wrap(lookAtBytes).order(ByteOrder.nativeOrder());
                        FloatBuffer matFloatBuf = matByteBuf.asFloatBuffer();
                        pieceMat.store(matFloatBuf);
                        oStream.write(lookAtBytes);

                        for (int k = 0; k < RAIL_MAPPING.paddingInstance; k++) oStream.writeByte(0);
                    } catch (IOException e) {
                        RTE.LOGGER.error("Error writing rail instance data", e);
                    }
                }
            }
            
            byteBuf.rewind();

            float finalYMin = yMin;
            float finalYMax = yMax;

            UPLOAD_QUEUE.offer(() -> {
                try {
                    instanceBuf.size = instanceCount;
                    instanceBuf.upload(byteBuf, VertBuf.USAGE_DYNAMIC_DRAW);
                    OffHeapAllocator.free(byteBuf);
                } catch (Exception e) {
                    RTE.LOGGER.warn("[RailChunk] instanced upload failed for chunk {}: {}", chunkId, e.toString());
                    isDirty = true;
                } finally {
                    bufferBuilding = false;
                }
            });

            if (finalYMin > finalYMax) setBoundingBox(finalYMax, finalYMax);
            else setBoundingBox(finalYMin, finalYMax);
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailChunk] instanced build failed for chunk {}: {}", chunkId, e.toString());
                isDirty = true;
                bufferBuilding = false;
            }
        });
    }

    @Override
    public void enqueue(BatchManager batchManager, ShaderProp shaderProp) {
        if (vertArrays == null) return;
        if (instanceBuf.size < 1) return;
        VertAttrState attrState = new VertAttrState().setOverlayUVNoOverlay();
        if (!RailRenderDispatcher.isHoldingRailItem) attrState.setColor(-1);
        batchManager.enqueue(vertArrays, new EnqueueProp(attrState), shaderProp);
    }

    @Override
    public void close() {
        if (vertArrays == null) return;
        vertArrays.close();
        instanceBuf.close();
    }
}
