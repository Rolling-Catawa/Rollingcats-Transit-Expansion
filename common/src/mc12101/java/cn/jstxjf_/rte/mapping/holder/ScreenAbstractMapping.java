package cn.jstxjf_.rte.mapping.holder;

public abstract class ScreenAbstractMapping extends net.minecraft.client.gui.screens.Screen {

    public static boolean hasShiftDown2() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    public boolean mouseDragged2(double p0, double p1, int p2, double p3, double p4) {
        return super.mouseDragged(p0, p1, p2, p3, p4);
    }

    public final boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return this.mouseDragged2(p0, p1, p2, p3, p4);
    }

    public boolean keyPressed2(int p0, int p1, int p2) {
        return super.keyPressed(p0, p1, p2);
    }

    public final boolean keyPressed(int p0, int p1, int p2) {
        return this.keyPressed2(p0, p1, p2);
    }

    public void tick2() {
        super.tick();
    }

    public final void tick() {
        this.tick2();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getTitle2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getTitle());
    }

    public final net.minecraft.network.chat.Component getTitle() {
        final var v0 = this.getTitle2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public final void setDragging2(boolean p0) {
        super.setDragging(p0);
    }

    public boolean isPauseScreen2() {
        return super.isPauseScreen();
    }

    public final boolean isPauseScreen() {
        return this.isPauseScreen2();
    }

    public boolean charTyped2(char p0, int p1) {
        return super.charTyped(p0, p1);
    }

    public final boolean charTyped(char p0, int p1) {
        return this.charTyped2(p0, p1);
    }

    public void filesDragged2(java.util.List<java.nio.file.Path> p0) {
        super.onFilesDrop(p0);
    }

    public final void onFilesDrop(java.util.List<java.nio.file.Path> p0) {
        this.filesDragged2(p0);
    }

    public void resize2(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, int p1, int p2) {
        super.resize(((net.minecraft.client.Minecraft) p0.data), p1, p2);
    }

    public final void resize(net.minecraft.client.Minecraft p0, int p1, int p2) {
        this.resize2(new cn.jstxjf_.rte.mapping.holder.MinecraftClient(p0), p1, p2);
    }

    public boolean mouseReleased2(double p0, double p1, int p2) {
        return super.mouseReleased(p0, p1, p2);
    }

    public final boolean mouseReleased(double p0, double p1, int p2) {
        return this.mouseReleased2(p0, p1, p2);
    }

    public boolean keyReleased2(int p0, int p1, int p2) {
        return super.keyReleased(p0, p1, p2);
    }

    public final boolean keyReleased(int p0, int p1, int p2) {
        return this.keyReleased2(p0, p1, p2);
    }

    public void mouseMoved2(double p0, double p1) {
        super.mouseMoved(p0, p1);
    }

    public final void mouseMoved(double p0, double p1) {
        this.mouseMoved2(p0, p1);
    }

    public static void wrapScreenError2(java.lang.Runnable p0, java.lang.String p1, java.lang.String p2) {
        net.minecraft.client.gui.screens.Screen.wrapScreenError(p0, p1, p2);
    }

    public static boolean hasAltDown2() {
        return net.minecraft.client.gui.screens.Screen.hasAltDown();
    }

    public static boolean isPaste2(int p0) {
        return net.minecraft.client.gui.screens.Screen.isPaste(p0);
    }

    public final boolean isDragging2() {
        return super.isDragging();
    }

    public int getNavigationOrder2() {
        return super.getTabOrderGroup();
    }

    public final int getTabOrderGroup() {
        return this.getNavigationOrder2();
    }

    public boolean isMouseOver2(double p0, double p1) {
        return super.isMouseOver(p0, p1);
    }

    public final boolean isMouseOver(double p0, double p1) {
        return this.isMouseOver2(p0, p1);
    }

    protected void init2() {
        super.init();
    }

    protected final void init() {
        this.init2();
    }

    protected void insertText2(java.lang.String p0, boolean p1) {
        super.insertText(p0, p1);
    }

    protected final void insertText(java.lang.String p0, boolean p1) {
        this.insertText2(p0, p1);
    }

    public static boolean hasControlDown2() {
        return net.minecraft.client.gui.screens.Screen.hasControlDown();
    }

    public boolean shouldCloseOnEsc2() {
        return super.shouldCloseOnEsc();
    }

    public final boolean shouldCloseOnEsc() {
        return this.shouldCloseOnEsc2();
    }

    public static boolean isCopy2(int p0) {
        return net.minecraft.client.gui.screens.Screen.isCopy(p0);
    }

    public ScreenAbstractMapping(cn.jstxjf_.rte.mapping.holder.Text p0) {
        super(((net.minecraft.network.chat.Component) p0.data));
    }

    public boolean mouseClicked2(double p0, double p1, int p2) {
        return super.mouseClicked(p0, p1, p2);
    }

    public final boolean mouseClicked(double p0, double p1, int p2) {
        return this.mouseClicked2(p0, p1, p2);
    }

    public static boolean isSelectAll2(int p0) {
        return net.minecraft.client.gui.screens.Screen.isSelectAll(p0);
    }

    public void removed2() {
        super.removed();
    }

    public final void removed() {
        this.removed2();
    }

    protected boolean isValidCharacterForName2(java.lang.String p0, char p1, int p2) {
        return super.isValidCharacterForName(p0, p1, p2);
    }

    protected final boolean isValidCharacterForName(java.lang.String p0, char p1, int p2) {
        return this.isValidCharacterForName2(p0, p1, p2);
    }

    public void onClose2() {
        super.onClose();
    }

    public final void onClose() {
        this.onClose2();
    }

    public static boolean isCut2(int p0) {
        return net.minecraft.client.gui.screens.Screen.isCut(p0);
    }

    public boolean handleTextClick2(cn.jstxjf_.rte.mapping.holder.Style p0) {
        return super.handleComponentClicked((p0 == null ? null : ((net.minecraft.network.chat.Style) p0.data)));
    }

    public final boolean handleComponentClicked(net.minecraft.network.chat.Style p0) {
        return this.handleTextClick2((p0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Style(p0)));
    }

    public int getWidthMapped() {
        return this.width;
    }

    public void setWidthMapped(int p0) {
        this.width = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.TextRenderer getTextRendererMapped() {
        return new cn.jstxjf_.rte.mapping.holder.TextRenderer(this.font);
    }

    public void setTextRendererMapped(cn.jstxjf_.rte.mapping.holder.TextRenderer p0) {
        this.font = ((net.minecraft.client.gui.Font) p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftClient getClientMapped() {
        return (this.minecraft == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftClient(this.minecraft));
    }

    public void setClientMapped(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0) {
        this.minecraft = (p0 == null ? null : ((net.minecraft.client.Minecraft) p0.data));
    }

    public int getHeightMapped() {
        return this.height;
    }

    public void setHeightMapped(int p0) {
        this.height = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.Text getTitleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Text(this.title);
    }
}
