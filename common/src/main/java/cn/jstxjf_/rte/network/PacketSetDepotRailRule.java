package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.DepotRailRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketSetDepotRailRule extends RtePacket {

    private final long depotId;
    private final String railId;
    private final int rule;

    public PacketSetDepotRailRule(RtePacketReader receiver) {
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
    protected void write(RtePacketWriter sender) {
        sender.writeLong(depotId);
        sender.writeString(railId);
        sender.writeInt(rule);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
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
