package cn.jstxjf_.rte.mapping.holder;

public final class Tessellator extends cn.jstxjf_.rte.mapping.tool.HolderBase<com.mojang.blaze3d.vertex.Tesselator> {

    public Tessellator(com.mojang.blaze3d.vertex.Tesselator p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Tessellator cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Tessellator((com.mojang.blaze3d.vertex.Tesselator) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof com.mojang.blaze3d.vertex.Tesselator;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public Tessellator(int p0) {
        super(new com.mojang.blaze3d.vertex.Tesselator(p0));
    }

    public Tessellator() {
        super(new com.mojang.blaze3d.vertex.Tesselator());
    }

    public static cn.jstxjf_.rte.mapping.holder.Tessellator getInstance() {
        return new cn.jstxjf_.rte.mapping.holder.Tessellator(com.mojang.blaze3d.vertex.Tesselator.getInstance());
    }
}
