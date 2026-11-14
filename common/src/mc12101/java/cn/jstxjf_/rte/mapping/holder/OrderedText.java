package cn.jstxjf_.rte.mapping.holder;

public final class OrderedText extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.util.FormattedCharSequence> {

    public OrderedText(net.minecraft.util.FormattedCharSequence p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.OrderedText cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new OrderedText((net.minecraft.util.FormattedCharSequence) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.util.FormattedCharSequence;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.OrderedText styled(int p0, cn.jstxjf_.rte.mapping.holder.Style p1) {
        return new cn.jstxjf_.rte.mapping.holder.OrderedText(net.minecraft.util.FormattedCharSequence.codepoint(p0, ((net.minecraft.network.chat.Style) p1.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.OrderedText getEmptyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.OrderedText(net.minecraft.util.FormattedCharSequence.EMPTY);
    }
}
