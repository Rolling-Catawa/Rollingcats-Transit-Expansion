package cn.jstxjf_.rte.mapping.holder;

public final class IntegerProperty extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.properties.IntegerProperty> {

    public IntegerProperty(net.minecraft.world.level.block.state.properties.IntegerProperty p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.IntegerProperty cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new IntegerProperty((net.minecraft.world.level.block.state.properties.IntegerProperty) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.properties.IntegerProperty;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).getName();
    }

    public java.lang.String name(java.lang.Integer p0) {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).getName(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.IntegerProperty of(java.lang.String p0, int p1, int p2) {
        return new cn.jstxjf_.rte.mapping.holder.IntegerProperty(net.minecraft.world.level.block.state.properties.IntegerProperty.create(p0, p1, p2));
    }

    public java.util.Optional<java.lang.Integer> parse(java.lang.String p0) {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).getValue(p0);
    }

    public java.lang.Class<java.lang.Integer> getType() {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).getValueClass();
    }

    public int computeHashCode() {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).generateHashCode();
    }

    public java.util.Collection<java.lang.Integer> getValues() {
        return ((net.minecraft.world.level.block.state.properties.IntegerProperty) this.data).getPossibleValues();
    }
}
