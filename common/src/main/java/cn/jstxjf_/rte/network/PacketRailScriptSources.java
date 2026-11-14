package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import cn.jstxjf_.rte.mechanism.RailScriptStore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class PacketRailScriptSources extends RtePacket {

    public final boolean replaceAll;
    public final Map<String, String> clientSources;

    public PacketRailScriptSources(boolean replaceAll, Map<String, String> clientSources) {
        this.replaceAll = replaceAll;
        this.clientSources = clientSources;
    }

    public PacketRailScriptSources(RtePacketReader reader) {
        replaceAll = reader.readBoolean();
        final int count = Math.max(0, Math.min(1024, reader.readInt()));
        clientSources = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            final String id = reader.readString();
            clientSources.put(id, PacketStrings.readLong(reader));
        }
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeBoolean(replaceAll);
        writer.writeInt(clientSources.size());
        clientSources.forEach((id, source) -> {
            writer.writeString(id);
            PacketStrings.writeLong(writer, source);
        });
    }

    @Override
    protected void handleClient() {
        cn.jstxjf_.rte.mechanism.client.ClientMechanisms.acceptSources(replaceAll, clientSources);
    }

    public static void sendAll(ServerPlayer player) {
        final RailScriptRuntime runtime = RailScriptRuntime.get(player.getServer());
        final Map<String, String> sources = new LinkedHashMap<>();
        for (final String id : runtime.store.list()) {
            sources.put(id, runtime.store.read(id, RailScriptStore.Part.CLIENT).orElse(""));
        }
        cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketRailScriptSources(true, sources));
        runtime.levelState(cn.jstxjf_.rte.compat.McCompat.serverLevel(player)).sendAllTo(player);
    }

    public static void broadcastOne(MinecraftServer server, String id, String source) {
        final Map<String, String> sources = new LinkedHashMap<>();
        sources.put(id, source);
        final PacketRailScriptSources packet = new PacketRailScriptSources(false, sources);
        for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
            cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, packet);
        }
    }
}
