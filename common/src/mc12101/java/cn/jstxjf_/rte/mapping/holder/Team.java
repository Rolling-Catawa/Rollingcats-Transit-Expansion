package cn.jstxjf_.rte.mapping.holder;

public final class Team extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.scores.PlayerTeam> {

    public Team(net.minecraft.world.scores.PlayerTeam p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Team cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Team((net.minecraft.world.scores.PlayerTeam) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.scores.PlayerTeam;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText decorateName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(((net.minecraft.world.scores.PlayerTeam) this.data).getFormattedName(((net.minecraft.network.chat.Component) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.TextFormatting getColor() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(((net.minecraft.world.scores.PlayerTeam) this.data).getColor());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.scores.PlayerTeam) this.data).getDisplayName());
    }

    public void setColor(cn.jstxjf_.rte.mapping.holder.TextFormatting p0) {
        ((net.minecraft.world.scores.PlayerTeam) this.data).setColor(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText getFormattedName() {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(((net.minecraft.world.scores.PlayerTeam) this.data).getFormattedDisplayName());
    }

    public java.util.Collection<java.lang.String> getPlayerList() {
        return ((net.minecraft.world.scores.PlayerTeam) this.data).getPlayers();
    }

    public int getFriendlyFlagsBitwise() {
        return ((net.minecraft.world.scores.PlayerTeam) this.data).packOptions();
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.scores.PlayerTeam) this.data).getName();
    }

    public Team(cn.jstxjf_.rte.mapping.holder.Scoreboard p0, java.lang.String p1) {
        super(new net.minecraft.world.scores.PlayerTeam(((net.minecraft.world.scores.Scoreboard) p0.data), p1));
    }

    public void setDisplayName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.world.scores.PlayerTeam) this.data).setDisplayName(((net.minecraft.network.chat.Component) p0.data));
    }

    public void setFriendlyFlagsBitwise(int p0) {
        ((net.minecraft.world.scores.PlayerTeam) this.data).unpackOptions(p0);
    }
}
