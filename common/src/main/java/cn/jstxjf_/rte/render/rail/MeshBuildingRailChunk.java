package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.batch.EnqueueProp;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.batch.MaterialProp; // Added import
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfx.model.Model;
import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfx.vertex.VertAttrMapping;
import cn.jstxjf_.rte.gfx.vertex.VertAttrSrc;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.gfxext.model.*;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher; 
import cn.jstxjf_.rte.RTE;

import net.minecraft.client.renderer.LightTexture;
// import net.minecraft.world.level.LightLayer;
import org.mtr.mapping.holder.LightType; // Fixed
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3; 
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import cn.jstxjf_.rte.data.RailExtraSupplier;

public class MeshBuildingRailChunk extends RailChunkBase {

    private final RawModel railModel;

    private boolean deform = false;
    private Model uploadedCombinedModel;
    private VertArrays vertArrays;

    private static final double ACCEPT_THRESHOLD = 1e-4;
    private static final double HALF_ACCEPT_THRESHOLD = ACCEPT_THRESHOLD / 2;

    private static final VertAttrMapping RAIL_MAPPING = new VertAttrMapping.Builder()
            .set(VertAttrType.POSITION, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.COLOR, VertAttrSrc.VERTEX_BUF_OR_GLOBAL)
            .set(VertAttrType.UV_TEXTURE, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.UV_OVERLAY, VertAttrSrc.GLOBAL)
            .set(VertAttrType.UV_LIGHTMAP, VertAttrSrc.VERTEX_BUF_OR_GLOBAL)
            .set(VertAttrType.NORMAL, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.MATRIX_MODEL, VertAttrSrc.GLOBAL)
            .build();

    protected MeshBuildingRailChunk(Long chunkId, String modelKey) {
        super(chunkId, modelKey);
        this.railModel = RailModelRegistry.getProperty(modelKey).rawModel;
    }

