package cn.jstxjf_.rte.mapping.holder;

public final class TextFieldWidget extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.gui.components.EditBox> {

    public TextFieldWidget(net.minecraft.client.gui.components.EditBox p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFieldWidget cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new TextFieldWidget((net.minecraft.client.gui.components.EditBox) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.gui.components.EditBox;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void playDownSound(cn.jstxjf_.rte.mapping.holder.SoundManager p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).playDownSound(((net.minecraft.client.sounds.SoundManager) p0.data));
    }

    public boolean keyReleased(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).keyReleased(p0, p1, p2);
    }

    public void setDrawsBackground(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setBordered(p0);
    }

    public void setSelectionEnd(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setHighlightPos(p0);
    }

    public void write(java.lang.String p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).insertText(p0);
    }

    public void setSelectionStart(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setCursorPosition(p0);
    }

    public void onRelease(double p0, double p1) {
        ((net.minecraft.client.gui.components.EditBox) this.data).onRelease(p0, p1);
    }

    public TextFieldWidget(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, int p3, int p4, cn.jstxjf_.rte.mapping.holder.TextFieldWidget p5, cn.jstxjf_.rte.mapping.holder.Text p6) {
        super(new net.minecraft.client.gui.components.EditBox(((net.minecraft.client.gui.Font) p0.data), p1, p2, p3, p4, (p5 == null ? null : ((net.minecraft.client.gui.components.EditBox) p5.data)), ((net.minecraft.network.chat.Component) p6.data)));
    }

    public TextFieldWidget(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, int p3, int p4, cn.jstxjf_.rte.mapping.holder.Text p5) {
        super(new net.minecraft.client.gui.components.EditBox(((net.minecraft.client.gui.Font) p0.data), p1, p2, p3, p4, ((net.minecraft.network.chat.Component) p5.data)));
    }

    public TextFieldWidget(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, cn.jstxjf_.rte.mapping.holder.Text p3) {
        super(new net.minecraft.client.gui.components.EditBox(((net.minecraft.client.gui.Font) p0.data), p1, p2, ((net.minecraft.network.chat.Component) p3.data)));
    }

    public int getWordSkipPosition(int p0) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getWordPosition(p0);
    }

    public void mouseMoved(double p0, double p1) {
        ((net.minecraft.client.gui.components.EditBox) this.data).mouseMoved(p0, p1);
    }

    public void setChangedListener(java.util.function.Consumer<java.lang.String> p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setResponder(p0);
    }

    public boolean mouseClicked(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).mouseClicked(p0, p1, p2);
    }

    public boolean charTyped(char p0, int p1) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).charTyped(p0, p1);
    }

    public int getCharacterX(int p0) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getScreenX(p0);
    }

    public boolean isFocused() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).isFocused();
    }

    public java.lang.String getText() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getValue();
    }

    public void setAlpha(float p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setAlpha(p0);
    }

    public void eraseWords(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).deleteWords(p0);
    }

    public void setUneditableColor(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setTextColorUneditable(p0);
    }

    public boolean mouseReleased(double p0, double p1, int p2) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).mouseReleased(p0, p1, p2);
    }

    public int getHeight() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getHeight();
    }

    public int getWidth() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getWidth();
    }

    public void setSuggestion(java.lang.String p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setSuggestion(p0);
    }

    public void setTextPredicate(java.util.function.Predicate<java.lang.String> p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setFilter(p0);
    }

    public void setEditable(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setEditable(p0);
    }

    public void setText(java.lang.String p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setValue(p0);
    }

    public void setEditableColor(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setTextColor(p0);
    }

    public void setWidth(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setWidth(p0);
    }

    public void setMessage(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setMessage(((net.minecraft.network.chat.Component) p0.data));
    }

    public int getInnerWidth() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getInnerWidth();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getMessage() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.gui.components.EditBox) this.data).getMessage());
    }

    public void setFocusUnlocked(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setCanLoseFocus(p0);
    }

    public int getCursor() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getCursorPosition();
    }

    public void onClick(double p0, double p1) {
        ((net.minecraft.client.gui.components.EditBox) this.data).onClick(p0, p1);
    }

    public java.lang.String getSelectedText() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).getHighlighted();
    }

    public boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).mouseDragged(p0, p1, p2, p3, p4);
    }

    public void setX(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setX(p0);
    }

    public void eraseCharacters(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).deleteChars(p0);
    }

    public void setMaxLength(int p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setMaxLength(p0);
    }

    public void setTextFieldFocused(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setFocused(p0);
    }

    public boolean keyPressed(int p0, int p1, int p2) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).keyPressed(p0, p1, p2);
    }

    public void setVisible(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).setVisible(p0);
    }

    public boolean isVisible() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).isVisible();
    }

    public boolean isMouseOver(double p0, double p1) {
        return ((net.minecraft.client.gui.components.EditBox) this.data).isMouseOver(p0, p1);
    }

    public boolean getActiveMapped() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).active;
    }

    public void setActiveMapped(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).active = p0;
    }

    public boolean getVisibleMapped() {
        return ((net.minecraft.client.gui.components.EditBox) this.data).visible;
    }

    public void setVisibleMapped(boolean p0) {
        ((net.minecraft.client.gui.components.EditBox) this.data).visible = p0;
    }
}
