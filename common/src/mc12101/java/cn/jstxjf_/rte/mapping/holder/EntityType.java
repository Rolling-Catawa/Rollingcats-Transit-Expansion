package cn.jstxjf_.rte.mapping.holder;

public final class EntityType<T extends cn.jstxjf_.rte.mapping.holder.EntityAbstractMapping> extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.entity.EntityType<T>> {

    public EntityType(net.minecraft.world.entity.EntityType<T> p0) {
        super(p0);
    }

    public static <T extends org.mtr.mapping.holder.EntityAbstractMapping> cn.jstxjf_.rte.mapping.holder.EntityType<T> cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new EntityType<>((net.minecraft.world.entity.EntityType<T>) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.entity.EntityType;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.EntityType) this.data).getDescription());
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier getId(cn.jstxjf_.rte.mapping.holder.EntityType<?> p0) {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(net.minecraft.world.entity.EntityType.getKey(((net.minecraft.world.entity.EntityType) p0.data)));
    }

    public float getWidth() {
        return ((net.minecraft.world.entity.EntityType) this.data).getWidth();
    }

    public float getHeight() {
        return ((net.minecraft.world.entity.EntityType) this.data).getHeight();
    }

    public boolean isInvalidSpawn(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.world.entity.EntityType) this.data).isBlockDangerous(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }
}
