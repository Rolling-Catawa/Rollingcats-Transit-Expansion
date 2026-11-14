package cn.jstxjf_.rte.mapping.holder;

public final class DirectionProperty extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.properties.DirectionProperty> {

    public DirectionProperty(net.minecraft.world.level.block.state.properties.DirectionProperty p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.DirectionProperty cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new DirectionProperty((net.minecraft.world.level.block.state.properties.DirectionProperty) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.properties.DirectionProperty;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String name(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return ((net.minecraft.world.level.block.state.properties.DirectionProperty) this.data).getName(p0.data);
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.level.block.state.properties.DirectionProperty) this.data).getName();
    }

    public static <T extends java.lang.Enum<T> & cn.jstxjf_.rte.mapping.holder.StringIdentifiable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, T[] p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.DirectionProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & cn.jstxjf_.rte.mapping.holder.StringIdentifiable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, java.util.function.Predicate<T> p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.DirectionProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & cn.jstxjf_.rte.mapping.holder.StringIdentifiable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, java.util.Collection<T> p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.DirectionProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & cn.jstxjf_.rte.mapping.holder.StringIdentifiable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.DirectionProperty.create(p0, p1));
    }

    public int computeHashCode() {
        return ((net.minecraft.world.level.block.state.properties.DirectionProperty) this.data).generateHashCode();
    }
}
