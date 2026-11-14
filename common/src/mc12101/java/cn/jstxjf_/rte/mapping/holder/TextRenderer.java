package cn.jstxjf_.rte.mapping.holder;

public final class TextRenderer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.gui.Font> {

    public TextRenderer(net.minecraft.client.gui.Font p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextRenderer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new TextRenderer((net.minecraft.client.gui.Font) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.gui.Font;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String trimToWidth(java.lang.String p0, int p1) {
        return ((net.minecraft.client.gui.Font) this.data).plainSubstrByWidth(p0, p1);
    }

    public java.lang.String trimToWidth(java.lang.String p0, int p1, boolean p2) {
        return ((net.minecraft.client.gui.Font) this.data).plainSubstrByWidth(p0, p1, p2);
    }

    public int getWrappedLinesHeight(java.lang.String p0, int p1) {
        return ((net.minecraft.client.gui.Font) this.data).wordWrapHeight(p0, p1);
    }

    public boolean isRightToLeft() {
        return ((net.minecraft.client.gui.Font) this.data).isBidirectional();
    }

    public int getWidth(java.lang.String p0) {
        return ((net.minecraft.client.gui.Font) this.data).width(p0);
    }

    public int getWidth(cn.jstxjf_.rte.mapping.holder.OrderedText p0) {
        return ((net.minecraft.client.gui.Font) this.data).width(((net.minecraft.util.FormattedCharSequence) p0.data));
    }

    public TextRenderer(java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.gui.font.FontSet> p0, boolean p1) {
        super(new net.minecraft.client.gui.Font(p0, p1));
    }

    public java.lang.String mirror(java.lang.String p0) {
        return ((net.minecraft.client.gui.Font) this.data).bidirectionalShaping(p0);
    }

    public int getFontHeightMapped() {
        java.util.Objects.requireNonNull(((net.minecraft.client.gui.Font) this.data));
        return 9;
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.client.gui.Font) this.data).random);
    }
}
