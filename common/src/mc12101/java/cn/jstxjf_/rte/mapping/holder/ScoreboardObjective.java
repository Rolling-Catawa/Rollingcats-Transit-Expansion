package cn.jstxjf_.rte.mapping.holder;

public final class ScoreboardObjective extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.scores.Objective> {

    public ScoreboardObjective(net.minecraft.world.scores.Objective p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardObjective cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ScoreboardObjective((net.minecraft.world.scores.Objective) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.scores.Objective;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.scores.Objective) this.data).getName();
    }

    public ScoreboardObjective(cn.jstxjf_.rte.mapping.holder.Scoreboard p0, java.lang.String p1, cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion p2, cn.jstxjf_.rte.mapping.holder.Text p3, cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType p4, boolean p5, net.minecraft.network.chat.numbers.NumberFormat p6) {
        super(new net.minecraft.world.scores.Objective(((net.minecraft.world.scores.Scoreboard) p0.data), p1, ((net.minecraft.world.scores.criteria.ObjectiveCriteria) p2.data), ((net.minecraft.network.chat.Component) p3.data), p4.data, p5, p6));
    }

    public cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType getRenderType() {
        return cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType.convert(((net.minecraft.world.scores.Objective) this.data).getRenderType());
    }

    public void setDisplayName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.world.scores.Objective) this.data).setDisplayName(((net.minecraft.network.chat.Component) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Scoreboard getScoreboard() {
        return new cn.jstxjf_.rte.mapping.holder.Scoreboard(((net.minecraft.world.scores.Objective) this.data).getScoreboard());
    }

    public void setRenderType(cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType p0) {
        ((net.minecraft.world.scores.Objective) this.data).setRenderType(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Text toHoverableText() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.scores.Objective) this.data).getFormattedDisplayName());
    }

    public cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getCriterion() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(((net.minecraft.world.scores.Objective) this.data).getCriteria());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.scores.Objective) this.data).getDisplayName());
    }
}
