package cn.jstxjf_.rte.scripting.util;

import cn.jstxjf_.rte.BuildConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.scripting.util.client.GraphicsTexture;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

@SuppressWarnings("unused")
public class ScriptResourceUtil {

    protected static cn.jstxjf_.rte.scripting.engine.ScriptBackend activeBackend;
    protected static final Stack<ResourceLocation> scriptLocationStack = new Stack<>();
    protected static final Logger LOGGER = LoggerFactory.getLogger("RTE JS");

    public static final boolean RTE_FLAG = true;
    public static final boolean ANTE_FLAG = true;
    public static final boolean NTE_FLAG = true;

    protected static final FontRenderContext FONT_CONTEXT = new FontRenderContext(new AffineTransform(), true, false);

    public static void executeScript(cn.jstxjf_.rte.scripting.engine.ScriptBackend backend, String script, ResourceLocation identifier) throws IOException {
        activeBackend = backend;
        scriptLocationStack.push(identifier);
        try {
            backend.eval(script, identifier.toString());
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scriptLocationStack.pop();
        }
    }

    public static void includeScript(Object pathOrIdentifier) throws IOException {
        if (activeBackend == null) throw new RuntimeException(
                "Cannot use include in functions, as by that time RTE no longer processes scripts.");
        ResourceLocation identifier;
        if (pathOrIdentifier instanceof ResourceLocation) {
            identifier = (ResourceLocation) pathOrIdentifier;
        } else {
            identifier = idRelative(pathOrIdentifier.toString());
        }

        if (!hasResource(identifier)) {
            RTE.LOGGER.warn("File not found in include: at " + scriptLocationStack.peek().toString() + " for input " + pathOrIdentifier.toString() + " resolved to " + identifier.toString());
            return;
        }

        executeScript(activeBackend, readString(identifier), identifier);
    }

