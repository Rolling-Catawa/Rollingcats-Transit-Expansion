package cn.jstxjf_.rte.mapping.holder;

public final class ScoreboardScore extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.scores.Score> {

    public ScoreboardScore(net.minecraft.world.scores.Score p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardScore cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ScoreboardScore((net.minecraft.world.scores.Score) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.scores.Score;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isLocked() {
        return ((net.minecraft.world.scores.Score) this.data).isLocked();
    }

    public void setScore(int p0) {
        ((net.minecraft.world.scores.Score) this.data).value(p0);
    }

    public int getScore() {
        return ((net.minecraft.world.scores.Score) this.data).value();
    }

    public ScoreboardScore() {
        super(new net.minecraft.world.scores.Score());
    }

    public void setLocked(boolean p0) {
        ((net.minecraft.world.scores.Score) this.data).setLocked(p0);
    }
}
