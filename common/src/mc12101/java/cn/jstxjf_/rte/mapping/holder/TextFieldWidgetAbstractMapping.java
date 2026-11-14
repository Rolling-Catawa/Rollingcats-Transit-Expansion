package cn.jstxjf_.rte.mapping.holder;

public abstract class TextFieldWidgetAbstractMapping extends net.minecraft.client.gui.components.EditBox {

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

    public void setDrawsBackground2(boolean p0) {
        super.setBordered(p0);
    }

    public final void setBordered(boolean p0) {
        this.setDrawsBackground2(p0);
    }

    public void setSelectionEnd2(int p0) {
        super.setHighlightPos(p0);
    }

    public final void setHighlightPos(int p0) {
        this.setSelectionEnd2(p0);
    }

    public void write2(java.lang.String p0) {
        super.insertText(p0);
    }

    public final void insertText(java.lang.String p0) {
        this.write2(p0);
    }

    protected cn.jstxjf_.rte.mapping.holder.MutableText getNarrationMessage2() {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(super.createNarrationMessage());
    }

    protected final net.minecraft.network.chat.MutableComponent createNarrationMessage() {
        final var v0 = this.getNarrationMessage2();
        return (v0 == null ? null : ((net.minecraft.network.chat.MutableComponent) v0.data));
    }

    public void setSelectionStart2(int p0) {
        super.setCursorPosition(p0);
    }

    public final void setCursorPosition(int p0) {
        this.setSelectionStart2(p0);
    }

    public void onRelease2(double p0, double p1) {
        super.onRelease(p0, p1);
    }

    public final void onRelease(double p0, double p1) {
        this.onRelease2(p0, p1);
    }

    public TextFieldWidgetAbstractMapping(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, int p3, int p4, cn.jstxjf_.rte.mapping.holder.TextFieldWidget p5, cn.jstxjf_.rte.mapping.holder.Text p6) {
        super(((net.minecraft.client.gui.Font) p0.data), p1, p2, p3, p4, (p5 == null ? null : ((net.minecraft.client.gui.components.EditBox) p5.data)), ((net.minecraft.network.chat.Component) p6.data));
    }

    public TextFieldWidgetAbstractMapping(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, int p3, int p4, cn.jstxjf_.rte.mapping.holder.Text p5) {
        super(((net.minecraft.client.gui.Font) p0.data), p1, p2, p3, p4, ((net.minecraft.network.chat.Component) p5.data));
    }

    public TextFieldWidgetAbstractMapping(cn.jstxjf_.rte.mapping.holder.TextRenderer p0, int p1, int p2, cn.jstxjf_.rte.mapping.holder.Text p3) {
        super(((net.minecraft.client.gui.Font) p0.data), p1, p2, ((net.minecraft.network.chat.Component) p3.data));
    }

    public int getWordSkipPosition2(int p0) {
        return super.getWordPosition(p0);
    }

    public final int getWordPosition(int p0) {
        return this.getWordSkipPosition2(p0);
    }

    public void mouseMoved2(double p0, double p1) {
        super.mouseMoved(p0, p1);
    }

    public final void mouseMoved(double p0, double p1) {
        this.mouseMoved2(p0, p1);
    }

    public void setChangedListener2(java.util.function.Consumer<java.lang.String> p0) {
        super.setResponder(p0);
    }

    public final void setResponder(java.util.function.Consumer<java.lang.String> p0) {
        this.setChangedListener2(p0);
    }

    public boolean mouseClicked2(double p0, double p1, int p2) {
        return super.mouseClicked(p0, p1, p2);
    }

    public final boolean mouseClicked(double p0, double p1, int p2) {
        return this.mouseClicked2(p0, p1, p2);
    }

    public boolean charTyped2(char p0, int p1) {
        return super.charTyped(p0, p1);
    }

    public final boolean charTyped(char p0, int p1) {
        return this.charTyped2(p0, p1);
    }

    public int getCharacterX2(int p0) {
        return super.getScreenX(p0);
    }

    public final int getScreenX(int p0) {
        return this.getCharacterX2(p0);
    }

    public boolean isFocused2() {
        return super.isFocused();
    }

    public final boolean isFocused() {
        return this.isFocused2();
    }

    public java.lang.String getText2() {
        return super.getValue();
    }

