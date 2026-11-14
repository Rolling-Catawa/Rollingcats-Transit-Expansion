package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.MsdBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceCatenary extends RtePacket {

    private final long sourceStatePos;
    private final String typeName;
    private final List<long[]> nodePlacements;
    private final List<String> nodeTags;
    private final List<long[]> links;

    public PacketPlaceCatenary(RtePacketReader receiver) {
        sourceStatePos = receiver.readLong();
        typeName = receiver.readString();
        final int nodeCount = receiver.readInt();
        nodePlacements = new ArrayList<>(nodeCount);
        nodeTags = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            nodePlacements.add(new long[]{receiver.readLong()});
            nodeTags.add(receiver.readString());
        }
        final int linkCount = receiver.readInt();
        links = new ArrayList<>(linkCount);
        for (int i = 0; i < linkCount; i++) {
            links.add(new long[]{receiver.readLong(), receiver.readLong()});
        }
    }

    public PacketPlaceCatenary(long sourceStatePos, String typeName, List<long[]> nodePlacements, List<String> nodeTags, List<long[]> links) {
        this.sourceStatePos = sourceStatePos;
        this.typeName = typeName;
        this.nodePlacements = nodePlacements;
        this.nodeTags = nodeTags;
        this.links = links;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeLong(sourceStatePos);
        sender.writeString(typeName);
        sender.writeInt(nodePlacements.size());
        for (int i = 0; i < nodePlacements.size(); i++) {
            sender.writeLong(nodePlacements.get(i)[0]);
            sender.writeString(nodeTags.get(i));
        }
        sender.writeInt(links.size());
        for (final long[] link : links) {
            sender.writeLong(link[0]);
            sender.writeLong(link[1]);
        }
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null || !MsdBridge.isLoaded()) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = cn.jstxjf_.rte.compat.McCompat.serverLevel(player);
            MsdBridge.serverPlaceAndLink(level, nodePlacements, nodeTags, sourceStatePos, links, typeName);
        });
    }

    public static void sendPlaceC2S(long sourceStatePos, String typeName, List<long[]> nodePlacements, List<String> nodeTags, List<long[]> links) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketPlaceCatenary(sourceStatePos, typeName, nodePlacements, nodeTags, links));
    }
}
