package cn.jstxjf_.rte.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class AtomicFileWriter {

    private AtomicFileWriter() {
    }

    public static void writeString(Path target, String content) throws IOException {
        final Path absolute = target.toAbsolutePath();
        final Path parent = absolute.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        final Path temporary = absolute.resolveSibling(absolute.getFileName() + ".tmp");
        try {
            Files.writeString(temporary, content, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, absolute, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException ignored) {
            }
        }
    }
}
