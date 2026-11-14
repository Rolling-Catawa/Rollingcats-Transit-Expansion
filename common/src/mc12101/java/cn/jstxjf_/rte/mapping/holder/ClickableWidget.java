package cn.jstxjf_.rte.mapping.holder;

public final class ClickableWidget extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.gui.components.AbstractWidget> {

    public ClickableWidget(net.minecraft.client.gui.components.AbstractWidget p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ClickableWidget cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ClickableWidget((net.minecraft.client.gui.components.AbstractWidget) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.gui.components.AbstractWidget;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean mouseReleased(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).mouseReleased(p0, p1, p2);
    }

    public int getHeight() {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).getHeight();
    }

    public int getWidth() {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).getWidth();
    }

    public void playDownSound(cn.jstxjf_.rte.mapping.holder.SoundManager p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).playDownSound(((net.minecraft.client.sounds.SoundManager) p0.data));
    }

    public boolean keyReleased(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).keyReleased(p0, p1, p2);
    }

    public void setWidth(int p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).setWidth(p0);
    }

    public void setMessage(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).setMessage(((net.minecraft.network.chat.Component) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getMessage() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.gui.components.AbstractWidget) this.data).getMessage());
    }

    public boolean charTyped(char p0, int p1) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).charTyped(p0, p1);
    }

    public void onRelease(double p0, double p1) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).onRelease(p0, p1);
    }

    public boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).mouseDragged(p0, p1, p2, p3, p4);
    }

    public void onClick(double p0, double p1) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).onClick(p0, p1);
    }

    public boolean keyPressed(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).keyPressed(p0, p1, p2);
    }

    public void mouseMoved(double p0, double p1) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).mouseMoved(p0, p1);
    }

    public boolean mouseClicked(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).mouseClicked(p0, p1, p2);
    }

    public boolean isFocused() {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).isFocused();
    }

    public void setAlpha(float p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).setAlpha(p0);
    }

    public boolean isMouseOver(double p0, double p1) {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).isMouseOver(p0, p1);
    }

    public boolean getActiveMapped() {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).active;
    }

    public void setActiveMapped(boolean p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).active = p0;
    }

    public boolean getVisibleMapped() {
        return ((net.minecraft.client.gui.components.AbstractWidget) this.data).visible;
    }

    public void setVisibleMapped(boolean p0) {
        ((net.minecraft.client.gui.components.AbstractWidget) this.data).visible = p0;
    }
}
