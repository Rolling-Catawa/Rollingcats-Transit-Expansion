package cn.jstxjf_.rte.network;

import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.Init;

import java.util.ArrayList;
import java.util.List;

public class PacketRequestVehicleMarkers extends PacketHandler {

    private static final int MAX_MARKERS = 4096;

    private final double minX;
    private final double minZ;
    private final double maxX;
    private final double maxZ;

    public PacketRequestVehicleMarkers(PacketBufferReceiver receiver) {
        this.minX = receiver.readDouble();
        this.minZ = receiver.readDouble();
        this.maxX = receiver.readDouble();
        this.maxZ = receiver.readDouble();
    }

    public PacketRequestVehicleMarkers(double minX, double minZ, double maxX, double maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeDouble(minX);
        sender.writeDouble(minZ);
        sender.writeDouble(maxX);
        sender.writeDouble(maxZ);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        if (!cn.jstxjf_.rte.RTEConfig.allowVehicleMarkerSync) {
            return;
        }
        if (!Double.isFinite(minX) || !Double.isFinite(minZ)
                || !Double.isFinite(maxX) || !Double.isFinite(maxZ)) {
            return;
        }
        if (!cn.jstxjf_.rte.network.util.RequestRateLimiter.allow(player,
                cn.jstxjf_.rte.network.util.RequestRateLimiter.Channel.VEHICLE_MARKERS, 75)) {
            return;
        }
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final List<PacketVehicleMarkers.Marker> markers = new ArrayList<>();
            try {
                final Simulator simulator =
                    cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode.findSimulator(
                        new org.mtr.mapping.holder.World(
                            ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel()));
                if (simulator == null) {
                    return;
                }
                markerScan:
                for (final org.mtr.core.data.Siding siding : simulator.sidings) {
                    for (final Vehicle vehicle : ((cn.jstxjf_.rte.mixin.SidingAccessor) (Object) siding).rte$getVehicles()) {
                    if (markers.size() >= MAX_MARKERS) {
                        break markerScan;
                    }
                    final Vector head;
                    try {
                        head = vehicle.getHeadPosition();
                    } catch (Exception e) {
                        continue;
                    }
                    if (head == null || head.x < minX || head.x > maxX || head.z < minZ || head.z > maxZ) {
                        continue;
                    }
                    markers.add(new PacketVehicleMarkers.Marker(
                        vehicle.getId(), (float) head.x, (float) head.z, colorOf(simulator, vehicle)));
                }
                }
            } catch (Exception e) {
                return;
            }
            Init.REGISTRY.sendPacketToClient(player, new PacketVehicleMarkers(markers));
        });
    }

    private static int colorOf(Simulator simulator, Vehicle vehicle) {
        try {
            final long routeId = vehicle.vehicleExtraData.getThisRouteId();
            if (routeId != 0) {
                final org.mtr.core.data.Route route = simulator.routeIdMap.get(routeId);
                if (route != null) {
                    return 0xFF000000 | (route.getColor() & 0xFFFFFF);
                }
            }
        } catch (Exception ignored) {
        }
        return 0xFFFF6600;
    }
}
