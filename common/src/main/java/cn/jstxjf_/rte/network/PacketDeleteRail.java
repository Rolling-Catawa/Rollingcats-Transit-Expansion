package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;
import org.mtr.mod.packet.PacketDeleteData;

import java.util.LinkedHashSet;
import java.util.Set;

public class PacketDeleteRail extends PacketHandler {

    private final String railId;
    private final String reverseRailId;

    public PacketDeleteRail(PacketBufferReceiver receiver) {
        railId = receiver.readString();
        reverseRailId = receiver.readString();
    }

    public PacketDeleteRail(String railId, String reverseRailId) {
        this.railId = railId == null ? "" : railId;
        this.reverseRailId = reverseRailId == null ? "" : reverseRailId;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeString(railId);
        sender.writeString(reverseRailId);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final ServerWorld serverWorld = new ServerWorld(((net.minecraft.server.level.ServerPlayer) player.data).serverLevel());
            final Set<String> deleted = new LinkedHashSet<>();
            if (!railId.isEmpty() && deleted.add(railId)) {
                PacketDeleteData.sendDirectlyToServerRailId(serverWorld, railId);
            }
            if (!reverseRailId.isEmpty() && deleted.add(reverseRailId)) {
                PacketDeleteData.sendDirectlyToServerRailId(serverWorld, reverseRailId);
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
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketDeleteRail(rail.getHexId(), reverseId));
    }
}
