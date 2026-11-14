package cn.jstxjf_.rte.mapping.holder;

public final class MutableText extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.network.chat.MutableComponent> {

    public MutableText(net.minecraft.network.chat.MutableComponent p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MutableText cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MutableText((net.minecraft.network.chat.MutableComponent) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.network.chat.MutableComponent;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String asTruncatedString(int p0) {
        return ((net.minecraft.network.chat.MutableComponent) this.data).getString(p0);
    }

    public java.lang.String getString() {
        return ((net.minecraft.network.chat.MutableComponent) this.data).getString();
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText formatted(cn.jstxjf_.rte.mapping.holder.TextFormatting p0) {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(((net.minecraft.network.chat.MutableComponent) this.data).withStyle(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText append(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(((net.minecraft.network.chat.MutableComponent) this.data).append(p0));
    }

    public static java.util.Optional<net.minecraft.util.Unit> getTerminateVisitMapped() {
        return net.minecraft.network.chat.MutableComponent.STOP_ITERATION;
    }
}
