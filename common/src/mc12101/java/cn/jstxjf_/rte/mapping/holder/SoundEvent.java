package cn.jstxjf_.rte.mapping.holder;

public final class SoundEvent extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.sounds.SoundEvent> {

    public SoundEvent(net.minecraft.sounds.SoundEvent p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SoundEvent((net.minecraft.sounds.SoundEvent) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.sounds.SoundEvent;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getId() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.sounds.SoundEvent) this.data).getLocation());
    }
}
