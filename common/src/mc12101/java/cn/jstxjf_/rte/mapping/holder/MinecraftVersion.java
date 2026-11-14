package cn.jstxjf_.rte.mapping.holder;

public final class MinecraftVersion extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.DetectedVersion> {

    public MinecraftVersion(net.minecraft.DetectedVersion p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MinecraftVersion cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MinecraftVersion((net.minecraft.DetectedVersion) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.DetectedVersion;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getId() {
        return ((net.minecraft.DetectedVersion) this.data).getId();
    }

    public java.util.Date getBuildTime() {
        return ((net.minecraft.DetectedVersion) this.data).getBuildTime();
    }

    public java.lang.String getName() {
        return ((net.minecraft.DetectedVersion) this.data).getName();
    }

    public boolean isStable() {
        return ((net.minecraft.DetectedVersion) this.data).isStable();
    }

    public int getProtocolVersion() {
        return ((net.minecraft.DetectedVersion) this.data).getProtocolVersion();
    }
}