    @Override
    public void rebuildBuffer(World world) {
        super.rebuildBuffer(world);
        if (railModel == null) return;

        EXECUTOR.execute(() -> {
            try {
                RawModel combinedModel = ClientConfig.getEnableRailDeform() ? transformModelDeform(world) : transformModel(world);
                checkBoundingBox();

                Supplier<Model> supplier = combinedModel.uploadAsync(RAIL_MAPPING);
                UPLOAD_QUEUE.offer(() -> {
                    try {
                        if (uploadedCombinedModel != null) uploadedCombinedModel.close();
                        if (vertArrays != null) vertArrays.close();
                        uploadedCombinedModel = supplier.get();
                        vertArrays = VertArrays.createAll(uploadedCombinedModel, RAIL_MAPPING, null);
                    } catch (Exception e) {
                        RTE.LOGGER.warn("[RailChunk] upload failed for chunk {}: {}", chunkId, e.toString());
                        isDirty = true;
                    } finally {
                        bufferBuilding = false;
                    }
                });
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailChunk] mesh build failed for chunk {}: {}", chunkId, e.toString());
                isDirty = true;
                bufferBuilding = false;
            }
        });
    }

    private RawModel transformModel(World world) {
        deform = false;
        RawModel combinedModel = new RawModel();

        for (Map.Entry<BakedRail, ArrayList<Matrix4f>> entry : containingRails.entrySet()) {
            ArrayList<Matrix4f> railSpan = entry.getValue();
            for (Matrix4f pieceMat : railSpan) {
                final Vector3f lightPos = pieceMat.getTranslationPart();
                final BlockPos lightBlockPos = new BlockPos((int)Math.floor(lightPos.x()), (int)Math.floor(lightPos.y() + 0.1), (int)Math.floor(lightPos.z()));
                
                int sky = world.getLightLevel(LightType.SKY, lightBlockPos);
                int block = world.getLightLevel(LightType.BLOCK, lightBlockPos);
                int light = LightTexture.pack(block, sky);

                combinedModel.appendTransformed(railModel, pieceMat, entry.getKey().color, light);
            }
        }

        return combinedModel;
    }

    private void checkBoundingBox() {
        float yMin = 256, yMax = -64;
        for (Map.Entry<BakedRail, ArrayList<Matrix4f>> entry : containingRails.entrySet()) {
            for (Matrix4f pieceMat : entry.getValue()) {
                final Vector3f lightPos = pieceMat.getTranslationPart();
                yMin = Math.min(yMin, lightPos.y());
                yMax = Math.max(yMax, lightPos.y());
            }
        }

        if (yMin > yMax) yMin = yMax;
        setBoundingBox(yMin, yMax);
    }

    private RawModel transformModelDeform(World world) {
        deform = true;
        RawModel combinedModel = new RawModel();
        for (BakedRail bakedRail : containingRails.keySet()) {
            Rail rail = bakedRail.rail;
            RailExtraSupplier railExtra = (RailExtraSupplier) (Object) rail;
            RailModelProperties prop = bakedRail.getProperties();

            boolean reverse = railExtra.getRenderReversed();

            float interval = prop.repeatInterval;
            float yOffset = prop.yOffset;
            RawModel railModel = prop.rawModel;
            final double length = railExtra.getLength() - ACCEPT_THRESHOLD;
            if (!Double.isFinite(length) || length <= 0 || interval <= 0) continue;
            final long segCount = Math.min(Math.max(1, Math.round(length / interval)), 4096);
            final double ins = length / segCount;
            final double halfIns = ins / 2;

            int count = 0;
            for (double i = halfIns; i <= length ; i += ins) {
                if (++count > 4096) break;
                Vec3 midMapped = railExtra.getPosition(i);
                Vec3 mid = new Vec3(midMapped.x, midMapped.y, midMapped.z);

                if (BakedRail.chunkIdFromWorldPos((int) mid.x, (int) mid.z) != chunkId) continue;

                Vector3f lightPos = new Vector3f((float)mid.x, (float)mid.y, (float)mid.z);
                BlockPos lightBlockPos = new BlockPos((int)Math.floor(lightPos.x()), (int)Math.floor(lightPos.y() + 0.1), (int)Math.floor(lightPos.z()));
                int sky = world.getLightLevel(LightType.SKY, lightBlockPos);
                int block = world.getLightLevel(LightType.BLOCK, lightBlockPos);
                int light = LightTexture.pack(block, sky);

                RawModel rm = railModel.copy();
                for (Map.Entry<MaterialProp, RawMesh> entry : rm.meshList.entrySet()) {
                    for (Vertex v : entry.getValue().vertices) {
                        Matrix4f mat = getLookAtMat(rail, transform(i, ins, interval, v.position.z(), reverse), yOffset, reverse);
                        v.position = mat.transform(new Vector3f(v.position.x(), v.position.y(), 0));
                        v.normal = mat.transform3(v.normal);
                        v.light = light;
                        v.color = bakedRail.color;
                    }
                }
                combinedModel.append(rm);
            }
        }
        return combinedModel;
    }

    private double transform(double i, double di, double interval, double value, boolean reverse) {
        return i + di * (value / interval) * (reverse ? -1 : 1);
    }

    private Matrix4f getLookAtMat(Rail rail, double value, float yOffset, boolean reverse) {
        RailExtraSupplier railExtra = (RailExtraSupplier) (Object) rail;
        if (value < HALF_ACCEPT_THRESHOLD) value = HALF_ACCEPT_THRESHOLD;
        else if (value + HALF_ACCEPT_THRESHOLD > railExtra.getLength()) value = railExtra.getLength() - HALF_ACCEPT_THRESHOLD;

        Vec3 fromMapped = railExtra.getPosition(value - HALF_ACCEPT_THRESHOLD);
        Vec3 toMapped = railExtra.getPosition(value + HALF_ACCEPT_THRESHOLD);
        Vec3 from = new Vec3(fromMapped.x, fromMapped.y, fromMapped.z);
        Vec3 to = new Vec3(toMapped.x, toMapped.y, toMapped.z);

        Vec3 mid = from.add(to).scale(0.5F);
        float roll = RailExtraSupplier.getRollAngle(rail, value);
        return BakedRail.getLookAtMat(mid, from, to, roll, yOffset, reverse);
    }

    @Override
    public void enqueue(BatchManager batchManager, ShaderProp shaderProp) {
        if (railModel == null) return;

        if (vertArrays == null) return;

        if (ClientConfig.getEnableRailDeform() != deform) isDirty = true;

        VertAttrState attrState = new VertAttrState().setModelMatrix(shaderProp.viewMatrix).setOverlayUVNoOverlay();
        if (!RailRenderDispatcher.isHoldingRailItem) attrState.setColor(-1);
        batchManager.enqueue(vertArrays, new EnqueueProp(attrState), ShaderProp.DEFAULT);
    }

    @Override
    public void close() {
        if (vertArrays != null) vertArrays.close();
        if (uploadedCombinedModel != null) uploadedCombinedModel.close();
        vertArrays = null;
        uploadedCombinedModel = null;
    }
}