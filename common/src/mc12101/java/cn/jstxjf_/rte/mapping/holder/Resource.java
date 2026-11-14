package cn.jstxjf_.rte.mapping.holder;

public final class Resource extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.packs.resources.Resource> {

    public Resource(net.minecraft.server.packs.resources.Resource p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Resource cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Resource((net.minecraft.server.packs.resources.Resource) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.packs.resources.Resource;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public Resource(net.minecraft.server.packs.PackResources p0, net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> p1, net.minecraft.server.packs.resources.IoSupplier<net.minecraft.server.packs.resources.ResourceMetadata> p2) {
        super(new net.minecraft.server.packs.resources.Resource(p0, p1, p2));
    }

    public Resource(net.minecraft.server.packs.PackResources p0, net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> p1) {
        super(new net.minecraft.server.packs.resources.Resource(p0, p1));
    }
}
