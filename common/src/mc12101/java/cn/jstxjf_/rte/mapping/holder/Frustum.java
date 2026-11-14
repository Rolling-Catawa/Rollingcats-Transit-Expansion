package cn.jstxjf_.rte.mapping.holder;

public final class Frustum extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.culling.Frustum> {

    public Frustum(net.minecraft.client.renderer.culling.Frustum p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Frustum cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Frustum((net.minecraft.client.renderer.culling.Frustum) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.culling.Frustum;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public Frustum(cn.jstxjf_.rte.mapping.holder.Matrix4f p0, cn.jstxjf_.rte.mapping.holder.Matrix4f p1) {
        super(new net.minecraft.client.renderer.culling.Frustum(((org.joml.Matrix4f) p0.data), ((org.joml.Matrix4f) p1.data)));
    }

    public Frustum(cn.jstxjf_.rte.mapping.holder.Frustum p0) {
        super(new net.minecraft.client.renderer.culling.Frustum(((net.minecraft.client.renderer.culling.Frustum) p0.data)));
    }

    public void setPosition(double p0, double p1, double p2) {
        ((net.minecraft.client.renderer.culling.Frustum) this.data).prepare(p0, p1, p2);
    }

    public boolean isVisible(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.client.renderer.culling.Frustum) this.data).isVisible(((net.minecraft.world.phys.AABB) p0.data));
    }
}
