package cn.jstxjf_.rte.gfxext.multipart;

import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfx.math.Matrix4f;

public abstract class PartBase {

    public PartBase parent;

    public abstract void update(MultipartUpdateProp prop);

    public abstract ModelCluster getModel(MultipartUpdateProp prop);

    public abstract Matrix4f getTransform(MultipartUpdateProp prop);

    public abstract boolean isStatic();

}
