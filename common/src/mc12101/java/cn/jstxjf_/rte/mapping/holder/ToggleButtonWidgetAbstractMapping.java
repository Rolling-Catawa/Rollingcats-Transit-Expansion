package cn.jstxjf_.rte.mapping.holder;

public abstract class ToggleButtonWidgetAbstractMapping extends net.minecraft.client.gui.components.StateSwitchingButton {

    public boolean mouseReleased2(double p0, double p1, int p2) {
        return super.mouseReleased(p0, p1, p2);
    }

    public final boolean mouseReleased(double p0, double p1, int p2) {
        return this.mouseReleased2(p0, p1, p2);
    }

    public int getHeight2() {
        return super.getHeight();
    }

    public final int getHeight() {
        return this.getHeight2();
    }

    public int getWidth2() {
        return super.getWidth();
    }

    public final int getWidth() {
        return this.getWidth2();
    }

    public void playDownSound2(cn.jstxjf_.rte.mapping.holder.SoundManager p0) {
        super.playDownSound(((net.minecraft.client.sounds.SoundManager) p0.data));
    }

    public final void playDownSound(net.minecraft.client.sounds.SoundManager p0) {
        this.playDownSound2(new cn.jstxjf_.rte.mapping.holder.SoundManager(p0));
    }

    public boolean keyReleased2(int p0, int p1, int p2) {
        return super.keyReleased(p0, p1, p2);
    }

    public final boolean keyReleased(int p0, int p1, int p2) {
        return this.keyReleased2(p0, p1, p2);
    }

    public void setWidth2(int p0) {
        super.setWidth(p0);
    }

    public final void setWidth(int p0) {
        this.setWidth2(p0);
    }

    public void setMessage2(cn.jstxjf_.rte.mapping.holder.Text p0) {
        super.setMessage(((net.minecraft.network.chat.Component) p0.data));
    }

    public final void setMessage(net.minecraft.network.chat.Component p0) {
        this.setMessage2(new cn.jstxjf_.rte.mapping.holder.Text(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getMessage2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getMessage());
    }

    public final net.minecraft.network.chat.Component getMessage() {
        final var v0 = this.getMessage2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public boolean charTyped2(char p0, int p1) {
        return super.charTyped(p0, p1);
    }

    public final boolean charTyped(char p0, int p1) {
        return this.charTyped2(p0, p1);
    }

    public void onRelease2(double p0, double p1) {
        super.onRelease(p0, p1);
    }

    public final void onRelease(double p0, double p1) {
        this.onRelease2(p0, p1);
    }

    public boolean mouseDragged2(double p0, double p1, int p2, double p3, double p4) {
        return super.mouseDragged(p0, p1, p2, p3, p4);
    }

    public final boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return this.mouseDragged2(p0, p1, p2, p3, p4);
    }

    public void onClick2(double p0, double p1) {
        super.onClick(p0, p1);
    }

    public final void onClick(double p0, double p1) {
        this.onClick2(p0, p1);
    }

    public boolean keyPressed2(int p0, int p1, int p2) {
        return super.keyPressed(p0, p1, p2);
    }

    public final boolean keyPressed(int p0, int p1, int p2) {
        return this.keyPressed2(p0, p1, p2);
    }

    public void mouseMoved2(double p0, double p1) {
        super.mouseMoved(p0, p1);
    }

    public final void mouseMoved(double p0, double p1) {
        this.mouseMoved2(p0, p1);
    }

    public boolean mouseClicked2(double p0, double p1, int p2) {
        return super.mouseClicked(p0, p1, p2);
    }

    public final boolean mouseClicked(double p0, double p1, int p2) {
        return this.mouseClicked2(p0, p1, p2);
    }

    public boolean isFocused2() {
        return super.isFocused();
    }

    public final boolean isFocused() {
        return this.isFocused2();
    }

    public ToggleButtonWidgetAbstractMapping(int p0, int p1, int p2, int p3, boolean p4) {
        super(p0, p1, p2, p3, p4);
    }

    public void setAlpha2(float p0) {
        super.setAlpha(p0);
    }

    public final void setAlpha(float p0) {
        this.setAlpha2(p0);
    }

    public boolean isMouseOver2(double p0, double p1) {
        return super.isMouseOver(p0, p1);
    }

    public final boolean isMouseOver(double p0, double p1) {
        return this.isMouseOver2(p0, p1);
    }

    public boolean getActiveMapped() {
        return this.active;
    }

    public void setActiveMapped(boolean p0) {
        this.active = p0;
    }

    public boolean getToggledMapped() {
        return this.isStateTriggered;
    }

    public void setToggledMapped(boolean p0) {
        this.isStateTriggered = p0;
    }

    public boolean getVisibleMapped() {
        return this.visible;
    }

    public void setVisibleMapped(boolean p0) {
        this.visible = p0;
    }
}
