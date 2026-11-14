package cn.jstxjf_.rte.mapping.holder;

public final class SpriteProvider extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.particle.SpriteSet> {

    public SpriteProvider(net.minecraft.client.particle.SpriteSet p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SpriteProvider cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SpriteProvider((net.minecraft.client.particle.SpriteSet) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.particle.SpriteSet;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }
}
