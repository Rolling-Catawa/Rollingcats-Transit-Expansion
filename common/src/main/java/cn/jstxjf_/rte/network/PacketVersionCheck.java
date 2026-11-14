package cn.jstxjf_.rte.network;

import net.minecraft.server.level.ServerPlayer;
import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.BuildConfig;
import cn.jstxjf_.rte.RTE;

public class PacketVersionCheck extends RtePacket {

    private final String remoteVersion;
    private final int remoteProtocolVersion;
    private final int remoteManualNotchMultiplier;
    private final int remoteSyncFlags;

    public PacketVersionCheck() {
        this(BuildConfig.MOD_VERSION, BuildConfig.MOD_PROTOCOL_VERSION,
            cn.jstxjf_.rte.RTEConfig.manualNotchMultiplier, packSyncFlags());
    }

    public PacketVersionCheck(String remoteVersion, int remoteProtocolVersion,
                              int remoteManualNotchMultiplier, int remoteSyncFlags) {
        this.remoteVersion = remoteVersion;
        this.remoteProtocolVersion = remoteProtocolVersion;
        this.remoteManualNotchMultiplier = remoteManualNotchMultiplier;
        this.remoteSyncFlags = remoteSyncFlags;
    }

    public PacketVersionCheck(RtePacketReader receiver) {
        remoteVersion = receiver.readString();
        remoteProtocolVersion = receiver.readInt();
        remoteManualNotchMultiplier = receiver.readInt();
        int flags = ALL_SYNC_FLAGS;
        try {
            flags = receiver.readInt();
        } catch (Exception ignored) {
        }
        remoteSyncFlags = flags;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeString(remoteVersion);
        sender.writeInt(remoteProtocolVersion);
        sender.writeInt(remoteManualNotchMultiplier);
        sender.writeInt(remoteSyncFlags);
    }

    private static final int FLAG_VEHICLE_PATH = 1;
    private static final int FLAG_VEHICLE_MARKER = 2;
    private static final int FLAG_FULL_RAIL = 4;
    private static final int ALL_SYNC_FLAGS = FLAG_VEHICLE_PATH | FLAG_VEHICLE_MARKER | FLAG_FULL_RAIL;

    private static int packSyncFlags() {
        int flags = 0;
        if (cn.jstxjf_.rte.RTEConfig.allowVehiclePathSync) flags |= FLAG_VEHICLE_PATH;
        if (cn.jstxjf_.rte.RTEConfig.allowVehicleMarkerSync) flags |= FLAG_VEHICLE_MARKER;
        if (cn.jstxjf_.rte.RTEConfig.allowFullRailSync) flags |= FLAG_FULL_RAIL;
        return flags;
    }

    @Override
    protected void handleClient() {
        ClientHandler.apply(remoteVersion, remoteProtocolVersion, remoteManualNotchMultiplier, remoteSyncFlags);
    }

    public static void sendVersionCheckS2C(ServerPlayer player) {
        cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketVersionCheck());
    }

    private static final class ClientHandler {

        private static void apply(String remoteVersion, int remoteProtocolVersion,
                                  int remoteManualNotchMultiplier, int remoteSyncFlags) {
            final net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
            client.execute(() -> {
                if (remoteProtocolVersion == BuildConfig.MOD_PROTOCOL_VERSION) {
                    cn.jstxjf_.rte.RTEConfig.manualNotchMultiplier = Math.max(1, Math.min(10, remoteManualNotchMultiplier));
                    cn.jstxjf_.rte.RTEConfig.allowVehiclePathSync = (remoteSyncFlags & FLAG_VEHICLE_PATH) != 0;
                    cn.jstxjf_.rte.RTEConfig.allowVehicleMarkerSync = (remoteSyncFlags & FLAG_VEHICLE_MARKER) != 0;
                    cn.jstxjf_.rte.RTEConfig.allowFullRailSync = (remoteSyncFlags & FLAG_FULL_RAIL) != 0;
                    if (remoteSyncFlags != ALL_SYNC_FLAGS) {
                        RTE.LOGGER.info("[RTE] server restricted data sync: path={}, marker={}, rail={}",
                                cn.jstxjf_.rte.RTEConfig.allowVehiclePathSync,
                                cn.jstxjf_.rte.RTEConfig.allowVehicleMarkerSync,
                                cn.jstxjf_.rte.RTEConfig.allowFullRailSync);
                    }
                    return;
                }

                final String localVersion = BuildConfig.MOD_VERSION + " (" + BuildConfig.MOD_PROTOCOL_VERSION + ")";
                final String serverVersion = remoteVersion + " (" + remoteProtocolVersion + ")";
                final net.minecraft.network.chat.Component disconnectReason = net.minecraft.network.chat.Component.literal(
                        "RTE version mismatch.\nYour version: " + localVersion + "\nServer version: " + serverVersion + "\nPlease use matching RTE builds."
                );

                final var connection = client.getConnection();
                if (connection != null) {
                    connection.getConnection().disconnect(disconnectReason);
                }

                RTE.LOGGER.warn("Disconnected due to mismatched RTE protocol: client={}, server={}", localVersion, serverVersion);
            });
        }
    }
}
