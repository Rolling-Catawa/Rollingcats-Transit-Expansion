package cn.jstxjf_.rte.gfxext.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import cn.jstxjf_.rte.compat.McCompat;

public class ResourceUtil {

    public static String readResource(ResourceManager manager, ResourceLocation location) throws IOException {
        Optional<Resource> resource = getResource(manager, location);
        if (resource.isEmpty()) return "";
        try (InputStream is = resource.get().open()) {
            return IOUtils.toString(new BOMInputStream(is), StandardCharsets.UTF_8);
        }
    }

    public static Optional<Resource> getResource(ResourceManager manager, ResourceLocation location) {
        Optional<Resource> resource = manager.getResource(location);
        if (resource.isPresent()) return resource;
        ResourceLocation fallback = fallbackLocation(location);
        return fallback == location ? resource : manager.getResource(fallback);
    }

    public static List<Resource> getResourceStack(ResourceManager manager, ResourceLocation location) {
        List<Resource> resources = manager.getResourceStack(location);
        if (!resources.isEmpty()) return resources;
        ResourceLocation fallback = fallbackLocation(location);
        return fallback == location ? resources : manager.getResourceStack(fallback);
    }

    public static ResourceLocation fallbackLocation(ResourceLocation location) {
        if (location.getNamespace().equals(cn.jstxjf_.rte.RTE.LEGACY_RESOURCE_NAMESPACE)) {
            return McCompat.id(cn.jstxjf_.rte.RTE.MOD_ID, location.getPath());
        }
        return location;
    }

    public static String normalizeResourcePath(String path) {
        if (path == null) {
            return "_";
        }
        final String cleaned = path.toLowerCase(Locale.ROOT)
            .replace('\\', '/')
            .replaceAll("[^a-z0-9/._-]", "_");
        final Deque<String> segments = new ArrayDeque<>();
        for (final String segment : cleaned.split("/+")) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }
            if (segment.equals("..")) {
                if (!segments.isEmpty()) {
                    segments.removeLast();
                }
            } else {
                segments.addLast(segment);
            }
        }
        return segments.isEmpty() ? "_" : String.join("/", segments);
    }

    private static String normalizeNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return "minecraft";
        }
        final String normalized = namespace.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9_.-]", "_");
        return normalized.isEmpty() ? "minecraft" : normalized;
    }

    private static ResourceLocation resolveQualifiedAgainstBase(ResourceLocation baseFile, ResourceLocation absolute) {
        if (baseFile == null || resourceExists(absolute)) {
            return null;
        }
        String folder = baseFile.getPath();
        final int lastSlash = folder.lastIndexOf('/');
        folder = lastSlash >= 0 ? folder.substring(0, lastSlash) : "";
        while (true) {
            final ResourceLocation candidate = McCompat.id(absolute.getNamespace(),
                folder.isEmpty() ? absolute.getPath() : folder + "/" + absolute.getPath());
            if (resourceExists(candidate)) {
                cn.jstxjf_.rte.RTE.LOGGER.info("[ModelLoader] '{}' resolved to '{}' relative to {}",
                    absolute, candidate, baseFile);
                return candidate;
            }
            if (folder.isEmpty()) {
                return null;
            }
            final int slash = folder.lastIndexOf('/');
            folder = slash >= 0 ? folder.substring(0, slash) : "";
        }
    }

    public static boolean resourceExists(ResourceLocation location) {
        try {
            final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            return minecraft != null && minecraft.getResourceManager().getResource(location).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public static ResourceLocation resolveRelativePath(ResourceLocation baseFile, String relative, String expectExtension) {
        relative = relative == null ? "" : relative.toLowerCase(Locale.ROOT).replace('\\', '/');

        final int namespaceSeparator = relative.indexOf(':');
        if (namespaceSeparator >= 0) {
            final String namespace = normalizeNamespace(relative.substring(0, namespaceSeparator));
            String path = normalizeResourcePath(relative.substring(namespaceSeparator + 1));
            if (expectExtension != null && !path.endsWith(expectExtension)) {
                path += expectExtension;
            }
            final ResourceLocation absolute = McCompat.id(namespace, path);
            final ResourceLocation resolved = resolveQualifiedAgainstBase(baseFile, absolute);
            return resolved != null ? resolved : absolute;
        }

        relative = relative.replaceAll("[^a-z0-9/._-]", "_");

        if (relative.endsWith(".jpg") || relative.endsWith(".bmp") || relative.endsWith(".tga")) {
            relative = relative.substring(0, relative.length() - 4) + ".png";
        }

        if (expectExtension != null && !relative.endsWith(expectExtension)) {
            relative += expectExtension;
        }

        final String base = normalizeResourcePath(baseFile.getPath());
        final String[] baseParts = base.split("/");
        final String[] relativeParts = relative.split("/+", -1);
        final Deque<String> stack = new ArrayDeque<>();
        for (int i = 0; i < baseParts.length - 1; i++) {
            if (!baseParts[i].isEmpty()) {
                stack.addLast(baseParts[i]);
            }
        }
        for (String part : relativeParts) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.removeLast();
                }
                continue;
            }
            stack.addLast(part);
        }
        final String path = stack.isEmpty() ? "_" : String.join("/", stack);

        return McCompat.id(baseFile.getNamespace(), path);
    }

    public static InputStream open(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        return getResource(resourceManager, location)
            .orElseThrow(() -> new IOException("Resource not found: " + location))
            .open();
    }
}
