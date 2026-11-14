package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Rail;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashSet;
import java.util.Set;

public class PacketDeleteRail extends RtePacket {

    private final String railId;
    private final String reverseRailId;

    public PacketDeleteRail(RtePacketReader receiver) {
        railId = receiver.readString();
        reverseRailId = receiver.readString();
    }

    public PacketDeleteRail(String railId, String reverseRailId) {
        this.railId = railId == null ? "" : railId;
        this.reverseRailId = reverseRailId == null ? "" : reverseRailId;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeString(railId);
        sender.writeString(reverseRailId);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            if (!cn.jstxjf_.rte.util.RailEditPermission.allowedRailId(player, railId)
                || !cn.jstxjf_.rte.util.RailEditPermission.allowedRailId(player, reverseRailId)) {
                cn.jstxjf_.rte.util.RailEditPermission.logRejected("PacketDeleteRail", player);
                return;
            }
            final Set<String> deleted = new LinkedHashSet<>();
            if (!railId.isEmpty() && deleted.add(railId)) {
                cn.jstxjf_.rte.bridge.MtrBridge.deleteRailOnServer(cn.jstxjf_.rte.compat.McCompat.serverLevel(player), railId);
            }
            if (!reverseRailId.isEmpty() && deleted.add(reverseRailId)) {
                cn.jstxjf_.rte.bridge.MtrBridge.deleteRailOnServer(cn.jstxjf_.rte.compat.McCompat.serverLevel(player), reverseRailId);
            }
        });
    }

    public static void sendUpdateC2S(Rail rail) {
        if (rail == null) {
            return;
        }
        String reverseId = "";
        try {
            reverseId = ((RailExtraSupplier) (Object) rail).getTransposition().getHexId();
        } catch (Exception ignored) {
        }
        RailRebuildHelper.removeClientRail(rail);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketDeleteRail(rail.getHexId(), reverseId));
    }
}
