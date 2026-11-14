package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import java.util.ArrayList;
import java.util.List;

public class PacketRailScriptContent extends RtePacket {

    public final List<String> ids;
    public final String id;
    public final String meta;
    public final String serverSource;
    public final String clientSource;

    public PacketRailScriptContent(List<String> ids, String id, String meta, String serverSource, String clientSource) {
        this.ids = ids;
        this.id = id;
        this.meta = meta;
        this.serverSource = serverSource;
        this.clientSource = clientSource;
    }

    public PacketRailScriptContent(RtePacketReader reader) {
        final int count = Math.max(0, Math.min(1024, reader.readInt()));
        ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(reader.readString());
        }
        id = reader.readString();
        meta = PacketStrings.readLong(reader);
        serverSource = PacketStrings.readLong(reader);
        clientSource = PacketStrings.readLong(reader);
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeInt(ids.size());
        ids.forEach(writer::writeString);
        writer.writeString(id);
        PacketStrings.writeLong(writer, meta);
        PacketStrings.writeLong(writer, serverSource);
        PacketStrings.writeLong(writer, clientSource);
    }

    @Override
    protected void handleClient() {
        cn.jstxjf_.rte.gui.imgui.RailScriptEditorPanel.acceptContent(this);
    }
}
