package cn.jstxjf_.rte.mapping.holder;

public final class Property<T extends java.lang.Comparable<T>> extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.properties.Property<T>> {

    public Property(net.minecraft.world.level.block.state.properties.Property<T> p0) {
        super(p0);
    }

    public static <T extends java.lang.Comparable<T>> cn.jstxjf_.rte.mapping.holder.Property<T> cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Property<>((net.minecraft.world.level.block.state.properties.Property<T>) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.properties.Property;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.util.Collection<T> getValues() {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).getPossibleValues();
    }

    public java.util.Optional<T> parse(java.lang.String p0) {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).getValue(p0);
    }

    public java.lang.Class<T> getType() {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).getValueClass();
    }

    public java.lang.String name(T p0) {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).getName(p0);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).getName();
    }

    public int computeHashCode() {
        return ((net.minecraft.world.level.block.state.properties.Property) this.data).generateHashCode();
    }
}
