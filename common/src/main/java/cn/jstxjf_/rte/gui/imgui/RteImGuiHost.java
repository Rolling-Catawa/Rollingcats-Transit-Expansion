package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.RTE;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.nio.file.Files;
import java.nio.file.Path;

public final class RteImGuiHost {

    private static ImGuiImplGlfw implGlfw;
    private static ImGuiImplGl3 implGl3;
    private static Boolean available;
    private static boolean frameActive;

    private static String resolveLayoutFile() {
        try {
            final java.nio.file.Path dir = net.minecraft.client.Minecraft.getInstance()
                .gameDirectory.toPath().resolve("config").resolve("rte");
            java.nio.file.Files.createDirectories(dir);
            return dir.resolve("imgui_layout.ini").toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String unavailableReason = "";

    public static String unavailableReason() {
        return unavailableReason;
    }

    public static boolean isAvailable() {
        if (available == null) {
            try {
                final long windowHandle = net.minecraft.client.Minecraft.getInstance().getWindow().getWindow();
                ImGui.createContext();
                final ImGuiIO io = ImGui.getIO();
                io.setIniFilename(resolveLayoutFile());
                io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
                io.setConfigDragClickToInputText(true);
                loadCjkFont(io);
                implGlfw = new ImGuiImplGlfw();
                implGlfw.init(windowHandle, true);
                resetPixelStoreState();
                implGl3 = new ImGuiImplGl3();
                implGl3.init("#version 150");
                available = true;
                RTE.LOGGER.info("[ImGui] initialized");
            } catch (Throwable e) {
                unavailableReason = e.toString();
                RTE.LOGGER.warn("[ImGui] unavailable, falling back to classic UI: {}", e.toString());
                available = false;
            }
        }
        return available;
    }

    private static final short[] SYMBOL_RANGES = {
        0x2000, 0x206F,
        0x2190, 0x21FF,
        0x2200, 0x22FF,
        0x2460, 0x24FF,
        0x2500, 0x25FF,
        0x2600, 0x26FF,
        0x2700, 0x27BF,
        0
    };

    private static void loadCjkFont(ImGuiIO io) {
        final String[] candidates = {
            "C:/Windows/Fonts/msyh.ttc",
            "C:/Windows/Fonts/simhei.ttf",
            "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
            "/System/Library/Fonts/PingFang.ttc"
        };
        for (final String candidate : candidates) {
            try {
                final Path path = Path.of(candidate);
                if (Files.exists(path)) {
                    final byte[] data = Files.readAllBytes(path);
                    final ImFontConfig config = new ImFontConfig();
                    config.setOversampleH(1);
                    config.setOversampleV(1);
                    io.getFonts().addFontFromMemoryTTF(data, 20, config, io.getFonts().getGlyphRangesChineseFull());
                    config.setMergeMode(true);
                    io.getFonts().addFontFromMemoryTTF(data, 20, config, SYMBOL_RANGES);
                    config.destroy();
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        io.getFonts().addFontDefault();
    }

    private static void resetPixelStoreState() {
        org.lwjgl.opengl.GL11.glPixelStorei(org.lwjgl.opengl.GL11.GL_UNPACK_ROW_LENGTH, 0);
        org.lwjgl.opengl.GL11.glPixelStorei(org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS, 0);
        org.lwjgl.opengl.GL11.glPixelStorei(org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS, 0);
        org.lwjgl.opengl.GL11.glPixelStorei(org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT, 4);
    }

    public static void beginFrame() {
        if (!isAvailable() || frameActive) {
            return;
        }
        resetPixelStoreState();
        implGlfw.newFrame();
        ImGui.newFrame();
        frameActive = true;
    }

    public static void endFrame() {
        if (!Boolean.TRUE.equals(available) || !frameActive) {
            return;
        }
        ImGui.render();
        implGl3.renderDrawData(ImGui.getDrawData());
        frameActive = false;
    }

    public static void notifyUnavailable() {
        final net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("gui.rte.imgui.unavailable"), false);
        }
    }

    public static boolean wantsInput() {
        return Boolean.TRUE.equals(available) && (ImGui.getIO().getWantCaptureMouse() || ImGui.getIO().getWantCaptureKeyboard());
    }

    private RteImGuiHost() {
    }
}
