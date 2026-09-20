package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class RawPackAliases {

    public static final String PACK_ID = "rte_path_recovery";
    private static final long MAX_ENTRY_BYTES = 128L * 1024 * 1024;
    private static final String ASSETS_PREFIX = "assets/";

    public static final class Alias {

        public final Path packFile;
        public final boolean zipped;
        public final String entryName;

        private Alias(Path packFile, boolean zipped, String entryName) {
            this.packFile = packFile;
            this.zipped = zipped;
            this.entryName = entryName;
        }
    }

    private static final Map<ResourceLocation, Alias> ALIASES = new LinkedHashMap<>();
    private static final Set<String> NAMESPACES = new LinkedHashSet<>();
    private static final Map<ResourceLocation, String> COLLISIONS = new LinkedHashMap<>();

    private RawPackAliases() {
    }

    public static synchronized Map<ResourceLocation, Alias> all() {
        return Collections.unmodifiableMap(ALIASES);
    }

    public static synchronized Set<String> namespaces() {
        return Collections.unmodifiableSet(NAMESPACES);
    }

    public static synchronized boolean isEmpty() {
        return ALIASES.isEmpty();
    }

    private static String lastSignature = "";

    private static String signatureOf(Path directory) {
        final StringBuilder builder = new StringBuilder();
        try (java.util.stream.Stream<Path> entries = Files.list(directory)) {
            entries.sorted().forEach(entry -> {
                try {
                    builder.append(entry.getFileName()).append(':')
                        .append(Files.isDirectory(entry) ? -1 : Files.size(entry)).append(';');
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
        return builder.toString();
    }

    public static synchronized void rebuild(Path resourcePackDirectory) {
        if (resourcePackDirectory != null && Files.isDirectory(resourcePackDirectory)) {
            final String signature = signatureOf(resourcePackDirectory);
            if (signature.equals(lastSignature) && !ALIASES.isEmpty()) {
                return;
            }
            lastSignature = signature;
        }
        final long startedAt = System.nanoTime();
        ALIASES.clear();
        NAMESPACES.clear();
        COLLISIONS.clear();
        if (resourcePackDirectory == null || !Files.isDirectory(resourcePackDirectory)) {
            return;
        }
        try (java.util.stream.Stream<Path> entries = Files.list(resourcePackDirectory)) {
            entries.forEach(RawPackAliases::scanCandidate);
        } catch (Exception e) {
            RTE.LOGGER.warn("[PathRecovery] failed to scan {}: {}", resourcePackDirectory, e.toString());
        }
        RTE.LOGGER.debug("[PathRecovery] scanned packs in {} ms, recovered {} resource(s), namespaces={}",
            (System.nanoTime() - startedAt) / 1000000L, ALIASES.size(), NAMESPACES);
        if (!COLLISIONS.isEmpty()) {
            RTE.LOGGER.warn("[PathRecovery] {} alias collision(s); first resource wins", COLLISIONS.size());
        }
    }

    private static void scanCandidate(Path candidate) {
        try {
            if (Files.isDirectory(candidate)) {
                scanDirectory(candidate);
            } else if (candidate.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                scanZip(candidate);
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[PathRecovery] failed to read pack {}: {}", candidate.getFileName(), e.toString());
        }
    }

    private static void scanZip(Path zipPath) throws IOException {
        try (ZipFile zip = new ZipFile(zipPath.toFile())) {
            final java.util.List<String> entryNames = new java.util.ArrayList<>();
            boolean recoverWholePack = false;
            final java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || entry.getSize() > MAX_ENTRY_BYTES) {
                    continue;
                }
                final String entryName = entry.getName();
                entryNames.add(entryName);
                recoverWholePack |= isLegacyAssetPath(entryName);
            }

            // A number of old MTR packs were produced by archivers which put
            // mixed-case names in the local ZIP header and different, cleaned
            // names in the central directory.  Minecraft can discover the
            // mixed-case JSON index but then cannot resolve perfectly legal
            // lower-case OBJ/PNG paths referenced by it.  If one legacy path
            // proves that the archive needs recovery, expose every asset from
            // that archive through the bottom-priority virtual pack.  Normal
            // resources still win; these aliases are fallback-only.
            for (final String entryName : entryNames) {
                register(zipPath, true, entryName, recoverWholePack);
            }
        }
    }

    private static boolean isLegacyAssetPath(String entryName) {
        if (entryName == null || !entryName.toLowerCase(Locale.ROOT).startsWith(ASSETS_PREFIX)) {
            return false;
        }
        final String relative = entryName.substring(ASSETS_PREFIX.length());
        final int slash = relative.indexOf('/');
        if (slash <= 0) {
            return true;
        }
        final String namespace = relative.substring(0, slash);
        final String path = relative.substring(slash + 1);
        return !entryName.startsWith(ASSETS_PREFIX) || !isLegal(namespace) || !isLegal(path);
    }

    private static void scanDirectory(Path root) throws IOException {
        final Path assets = root.resolve("assets");
        if (!Files.isDirectory(assets)) {
            return;
        }
        try (java.util.stream.Stream<Path> walk = Files.walk(assets)) {
            walk.filter(Files::isRegularFile).forEach(file -> {
                try {
                    if (Files.size(file) > MAX_ENTRY_BYTES) {
                        return;
                    }
                } catch (IOException ignored) {
                    return;
                }
                register(root, false, root.relativize(file).toString().replace('\\', '/'), false);
            });
        }
    }

    private static synchronized void register(Path packFile, boolean zipped, String entryName, boolean force) {
        if (entryName == null || !entryName.toLowerCase(Locale.ROOT).startsWith(ASSETS_PREFIX)) {
            return;
        }
        final String relative = entryName.substring(ASSETS_PREFIX.length());
        final int slash = relative.indexOf('/');
        if (slash <= 0) {
            return;
        }
        final String namespace = relative.substring(0, slash);
        final String path = relative.substring(slash + 1);
        if (path.isEmpty() || isIgnored(path)) {
            return;
        }
        if (!force && isLegal(namespace) && isLegal(path)) {
            return;
        }
        final String safeNamespace = sanitize(namespace);
        if (!isLegal(safeNamespace)) {
            return;
        }
        for (final String candidate : sanitizedVariants(path)) {
            if (!isLegal(candidate)) {
                continue;
            }
            final ResourceLocation location;
            try {
                location = new ResourceLocation(safeNamespace, candidate);
            } catch (Exception ignored) {
                continue;
            }
            final Alias previous = ALIASES.putIfAbsent(location, new Alias(packFile, zipped, entryName));
            if (previous == null) {
                NAMESPACES.add(safeNamespace);
            } else if (!previous.entryName.equals(entryName)) {
                COLLISIONS.put(location, previous.entryName + " <> " + entryName);
            }
        }
    }

    private static boolean isIgnored(String path) {
        final int slash = path.lastIndexOf('/');
        final String name = slash >= 0 ? path.substring(slash + 1) : path;
        return name.startsWith(".") || name.equalsIgnoreCase("Thumbs.db");
    }

    private static Set<String> sanitizedVariants(String path) {
        final Set<String> variants = new LinkedHashSet<>();
        final String lowered = path.toLowerCase(Locale.ROOT);
        variants.add(lowered);
        variants.add(sanitize(path));
        return variants;
    }

    private static String sanitize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
    }

    private static boolean isLegal(String value) {
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            final boolean ok = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || c == '/' || c == '.' || c == '_' || c == '-';
            if (!ok) {
                return false;
            }
        }
        return !value.isEmpty();
    }

    public static byte[] read(Alias alias) throws IOException {
        if (alias.zipped) {
            try (ZipFile zip = new ZipFile(alias.packFile.toFile())) {
                final ZipEntry entry = zip.getEntry(alias.entryName);
                if (entry == null) {
                    throw new IOException("Entry vanished: " + alias.entryName);
                }
                try (InputStream stream = zip.getInputStream(entry)) {
                    return stream.readAllBytes();
                }
            }
        }
        return Files.readAllBytes(alias.packFile.resolve(alias.entryName));
    }

    public static InputStream open(Alias alias) throws IOException {
        return new ByteArrayInputStream(read(alias));
    }
}
