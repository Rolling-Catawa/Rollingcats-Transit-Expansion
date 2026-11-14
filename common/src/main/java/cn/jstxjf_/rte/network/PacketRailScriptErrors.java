package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.TreeMap;

public class PacketRailScriptErrors extends RtePacket {

    public final String scriptId;
    public final Map<Integer, String> errors;

    public PacketRailScriptErrors(String scriptId, Map<Integer, String> errors) {
        this.scriptId = scriptId;
        this.errors = new TreeMap<>(errors);
    }

    public PacketRailScriptErrors(RtePacketReader reader) {
        scriptId = reader.readString();
        final int count = Math.max(0, Math.min(64, reader.readInt()));
        errors = new TreeMap<>();
        for (int i = 0; i < count; i++) {
            final int line = reader.readInt();
            errors.put(line, reader.readString());
        }
    }

    @Override
    protected void write(RtePacketWriter writer) {
        final int count = Math.min(64, errors.size());
        writer.writeString(scriptId);
        writer.writeInt(count);
        errors.entrySet().stream().limit(count).forEach(entry -> {
            writer.writeInt(entry.getKey());
            final String message = entry.getValue();
            writer.writeString(message.length() > 1000 ? message.substring(0, 1000) : message);
        });
    }

    @Override
    protected void handleClient() {
        cn.jstxjf_.rte.gui.imgui.RailScriptEditorPanel.acceptErrors(scriptId, errors);
    }

    public static void broadcast(MinecraftServer server, String scriptId, Map<Integer, String> errors) {
        final PacketRailScriptErrors packet = new PacketRailScriptErrors(scriptId, errors);
        server.execute(() -> {
            for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (PacketSaveRailScript.mayEdit(player)) {
                    cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, packet);
                }
            }
        });
    }
}
