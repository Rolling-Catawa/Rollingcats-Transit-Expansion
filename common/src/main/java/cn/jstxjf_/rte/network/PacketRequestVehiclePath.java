package cn.jstxjf_.rte.network;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.Init;

import java.util.ArrayList;
import java.util.List;

public class PacketRequestVehiclePath extends PacketHandler {

    private static final int MAX_SEGMENTS = 2048;

    private final long vehicleId;
    private final boolean includeGeometry;

    public PacketRequestVehiclePath(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeLong(vehicleId);
        sender.writeBoolean(includeGeometry);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
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
                    cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode.findSimulator(
                        new org.mtr.mapping.holder.World(
                            ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel()));
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
                            Init.REGISTRY.sendPacketToClient(player, new PacketVehiclePath(vehicleId, segments, includeGeometry));
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
