package cn.jstxjf_.rte.gfx.batch;

import cn.jstxjf_.rte.gfx.vertex.VertAttrState;

/** Additional property affecting rendering process. Set when enqueue. Does not affect batching. */
public class EnqueueProp {

    public static EnqueueProp DEFAULT = new EnqueueProp(null);

    /** The vertex attribute values to use for those specified with VertAttrSrc ENQUEUE. */
    public VertAttrState attrState;

    /** Squared distance from camera, used to order translucent draws. NaN disables ordering. */
    public float sortDepth = Float.NaN;

    public EnqueueProp(VertAttrState attrState) {
        this.attrState = attrState;
    }

    public EnqueueProp(VertAttrState attrState, float sortDepth) {
        this.attrState = attrState;
        this.sortDepth = sortDepth;
    }
}
