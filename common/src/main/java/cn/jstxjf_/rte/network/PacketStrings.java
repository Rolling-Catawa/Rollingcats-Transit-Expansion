package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import java.util.ArrayList;
import java.util.List;

public final class PacketStrings {

    public static final int CHUNK_CHARS = 8000;

    private PacketStrings() {
    }

    public static List<String> split(String text) {
        final String value = text == null ? "" : text;
        final List<String> chunks = new ArrayList<>();
        for (int i = 0; i < value.length(); i += CHUNK_CHARS) {
            chunks.add(value.substring(i, Math.min(value.length(), i + CHUNK_CHARS)));
        }
        if (chunks.isEmpty()) {
            chunks.add("");
        }
        return chunks;
    }

    public static void writeLong(RtePacketWriter writer, String text) {
        final List<String> chunks = split(text);
        writer.writeInt(chunks.size());
        chunks.forEach(writer::writeString);
    }

    public static String readLong(RtePacketReader reader) {
        final int count = Math.max(0, Math.min(64, reader.readInt()));
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(reader.readString());
        }
        return builder.toString();
    }
}
