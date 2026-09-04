package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.render.VehicleMarkerCache;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.List;

public class PacketVehicleMarkers extends PacketHandler {

    private final List<Marker> markers;

    public static final class Marker {

        public final long id;
        public final float x;
        public final float z;
        public final int color;

        public Marker(long id, float x, float z, int color) {
            this.id = id;
            this.x = x;
            this.z = z;
            this.color = color;
        }
    }

    public PacketVehicleMarkers(PacketBufferReceiver receiver) {
        final int size = receiver.readInt();
        markers = new ArrayList<>(Math.max(16, size));
        for (int i = 0; i < size; i++) {
            markers.add(new Marker(
                receiver.readLong(), (float) receiver.readDouble(), (float) receiver.readDouble(), receiver.readInt()));
        }
    }

    public PacketVehicleMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeInt(markers.size());
        for (final Marker marker : markers) {
            sender.writeLong(marker.id);
            sender.writeDouble(marker.x);
            sender.writeDouble(marker.z);
            sender.writeInt(marker.color);
        }
        cn.jstxjf_.rte.network.util.SyncTraffic.addMarker(4L + markers.size() * 28L);
    }

    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> VehicleMarkerCache.accept(markers));
    }
}
