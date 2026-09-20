package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.render.train.VehiclePathCache;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.List;

public class PacketVehiclePath extends PacketHandler {

    public static final int MAX_SEGMENTS = 2048;
    public static final int MAX_SEGMENT_CHARS = 8192;
    public static final int MAX_GEOMETRY_CHARS = 2 * 1024 * 1024;

    private final long vehicleId;
    private final boolean includeGeometry;
    private final List<Segment> segments;

    public static final class Segment {

        public final String geometry;
        public final double railLength;
        public final long speedLimitKilometersPerHour;
        public final boolean platform;
        public final long dwellTime;
        public final long savedRailBaseId;
        public final double startDistance;
        public final double endDistance;

        public Segment(double railLength, long speedLimitKilometersPerHour, boolean platform,
                       long dwellTime, long savedRailBaseId, double startDistance, double endDistance) {
            this(railLength, speedLimitKilometersPerHour, platform, dwellTime, savedRailBaseId,
                startDistance, endDistance, "");
        }

        public Segment(double railLength, long speedLimitKilometersPerHour, boolean platform,
                       long dwellTime, long savedRailBaseId, double startDistance, double endDistance,
                       String geometry) {
            this.geometry = geometry;
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
        final int encodedSize = receiver.readInt();
        this.includeGeometry = encodedSize < 0;
        final int size = includeGeometry ? -(encodedSize + 1) : encodedSize;
        if (size < 0 || size > MAX_SEGMENTS) {
            throw new IllegalArgumentException("Invalid vehicle path size: " + size);
        }
        int geometryChars = 0;
        this.segments = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            segments.add(new Segment(
                receiver.readDouble(), receiver.readLong(), receiver.readBoolean(),
                receiver.readLong(), receiver.readLong(), receiver.readDouble(), receiver.readDouble(),
                includeGeometry ? readGeometry(receiver) : ""));
            geometryChars += segments.get(i).geometry.length();
            if (geometryChars > MAX_GEOMETRY_CHARS) {
                throw new IllegalArgumentException("Vehicle path geometry exceeds budget");
            }
        }
    }

    public PacketVehiclePath(long vehicleId, List<Segment> segments) {
        this(vehicleId, segments, false);
    }

    public PacketVehiclePath(long vehicleId, List<Segment> segments, boolean includeGeometry) {
        if (segments.size() > MAX_SEGMENTS) {
            throw new IllegalArgumentException("Too many path segments");
        }
        this.vehicleId = vehicleId;
        this.segments = List.copyOf(segments);
        this.includeGeometry = includeGeometry;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(vehicleId);
        sender.writeInt(includeGeometry ? -segments.size() - 1 : segments.size());
        long bytes = 12;
        for (final Segment segment : segments) {
            sender.writeDouble(segment.railLength);
            sender.writeLong(segment.speedLimitKilometersPerHour);
            sender.writeBoolean(segment.platform);
            sender.writeLong(segment.dwellTime);
            sender.writeLong(segment.savedRailBaseId);
            sender.writeDouble(segment.startDistance);
            sender.writeDouble(segment.endDistance);
            bytes += 49;
            if (includeGeometry) {
                sender.writeString(segment.geometry);
                bytes += 4L + segment.geometry.length() * 2L;
            }
        }
        cn.jstxjf_.rte.network.util.SyncTraffic.addPath(bytes);
    }

    private static String readGeometry(PacketBufferReceiver receiver) {
        final int length = receiver.readInt();
        if (length < 0 || length > MAX_SEGMENT_CHARS) {
            throw new IllegalArgumentException("Invalid path geometry length: " + length);
        }
        final StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(receiver.readChar());
        }
        return result.toString();
    }

    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> VehiclePathCache.accept(vehicleId, segments));
    }
}
