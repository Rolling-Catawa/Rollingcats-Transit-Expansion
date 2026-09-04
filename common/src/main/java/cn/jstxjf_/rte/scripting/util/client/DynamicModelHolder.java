package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfx.util.GlStateTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.pipeline.RenderCall;

public class DynamicModelHolder {

    private ModelCluster uploadedModel = null;

    private RenderCall uploadCall = null;

    public void uploadLater(RawModel rawModel) {
        RawModel finalRawModel = rawModel.copyForMaterialChanges();
        finalRawModel.sourceLocation = null;
        uploadCall = () -> upload(finalRawModel);
        RenderSystem.recordRenderCall(() -> {
            if (uploadCall != null) {
                uploadCall.execute();
                uploadCall = null;
            }
        });
    }

    public void uploadNow(RawModel rawModel) {
        uploadCall = null;
        RawModel finalRawModel = rawModel.copyForMaterialChanges();
        finalRawModel.sourceLocation = null;
        upload(finalRawModel);
    }

    private void upload(RawModel finalRawModel) {
        assert RenderSystem.isOnRenderThreadOrInit();
        boolean needProtection = !GlStateTracker.isStateProtected;
        if (needProtection) GlStateTracker.capture();
        try {
            ModelCluster lastUploadedModel = uploadedModel;
            ModelCluster newOne = new ModelCluster(finalRawModel, ModelManager.DEFAULT_MAPPING);
            this.uploadedModel = newOne;
            if (lastUploadedModel != null) lastUploadedModel.close();
        } finally {
            if (needProtection) GlStateTracker.restore();
        }
    }

    public ModelCluster getUploadedModel() {
        return uploadedModel;
    }

    public void close() {
        RenderSystem.recordRenderCall(() -> {
            if (uploadedModel != null) {
                uploadedModel.close();
                uploadedModel = null;
            }
        });
    }
}
