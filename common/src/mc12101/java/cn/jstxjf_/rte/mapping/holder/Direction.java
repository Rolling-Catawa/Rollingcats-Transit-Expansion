package cn.jstxjf_.rte.mapping.holder;

public enum Direction {
    DOWN(net.minecraft.core.Direction.DOWN),
    UP(net.minecraft.core.Direction.UP),
    NORTH(net.minecraft.core.Direction.NORTH),
    SOUTH(net.minecraft.core.Direction.SOUTH),
    WEST(net.minecraft.core.Direction.WEST),
    EAST(net.minecraft.core.Direction.EAST);

    public final net.minecraft.core.Direction data;

    Direction(net.minecraft.core.Direction data) {
        this.data = data;
    }

    public static Direction convert(net.minecraft.core.Direction data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction random(cn.jstxjf_.rte.mapping.holder.Random p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.getRandom(((net.minecraft.util.RandomSource) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction transform(cn.jstxjf_.rte.mapping.holder.Matrix4f p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.rotate(((org.joml.Matrix4f) p0.data), p1.data));
    }

    public float asRotation() {
        return this.data.toYRot();
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction fromRotation(double p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.fromYRot(p0));
    }

    public int getId() {
        return this.data.get3DDataValue();
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction fromHorizontal(int p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.from2DDataValue(p0));
    }

    public int getOffsetY() {
        return this.data.getStepY();
    }

    public int getOffsetZ() {
        return this.data.getStepZ();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction rotateYCounterclockwise() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(this.data.getCounterClockWise());
    }

    public boolean method_30928(float p0) {
        return this.data.isFacingAngle(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getOpposite() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(this.data.getOpposite());
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getFacing(float p0, float p1, float p2) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.getNearest(p0, p1, p2));
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getFacing(double p0, double p1, double p2) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.getNearest(p0, p1, p2));
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction byId(int p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.from3DDataValue(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction fromVector(int p0, int p1, int p2) {
        final var v0 = net.minecraft.core.Direction.fromDelta(p0, p1, p2);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Direction.convert(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.Axis getAxis() {
        return cn.jstxjf_.rte.mapping.holder.Axis.convert(this.data.getAxis());
    }

    public int getHorizontal() {
        return this.data.get2DDataValue();
    }

    public int getOffsetX() {
        return this.data.getStepX();
    }

    public java.lang.String asString() {
        return this.data.getSerializedName();
    }

    public java.lang.String getName() {
        return this.data.getName();
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i getVector() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(this.data.getNormal());
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction byName(java.lang.String p0) {
        final var v0 = net.minecraft.core.Direction.byName(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Direction.convert(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.Direction rotateYClockwise() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(this.data.getClockWise());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3f getUnitVector() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3f(this.data.step());
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getNorthMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.NORTH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getUpMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.UP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getSouthMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.SOUTH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getEastMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.EAST);
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getDownMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.DOWN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Direction getWestMapped() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(net.minecraft.core.Direction.WEST);
    }
}
