package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.data.DepotRailRules;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public class PacketSetDepotRailRule extends PacketHandler {

    private final long depotId;
    private final String railId;
    private final int rule;

    public PacketSetDepotRailRule(PacketBufferReceiver receiver) {
        depotId = receiver.readLong();
        railId = receiver.readString();
        rule = receiver.readInt();
    }

    public PacketSetDepotRailRule(long depotId, String railId, DepotRailRules.Rule rule) {
        this.depotId = depotId;
        this.railId = railId == null ? "" : railId;
        this.rule = rule.ordinal();
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(depotId);
        sender.writeString(railId);
        sender.writeInt(rule);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null || railId.isEmpty()) {
                return;
            }
            final DepotRailRules.Rule[] values = DepotRailRules.Rule.values();
            DepotRailRules.set(depotId, railId, values[Math.max(0, Math.min(values.length - 1, rule))]);
            DepotRailRules.save();
        });
    }
}
