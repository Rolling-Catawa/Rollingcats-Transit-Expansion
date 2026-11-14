package cn.jstxjf_.rte.mechanism;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public final class MessageRateLimiter {

    private final int limit;
    private final long windowMillis;
    private final Map<Long, ArrayDeque<Long>> history = new HashMap<>();

    public MessageRateLimiter(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
    }

    public synchronized boolean tryAcquire(long key, long nowMillis) {
        final ArrayDeque<Long> times = history.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        while (!times.isEmpty() && nowMillis - times.peekFirst() >= windowMillis) {
            times.pollFirst();
        }
        if (times.size() >= limit) {
            return false;
        }
        times.addLast(nowMillis);
        return true;
    }

    public synchronized void forgetIdle(long nowMillis) {
        history.values().removeIf(times -> times.isEmpty() || nowMillis - times.peekLast() >= windowMillis);
    }
}
