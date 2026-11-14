package cn.jstxjf_.rte.mapping.holder;

public final class BufferBuilderStorage extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.RenderBuffers> {

    public BufferBuilderStorage(net.minecraft.client.renderer.RenderBuffers p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BufferBuilderStorage cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BufferBuilderStorage((net.minecraft.client.renderer.RenderBuffers) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.RenderBuffers;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public BufferBuilderStorage(int p0) {
        super(new net.minecraft.client.renderer.RenderBuffers(p0));
    }
}
