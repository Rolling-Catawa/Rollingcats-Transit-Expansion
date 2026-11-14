package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.mechanism.RailScriptPolicy;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import cn.jstxjf_.rte.mechanism.RailScriptStore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketSaveRailScript extends RtePacket {

    private static final Map<String, String[]> PENDING = new ConcurrentHashMap<>();

    private final String id;
    private final int part;
    private final long session;
    private final int index;
    private final int count;
    private final String chunk;

    private PacketSaveRailScript(String id, int part, long session, int index, int count, String chunk) {
        this.id = id;
        this.part = part;
        this.session = session;
        this.index = index;
        this.count = count;
        this.chunk = chunk;
    }

    public PacketSaveRailScript(RtePacketReader reader) {
        id = reader.readString();
        part = reader.readInt();
        session = reader.readLong();
        index = reader.readInt();
        count = reader.readInt();
        chunk = reader.readString();
    }

    public static void send(String id, RailScriptStore.Part part, String text) {
        final List<String> chunks = PacketStrings.split(text);
        final long session = System.nanoTime();
        for (int i = 0; i < chunks.size(); i++) {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(
                new PacketSaveRailScript(id, part.ordinal(), session, i, chunks.size(), chunks.get(i)));
        }
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeString(id);
        writer.writeInt(part);
        writer.writeLong(session);
        writer.writeInt(index);
        writer.writeInt(count);
        writer.writeString(chunk);
    }

    public static boolean mayEdit(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        final MinecraftServer server = player.getServer();
        return player.hasPermissions(2) || (server != null && server.isSingleplayerOwner(player.getGameProfile()));
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null || count <= 0 || count > 64 || index < 0 || index >= count
                || part < 0 || part >= RailScriptStore.Part.values().length) {
                return;
            }
            if (!mayEdit(player) || !RailScriptStore.isValidId(id)) {
                RTE.LOGGER.warn("[RailScript] rejected save of {} from {}", id, player.getGameProfile().getName());
                return;
            }
            final String key = player.getUUID() + "|" + session;
            final String[] parts = PENDING.computeIfAbsent(key, ignored -> new String[count]);
            if (parts.length != count) {
                PENDING.remove(key);
                return;
            }
            parts[index] = chunk;
            for (final String piece : parts) {
                if (piece == null) {
                    return;
                }
            }
            PENDING.remove(key);
            final String text = String.join("", parts);
            final RailScriptStore.Part target = RailScriptStore.Part.values()[part];
            final String decision = RailScriptPolicy.decideSave(player.hasPermissions(2),
                server.isSingleplayerOwner(player.getGameProfile()), id, text);
            if (!decision.equals(RailScriptPolicy.OK)) {
                RTE.LOGGER.warn("[RailScript] save of {} from {} refused: {}", id, player.getGameProfile().getName(), decision);
                return;
            }
            final RailScriptRuntime runtime = RailScriptRuntime.get(server);
            try {
                runtime.store.write(id, target, text);
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailScript] failed writing {}: {}", id, e.toString());
                return;
            }
            RTE.LOGGER.info("[RailScript] {} saved {}{}", player.getGameProfile().getName(), id, target.suffix);
            runtime.reload(id);
            runtime.holder(id);
            PacketRailScriptErrors.broadcast(server, id, runtime.errors(id));
        });
    }
}
