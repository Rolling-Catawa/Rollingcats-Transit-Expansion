package cn.jstxjf_.rte.mapping.holder;

public final class Window extends cn.jstxjf_.rte.mapping.tool.HolderBase<com.mojang.blaze3d.platform.Window> {

    public Window(com.mojang.blaze3d.platform.Window p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Window cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Window((com.mojang.blaze3d.platform.Window) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof com.mojang.blaze3d.platform.Window;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setFramerateLimit(int p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).setFramerateLimit(p0);
    }

    public double getScaleFactor() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getGuiScale();
    }

    public int getFramerateLimit() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getFramerateLimit();
    }

    public void setPhase(java.lang.String p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).setErrorSection(p0);
    }

    public void setTitle(java.lang.String p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).setTitle(p0);
    }

    public void setVsync(boolean p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).updateVsync(p0);
    }

    public void toggleFullscreen() {
        ((com.mojang.blaze3d.platform.Window) this.data).toggleFullScreen();
    }

    public int calculateScaleFactor(int p0, boolean p1) {
        return ((com.mojang.blaze3d.platform.Window) this.data).calculateScale(p0, p1);
    }

    public int getFramebufferHeight() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getHeight();
    }

    public Window(com.mojang.blaze3d.platform.WindowEventHandler p0, com.mojang.blaze3d.platform.ScreenManager p1, com.mojang.blaze3d.platform.DisplayData p2, java.lang.String p3, java.lang.String p4) {
        super(new com.mojang.blaze3d.platform.Window(p0, p1, p2, p3, p4));
    }

    public void swapBuffers() {
        ((com.mojang.blaze3d.platform.Window) this.data).updateDisplay();
    }

    public int getRefreshRate() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getRefreshRate();
    }

    public long getHandle() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getWindow();
    }

    public int getScaledWidth() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getGuiScaledWidth();
    }

    public int getX() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getX();
    }

    public int getY() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getY();
    }

    public boolean isFullscreen() {
        return ((com.mojang.blaze3d.platform.Window) this.data).isFullscreen();
    }

    public static void acceptError(java.util.function.BiConsumer<java.lang.Integer, java.lang.String> p0) {
        com.mojang.blaze3d.platform.Window.checkGlfwError(p0);
    }

    public void setScaleFactor(double p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).setGuiScale(p0);
    }

    public int getHeight() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getScreenHeight();
    }

    public int getScaledHeight() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getGuiScaledHeight();
    }

    public void logGlError(int p0, long p1) {
        ((com.mojang.blaze3d.platform.Window) this.data).defaultErrorCallback(p0, p1);
    }

    public boolean shouldClose() {
        return ((com.mojang.blaze3d.platform.Window) this.data).shouldClose();
    }

    public void applyVideoMode() {
        ((com.mojang.blaze3d.platform.Window) this.data).changeFullscreenVideoMode();
    }

    public void close() {
        ((com.mojang.blaze3d.platform.Window) this.data).close();
    }

    public void setRawMouseMotion(boolean p0) {
        ((com.mojang.blaze3d.platform.Window) this.data).updateRawMouseInput(p0);
    }

    public int getWidth() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getScreenWidth();
    }

    public int getFramebufferWidth() {
        return ((com.mojang.blaze3d.platform.Window) this.data).getWidth();
    }

    public void logOnGlError() {
        ((com.mojang.blaze3d.platform.Window) this.data).setDefaultErrorCallback();
    }
}
