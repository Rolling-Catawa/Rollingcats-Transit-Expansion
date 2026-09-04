package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.data.RouteRailColors;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.HashMap;
import java.util.Map;

public class PacketRoutePathColors extends PacketHandler {

    private final Map<String, Integer> railColors;

    public PacketRoutePathColors(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeInt(railColors.size());
        railColors.forEach((railId, color) -> {
            sender.writeString(railId);
            sender.writeInt(color);
        });
    }

    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> RouteRailColors.acceptGeneratedPathColors(railColors));
    }
}