    public final java.lang.String getValue() {
        return this.getText2();
    }

    public void setAlpha2(float p0) {
        super.setAlpha(p0);
    }

    public final void setAlpha(float p0) {
        this.setAlpha2(p0);
    }

    public void eraseWords2(int p0) {
        super.deleteWords(p0);
    }

    public final void deleteWords(int p0) {
        this.eraseWords2(p0);
    }

    public void setUneditableColor2(int p0) {
        super.setTextColorUneditable(p0);
    }

    public final void setTextColorUneditable(int p0) {
        this.setUneditableColor2(p0);
    }

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

    public void setSuggestion2(java.lang.String p0) {
        super.setSuggestion(p0);
    }

    public final void setSuggestion(java.lang.String p0) {
        this.setSuggestion2(p0);
    }

    public void setTextPredicate2(java.util.function.Predicate<java.lang.String> p0) {
        super.setFilter(p0);
    }

    public final void setFilter(java.util.function.Predicate<java.lang.String> p0) {
        this.setTextPredicate2(p0);
    }

    public void setEditable2(boolean p0) {
        super.setEditable(p0);
    }

    public final void setEditable(boolean p0) {
        this.setEditable2(p0);
    }

    public void setText2(java.lang.String p0) {
        super.setValue(p0);
    }

    public final void setValue(java.lang.String p0) {
        this.setText2(p0);
    }

    public void setEditableColor2(int p0) {
        super.setTextColor(p0);
    }

    public final void setTextColor(int p0) {
        this.setEditableColor2(p0);
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

    public int getInnerWidth2() {
        return super.getInnerWidth();
    }

    public final int getInnerWidth() {
        return this.getInnerWidth2();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getMessage2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getMessage());
    }

    public final net.minecraft.network.chat.Component getMessage() {
        final var v0 = this.getMessage2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public void setFocusUnlocked2(boolean p0) {
        super.setCanLoseFocus(p0);
    }

    public final void setCanLoseFocus(boolean p0) {
        this.setFocusUnlocked2(p0);
    }

    public int getCursor2() {
        return super.getCursorPosition();
    }

    public final int getCursorPosition() {
        return this.getCursor2();
    }

    public void onClick2(double p0, double p1) {
        super.onClick(p0, p1);
    }

    public final void onClick(double p0, double p1) {
        this.onClick2(p0, p1);
    }

    public java.lang.String getSelectedText2() {
        return super.getHighlighted();
    }

    public final java.lang.String getHighlighted() {
        return this.getSelectedText2();
    }

    public boolean mouseDragged2(double p0, double p1, int p2, double p3, double p4) {
        return super.mouseDragged(p0, p1, p2, p3, p4);
    }

    public final boolean mouseDragged(double p0, double p1, int p2, double p3, double p4) {
        return this.mouseDragged2(p0, p1, p2, p3, p4);
    }

    public void setX2(int p0) {
        super.setX(p0);
    }

    public final void setX(int p0) {
        this.setX2(p0);
    }

    public void eraseCharacters2(int p0) {
        super.deleteChars(p0);
    }

    public final void deleteChars(int p0) {
        this.eraseCharacters2(p0);
    }

    public void setMaxLength2(int p0) {
        super.setMaxLength(p0);
    }

    public final void setMaxLength(int p0) {
        this.setMaxLength2(p0);
    }

    public void setTextFieldFocused2(boolean p0) {
        super.setFocused(p0);
    }

    public final void setFocused(boolean p0) {
        this.setTextFieldFocused2(p0);
    }

    public boolean keyPressed2(int p0, int p1, int p2) {
        return super.keyPressed(p0, p1, p2);
    }

    public final boolean keyPressed(int p0, int p1, int p2) {
        return this.keyPressed2(p0, p1, p2);
    }

    public void setVisible2(boolean p0) {
        super.setVisible(p0);
    }

    public final void setVisible(boolean p0) {
        this.setVisible2(p0);
    }

    public boolean isVisible2() {
        return super.isVisible();
    }

    public final boolean isVisible() {
        return this.isVisible2();
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

    public boolean getVisibleMapped() {
        return this.visible;
    }

    public void setVisibleMapped(boolean p0) {
        this.visible = p0;
    }
}
