package cn.jstxjf_.rte.gfx.vertex;

public enum VertAttrSrc {
    GLOBAL,

    VERTEX_BUF,

    VERTEX_BUF_OR_GLOBAL,

    INSTANCE_BUF,

    INSTANCE_BUF_OR_GLOBAL;

    public boolean isToggleable() {
        return this == VERTEX_BUF_OR_GLOBAL || this == INSTANCE_BUF_OR_GLOBAL;
    }

    public boolean inVertBuf() {
        return this == VERTEX_BUF || this == VERTEX_BUF_OR_GLOBAL;
    }
}
