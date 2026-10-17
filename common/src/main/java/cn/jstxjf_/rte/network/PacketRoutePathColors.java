package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.RouteRailColors;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class PacketRoutePathColors extends RtePacket {

    private final Map<String, Integer> railColors;

    public PacketRoutePathColors(RtePacketReader receiver) {
        final int size = receiver.readInt();
        railColors = new HashMap<>(Math.max(16, size));
        for (int i = 0; i < size; i++) {
            final String railId = receiver.readString();
            railColors.put(railId, receiver.readInt());
        }
    }

    public PacketRoutePathColors(Map<String, Integer> railColors) {
        this.railColors = railColors;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeInt(railColors.size());
        railColors.forEach((railId, color) -> {
            sender.writeString(railId);
            sender.writeInt(color);
        });
    }

    @Override
    protected void handleClient() {
        Minecraft.getInstance().execute(() -> RouteRailColors.acceptGeneratedPathColors(railColors));
    }
}
