package cn.jstxjf_.rte.gfx.batch;

import cn.jstxjf_.rte.gfx.vertex.VertAttrState;

public class EnqueueProp {

    public static EnqueueProp DEFAULT = new EnqueueProp(null);

    public VertAttrState attrState;

    public float sortDepth = Float.NaN;

    public EnqueueProp(VertAttrState attrState) {
        this.attrState = attrState;
    }

    public EnqueueProp(VertAttrState attrState, float sortDepth) {
        this.attrState = attrState;
        this.sortDepth = sortDepth;
    }
}
