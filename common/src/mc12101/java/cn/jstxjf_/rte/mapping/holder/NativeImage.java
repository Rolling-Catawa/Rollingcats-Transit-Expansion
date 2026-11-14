package cn.jstxjf_.rte.mapping.holder;

public final class NativeImage extends cn.jstxjf_.rte.mapping.tool.HolderBase<com.mojang.blaze3d.platform.NativeImage> {

    public NativeImage(com.mojang.blaze3d.platform.NativeImage p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImage cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new NativeImage((com.mojang.blaze3d.platform.NativeImage) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof com.mojang.blaze3d.platform.NativeImage;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setPixelColor(int p0, int p1, int p2) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).setPixelRGBA(p0, p1, p2);
    }

    public void copyFrom(cn.jstxjf_.rte.mapping.holder.NativeImage p0) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).copyFrom(((com.mojang.blaze3d.platform.NativeImage) p0.data));
    }

    public int getColor(int p0, int p1) {
        return ((com.mojang.blaze3d.platform.NativeImage) this.data).getPixelRGBA(p0, p1);
    }

    public void untrack() {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).untrack();
    }

    public int getHeight() {
        return ((com.mojang.blaze3d.platform.NativeImage) this.data).getHeight();
    }

    public void close() {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).close();
    }

    public void resizeSubRectTo(int p0, int p1, int p2, int p3, cn.jstxjf_.rte.mapping.holder.NativeImage p4) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).resizeSubRectTo(p0, p1, p2, p3, ((com.mojang.blaze3d.platform.NativeImage) p4.data));
    }

    public int getWidth() {
        return ((com.mojang.blaze3d.platform.NativeImage) this.data).getWidth();
    }

    public void writeTo(java.nio.file.Path p0) throws java.io.IOException {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).writeToFile(p0);
    }

    public void writeTo(java.io.File p0) throws java.io.IOException {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).writeToFile(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImage read(cn.jstxjf_.rte.mapping.holder.NativeImageFormat p0, java.io.InputStream p1) throws java.io.IOException {
        return new cn.jstxjf_.rte.mapping.holder.NativeImage(com.mojang.blaze3d.platform.NativeImage.read((p0 == null ? null : p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImage read(java.io.InputStream p0) throws java.io.IOException {
        return new cn.jstxjf_.rte.mapping.holder.NativeImage(com.mojang.blaze3d.platform.NativeImage.read(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImage read(java.nio.ByteBuffer p0) throws java.io.IOException {
        return new cn.jstxjf_.rte.mapping.holder.NativeImage(com.mojang.blaze3d.platform.NativeImage.read(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImage read(cn.jstxjf_.rte.mapping.holder.NativeImageFormat p0, java.nio.ByteBuffer p1) throws java.io.IOException {
        return new cn.jstxjf_.rte.mapping.holder.NativeImage(com.mojang.blaze3d.platform.NativeImage.read((p0 == null ? null : p0.data), p1));
    }

    public void copyRect(int p0, int p1, int p2, int p3, int p4, int p5, boolean p6, boolean p7) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).copyRect(p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public cn.jstxjf_.rte.mapping.holder.NativeImageFormat getFormat() {
        return cn.jstxjf_.rte.mapping.holder.NativeImageFormat.convert(((com.mojang.blaze3d.platform.NativeImage) this.data).format());
    }

    public void loadFromTextureImage(int p0, boolean p1) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).downloadTexture(p0, p1);
    }

    public void upload(int p0, int p1, int p2, int p3, int p4, int p5, int p6, boolean p7, boolean p8, boolean p9, boolean p10) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).upload(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }

    public void upload(int p0, int p1, int p2, boolean p3) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).upload(p0, p1, p2, p3);
    }

    public void upload(int p0, int p1, int p2, int p3, int p4, int p5, int p6, boolean p7, boolean p8) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).upload(p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public NativeImage(cn.jstxjf_.rte.mapping.holder.NativeImageFormat p0, int p1, int p2, boolean p3) {
        super(new com.mojang.blaze3d.platform.NativeImage(p0.data, p1, p2, p3));
    }

    public NativeImage(int p0, int p1, boolean p2) {
        super(new com.mojang.blaze3d.platform.NativeImage(p0, p1, p2));
    }

    public byte[] getBytes() throws java.io.IOException {
        return ((com.mojang.blaze3d.platform.NativeImage) this.data).asByteArray();
    }

    public void fillRect(int p0, int p1, int p2, int p3, int p4) {
        ((com.mojang.blaze3d.platform.NativeImage) this.data).fillRect(p0, p1, p2, p3, p4);
    }

    public int[] makePixelArray() {
        return ((com.mojang.blaze3d.platform.NativeImage) this.data).makePixelArray();
    }
}
