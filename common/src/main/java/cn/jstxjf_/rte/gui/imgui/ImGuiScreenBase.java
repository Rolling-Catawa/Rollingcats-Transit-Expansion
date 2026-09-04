package cn.jstxjf_.rte.gui.imgui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

public abstract class ImGuiScreenBase extends Screen implements ImGuiWorkspace.Panel {

    protected final Screen parentScreen;

    protected ImGuiScreenBase(Component title, Screen parentScreen) {
        super(title);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        ImGuiWorkspace.open(this);
    }

    @Override
    public void drawPanel() {
        buildWindow();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!RteImGuiHost.isAvailable()) {
            graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
            final net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
            graphics.drawCenteredString(font,
                net.minecraft.network.chat.Component.translatable("gui.rte.imgui.unavailable"),
                width / 2, height / 2 - 20, 0xFFFFFF);
            graphics.drawCenteredString(font,
                net.minecraft.network.chat.Component.literal(RteImGuiHost.unavailableReason()),
                width / 2, height / 2, 0xFFA0A0A0);
            graphics.drawCenteredString(font,
                net.minecraft.network.chat.Component.translatable("gui.rte.imgui.unavailable.hint"),
                width / 2, height / 2 + 20, 0xFF808080);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
    }

    @Override
    //#if MC>=12002
    public void renderBackground(GuiGraphics graphics, int rteMouseX, int rteMouseY, float rteDelta) {
    //#else
    public void renderBackground(GuiGraphics graphics) {
    //#endif
    }

    protected abstract void buildWindow();

    protected static String t(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }

    protected static String t(String key, Object... args) {
        return net.minecraft.client.resources.language.I18n.get(key, args);
    }

    @Override
    public void onClose() {
        if (ImGuiWorkspace.activateRemaining(minecraft, this)) {
            return;
        }
        minecraft.setScreen(parentScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
