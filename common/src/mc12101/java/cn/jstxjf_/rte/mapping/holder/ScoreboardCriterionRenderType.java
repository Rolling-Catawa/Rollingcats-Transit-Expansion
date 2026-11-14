package cn.jstxjf_.rte.mapping.holder;

public enum ScoreboardCriterionRenderType {
    INTEGER(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER),
    HEARTS(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.HEARTS);

    public final net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data;

    ScoreboardCriterionRenderType(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data) {
        this.data = data;
    }

    public static ScoreboardCriterionRenderType convert(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType getType(java.lang.String p0) {
        return cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType.convert(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.byId(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType getHeartsMapped() {
        return cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType.convert(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.HEARTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType getIntegerMapped() {
        return cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType.convert(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
    }
}
