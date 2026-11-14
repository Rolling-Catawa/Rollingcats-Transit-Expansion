package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.util.StringMapSerializer;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PacketUpdateTrainCustomConfigs extends RtePacket {

    private final long sidingId;
    private final long vehicleId;
    private final String serializedConfigs;
    private static Field sidingVehiclesField;

    public PacketUpdateTrainCustomConfigs(RtePacketReader receiver) {
        this.sidingId = receiver.readLong();
        this.vehicleId = receiver.readLong();
        this.serializedConfigs = receiver.readString();
    }

    public PacketUpdateTrainCustomConfigs(long sidingId, long vehicleId, Map<String, String> customConfigs) {
        this.sidingId = sidingId;
        this.vehicleId = vehicleId;
        String str = "";
        try {
            str = StringMapSerializer.serializeToString(customConfigs);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed serializing train custom configs", e);
        }
        this.serializedConfigs = str;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeLong(sidingId);
        sender.writeLong(vehicleId);
        sender.writeString(serializedConfigs);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            try {
                Map<String, String> customConfigs = StringMapSerializer.deserialize(serializedConfigs);
                if (customConfigs == null) customConfigs = new HashMap<>();
                Vehicle vehicle = findVehicle(player, sidingId, vehicleId);
                if (vehicle instanceof TrainExtraSupplier supplier) {
                    supplier.setCustomConfigs(customConfigs);
                    supplier.isConfigsChanged(true);
                }
            } catch (Exception e) {
                RTE.LOGGER.error("Failed applying train custom configs", e);
            }
        });
    }

    public static void sendUpdateC2S(Vehicle vehicle, Map<String, String> customConfigs) {
        if (customConfigs == null) return;
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateTrainCustomConfigs(vehicle.vehicleExtraData.getSidingId(), vehicle.getId(), customConfigs));
    }

    @SuppressWarnings("unchecked")
    private static Vehicle findVehicle(ServerPlayer player, long sidingId, long vehicleId) throws Exception {
        if (sidingVehiclesField == null) {
            sidingVehiclesField = Siding.class.getDeclaredField("vehicles");
            sidingVehiclesField.setAccessible(true);
        }

        final Simulator simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(cn.jstxjf_.rte.compat.McCompat.level(player));
        if (simulator == null) {
            return null;
        }

        final Siding siding = simulator.sidingIdMap.get(sidingId);
        if (siding == null) {
            return null;
        }

        final Set<Vehicle> vehicles = (Set<Vehicle>) sidingVehiclesField.get(siding);
        for (final Vehicle vehicle : vehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }
}
