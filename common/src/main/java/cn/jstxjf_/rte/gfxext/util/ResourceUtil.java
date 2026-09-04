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
            return new ResourceLocation(cn.jstxjf_.rte.RTE.MOD_ID, location.getPath());
        }
        return location;
    }

    private static ResourceLocation resolveQualifiedAgainstBase(ResourceLocation baseFile, ResourceLocation absolute) {
        if (baseFile == null || resourceExists(absolute)) {
            return null;
        }
        String folder = baseFile.getPath();
        final int lastSlash = folder.lastIndexOf('/');
        folder = lastSlash >= 0 ? folder.substring(0, lastSlash) : "";
        while (true) {
            final ResourceLocation candidate = new ResourceLocation(absolute.getNamespace(),
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

    private static boolean resourceExists(ResourceLocation location) {
        try {
            final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            return minecraft != null && minecraft.getResourceManager().getResource(location).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public static ResourceLocation resolveRelativePath(ResourceLocation baseFile, String relative, String expectExtension) {
        relative = relative.toLowerCase(Locale.ROOT).replace('\\', '/');

        if (relative.contains(":")) {
            relative = relative.replaceAll("[^a-z0-9/.:_-]", "_");
            if (expectExtension != null && !relative.endsWith(expectExtension)) {
                relative += expectExtension;
            }
            final ResourceLocation absolute = new ResourceLocation(relative);
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

        String base = baseFile.getPath();
        String[] baseParts = base.split("/");
        String[] relativeParts = relative.split("/");
        Deque<String> stack = new LinkedList<>();
        for (String part : baseParts) {
            if (part.isEmpty()) throw new IllegalArgumentException("Invalid base file path: " + base);
            stack.push(part);
        }
        stack.pop();
        for (String part : relativeParts) {
            if (part.equals(".")) continue;
            if (part.equals("..")) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Out of range: " + relative + " relative to "   + base);
                } else {
                    stack.pop();
                }
                continue;
            }
            stack.push(part);
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.removeLast()).append("/");
        }
        String path = sb.toString();
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        return new ResourceLocation(baseFile.getNamespace(), path);
    }
}
