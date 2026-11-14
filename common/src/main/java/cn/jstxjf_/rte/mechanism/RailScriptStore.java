package cn.jstxjf_.rte.mechanism;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class RailScriptStore {

    public static final int MAX_BYTES = 262144;
    private static final Pattern ID = Pattern.compile("^[a-z0-9_]{1,64}$");

    public enum Part {
        META(".json"),
        SERVER(".server.js"),
        CLIENT(".client.js");

        public final String suffix;

        Part(String suffix) {
            this.suffix = suffix;
        }
    }

    private final Path root;

    public RailScriptStore(Path root) {
        this.root = root;
    }

    public static Path defaultRoot() {
        return Path.of("config", "rte", "rail_scripts");
    }

    public Path root() {
        return root;
    }

    public static boolean isValidId(String id) {
        return id != null && ID.matcher(id).matches();
    }

    public List<String> list() {
        final TreeSet<String> ids = new TreeSet<>();
        if (!Files.isDirectory(root)) {
            return new ArrayList<>();
        }
        try (Stream<Path> files = Files.list(root)) {
            files.forEach(file -> {
                final String name = file.getFileName().toString();
                for (final Part part : new Part[]{Part.SERVER, Part.CLIENT}) {
                    if (name.endsWith(part.suffix)) {
                        final String id = name.substring(0, name.length() - part.suffix.length());
                        if (isValidId(id)) {
                            ids.add(id);
                        }
                    }
                }
            });
        } catch (IOException ignored) {
        }
        return new ArrayList<>(ids);
    }

    public Optional<String> read(String id, Part part) {
        if (!isValidId(id)) {
            return Optional.empty();
        }
        final Path file = root.resolve(id + part.suffix);
        try {
            if (!Files.isRegularFile(file) || Files.size(file) > MAX_BYTES) {
                return Optional.empty();
            }
            return Optional.of(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void write(String id, Part part, String text) throws IOException {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Invalid rail script id: " + id);
        }
        final byte[] bytes = (text == null ? "" : text).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_BYTES) {
            throw new IllegalArgumentException("Rail script too large: " + bytes.length);
        }
        Files.createDirectories(root);
        final Path target = root.resolve(id + part.suffix);
        final Path temp = root.resolve(id + part.suffix + ".tmp");
        Files.write(temp, bytes);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
