package cn.jstxjf_.rte.mapping.holder;

public enum InternalFormat {
    RGBA(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat.RGBA),
    RGB(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat.RGB),
    RG(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat.RG),
    RED(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat.RED);

    public final com.mojang.blaze3d.platform.NativeImage.InternalGlFormat data;

    InternalFormat(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat data) {
        this.data = data;
    }

    public static InternalFormat convert(com.mojang.blaze3d.platform.NativeImage.InternalGlFormat data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.InternalFormat p0) {
        return this == p0;
    }
}
