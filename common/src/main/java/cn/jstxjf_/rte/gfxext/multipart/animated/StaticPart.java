package cn.jstxjf_.rte.gfxext.multipart.animated;

import cn.jstxjf_.rte.gfx.util.AttrUtil;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.multipart.MultipartUpdateProp;
import cn.jstxjf_.rte.gfxext.multipart.PartBase;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfx.math.Matrix4f;

public class StaticPart extends PartBase {

    private final ModelCluster model;

    private StaticPart(ModelCluster model) {
        this.model = model;
    }

    public StaticPart(RawModel rawModel, ModelManager modelManager) {
        model = modelManager.uploadVertArrays(rawModel);
    }

    @Override
    public void update(MultipartUpdateProp prop) {
    }

    @Override
    public ModelCluster getModel(MultipartUpdateProp prop) {
        return model;
    }

    @Override
    public Matrix4f getTransform(MultipartUpdateProp prop) {
        return parent == null ? AttrUtil.MAT_NO_TRANSFORM : parent.getTransform(prop);
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    public PartBase copy() {
        StaticPart result = new StaticPart(model);
        return result;
    }
}
