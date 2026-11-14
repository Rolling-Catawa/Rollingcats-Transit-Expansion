package cn.jstxjf_.rte.gfxext.multipart;

import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;

import java.util.*;

public class MultipartContainer {

    public List<PartBase> parts = new ArrayList<>();

    public void updateAndEnqueueAll(DrawScheduler scheduler, MultipartUpdateProp prop, Matrix4f basePose, int light) {
        for (PartBase part : parts) {
            part.update(prop);
        }
        for (PartBase part : parts) {
            ModelCluster model = part.getModel(prop);
            if (model == null) continue;
            Matrix4f partPose = basePose.copy();
            partPose.multiply(part.getTransform(prop));
            scheduler.enqueue(model, partPose, light);
        }
    }

    public void topologicalSort() {
        List<PartBase> result = new ArrayList<>(parts.size());
        HashMap<PartBase, Integer> inDeg = new HashMap<>();
        Queue<PartBase> queue = new LinkedList<>();
        for (PartBase part : parts) {
            int crntInDeg = part.parent == null ? 0 : 1;
            inDeg.put(part, crntInDeg);
            if (crntInDeg == 0) queue.add(part);
        }
        while (!queue.isEmpty()) {
            PartBase partU = queue.poll();
            result.add(partU);
            for (PartBase partV : parts) {
                if (partV.parent != partU) continue;
                int crntInDeg = inDeg.get(partV) - 1;
                inDeg.put(partV, crntInDeg);
                if (crntInDeg == 0) {
                    queue.add(partV);
                }
            }
        }
        if (result.size() != parts.size()) throw new IllegalArgumentException("Multipart contains loop reference.");
        this.parts = result;
    }

}
