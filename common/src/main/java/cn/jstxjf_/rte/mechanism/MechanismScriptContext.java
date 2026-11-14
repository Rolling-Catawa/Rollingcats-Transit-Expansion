package cn.jstxjf_.rte.mechanism;

import cn.jstxjf_.rte.scripting.AbstractScriptContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MechanismScriptContext extends AbstractScriptContext {

    public static final int SYNC_MAX_BYTES = 4096;
    public static final long SYNC_INTERVAL_MILLIS = 100;
    private static final int EVENT_QUEUE_LIMIT = 256;

    public final RailScriptRuntime.LevelState level;
    public final Mechanism mechanism;
    public final MechanismWrapper wrapper;
    public boolean restored;
    public boolean alive = true;
    private final List<MechanismEventInfo> events = new ArrayList<>();
    private final List<long[]> timerDue = new ArrayList<>();
    private final List<String> timerNames = new ArrayList<>();
    public volatile String pendingSync;
    public volatile String lastSync = "";
    public long lastSyncMillis;
    public volatile String savedStateJson;

    public MechanismScriptContext(RailScriptRuntime.LevelState level, Mechanism mechanism) {
        this.level = level;
        this.mechanism = mechanism;
        this.wrapper = new MechanismWrapper(level, mechanism, this);
    }

    public synchronized void queue(MechanismEventInfo event) {
        if (events.size() < EVENT_QUEUE_LIMIT) {
            events.add(event);
        }
    }

    public synchronized List<MechanismEventInfo> drainEvents(long nowMillis) {
        for (int i = timerDue.size() - 1; i >= 0; i--) {
            if (timerDue.get(i)[0] <= nowMillis) {
                final MechanismEventInfo event = new MechanismEventInfo("timer");
                event.timer = timerNames.get(i);
                events.add(event);
                timerDue.remove(i);
                timerNames.remove(i);
            }
        }
        final List<MechanismEventInfo> drained = new ArrayList<>(events);
        events.clear();
        return drained;
    }

    public synchronized void after(long millis, String name) {
        if (timerDue.size() >= EVENT_QUEUE_LIMIT) {
            return;
        }
        timerDue.add(new long[]{System.currentTimeMillis() + Math.max(0, millis)});
        timerNames.add(name == null ? "" : name);
    }

    public void sync(Object value) {
        final String text = value == null ? "" : value.toString();
        if (text.getBytes(StandardCharsets.UTF_8).length > SYNC_MAX_BYTES) {
            reportError("ctx.sync payload exceeds " + SYNC_MAX_BYTES + " bytes", -1);
            return;
        }
        pendingSync = text;
    }

    public String savedState() {
        return mechanism.stateJson;
    }

    public void storeState(String json) {
        if (json != null && json.getBytes(StandardCharsets.UTF_8).length <= RailScriptStore.MAX_BYTES) {
            mechanism.stateJson = json;
        }
    }

    public void reportError(String message, int line) {
        level.runtime.reportRuntimeError(mechanism.scriptId, line, message);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public void log(Object message) {
        cn.jstxjf_.rte.RTE.LOGGER.info("[RailScript:{}] {}", mechanism.group, message);
    }

    @Override
    public void renderFunctionFinished() {
    }

    @Override
    public Object getWrapperObject() {
        return wrapper;
    }

    @Override
    public boolean isBearerAlive() {
        return alive;
    }
}
