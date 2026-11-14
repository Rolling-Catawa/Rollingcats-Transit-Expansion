package cn.jstxjf_.rte.mapping.holder;

public final class GameOptions extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.Options> {

    public GameOptions(net.minecraft.client.Options p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameOptions cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new GameOptions((net.minecraft.client.Options) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.Options;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean shouldUseNativeTransport() {
        return ((net.minecraft.client.Options) this.data).useNativeTransport();
    }

    public int getTextBackgroundColor(float p0) {
        return ((net.minecraft.client.Options) this.data).getBackgroundColor(p0);
    }

    public int getTextBackgroundColor(int p0) {
        return ((net.minecraft.client.Options) this.data).getBackgroundColor(p0);
    }

    public float getTextBackgroundOpacity(float p0) {
        return ((net.minecraft.client.Options) this.data).getBackgroundOpacity(p0);
    }

    public void load() {
        ((net.minecraft.client.Options) this.data).load();
    }

    public GameOptions(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, java.io.File p1) {
        super(new net.minecraft.client.Options(((net.minecraft.client.Minecraft) p0.data), p1));
    }

    public int getOverrideHeightMapped() {
        return ((net.minecraft.client.Options) this.data).overrideHeight;
    }

    public void setOverrideHeightMapped(int p0) {
        ((net.minecraft.client.Options) this.data).overrideHeight = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyBackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyDown);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySmoothCameraMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySmoothCamera);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyCommandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyCommand);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyLoadToolbarActivatorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyLoadHotbarActivator);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyInventoryMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyInventory);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyLeftMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyLeft);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySpectatorOutlinesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySpectatorOutlines);
    }

    public java.util.List<java.lang.String> getResourcePacksMapped() {
        return ((net.minecraft.client.Options) this.data).resourcePacks;
    }

    public void setResourcePacksMapped(java.util.List<java.lang.String> p0) {
        ((net.minecraft.client.Options) this.data).resourcePacks = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyAttackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyAttack);
    }

    public boolean getHudHiddenMapped() {
        return ((net.minecraft.client.Options) this.data).hideGui;
    }

    public void setHudHiddenMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).hideGui = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyPickItemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyPickItem);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyFullscreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyFullscreen);
    }

    public boolean getJoinedFirstServerMapped() {
        return ((net.minecraft.client.Options) this.data).joinedFirstServer;
    }

    public void setJoinedFirstServerMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).joinedFirstServer = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyChatMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyChat);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyAdvancementsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyAdvancements);
    }

    public int getOverrideWidthMapped() {
        return ((net.minecraft.client.Options) this.data).overrideWidth;
    }

    public void setOverrideWidthMapped(int p0) {
        ((net.minecraft.client.Options) this.data).overrideWidth = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySprintMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySprint);
    }

    public boolean getPauseOnLostFocusMapped() {
        return ((net.minecraft.client.Options) this.data).pauseOnLostFocus;
    }

    public void setPauseOnLostFocusMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).pauseOnLostFocus = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySocialInteractionsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySocialInteractions);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyRightMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyRight);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyPlayerListMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyPlayerList);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyDropMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyDrop);
    }

    public java.util.List<java.lang.String> getIncompatibleResourcePacksMapped() {
        return ((net.minecraft.client.Options) this.data).incompatibleResourcePacks;
    }

    public void setIncompatibleResourcePacksMapped(java.util.List<java.lang.String> p0) {
        ((net.minecraft.client.Options) this.data).incompatibleResourcePacks = p0;
    }

    public boolean getAdvancedItemTooltipsMapped() {
        return ((net.minecraft.client.Options) this.data).advancedItemTooltips;
    }

    public void setAdvancedItemTooltipsMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).advancedItemTooltips = p0;
    }

    public boolean getUseNativeTransportMapped() {
        return ((net.minecraft.client.Options) this.data).useNativeTransport;
    }

    public void setUseNativeTransportMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).useNativeTransport = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyJumpMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyJump);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyUseMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyUse);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyForwardMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyUp);
    }

    public boolean getHideServerAddressMapped() {
        return ((net.minecraft.client.Options) this.data).hideServerAddress;
    }

    public void setHideServerAddressMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).hideServerAddress = p0;
    }

    public int getGlDebugVerbosityMapped() {
        return ((net.minecraft.client.Options) this.data).glDebugVerbosity;
    }

    public void setGlDebugVerbosityMapped(int p0) {
        ((net.minecraft.client.Options) this.data).glDebugVerbosity = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyTogglePerspectiveMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyTogglePerspective);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySneakMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyShift);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySwapHandsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySwapOffhand);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeySaveToolbarActivatorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keySaveHotbarActivator);
    }

    public cn.jstxjf_.rte.mapping.holder.KeyBinding getKeyScreenshotMapped() {
        return new cn.jstxjf_.rte.mapping.holder.KeyBinding(((net.minecraft.client.Options) this.data).keyScreenshot);
    }

    public boolean getSkipMultiplayerWarningMapped() {
        return ((net.minecraft.client.Options) this.data).skipMultiplayerWarning;
    }

    public void setSkipMultiplayerWarningMapped(boolean p0) {
        ((net.minecraft.client.Options) this.data).skipMultiplayerWarning = p0;
    }
}
