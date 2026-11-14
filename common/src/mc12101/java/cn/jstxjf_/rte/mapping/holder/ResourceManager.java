package cn.jstxjf_.rte.mapping.holder;

public final class ResourceManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.packs.resources.ResourceManager> {

    public ResourceManager(net.minecraft.server.packs.resources.ResourceManager p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ResourceManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ResourceManager((net.minecraft.server.packs.resources.ResourceManager) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.packs.resources.ResourceManager;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.util.Set<java.lang.String> getAllNamespaces() {
        return ((net.minecraft.server.packs.resources.ResourceManager) this.data).getNamespaces();
    }
}
