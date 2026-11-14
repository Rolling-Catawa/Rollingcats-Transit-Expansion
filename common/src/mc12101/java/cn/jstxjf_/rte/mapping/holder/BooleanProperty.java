package cn.jstxjf_.rte.mapping.holder;

public final class BooleanProperty extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.properties.BooleanProperty> {

    public BooleanProperty(net.minecraft.world.level.block.state.properties.BooleanProperty p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanProperty cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BooleanProperty((net.minecraft.world.level.block.state.properties.BooleanProperty) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.properties.BooleanProperty;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).getName();
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanProperty of(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.BooleanProperty(net.minecraft.world.level.block.state.properties.BooleanProperty.create(p0));
    }

    public java.lang.String name(java.lang.Boolean p0) {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).getName(p0);
    }

    public java.util.Collection<java.lang.Boolean> getValues() {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).getPossibleValues();
    }

    public java.lang.Class<java.lang.Boolean> getType() {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).getValueClass();
    }

    public int computeHashCode() {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).generateHashCode();
    }

    public java.util.Optional<java.lang.Boolean> parse(java.lang.String p0) {
        return ((net.minecraft.world.level.block.state.properties.BooleanProperty) this.data).getValue(p0);
    }
}
