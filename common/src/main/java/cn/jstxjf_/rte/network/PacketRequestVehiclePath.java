package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketRequestVehiclePath extends RtePacket {

    private static final int MAX_SEGMENTS = 2048;

    private final long vehicleId;
    private final boolean includeGeometry;

    public PacketRequestVehiclePath(RtePacketReader receiver) {
        this.vehicleId = receiver.readLong();
        boolean requested;
        try {
            requested = receiver.readBoolean();
        } catch (IndexOutOfBoundsException legacyRequest) {
            requested = false;
        }
        this.includeGeometry = requested;
    }

    public PacketRequestVehiclePath(long vehicleId) {
        this.vehicleId = vehicleId;
        this.includeGeometry = true;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeLong(vehicleId);
        sender.writeBoolean(includeGeometry);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        if (!cn.jstxjf_.rte.RTEConfig.allowVehiclePathSync) {
            return;
        }
        if (!cn.jstxjf_.rte.network.util.RequestRateLimiter.allow(player,
                cn.jstxjf_.rte.network.util.RequestRateLimiter.Channel.VEHICLE_PATH, 1000)) {
            return;
        }
        server.execute(() -> {
            if (player == null) {
                return;
            }
            try {
                final Simulator simulator =
                    cn.jstxjf_.rte.bridge.MtrBridge.simulator(player.serverLevel());
                if (simulator == null) {
                    return;
                }
                for (final Siding siding : simulator.sidings) {
                    for (final Vehicle vehicle
                            : ((cn.jstxjf_.rte.mixin.SidingAccessor) (Object) siding).rte$getVehicles()) {
                        if (vehicle.getId() != vehicleId) {
                            continue;
                        }
                        final List<PacketVehiclePath.Segment> segments = collect(vehicle, includeGeometry);
                        if (!segments.isEmpty()) {
                            cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketVehiclePath(vehicleId, segments, includeGeometry));
                        }
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    private static List<PacketVehiclePath.Segment> collect(Vehicle vehicle, boolean includeGeometry) {
        final List<PacketVehiclePath.Segment> segments = new ArrayList<>();
        int geometryChars = 0;
        for (final PathData pathData : vehicle.vehicleExtraData.immutablePath) {
            if (segments.size() >= MAX_SEGMENTS) {
                break;
            }
            double length = 0;
            long speedLimit = 0;
            boolean platform = false;
            try {
                final Rail rail = pathData.getRail();
                if (rail != null) {
                    length = rail.railMath.getLength();
                    speedLimit = rail.getSpeedLimitKilometersPerHour(false);
                    if (speedLimit <= 0) {
                        speedLimit = rail.getSpeedLimitKilometersPerHour(true);
                    }
                    platform = rail.isPlatform();
                }
            } catch (Exception ignored) {
            }
            String geometry = "";
            if (includeGeometry) {
                try {
                    geometry = cn.jstxjf_.rte.scripting.compat.PathGeometryCodec.encode(pathData);
                } catch (RuntimeException invalidRail) {
                    cn.jstxjf_.rte.RTE.LOGGER.debug("Unable to serialize vehicle path geometry", invalidRail);
                }
            }
            if (geometry.length() > PacketVehiclePath.MAX_SEGMENT_CHARS
                    || geometryChars + geometry.length() > PacketVehiclePath.MAX_GEOMETRY_CHARS) {
                geometry = "";
            }
            geometryChars += geometry.length();
            segments.add(new PacketVehiclePath.Segment(
                length, speedLimit, platform, pathData.getDwellTime(), pathData.getSavedRailBaseId(),
                pathData.getStartDistance(), pathData.getEndDistance(), geometry));
        }
        return segments;
    }
}
