package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.render.train.VehiclePathCache;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.List;

public class PacketVehiclePath extends PacketHandler {

    private final long vehicleId;
    private final List<Segment> segments;

    public static final class Segment {

        public final double railLength;
        public final long speedLimitKilometersPerHour;
        public final boolean platform;
        public final long dwellTime;
        public final long savedRailBaseId;
        public final double startDistance;
        public final double endDistance;

        public Segment(double railLength, long speedLimitKilometersPerHour, boolean platform,
                       long dwellTime, long savedRailBaseId, double startDistance, double endDistance) {
            this.railLength = railLength;
            this.speedLimitKilometersPerHour = speedLimitKilometersPerHour;
            this.platform = platform;
            this.dwellTime = dwellTime;
            this.savedRailBaseId = savedRailBaseId;
            this.startDistance = startDistance;
            this.endDistance = endDistance;
        }
    }

    public PacketVehiclePath(PacketBufferReceiver receiver) {
        this.vehicleId = receiver.readLong();
        final int size = receiver.readInt();
        this.segments = new ArrayList<>(Math.max(16, size));
        for (int i = 0; i < size; i++) {
            segments.add(new Segment(
                receiver.readDouble(), receiver.readLong(), receiver.readBoolean(),
                receiver.readLong(), receiver.readLong(), receiver.readDouble(), receiver.readDouble()));
        }
    }

    public PacketVehiclePath(long vehicleId, List<Segment> segments) {
        this.vehicleId = vehicleId;
        this.segments = segments;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(vehicleId);
        sender.writeInt(segments.size());
        for (final Segment segment : segments) {
            sender.writeDouble(segment.railLength);
            sender.writeLong(segment.speedLimitKilometersPerHour);
            sender.writeBoolean(segment.platform);
            sender.writeLong(segment.dwellTime);
            sender.writeLong(segment.savedRailBaseId);
            sender.writeDouble(segment.startDistance);
            sender.writeDouble(segment.endDistance);
        }
        cn.jstxjf_.rte.network.util.SyncTraffic.addPath(12L + segments.size() * 49L);
    }

    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> VehiclePathCache.accept(vehicleId, segments));
    }
}
