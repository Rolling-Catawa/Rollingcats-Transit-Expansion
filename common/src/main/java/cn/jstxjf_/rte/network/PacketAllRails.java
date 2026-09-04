package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.render.MapRailArchive;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.List;

public class PacketAllRails extends PacketHandler {

    private final int page;
    private final boolean hasMore;
    private final List<RailLine> lines;

    public static final class RailLine {

        public final String id;
        public final int color;
        public final float[] xs;
        public final float[] zs;

        public RailLine(String id, int color, float[] xs, float[] zs) {
            this.id = id;
            this.color = color;
            this.xs = xs;
            this.zs = zs;
        }
    }

    public PacketAllRails(PacketBufferReceiver receiver) {
        this.page = receiver.readInt();
        this.hasMore = receiver.readBoolean();
        final int size = receiver.readInt();
        this.lines = new ArrayList<>(Math.max(16, size));
        for (int i = 0; i < size; i++) {
            final String id = receiver.readString();
            final int color = receiver.readInt();
            final int points = receiver.readInt();
            final float[] xs = new float[points];
            final float[] zs = new float[points];
            for (int p = 0; p < points; p++) {
                xs[p] = (float) receiver.readDouble();
                zs[p] = (float) receiver.readDouble();
            }
            lines.add(new RailLine(id, color, xs, zs));
        }
    }

    public PacketAllRails(int page, boolean hasMore, List<RailLine> lines) {
        this.page = page;
        this.hasMore = hasMore;
        this.lines = lines;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeInt(page);
        sender.writeBoolean(hasMore);
        sender.writeInt(lines.size());
        long bytes = 9;
        for (final RailLine line : lines) {
            sender.writeString(line.id);
            sender.writeInt(line.color);
            sender.writeInt(line.xs.length);
            for (int i = 0; i < line.xs.length; i++) {
                sender.writeDouble(line.xs[i]);
                sender.writeDouble(line.zs[i]);
            }
            bytes += line.id.length() * 2L + 8 + line.xs.length * 16L;
        }
        cn.jstxjf_.rte.network.util.SyncTraffic.addRail(bytes);
    }

    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> MapRailArchive.acceptServerRails(page, hasMore, lines));
    }
}
