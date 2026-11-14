package cn.jstxjf_.rte.gfx.batch;

import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfx.object.VertArray;
import cn.jstxjf_.rte.gfx.shader.ShaderManager;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;

import java.util.*;

public class BatchManager {

    private static final int PRUNE_INTERVAL = 20;

    private final HashMap<BatchTuple, List<RenderCall>> opaqueBatches = new HashMap<>();
    private final HashMap<BatchTuple, List<RenderCall>> cutoutBatches = new HashMap<>();
    private final HashMap<BatchTuple, List<RenderCall>> translucentBatches = new HashMap<>();
    private final List<OrderedCall> translucentOrder = new ArrayList<>();
    private int framesSincePrune;

    public void enqueue(VertArrays model, EnqueueProp enqueueProp, ShaderProp shaderProp) {
        for (VertArray vertArray : model.meshList) {
            enqueue(vertArray, enqueueProp, shaderProp);
        }
    }

    public void enqueue(VertArray vertArray, EnqueueProp enqueueProp, ShaderProp shaderProp) {
        HashMap<BatchTuple, List<RenderCall>> target;
        if (vertArray.materialProp.translucent) {
            target = translucentBatches;
        } else if (vertArray.materialProp.cutoutHack) {
            target = cutoutBatches;
        } else {
            target = opaqueBatches;
        }
        target.computeIfAbsent(
                new BatchTuple(vertArray.materialProp, shaderProp, vertArray.mapping.instancedModelMatrix),
                (key) -> new ArrayList<>()
        ).add(new RenderCall(vertArray, enqueueProp));
    }

    public int getBatchCount() {
        return countNonEmpty(opaqueBatches) + countNonEmpty(cutoutBatches) + countNonEmpty(translucentBatches);
    }

    public void drawAll(ShaderManager shaderManager, DrawContext drawContext) {
        try {
            drawContext.recordBatches(getBatchCount());
            VertAttrState.invalidateUniformCache();

            drawBatches(shaderManager, opaqueBatches, drawContext);
            drawBatches(shaderManager, cutoutBatches, drawContext);
            drawTranslucent(shaderManager, drawContext);
        } finally {
            resetBatches();
        }
    }

    public void clearAll() {
        resetBatches();
    }

    private void resetBatches() {
        translucentOrder.clear();
        framesSincePrune++;
        final boolean prune = framesSincePrune >= PRUNE_INTERVAL;
        if (prune) {
            framesSincePrune = 0;
        }
        recycle(opaqueBatches, prune);
        recycle(cutoutBatches, prune);
        recycle(translucentBatches, prune);
    }

    private static void recycle(HashMap<BatchTuple, List<RenderCall>> batches, boolean prune) {
        if (prune) {
            batches.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        }
        for (List<RenderCall> calls : batches.values()) {
            calls.clear();
            if (calls instanceof ArrayList<RenderCall> list && list.size() == 0) {
                list.trimToSize();
            }
        }
    }

    private static int countNonEmpty(HashMap<BatchTuple, List<RenderCall>> batches) {
        int count = 0;
        for (List<RenderCall> calls : batches.values()) {
            if (!calls.isEmpty()) count++;
        }
        return count;
    }

    private void drawBatches(ShaderManager shaderManager, HashMap<BatchTuple, List<RenderCall>> batches, DrawContext drawContext) {
        for (Map.Entry<BatchTuple, List<RenderCall>> entry : batches.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            shaderManager.setupShaderBatchState(entry.getKey().materialProp, entry.getKey().shaderProp, entry.getKey().instanced);
            try {
                for (RenderCall renderCall : entry.getValue()) {
                    renderCall.draw();
                    drawContext.recordDrawCall(renderCall);
                }
            } finally {
                shaderManager.cleanupShaderBatchState(entry.getKey().materialProp, entry.getKey().shaderProp);
            }
        }
    }

    private void drawTranslucent(ShaderManager shaderManager, DrawContext drawContext) {
        boolean sortable = true;
        for (Map.Entry<BatchTuple, List<RenderCall>> entry : translucentBatches.entrySet()) {
            for (RenderCall renderCall : entry.getValue()) {
                if (Float.isNaN(renderCall.enqueueProp.sortDepth)) {
                    sortable = false;
                }
                translucentOrder.add(new OrderedCall(entry.getKey(), renderCall));
            }
        }
        if (translucentOrder.isEmpty()) {
            return;
        }
        if (!sortable) {
            translucentOrder.clear();
            drawBatches(shaderManager, translucentBatches, drawContext);
            return;
        }
        translucentOrder.sort((a, b) -> Float.compare(b.renderCall.enqueueProp.sortDepth, a.renderCall.enqueueProp.sortDepth));

        BatchTuple activeTuple = null;
        try {
            for (OrderedCall ordered : translucentOrder) {
                if (!ordered.tuple.equals(activeTuple)) {
                    if (activeTuple != null) {
                        shaderManager.cleanupShaderBatchState(activeTuple.materialProp, activeTuple.shaderProp);
                    }
                    activeTuple = ordered.tuple;
                    shaderManager.setupShaderBatchState(activeTuple.materialProp, activeTuple.shaderProp, activeTuple.instanced);
                }
                ordered.renderCall.draw();
                drawContext.recordDrawCall(ordered.renderCall);
            }
        } finally {
            if (activeTuple != null) {
                shaderManager.cleanupShaderBatchState(activeTuple.materialProp, activeTuple.shaderProp);
            }
        }
    }

    private static class OrderedCall {

        public final BatchTuple tuple;
        public final RenderCall renderCall;

        public OrderedCall(BatchTuple tuple, RenderCall renderCall) {
            this.tuple = tuple;
            this.renderCall = renderCall;
        }
    }

    private static class BatchTuple {

        public MaterialProp materialProp;
        public ShaderProp shaderProp;
        public boolean instanced;

        public BatchTuple(MaterialProp materialProp, ShaderProp shaderProp, boolean instanced) {
            this.materialProp = materialProp;
            this.shaderProp = shaderProp == null ? ShaderProp.DEFAULT : shaderProp;
            this.instanced = instanced;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BatchTuple that = (BatchTuple) o;
            return instanced == that.instanced
                && materialProp.equals(that.materialProp) && shaderProp.equals(that.shaderProp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(materialProp, shaderProp, instanced);
        }
    }

    public static class RenderCall {

        public VertArray vertArray;
        public EnqueueProp enqueueProp;

        public RenderCall(VertArray vertArray, EnqueueProp enqueueProp) {
            this.vertArray = vertArray;
            this.enqueueProp = enqueueProp;
        }

        public void draw() {
            if (vertArray == null || vertArray.id == 0) {
                return;
            }
            vertArray.bind();
            if (enqueueProp.attrState != null) enqueueProp.attrState.applyGlobal();
            if (vertArray.materialProp.attrState != null) vertArray.materialProp.attrState.applyGlobal();
            vertArray.mapping.applyToggleableAttr(enqueueProp.attrState, vertArray.materialProp.attrState);
            vertArray.draw();
        }
    }

}
