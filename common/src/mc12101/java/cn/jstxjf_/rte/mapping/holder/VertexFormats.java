package cn.jstxjf_.rte.mapping.holder;

public final class VertexFormats extends cn.jstxjf_.rte.mapping.tool.HolderBase<com.mojang.blaze3d.vertex.DefaultVertexFormat> {

    public VertexFormats(com.mojang.blaze3d.vertex.DefaultVertexFormat p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormats cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new VertexFormats((com.mojang.blaze3d.vertex.DefaultVertexFormat) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof com.mojang.blaze3d.vertex.DefaultVertexFormat;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionTextureColorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionColorLightMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionTextureColorNormalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionColorTextureLightMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionColorTextureLightNormalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionColorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionColorTextureOverlayLightNormalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionTextureMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionTextureColorLightMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.VertexFormat getPositionTextureLightColorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);
    }
}
