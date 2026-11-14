package cn.jstxjf_.rte.mapping.holder;

public final class VertexFormatElement extends cn.jstxjf_.rte.mapping.tool.HolderBase<com.mojang.blaze3d.vertex.VertexFormatElement> {

    public VertexFormatElement(com.mojang.blaze3d.vertex.VertexFormatElement p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormatElement cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new VertexFormatElement((com.mojang.blaze3d.vertex.VertexFormatElement) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof com.mojang.blaze3d.vertex.VertexFormatElement;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int hashCode() {
        return ((com.mojang.blaze3d.vertex.VertexFormatElement) this.data).hashCode();
    }
}