    public static void print(Object... objects) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        if (objects.length == 0) objects = new Object[] {"null"};
        StringBuilder sb = new StringBuilder();
        sb.append("[RTE-JS] ");
        for (Object object : objects) {
            sb.append(object == null ? "null" : object.toString());
            sb.append(" ");
        }
        final String message = sb.toString().trim();
        RTE.LOGGER.info(message.length() <= 8192 ? message : message.substring(0, 8192) + " …[truncated]");
    }

    public static ResourceManager manager() {
        return Minecraft.getInstance().getResourceManager();
    }

    public static ResourceLocation identifier(String textForm) {
        return new ResourceLocation(textForm);
    }

    public static ResourceLocation id(String textForm) {
        return new ResourceLocation(textForm);
    }

    public static ResourceLocation idRelative(String textForm) {
        if (scriptLocationStack.empty()) throw new RuntimeException("Cannot use idRelative in functions.");
        return ResourceUtil.resolveRelativePath(scriptLocationStack.peek(), textForm, null);
    }

    public static ResourceLocation idr(String textForm) {
        return idRelative(textForm);
    }

    public static InputStream readStream(ResourceLocation identifier) throws IOException {
        final List<Resource> resources = ResourceUtil.getResourceStack(manager(), identifier);
        if (resources.isEmpty()) throw new IOException("Resource not found: " + identifier);
        return resources.get(0).open();
    }

    public static boolean hasResource(ResourceLocation identifier) {
        final List<Resource> resources = ResourceUtil.getResourceStack(manager(), identifier);
        return resources != null && !resources.isEmpty();
    }

    public static String readString(ResourceLocation identifier) {
        String content;
        try {
            content = ResourceUtil.readResource(manager(), identifier);
        } catch (IOException e) {
            content = null;
        }
        if (content != null && !content.isEmpty()) {
            return content;
        }
        final String fallback = readLegacyRawPath(manager(), identifier);
        return fallback != null ? fallback : content;
    }

    public static String readScriptSource(ResourceManager resourceManager, ResourceLocation identifier) {
        final String result = readScriptSourceRaw(resourceManager, identifier);
        cn.jstxjf_.rte.data.ScriptTextureHints.scan(result);
        return result;
    }

    private static String readScriptSourceRaw(ResourceManager resourceManager, ResourceLocation identifier) {
        String content;
        try {
            content = ResourceUtil.readResource(resourceManager, identifier);
        } catch (Exception e) {
            content = null;
        }
        if (content != null && !content.isEmpty()) {
            return content;
        }
        final String fallback = readLegacyRawPath(resourceManager, identifier);
        return fallback != null ? fallback : content;
    }

    private static String readLegacyRawPath(ResourceManager resourceManager, ResourceLocation sanitized) {
        final String raw = cn.jstxjf_.rte.data.ScriptedCustomTrains.rawPathOf(sanitized);
        if (raw == null) {
            return null;
        }
        final int colon = raw.indexOf(':');
        final String namespace = colon > 0 ? raw.substring(0, colon) : "minecraft";
        final String path = colon > 0 ? raw.substring(colon + 1) : raw;
        final java.util.List<String> elements = new java.util.ArrayList<>();
        elements.add("assets");
        elements.add(namespace);
        for (final String part : path.split("/")) {
            if (!part.isEmpty()) {
                elements.add(part);
            }
        }
        final String[] segments = elements.toArray(new String[0]);
        final java.util.List<net.minecraft.server.packs.PackResources> packs =
            resourceManager.listPacks().collect(java.util.stream.Collectors.toList());
        for (int i = packs.size() - 1; i >= 0; i--) {
            try {
                final net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> supplier =
                    packs.get(i).getRootResource(segments);
                if (supplier == null) {
                    continue;
                }
                try (java.io.InputStream stream = supplier.get()) {
                    final String content = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    RTE.LOGGER.info("[ScriptedCustomTrains] Recovered '{}' by raw path from pack {} ({} chars)",
                        raw, packs.get(i).packId(), content.length());
                    return content;
                }
            } catch (Exception ignored) {
            }
        }
        RTE.LOGGER.warn("[ScriptedCustomTrains] Could not recover '{}' by raw path from any pack", raw);
        return null;
    }

    public static ResourceManager mgr() {
        return manager();
    }

    public static FontRenderContext getFontRenderContext() {
        return FONT_CONTEXT;
    }

    public static AttributedString ensureStrFonts(String text, Font font) {
        AttributedString result = new AttributedString(text);
        if (text.isEmpty()) return result;
        result.addAttribute(TextAttribute.FONT, font, 0, text.length());
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (!font.canDisplay(c)) {
                Font fallback = null;
                for (final Font testFont : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
                    if (testFont.canDisplay(c)) {
                        fallback = testFont;
                        break;
                    }
                }
                final Font derived = (fallback == null ? new Font(null) : fallback)
                        .deriveFont(font.getStyle(), font.getSize2D());
                result.addAttribute(TextAttribute.FONT, derived, i, i + 1);
            }
        }
        return result;
    }

    public static BufferedImage readBufferedImage(ResourceLocation identifier) throws IOException {
        try (InputStream is = readStream(identifier)) {
            return GraphicsTexture.createArgbBufferedImage(ImageIO.read(is));
        }
    }

    private static final java.util.Map<ResourceLocation, Font> FONT_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public static Font readFont(ResourceLocation identifier) throws IOException, FontFormatException {
        final Font cached = FONT_CACHE.get(identifier);
        if (cached != null) {
            return cached;
        }
        try (InputStream is = readStream(identifier)) {
            final Font font = new RteScriptFont(Font.createFont(Font.TRUETYPE_FONT, is));
            FONT_CACHE.put(identifier, font);
            return font;
        }
    }

    public static void clearFontCache() {
        FONT_CACHE.clear();
    }

    public static int getParticleTypeId(ResourceLocation identifier) {
        Optional<ParticleType<?>> particleType = BuiltInRegistries.PARTICLE_TYPE.getOptional(identifier);
        return particleType.map(BuiltInRegistries.PARTICLE_TYPE::getId).orElse(-1);
    }

    public static CompoundTag parseNbtString(String text) throws CommandSyntaxException {
        return TagParser.parseTag(text);
    }

    public static String getRTEVersion() {
        return BuildConfig.MOD_VERSION;
    }

    public static String getNTEVersion() {
        return BuildConfig.MOD_VERSION;
    }

    public static int getNTEVersionInt() {
        try {
            final String core = BuildConfig.MOD_VERSION.split("[+-]", 2)[0];
            final String[] parts = core.split("\\.", 3);
            final int major = leadingInt(parts.length > 0 ? parts[0] : "");
            final int minor = leadingInt(parts.length > 1 ? parts[1] : "");
            final int patch = leadingInt(parts.length > 2 ? parts[2] : "");
            return major * 10000 + minor * 100 + patch;
        } catch (Exception e) {
            return 0;
        }
    }

    private static int leadingInt(String text) {
        int end = 0;
        while (end < text.length() && Character.isDigit(text.charAt(end))) end++;
        return end == 0 ? 0 : Integer.parseInt(text.substring(0, end));
    }

    public static int getNTEProtoVersion() {
        return BuildConfig.MOD_PROTOCOL_VERSION;
    }

    public static String getMTRVersion() {
        String version = getActualMTRVersion();
        return version.startsWith("4.0") ? "3.2.2-rte" : version;
    }

    public static String getActualMTRVersion() {
        try {
            return (String) Class.forName("org.mtr.mod.Keys").getField("MOD_VERSION").get(null);
        } catch (ReflectiveOperationException ignored) {}
        try {
            return (String) Class.forName("mtr.Keys").getField("MOD_VERSION").get(null);
        } catch (ReflectiveOperationException ignored) {}
        return "0.0.0";
    }

    public static void resetComponents() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            cn.jstxjf_.rte.RTEClient.reloadResources(minecraft.getResourceManager());
        }
    }
}
