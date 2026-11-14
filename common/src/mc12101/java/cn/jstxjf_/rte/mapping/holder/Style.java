package cn.jstxjf_.rte.mapping.holder;

public final class Style extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.network.chat.Style> {

    public Style(net.minecraft.network.chat.Style p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Style cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Style((net.minecraft.network.chat.Style) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.network.chat.Style;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Style withFont(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withFont((p0 == null ? null : ((net.minecraft.resources.ResourceLocation) p0.data))));
    }

    public boolean isItalic() {
        return ((net.minecraft.network.chat.Style) this.data).isItalic();
    }

    public int hashCode() {
        return ((net.minecraft.network.chat.Style) this.data).hashCode();
    }

    public cn.jstxjf_.rte.mapping.holder.Style withParent(cn.jstxjf_.rte.mapping.holder.Style p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).applyTo(((net.minecraft.network.chat.Style) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Style withInsertion(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withInsertion(p0));
    }

    public boolean isObfuscated() {
        return ((net.minecraft.network.chat.Style) this.data).isObfuscated();
    }

    public boolean isStrikethrough() {
        return ((net.minecraft.network.chat.Style) this.data).isStrikethrough();
    }

    public cn.jstxjf_.rte.mapping.holder.Style withBold(java.lang.Boolean p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withBold(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Style withItalic(java.lang.Boolean p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withItalic(p0));
    }

    public java.lang.String getInsertion() {
        final var v0 = ((net.minecraft.network.chat.Style) this.data).getInsertion();
        return (v0 == null ? null : v0);
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getFont() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.network.chat.Style) this.data).getFont());
    }

    public boolean isUnderlined() {
        return ((net.minecraft.network.chat.Style) this.data).isUnderlined();
    }

    public cn.jstxjf_.rte.mapping.holder.Style withColor(cn.jstxjf_.rte.mapping.holder.TextFormatting p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withColor((p0 == null ? null : p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Style withColor(cn.jstxjf_.rte.mapping.holder.TextColor p0) {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Style) this.data).withColor((p0 == null ? null : ((net.minecraft.network.chat.TextColor) p0.data))));
    }

    public boolean isEmpty() {
        return ((net.minecraft.network.chat.Style) this.data).isEmpty();
    }

    public cn.jstxjf_.rte.mapping.holder.TextColor getColor() {
        final var v0 = ((net.minecraft.network.chat.Style) this.data).getColor();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.TextColor(v0));
    }

    public boolean isBold() {
        return ((net.minecraft.network.chat.Style) this.data).isBold();
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier getDefaultFontIdMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(net.minecraft.network.chat.Style.DEFAULT_FONT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Style getEmptyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Style(net.minecraft.network.chat.Style.EMPTY);
    }
}
