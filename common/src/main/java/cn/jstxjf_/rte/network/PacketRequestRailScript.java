package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import cn.jstxjf_.rte.mechanism.RailScriptStore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketRequestRailScript extends RtePacket {

    private final String id;

    public PacketRequestRailScript(String id) {
        this.id = id == null ? "" : id;
    }

    public PacketRequestRailScript(RtePacketReader reader) {
        id = reader.readString();
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeString(id);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (!PacketSaveRailScript.mayEdit(player)) {
                return;
            }
            final RailScriptRuntime runtime = RailScriptRuntime.get(server);
            final List<String> ids = new ArrayList<>(runtime.store.list());
            final String meta = id.isEmpty() ? "" : runtime.store.read(id, RailScriptStore.Part.META).orElse("");
            final String serverSource = id.isEmpty() ? "" : runtime.store.read(id, RailScriptStore.Part.SERVER).orElse("");
            final String clientSource = id.isEmpty() ? "" : runtime.store.read(id, RailScriptStore.Part.CLIENT).orElse("");
            cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player,
                new PacketRailScriptContent(ids, id, meta, serverSource, clientSource));
            if (!id.isEmpty()) {
                cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketRailScriptErrors(id, runtime.errors(id)));
            }
        });
    }
}
