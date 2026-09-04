package cn.jstxjf_.rte.scripting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptContextManager {
    public static final Map<AbstractScriptContext, ScriptHolderBase> livingContexts = new ConcurrentHashMap<>();

    public static void trackContext(AbstractScriptContext ctx, ScriptHolderBase holder) {
        livingContexts.put(ctx, holder);
    }

    public static void disposeDeadContexts() {
        livingContexts.entrySet().removeIf(entry -> {
            AbstractScriptContext ctx = entry.getKey();
            if (ctx.disposed) {
                return true;
            }
            if (!ctx.isBearerAlive()) {
                if (ctx.created) {
                    entry.getValue().tryCallDisposeFunctionAsync(ctx);
                }
                return true;
            }
            return false;
        });
        cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers.dropDeadContexts();
    }

    public static void disposeAllContexts() {
        livingContexts.forEach((ctx, holder) -> {
            if (!ctx.disposed && ctx.created) {
                holder.tryCallDisposeFunctionAsync(ctx);
            }
        });
        livingContexts.clear();
    }

    public static void clearDebugInfo() {
        livingContexts.keySet().forEach(AbstractScriptContext::clearDebugInfo);
    }
}
