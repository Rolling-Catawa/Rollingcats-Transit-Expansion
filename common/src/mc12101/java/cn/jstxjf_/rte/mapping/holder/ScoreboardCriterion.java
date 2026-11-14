package cn.jstxjf_.rte.mapping.holder;

public final class ScoreboardCriterion extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.scores.criteria.ObjectiveCriteria> {

    public ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ScoreboardCriterion((net.minecraft.world.scores.criteria.ObjectiveCriteria) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.scores.criteria.ObjectiveCriteria;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.scores.criteria.ObjectiveCriteria) this.data).getName();
    }

    public cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType getDefaultRenderType() {
        return cn.jstxjf_.rte.mapping.holder.ScoreboardCriterionRenderType.convert(((net.minecraft.world.scores.criteria.ObjectiveCriteria) this.data).getDefaultRenderType());
    }

    public boolean isReadOnly() {
        return ((net.minecraft.world.scores.criteria.ObjectiveCriteria) this.data).isReadOnly();
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getDummyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getLevelMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.LEVEL);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getAirMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.AIR);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getTriggerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.TRIGGER);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getArmorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.ARMOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getDeathCountMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.DEATH_COUNT);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getFoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.FOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion getHealthMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ScoreboardCriterion(net.minecraft.world.scores.criteria.ObjectiveCriteria.HEALTH);
    }
}
