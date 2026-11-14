package cn.jstxjf_.rte.mapping.holder;

public final class Text extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.network.chat.Component> {

    public Text(net.minecraft.network.chat.Component p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Text cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Text((net.minecraft.network.chat.Component) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.network.chat.Component;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String asTruncatedString(int p0) {
        return ((net.minecraft.network.chat.Component) this.data).getString(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.OrderedText asOrderedText() {
        return new cn.jstxjf_.rte.mapping.holder.OrderedText(((net.minecraft.network.chat.Component) this.data).getVisualOrderText());
    }

    public java.lang.String getString() {
        return ((net.minecraft.network.chat.Component) this.data).getString();
    }

    public cn.jstxjf_.rte.mapping.holder.Style getStyle() {
        return new cn.jstxjf_.rte.mapping.holder.Style(((net.minecraft.network.chat.Component) this.data).getStyle());
    }

    public static cn.jstxjf_.rte.mapping.holder.Text of(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.Text(net.minecraft.network.chat.Component.nullToEmpty(p0));
    }

    public static java.util.Optional<net.minecraft.util.Unit> getTerminateVisitMapped() {
        return net.minecraft.network.chat.Component.STOP_ITERATION;
    }
}
