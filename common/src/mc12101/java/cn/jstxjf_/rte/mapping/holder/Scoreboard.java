package cn.jstxjf_.rte.mapping.holder;

public final class Scoreboard extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.scores.Scoreboard> {

    public Scoreboard(net.minecraft.world.scores.Scoreboard p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Scoreboard cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Scoreboard((net.minecraft.world.scores.Scoreboard) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.scores.Scoreboard;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.util.Collection<net.minecraft.world.scores.Objective> getObjectives() {
        return ((net.minecraft.world.scores.Scoreboard) this.data).getObjectives();
    }

    public void updateExistingObjective(cn.jstxjf_.rte.mapping.holder.ScoreboardObjective p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onObjectiveChanged(((net.minecraft.world.scores.Objective) p0.data));
    }

    public void updateRemovedTeam(cn.jstxjf_.rte.mapping.holder.Team p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onTeamRemoved(((net.minecraft.world.scores.PlayerTeam) p0.data));
    }

    public void resetEntityScore(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).entityRemoved(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Team addTeam(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.Team(((net.minecraft.world.scores.Scoreboard) this.data).addPlayerTeam(p0));
    }

    public boolean addPlayerToTeam(java.lang.String p0, cn.jstxjf_.rte.mapping.holder.Team p1) {
        return ((net.minecraft.world.scores.Scoreboard) this.data).addPlayerToTeam(p0, ((net.minecraft.world.scores.PlayerTeam) p1.data));
    }

    public void updateScoreboardTeam(cn.jstxjf_.rte.mapping.holder.Team p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onTeamChanged(((net.minecraft.world.scores.PlayerTeam) p0.data));
    }

    public void removeObjective(cn.jstxjf_.rte.mapping.holder.ScoreboardObjective p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).removeObjective(((net.minecraft.world.scores.Objective) p0.data));
    }

    public Scoreboard() {
        super(new net.minecraft.world.scores.Scoreboard());
    }

    public void removePlayerFromTeam(java.lang.String p0, cn.jstxjf_.rte.mapping.holder.Team p1) {
        ((net.minecraft.world.scores.Scoreboard) this.data).removePlayerFromTeam(p0, ((net.minecraft.world.scores.PlayerTeam) p1.data));
    }

    public void removeTeam(cn.jstxjf_.rte.mapping.holder.Team p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).removePlayerTeam(((net.minecraft.world.scores.PlayerTeam) p0.data));
    }

    public java.util.Collection<java.lang.String> getObjectiveNames() {
        return ((net.minecraft.world.scores.Scoreboard) this.data).getObjectiveNames();
    }

    public void updateObjective(cn.jstxjf_.rte.mapping.holder.ScoreboardObjective p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onObjectiveAdded(((net.minecraft.world.scores.Objective) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Team getTeam(java.lang.String p0) {
        final var v0 = ((net.minecraft.world.scores.Scoreboard) this.data).getPlayerTeam(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Team(v0));
    }

    public void updateScoreboardTeamAndPlayers(cn.jstxjf_.rte.mapping.holder.Team p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onTeamAdded(((net.minecraft.world.scores.PlayerTeam) p0.data));
    }

    public void updateRemovedObjective(cn.jstxjf_.rte.mapping.holder.ScoreboardObjective p0) {
        ((net.minecraft.world.scores.Scoreboard) this.data).onObjectiveRemoved(((net.minecraft.world.scores.Objective) p0.data));
    }

    public java.util.Collection<java.lang.String> getTeamNames() {
        return ((net.minecraft.world.scores.Scoreboard) this.data).getTeamNames();
    }

    public cn.jstxjf_.rte.mapping.holder.Team getPlayerTeam(java.lang.String p0) {
        final var v0 = ((net.minecraft.world.scores.Scoreboard) this.data).getPlayersTeam(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Team(v0));
    }
}
