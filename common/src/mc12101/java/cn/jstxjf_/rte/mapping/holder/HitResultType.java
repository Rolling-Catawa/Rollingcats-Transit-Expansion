package cn.jstxjf_.rte.mapping.holder;

public enum HitResultType {
    MISS(net.minecraft.world.phys.HitResult.Type.MISS),
    BLOCK(net.minecraft.world.phys.HitResult.Type.BLOCK),
    ENTITY(net.minecraft.world.phys.HitResult.Type.ENTITY);

    public final net.minecraft.world.phys.HitResult.Type data;

    HitResultType(net.minecraft.world.phys.HitResult.Type data) {
        this.data = data;
    }

    public static HitResultType convert(net.minecraft.world.phys.HitResult.Type data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.HitResultType p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.HitResultType getMissMapped() {
        return cn.jstxjf_.rte.mapping.holder.HitResultType.convert(net.minecraft.world.phys.HitResult.Type.MISS);
    }

    public static cn.jstxjf_.rte.mapping.holder.HitResultType getBlockMapped() {
        return cn.jstxjf_.rte.mapping.holder.HitResultType.convert(net.minecraft.world.phys.HitResult.Type.BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.HitResultType getEntityMapped() {
        return cn.jstxjf_.rte.mapping.holder.HitResultType.convert(net.minecraft.world.phys.HitResult.Type.ENTITY);
    }
}
