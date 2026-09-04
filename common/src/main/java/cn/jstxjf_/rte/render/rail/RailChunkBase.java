package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;

import cn.jstxjf_.rte.gfx.util.GlStateTracker;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RailChunkBase implements Closeable {

    /*
     * Rail deformation is CPU-heavy. An unbounded cached pool used to create 30+
     * workers while entering a world, starving the render thread precisely when
     * vehicle and chunk models were being uploaded. The outer chunk queue already
     * provides enough parallel work, so keep a small, bounded worker set.
     */
    private static final AtomicInteger WORKER_ID = new AtomicInteger();
    private static final int WORKER_COUNT = Math.max(1,
        Math.min(4, Runtime.getRuntime().availableProcessors() / 3));
    protected static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(WORKER_COUNT, runnable -> {
        final Thread thread = new Thread(runnable, "rte-rail-build-" + WORKER_ID.incrementAndGet());
        thread.setDaemon(true);
        thread.setPriority(Math.max(Thread.MIN_PRIORITY, Thread.NORM_PRIORITY - 1));
        return thread;
    });
    protected static ConcurrentLinkedQueue<Runnable> UPLOAD_QUEUE = new ConcurrentLinkedQueue<>();

    public Long chunkId;
    public AABB boundingBox; // Use Mapped AABB if possible, but standard is fine for internal logic
    public HashMap<BakedRail, ArrayList<Matrix4f>> containingRails = new HashMap<>();
    public HashMap<BakedRail, ArrayList<Matrix4f>> containingRailsWriting = new HashMap<>();

    public final String modelKey;

    protected float modelYMin;
    protected float modelYMax;

    public boolean isDirty = false;
    public int lightRecheckFrames = 0;
    public long nextRebuildAllowedMillis = 0;
    public long nextLightDirtyAllowedMillis = 0;
    public static final long REBUILD_COOLDOWN_MILLIS = 250;
    public static final long LIGHT_DIRTY_COOLDOWN_MILLIS = 1500;
    public boolean bufferBuilt = false;
    public boolean bufferBuilding = false;
    public double cameraDistManhattanXZ = 0;

    public RailChunkBase(long chunkId, String modelKey) {
        this.chunkId = chunkId;
        this.modelKey = modelKey;
        long boundary = RailModelRegistry.getProperty(modelKey).boundingBox;
        modelYMin = Float.intBitsToFloat((int)(boundary >> 32));
        modelYMax = Float.intBitsToFloat((int)(boundary & 0xFFFFFFFFL));
        setBoundingBox(0, 0);
    }

    protected void setBoundingBox(float yMin, float yMax) {
        int posXMin = (int)(chunkId >> 32) << (4 + BakedRail.POS_SHIFT);
        int posZMin = (int)(chunkId & 0xFFFFFFFFL) << (4 + BakedRail.POS_SHIFT);
        int span = 1 << (4 + BakedRail.POS_SHIFT);
        boundingBox = new AABB(posXMin, yMin + modelYMin - 1, posZMin,
                posXMin + span, yMax + modelYMax + 1, posZMin + span);
    }

    public boolean isEven() { 
        return ((int)(chunkId >> 32) + (int)(chunkId & 0xFFFFFFFFL)) % 2 == 0;
    }

    /*
    public ChunkPos getChunkPos() {
        return new ChunkPos((int)(chunkId >> 32) << BakedRail.POS_SHIFT, (int)(chunkId & 0xFFFFFFFFL) << BakedRail.POS_SHIFT);
    }
    */

    public boolean containsYSection(int yMin, int yMax) {
        return (yMin << 4) <= boundingBox.maxY && (yMax << 4) >= boundingBox.minY;
    }

    public double getCameraDistManhattanXZ(Vector3d cameraPos) {
        cameraDistManhattanXZ = Math.abs(cameraPos.getXMapped() - (boundingBox.minX + boundingBox.maxX) / 2)
                + Math.abs(cameraPos.getZMapped() - (boundingBox.minZ + boundingBox.maxZ) / 2);
        return cameraDistManhattanXZ;
    }

    public void addRail(BakedRail rail) {
        ArrayList<Matrix4f> matrices = rail.coveredChunks.get(chunkId);
        if (matrices == null) return;
        containingRailsWriting.put(rail, matrices);
        isDirty = true;
    }

    public void removeRail(BakedRail rail) {
        containingRailsWriting.remove(rail);
        isDirty = true;
    }

    public void rebuildBuffer(World world) {
        bufferBuilding = true;
        isDirty = false;
        if (lightRecheckFrames > 0) {
            lightRecheckFrames--;
            isDirty = true;
        }
        bufferBuilt = true;
        containingRails.clear();
        containingRails.putAll(containingRailsWriting);
    }
    
    public abstract void enqueue(BatchManager batchManager, ShaderProp shaderProp);

    public static void upload() {
        if (!UPLOAD_QUEUE.isEmpty()) {
            boolean needProtection = !GlStateTracker.isStateProtected;
            if (needProtection) GlStateTracker.capture();
            try {
                UPLOAD_QUEUE.poll().run();
            } finally {
                if (needProtection) GlStateTracker.restore();
            }
        }
    }

    @Override
    public abstract void close();
}
