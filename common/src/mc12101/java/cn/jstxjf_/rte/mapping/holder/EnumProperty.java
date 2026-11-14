package cn.jstxjf_.rte.mapping.holder;

public final class EnumProperty<T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.properties.EnumProperty<T>> {

    public EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty<T> p0) {
        super(p0);
    }

    public static <T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new EnumProperty<>((net.minecraft.world.level.block.state.properties.EnumProperty<T>) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.properties.EnumProperty;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String name(T p0) {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).getName(p0);
    }

    public java.lang.Class<T> getType() {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).getValueClass();
    }

    public java.util.Optional<T> parse(java.lang.String p0) {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).getValue(p0);
    }

    public java.util.Collection<T> getValues() {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).getPossibleValues();
    }

    public java.lang.String getName() {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).getName();
    }

    public static <T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, T[] p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, java.util.function.Predicate<T> p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1, java.util.Collection<T> p2) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(p0, p1, p2));
    }

    public static <T extends java.lang.Enum<T> & net.minecraft.util.StringRepresentable> cn.jstxjf_.rte.mapping.holder.EnumProperty<T> of(java.lang.String p0, java.lang.Class<T> p1) {
        return new cn.jstxjf_.rte.mapping.holder.EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(p0, p1));
    }

    public int computeHashCode() {
        return ((net.minecraft.world.level.block.state.properties.EnumProperty) this.data).generateHashCode();
    }
}
