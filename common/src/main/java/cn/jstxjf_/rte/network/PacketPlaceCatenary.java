package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.data.MsdBridge;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceCatenary extends PacketHandler {

    private final long sourceStatePos;
    private final String typeName;
    private final List<long[]> nodePlacements;
    private final List<String> nodeTags;
    private final List<long[]> links;

    public PacketPlaceCatenary(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
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
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null || !MsdBridge.isLoaded()) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel();
            MsdBridge.serverPlaceAndLink(level, nodePlacements, nodeTags, sourceStatePos, links, typeName);
        });
    }

    public static void sendPlaceC2S(long sourceStatePos, String typeName, List<long[]> nodePlacements, List<String> nodeTags, List<long[]> links) {
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketPlaceCatenary(sourceStatePos, typeName, nodePlacements, nodeTags, links));
    }
}
