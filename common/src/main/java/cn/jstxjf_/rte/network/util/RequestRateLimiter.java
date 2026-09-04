package cn.jstxjf_.rte.network.util;

import org.mtr.mapping.holder.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Lightweight per-player limiter for server-side query packets. */
public final class RequestRateLimiter {

    public enum Channel {
        VEHICLE_MARKERS,
        VEHICLE_PATH
    }

    private static final long ENTRY_TTL_NANOS = 10L * 60L * 1_000_000_000L;
    private static final ConcurrentHashMap<Key, Long> LAST_REQUEST = new ConcurrentHashMap<>();
    private static final AtomicInteger REQUEST_COUNT = new AtomicInteger();

    private RequestRateLimiter() {
    }

    public static boolean allow(ServerPlayerEntity player, Channel channel, long minimumIntervalMillis) {
        if (player == null || channel == null) {
            return false;
        }
        final UUID playerId;
        try {
            playerId = ((net.minecraft.server.level.ServerPlayer) player.data).getUUID();
        } catch (Exception e) {
            return false;
        }
        final long now = System.nanoTime();
        final long interval = Math.max(0, minimumIntervalMillis) * 1_000_000L;
        final boolean[] accepted = {false};
        LAST_REQUEST.compute(new Key(playerId, channel), (ignored, previous) -> {
            if (previous == null || now - previous >= interval) {
                accepted[0] = true;
                return now;
            }
            return previous;
        });
        if ((REQUEST_COUNT.incrementAndGet() & 1023) == 0) {
            LAST_REQUEST.entrySet().removeIf(entry -> now - entry.getValue() > ENTRY_TTL_NANOS);
        }
        return accepted[0];
    }

    private record Key(UUID playerId, Channel channel) {
    }
}
