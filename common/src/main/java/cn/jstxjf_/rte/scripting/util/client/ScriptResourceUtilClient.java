package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.scripting.util.ScriptResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.*;
import java.io.InputStream;

@SuppressWarnings("unused")
public class ScriptResourceUtilClient extends ScriptResourceUtil {

    private static final ResourceLocation NOTO_SANS_CJK_LOCATION = new ResourceLocation("mtr", "font/noto-sans-cjk-tc-medium.otf");
    private static final ResourceLocation NOTO_SANS_LOCATION = new ResourceLocation("mtr", "font/noto-sans-semibold.ttf");
    private static final ResourceLocation NOTO_SERIF_LOCATION = new ResourceLocation("mtr", "font/noto-serif-cjk-tc-semibold.ttf");

    private static boolean hasNotoSansCjk;
    private static Font notoSansMaybeCjk;
    private static Font notoSerif;

    public static void init(ResourceManager resourceManager) {
        hasNotoSansCjk = resourceManager.getResource(NOTO_SANS_CJK_LOCATION).isPresent();
        notoSansMaybeCjk = null;
        notoSerif = null;
    }

    public static Font getSystemFont(String fontName) {
        if (fontName == null || fontName.isEmpty()) return new Font(Font.SANS_SERIF, Font.PLAIN, 1);
        return switch (fontName) {
            case "Noto Sans" -> {
                if (notoSansMaybeCjk == null) {
                    notoSansMaybeCjk = loadFont(hasNotoSansCjk ? NOTO_SANS_CJK_LOCATION : NOTO_SANS_LOCATION, fontName);
                }
                yield notoSansMaybeCjk;
            }
            case "Noto Serif" -> {
                if (notoSerif == null) {
                    notoSerif = loadFont(NOTO_SERIF_LOCATION, fontName);
                }
                yield notoSerif;
            }
            default -> new Font(fontName, Font.PLAIN, 1);
        };
    }

    public static String readJsonResource(String resourcePath) {
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation location = new ResourceLocation(resourcePath);
            if (resourceManager.getResource(location).isPresent()) {
                try (InputStream stream = resourceManager.getResource(location).get().open()) {
                    return new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("readJsonResource failed: {}", resourcePath, e);
        }
        return null;
    }

    public static String readResourceAsString(String resourcePath) {
        return readJsonResource(resourcePath);
    }

    public static String[] listResources(String pathPrefix) {
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation prefixLocation = new ResourceLocation(pathPrefix);
            java.util.List<String> results = new java.util.ArrayList<>();
            resourceManager.listResources(prefixLocation.getPath(), loc -> true)
                .keySet().stream()
                .filter(loc -> loc.getNamespace().equals(prefixLocation.getNamespace()))
                .forEach(loc -> results.add(loc.toString()));
            return results.toArray(new String[0]);
        } catch (Exception e) {
            RTE.LOGGER.warn("listResources failed: {}", pathPrefix, e);
        }
        return new String[0];
    }

    private static Font loadFont(ResourceLocation location, String fontName) {
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager.getResource(location).isPresent()) {
                try (InputStream stream = resourceManager.getResource(location).get().open()) {
                    return Font.createFont(Font.TRUETYPE_FONT, stream);
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("Failed loading font: {}", fontName, e);
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 1);
    }
}
