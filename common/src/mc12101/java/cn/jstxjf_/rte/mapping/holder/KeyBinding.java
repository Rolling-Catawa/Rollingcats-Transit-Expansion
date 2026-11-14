package cn.jstxjf_.rte.mapping.holder;

public final class KeyBinding extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.KeyMapping> {

    public KeyBinding(net.minecraft.client.KeyMapping p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.KeyBinding cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new KeyBinding((net.minecraft.client.KeyMapping) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.KeyMapping;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean matchesKey(int p0, int p1) {
        return ((net.minecraft.client.KeyMapping) this.data).matches(p0, p1);
    }

    public java.lang.String getCategory() {
        return ((net.minecraft.client.KeyMapping) this.data).getCategory();
    }

    public java.lang.String getTranslationKey() {
        return ((net.minecraft.client.KeyMapping) this.data).getName();
    }

    public void setPressed(boolean p0) {
        ((net.minecraft.client.KeyMapping) this.data).setDown(p0);
    }

    public boolean isPressed() {
        return ((net.minecraft.client.KeyMapping) this.data).isDown();
    }

    public boolean matchesMouse(int p0) {
        return ((net.minecraft.client.KeyMapping) this.data).matchesMouse(p0);
    }

    public java.lang.String getBoundKeyTranslationKey() {
        return ((net.minecraft.client.KeyMapping) this.data).saveString();
    }

    public boolean isDefault() {
        return ((net.minecraft.client.KeyMapping) this.data).isDefault();
    }

    public int compareTo(cn.jstxjf_.rte.mapping.holder.KeyBinding p0) {
        return ((net.minecraft.client.KeyMapping) this.data).compareTo(((net.minecraft.client.KeyMapping) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getBoundKeyLocalizedText() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.KeyMapping) this.data).getTranslatedKeyMessage());
    }

    public static void updatePressedStates() {
        net.minecraft.client.KeyMapping.setAll();
    }

    public KeyBinding(java.lang.String p0, com.mojang.blaze3d.platform.InputConstants.Type p1, int p2, java.lang.String p3) {
        super(new net.minecraft.client.KeyMapping(p0, p1, p2, p3));
    }

    public KeyBinding(java.lang.String p0, int p1, java.lang.String p2) {
        super(new net.minecraft.client.KeyMapping(p0, p1, p2));
    }

    public boolean wasPressed() {
        return ((net.minecraft.client.KeyMapping) this.data).consumeClick();
    }

    public boolean isUnbound() {
        return ((net.minecraft.client.KeyMapping) this.data).isUnbound();
    }

    public static void updateKeysByCode() {
        net.minecraft.client.KeyMapping.resetMapping();
    }

    public static void unpressAll() {
        net.minecraft.client.KeyMapping.releaseAll();
    }
}
