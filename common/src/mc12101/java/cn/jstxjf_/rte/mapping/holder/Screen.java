package cn.jstxjf_.rte.mapping.holder;

public final class Screen extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.gui.screens.Screen> {

    public Screen(net.minecraft.client.gui.screens.Screen p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Screen cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Screen((net.minecraft.client.gui.screens.Screen) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.gui.screens.Screen;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static boolean hasShiftDown() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    public boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).mouseDragged(p0, p1, p2, p3, p4);
    }

    public boolean keyPressed(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).keyPressed(p0, p1, p2);
    }

    public void tick() {
        ((net.minecraft.client.gui.screens.Screen) this.data).tick();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getTitle() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.gui.screens.Screen) this.data).getTitle());
    }

    public final void setDragging(boolean p0) {
        ((net.minecraft.client.gui.screens.Screen) this.data).setDragging(p0);
    }

    public boolean isPauseScreen() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).isPauseScreen();
    }

    public boolean charTyped(char p0, int p1) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).charTyped(p0, p1);
    }

    public void filesDragged(java.util.List<java.nio.file.Path> p0) {
        ((net.minecraft.client.gui.screens.Screen) this.data).onFilesDrop(p0);
    }

    public void resize(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, int p1, int p2) {
        ((net.minecraft.client.gui.screens.Screen) this.data).resize(((net.minecraft.client.Minecraft) p0.data), p1, p2);
    }

    public boolean mouseReleased(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).mouseReleased(p0, p1, p2);
    }

    public boolean keyReleased(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).keyReleased(p0, p1, p2);
    }

    public void mouseMoved(double p0, double p1) {
        ((net.minecraft.client.gui.screens.Screen) this.data).mouseMoved(p0, p1);
    }

    public static void wrapScreenError(java.lang.Runnable p0, java.lang.String p1, java.lang.String p2) {
        net.minecraft.client.gui.screens.Screen.wrapScreenError(p0, p1, p2);
    }

    public static boolean hasAltDown() {
        return net.minecraft.client.gui.screens.Screen.hasAltDown();
    }

    public static boolean isPaste(int p0) {
        return net.minecraft.client.gui.screens.Screen.isPaste(p0);
    }

    public final boolean isDragging() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).isDragging();
    }

    public int getNavigationOrder() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).getTabOrderGroup();
    }

    public boolean isMouseOver(double p0, double p1) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).isMouseOver(p0, p1);
    }

    public static boolean hasControlDown() {
        return net.minecraft.client.gui.screens.Screen.hasControlDown();
    }

    public boolean shouldCloseOnEsc() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).shouldCloseOnEsc();
    }

    public static boolean isCopy(int p0) {
        return net.minecraft.client.gui.screens.Screen.isCopy(p0);
    }

    public boolean mouseClicked(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).mouseClicked(p0, p1, p2);
    }

    public static boolean isSelectAll(int p0) {
        return net.minecraft.client.gui.screens.Screen.isSelectAll(p0);
    }

    public void removed() {
        ((net.minecraft.client.gui.screens.Screen) this.data).removed();
    }

    public void onClose() {
        ((net.minecraft.client.gui.screens.Screen) this.data).onClose();
    }

    public static boolean isCut(int p0) {
        return net.minecraft.client.gui.screens.Screen.isCut(p0);
    }

    public boolean handleTextClick(cn.jstxjf_.rte.mapping.holder.Style p0) {
        return ((net.minecraft.client.gui.screens.Screen) this.data).handleComponentClicked((p0 == null ? null : ((net.minecraft.network.chat.Style) p0.data)));
    }

    public int getWidthMapped() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).width;
    }

    public void setWidthMapped(int p0) {
        ((net.minecraft.client.gui.screens.Screen) this.data).width = p0;
    }

    public int getHeightMapped() {
        return ((net.minecraft.client.gui.screens.Screen) this.data).height;
    }

    public void setHeightMapped(int p0) {
        ((net.minecraft.client.gui.screens.Screen) this.data).height = p0;
    }
}
