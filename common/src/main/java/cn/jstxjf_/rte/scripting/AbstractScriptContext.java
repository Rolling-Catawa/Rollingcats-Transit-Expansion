package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.scripting.util.OrderedMap;
import java.util.concurrent.Future;
import java.util.*;

public abstract class AbstractScriptContext {

    public Object state = null;
    public boolean created = false;
    public Future<?> scriptStatus;
    public double lastExecuteTime = 0;
    public boolean disposed = false;
    public int rte$deadPasses = 0;
    public long rte$deadSinceNanos = 0;
    public long lastExecuteDuration = 0;
    public long nextRenderNanos = 0;
    public OrderedMap<String, Object> debugInfo = new OrderedMap<>();

    public final java.util.Map<String, ScriptUseBox> useBoxes = new java.util.concurrent.ConcurrentHashMap<>();

    public void addUseBox(String id, double x1, double y1, double z1, double x2, double y2, double z2) {
        addUseBox(id, 0, x1, y1, z1, x2, y2, z2);
    }

    public void addUseBox(String id, int car, double x1, double y1, double z1, double x2, double y2, double z2) {
        if (id == null || car < 0) {
            return;
        }
        useBoxes.put(id, new ScriptUseBox(id, car, x1, y1, z1, x2, y2, z2));
    }

    public void removeUseBox(String id) {
        if (id != null) {
            useBoxes.remove(id);
        }
    }

    public void clearUseBoxes() {
        useBoxes.clear();
    }

    public int useBoxCount() {
        return useBoxes.size();
    }

    public boolean rte$deadFor(long graceNanos, boolean alive, long now) {
        if (alive) {
            rte$deadSinceNanos = 0;
            return false;
        }
        if (rte$deadSinceNanos == 0) {
            rte$deadSinceNanos = now;
        }
        return now - rte$deadSinceNanos >= graceNanos;
    }

    public void releaseResources() {
        clearDebugInfo();
    }

    public abstract void renderFunctionFinished();

    public abstract Object getWrapperObject();

    public abstract boolean isBearerAlive();

    public OrderedMap<String, Object> getDebugInfo() {
        synchronized (debugInfo) {
            return new OrderedMap<>(debugInfo);
        }
    }

    public void removeDebugInfo(String key) {
        synchronized (debugInfo) {
            debugInfo.remove(key);
        }
    }

    public void setDebugInfo(String key, Object... values) {
        synchronized (debugInfo) {
            OrderedMap.PlacementOrder order = OrderedMap.PlacementOrder.CENTRAL;
            List<Object> list = new ArrayList<>();
            
            if (values == null || values.length == 0) return;
            Collections.addAll(list, values);
            
            if (list.size() > 1 && list.get(0) instanceof OrderedMap.PlacementOrder) {
                order = (OrderedMap.PlacementOrder) list.remove(0);
            }
            
            debugInfo.put(key, list.size() == 1 ? list.get(0) : list, order);
        }
    }

    public void clearDebugInfo() {
        synchronized (debugInfo) {
            debugInfo.clear();
        }
    }
}
