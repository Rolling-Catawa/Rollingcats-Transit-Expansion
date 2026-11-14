package cn.jstxjf_.rte.mapping.holder;

public final class BlockEntityType<T extends cn.jstxjf_.rte.mapping.holder.BlockEntityAbstractMapping> extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.entity.BlockEntityType<T>> {

    public BlockEntityType(net.minecraft.world.level.block.entity.BlockEntityType<T> p0) {
        super(p0);
    }

    public static <T extends org.mtr.mapping.holder.BlockEntityAbstractMapping> cn.jstxjf_.rte.mapping.holder.BlockEntityType<T> cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockEntityType<>((net.minecraft.world.level.block.entity.BlockEntityType<T>) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.entity.BlockEntityType;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier getId(cn.jstxjf_.rte.mapping.holder.BlockEntityType<?> p0) {
        final var v0 = net.minecraft.world.level.block.entity.BlockEntityType.getKey(((net.minecraft.world.level.block.entity.BlockEntityType) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Identifier(v0));
    }

    public BlockEntityType(net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier<? extends T> p0, java.util.Set<net.minecraft.world.level.block.Block> p1, com.mojang.datafixers.types.Type<?> p2) {
        super(new net.minecraft.world.level.block.entity.BlockEntityType(p0, p1, p2));
    }
}
