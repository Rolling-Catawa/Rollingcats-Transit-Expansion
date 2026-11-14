package cn.jstxjf_.rte.mapping.holder;

public final class TextColor extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.network.chat.TextColor> {

    public TextColor(net.minecraft.network.chat.TextColor p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextColor cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new TextColor((net.minecraft.network.chat.TextColor) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.network.chat.TextColor;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.network.chat.TextColor) this.data).serialize();
    }

    public static cn.jstxjf_.rte.mapping.holder.TextColor fromFormatting(cn.jstxjf_.rte.mapping.holder.TextFormatting p0) {
        final var v0 = net.minecraft.network.chat.TextColor.fromLegacyFormat(p0.data);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.TextColor(v0));
    }

    public int getRgb() {
        return ((net.minecraft.network.chat.TextColor) this.data).getValue();
    }

    public static cn.jstxjf_.rte.mapping.holder.TextColor fromRgb(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.TextColor(net.minecraft.network.chat.TextColor.fromRgb(p0));
    }

    public int hashCode() {
        return ((net.minecraft.network.chat.TextColor) this.data).hashCode();
    }
}
