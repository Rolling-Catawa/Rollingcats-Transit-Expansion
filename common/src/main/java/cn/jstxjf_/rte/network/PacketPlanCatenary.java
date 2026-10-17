package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.MsdBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketPlanCatenary extends RtePacket {

    private final String typeName;
    private final List<long[]> placements;
    private final List<double[]> offsets;
    private final List<String> blockIds;
    private final List<Integer> facings;
    private final List<Double> rotations;
    private final List<long[]> links;

    public PacketPlanCatenary(RtePacketReader receiver) {
        typeName = receiver.readString();
        final int nodeCount = receiver.readInt();
        placements = new ArrayList<>(nodeCount);
        offsets = new ArrayList<>(nodeCount);
        blockIds = new ArrayList<>(nodeCount);
        facings = new ArrayList<>(nodeCount);
        rotations = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            placements.add(new long[]{receiver.readLong()});
            offsets.add(new double[]{receiver.readDouble(), receiver.readDouble(), receiver.readDouble()});
            blockIds.add(receiver.readString());
            facings.add(receiver.readInt());
            rotations.add(receiver.readDouble());
        }
        final int linkCount = receiver.readInt();
        links = new ArrayList<>(linkCount);
        for (int i = 0; i < linkCount; i++) {
            links.add(new long[]{receiver.readLong(), receiver.readLong()});
        }
    }

    public PacketPlanCatenary(String typeName, List<long[]> placements, List<double[]> offsets,
                              List<String> blockIds, List<Integer> facings, List<Double> rotations,
                              List<long[]> links) {
        this.typeName = typeName;
        this.placements = placements;
        this.offsets = offsets;
        this.blockIds = blockIds;
        this.facings = facings;
        this.rotations = rotations;
        this.links = links;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeString(typeName);
        sender.writeInt(placements.size());
        for (int i = 0; i < placements.size(); i++) {
            sender.writeLong(placements.get(i)[0]);
            final double[] offset = offsets.get(i);
            sender.writeDouble(offset[0]);
            sender.writeDouble(offset[1]);
            sender.writeDouble(offset[2]);
            sender.writeString(blockIds.get(i));
            sender.writeInt(facings.get(i));
            sender.writeDouble(rotations.get(i));
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
            final net.minecraft.server.level.ServerLevel level = player.serverLevel();
            MsdBridge.serverPlacePlanned(level, placements, offsets, blockIds, facings, rotations, links, typeName);
        });
    }

    public static void sendPlanC2S(String typeName, List<long[]> placements, List<double[]> offsets,
                                   List<String> blockIds, List<Integer> facings, List<Double> rotations,
                              List<long[]> links) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketPlanCatenary(typeName, placements, offsets, blockIds, facings, rotations, links));
    }
}
