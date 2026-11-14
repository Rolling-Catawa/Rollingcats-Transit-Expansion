package cn.jstxjf_.rte.mapping.holder;

public final class Identifier extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.resources.ResourceLocation> {

    public Identifier(net.minecraft.resources.ResourceLocation p0) {
        super(p0);
    }

    public Identifier(java.lang.String p0) {
        this(net.minecraft.resources.ResourceLocation.parse(p0));
    }

    public Identifier(java.lang.String p0, java.lang.String p1) {
        this(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(p0, p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Identifier((net.minecraft.resources.ResourceLocation) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.resources.ResourceLocation;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier fromCommandInput(com.mojang.brigadier.StringReader p0) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(net.minecraft.resources.ResourceLocation.read(p0));
    }

    public java.lang.String getNamespace() {
        return ((net.minecraft.resources.ResourceLocation) this.data).getNamespace();
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier tryParse(java.lang.String p0) {
        final var v0 = net.minecraft.resources.ResourceLocation.tryParse(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Identifier(v0));
    }

    public java.lang.String getPath() {
        return ((net.minecraft.resources.ResourceLocation) this.data).getPath();
    }

    public int hashCode() {
        return ((net.minecraft.resources.ResourceLocation) this.data).hashCode();
    }

    public int compareTo(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return ((net.minecraft.resources.ResourceLocation) this.data).compareTo(((net.minecraft.resources.ResourceLocation) p0.data));
    }
}
