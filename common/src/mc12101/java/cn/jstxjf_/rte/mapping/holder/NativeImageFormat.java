package cn.jstxjf_.rte.mapping.holder;

public enum NativeImageFormat {
    RGBA(com.mojang.blaze3d.platform.NativeImage.Format.RGBA),
    RGB(com.mojang.blaze3d.platform.NativeImage.Format.RGB),
    LUMINANCE_ALPHA(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE_ALPHA),
    LUMINANCE(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE);

    public final com.mojang.blaze3d.platform.NativeImage.Format data;

    NativeImageFormat(com.mojang.blaze3d.platform.NativeImage.Format data) {
        this.data = data;
    }

    public static NativeImageFormat convert(com.mojang.blaze3d.platform.NativeImage.Format data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.NativeImageFormat p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImageFormat getLuminanceAlphaMapped() {
        return cn.jstxjf_.rte.mapping.holder.NativeImageFormat.convert(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE_ALPHA);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImageFormat getLuminanceMapped() {
        return cn.jstxjf_.rte.mapping.holder.NativeImageFormat.convert(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImageFormat getBgrMapped() {
        return cn.jstxjf_.rte.mapping.holder.NativeImageFormat.convert(com.mojang.blaze3d.platform.NativeImage.Format.RGB);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImageFormat getAbgrMapped() {
        return cn.jstxjf_.rte.mapping.holder.NativeImageFormat.convert(com.mojang.blaze3d.platform.NativeImage.Format.RGBA);
    }
}
