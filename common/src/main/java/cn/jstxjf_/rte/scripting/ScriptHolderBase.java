package cn.jstxjf_.rte.scripting;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.google.gson.JsonObject;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.scripting.engine.GraalAvailability;
import cn.jstxjf_.rte.scripting.engine.GraalScriptBackend;
import cn.jstxjf_.rte.scripting.engine.RhinoScriptBackend;
import cn.jstxjf_.rte.scripting.engine.ScriptBackend;
import cn.jstxjf_.rte.scripting.engine.ScriptEngineType;
import cn.jstxjf_.rte.scripting.util.*;
import cn.jstxjf_.rte.gfx.math.*;
import cn.jstxjf_.rte.data.ShapeSerializer;
import net.minecraft.network.chat.Component;
import com.google.gson.GsonBuilder;

import cn.jstxjf_.rte.scripting.compat.ClientDataCompat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public abstract class ScriptHolderBase {

    private static final int SCRIPT_THREADS = Math.max(2,
        Math.min(6, Runtime.getRuntime().availableProcessors() / 2));

    private static ExecutorService SCRIPT_THREAD = rte$newPool();

    private static ExecutorService rte$newPool() {
        final java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger();
        return Executors.newFixedThreadPool(SCRIPT_THREADS, runnable -> {
            final Thread thread = new Thread(runnable, "RTE-Script-" + counter.incrementAndGet());
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        });
    }

    private final java.util.concurrent.atomic.AtomicBoolean rte$engineBusy =
        new java.util.concurrent.atomic.AtomicBoolean();

    public final String side;
    public final ScriptEngineType engineType;
    private ScriptBackend backend;
    public final Map<String, List<Object>> functions = new HashMap<>();

    public long failTime = 0;
    public Exception failException = null;

    public String name;
    public String contextTypeName;
    private Map<ResourceLocation, String> scripts;

    private JsonObject config;
    private String key;
    private String[] functionNames;

    private final boolean[] loading = new boolean[] { true };

    private static final Set<String> ALLOWED_PACKAGES = Set.of(
        "java.awt", "java.util", "java.text", "java.time", "java.math", "mtr",
        "cn.jstxjf_.rte.scripting.compat"
    );

    private static final Set<String> ALLOWED_CLASSES = Set.of(
        "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", "java.lang.Integer",
        "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Character",
        "java.lang.String", "java.lang.CharSequence", "java.lang.Number", "java.lang.Object",
        "java.lang.Math", "java.lang.StrictMath", "java.lang.StringBuilder",
        "java.lang.Iterable", "java.lang.Comparable", "java.lang.Enum",
        "java.lang.Exception", "java.lang.RuntimeException",
        "java.lang.IllegalArgumentException", "java.lang.IllegalStateException"
    );

    public ScriptHolderBase(String side) {
        this(side, ScriptEngineType.AUTO);
    }

    public ScriptHolderBase(String side, ScriptEngineType engineType) {
        this.side = side;
        this.engineType = engineType;
    }

    private boolean allowClass(String className) {
        if (loading[0]) return true;
        if (ALLOWED_CLASSES.contains(className)) return true;
        for (String allowedPackage : ALLOWED_PACKAGES) {
            if (className.startsWith(allowedPackage)) {
                return true;
            }
        }
        return false;
    }

    public void load(
        String name, String contextTypeName, ResourceManager resourceManager,
        Map<ResourceLocation, String> scripts, JsonObject config, String key,
        String... functionNames) throws Exception {
        this.name = name;
        this.contextTypeName = contextTypeName;
        this.scripts = scripts;
        this.config = config;
        this.key = key;
        this.functionNames = functionNames;

        final Path scriptDataFolder = Path.of("./rte/script_data/").toAbsolutePath().normalize();
        Files.createDirectories(scriptDataFolder);

        final java.util.List<Map.Entry<ResourceLocation, String>> materialized = new java.util.ArrayList<>();
        for (Map.Entry<ResourceLocation, String> entry : scripts.entrySet()) {
            final String scriptContent = entry.getValue() != null ?
                entry.getValue() :
                ScriptResourceUtil.readString(entry.getKey());
            if (scriptContent != null) {
                materialized.add(Map.entry(entry.getKey(), scriptContent));
            }
        }

        final ScriptEngineType resolved;
        if (engineType == ScriptEngineType.AUTO) {
            if (key != null && RHINO_UNSUPPORTED.contains(key)) {
                resolved = ScriptEngineType.GRAAL;
            } else if (key != null && RHINO_FORCED.contains(key)) {
                resolved = ScriptEngineType.RHINO;
                RTE.LOGGER.info("[Script] {} loading on Rhino (previously failed under GraalJS)", name);
            } else {
                resolved = ScriptEngineType.RHINO;
            }
        } else {
            resolved = engineType;
        }

        try {
            createBackendAndRun(resolved, scriptDataFolder, materialized);
        } catch (Exception | Error e) {
            if (engineType == ScriptEngineType.AUTO && resolved == ScriptEngineType.GRAAL) {
                RTE.LOGGER.warn("[Script] {} failed under GraalJS, retrying with Rhino: {}", name, e.toString());
                close();
                createBackendAndRun(ScriptEngineType.RHINO, scriptDataFolder, materialized);
            } else if (engineType == ScriptEngineType.AUTO && resolved == ScriptEngineType.RHINO
                    && GraalAvailability.isUsable()) {
                if (key != null) {
                    RHINO_FORCED.remove(key);
                    RHINO_UNSUPPORTED.add(key);
                }
                RTE.LOGGER.warn("[Script] {} cannot be parsed by Rhino, returning to GraalJS: {}", name, e.toString());
                close();
                createBackendAndRun(ScriptEngineType.GRAAL, scriptDataFolder, materialized);
            } else {
                throw e;
            }
        }

        loading[0] = false;
    }

    private void createBackendAndRun(ScriptEngineType engine, Path scriptDataFolder, java.util.List<Map.Entry<ResourceLocation, String>> materialized) throws Exception {
        loading[0] = true;
        try {
            backend = engine == ScriptEngineType.RHINO
                ? new RhinoScriptBackend(this::allowClass)
                : new GraalScriptBackend(this::allowClass, scriptDataFolder);
        } catch (Exception | Error e) {
            RTE.LOGGER.error("Failed to create " + engine + " script backend for " + name, e);
            failException = e instanceof Exception exception ? exception : new RuntimeException(e);
            failTime = System.currentTimeMillis();
            throw e;
        }

        appendImporter();

        for (Map.Entry<ResourceLocation, String> entry : materialized) {
            try {
                ScriptResourceUtil.executeScript(backend, entry.getValue(), entry.getKey());
            } catch (Exception | Error e) {
                RTE.LOGGER.error("[Script] {} aborted while executing {} ({} chars)",
                    name, entry.getKey(), entry.getValue().length(), e);
                throw e;
            }
            for (String fn : functionNames) {
                registerFunction(fn);
                registerFunction(fn + contextTypeName);
            }
            RTE.LOGGER.debug("[Script] {} executed {} ({} chars) -> functions={}",
                name, entry.getKey(), entry.getValue().length(), functions.keySet());
        }

        if (materialized.stream().anyMatch(entry ->
                cn.jstxjf_.rte.scripting.compat.LegacyConnectionCache.matches(entry.getValue()))) {
            backend.eval(cn.jstxjf_.rte.scripting.compat.LegacyConnectionCache.SOURCE, "rte:legacy_connection_cache");
            final Object refresh = backend.getFunction(
                cn.jstxjf_.rte.scripting.compat.LegacyConnectionCache.FUNCTION_NAME);
            for (String renderName : new String[] { "render", "render" + contextTypeName }) {
                final List<Object> renderFunctions = functions.get(renderName);
                if (renderFunctions != null && refresh != null) {
                    renderFunctions.add(0, refresh);
                }
            }
            backend.removeGlobal(cn.jstxjf_.rte.scripting.compat.LegacyConnectionCache.FUNCTION_NAME);
        }
        defineFallbackConstants();

        rte$activeEngine = engine;
        RTE.LOGGER.debug("[Script] {} ready on {} (declared={}) scripts={} functions={}",
            name, engine, engineType, materialized.size(), functions.keySet());
    }


    public void reload(ResourceManager resourceManager) throws Exception {
        close();
        load(name, contextTypeName, resourceManager, scripts, config, key, functionNames);
    }

    protected void inject(Class<?> clazz, String method, String alias) {
        if (alias == null) alias = method;
        try {
            backend.bindStaticFunction(alias, clazz, method);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed to bind function {} from {}", alias, clazz.getName(), e);
        }
    }

    protected void inject(Class<?> clazz, String alias) {
        if (alias == null) alias = clazz.getSimpleName();
        try {
            backend.bindClass(alias, clazz);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed to bind class {} as {}", clazz.getName(), alias, e);
        }
    }

    protected void defineFallbackConstants() {
        defineIfAbsent("UNKNOWN_STATION", "未知站点|Unknown Station");
        defineIfAbsent("UNKNOWN_ROUTE", "未知线路|Unknown Route");
        defineIfAbsent("UNKNOWN_DEPOT", "未知车厂|Unknown Depot");
    }

    protected void defineIfAbsent(String key, String value) {
        eval("if (typeof " + key + " === 'undefined') { this." + key + " = '" + value + "'; }");
    }

    protected void inject(String key, String value) {
        eval("var " + key + " = '" + value + "';");
    }

    protected void eval(String script) {
        try {
            backend.eval(script, "rte:inline");
        } catch (Exception e) {
            RTE.LOGGER.error("Failed evaluating inline script", e);
        }
    }

    private static String toJsStringLiteral(String value) {
        final StringBuilder builder = new StringBuilder(value.length() + 16);
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '\\' -> builder.append("\\\\");
                case '"' -> builder.append("\\\"");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case 8232 -> builder.append("\\u2028");
                case 8233 -> builder.append("\\u2029");
                default -> builder.append(c);
            }
        }
        builder.append('"');
        return builder.toString();
    }

    protected void appendImporter() {
        inject("SIDE", side);
        eval("var RTE_FLAG = true;");
        eval("var ANTE_FLAG = true;");
        eval("(function () { var original = JSON.stringify;"
            + " if (original.rteBigIntSafe) { return; }"
            + " var wrapped = function (value, replacer, space) {"
            + "   return original(value, function (key, val) {"
            + "     var out = typeof replacer === 'function' ? replacer.call(this, key, val) : val;"
            + "     return typeof out === 'bigint' ? Number(out) : out;"
            + "   }, space); };"
            + " wrapped.rteBigIntSafe = true; JSON.stringify = wrapped; })();");
        eval("var NTE_FLAG = true;");
        eval("var RTE_VERSION = '" + ScriptResourceUtil.getRTEVersion() + "';");
        eval("var NTE_VERSION = '" + ScriptResourceUtil.getNTEVersion() + "';");
        eval("var NTE_VERSION_INT = " + ScriptResourceUtil.getNTEVersionInt() + ";");
        eval("var NTE_PROTO_VERSION = " + ScriptResourceUtil.getNTEProtoVersion() + ";");
        eval("var MTR_VERSION = '" + ScriptResourceUtil.getMTRVersion() + "';");
        if (config != null) {
            final String configInfo = new GsonBuilder().disableHtmlEscaping().create().toJson(config);
            eval("var CONFIG_INFO = JSON.parse(" + toJsStringLiteral(configInfo) + ");");
        }
        inject("MOD_ENV", RTE.class.getPackageName().split("\\.")[0]);

        inject(ScriptResourceUtil.class, "includeScript", "include");
        inject(ScriptResourceUtil.class, "print", "print");
        inject(JsFriendlyJavaUtils.class, "asJavaArray", "asJavaArray");

        inject(TimingUtil.class, "Timing");
        inject(StateTracker.class, "StateTracker");
        inject(CycleTracker.class, "CycleTracker");
        inject(RateLimit.class, "RateLimit");
        inject(TextUtil.class, "TextUtil");
        inject(GlobalRegister.class, "GlobalRegister");
        inject(WrappedEntity.class, "WrappedEntity");
        inject(ComponentUtil.class, "ComponentUtil");
        inject(OrderedMap.class, "OrderedMap");
        inject(OrderedMap.PlacementOrder.class, "PlacementOrder");
        inject(ShapeSerializer.class, "ShapeSerializer");

        inject(Matrices.class, "Matrices");
        inject(Matrix4f.class, "Matrix4f");
        inject(Vector3f.class, "Vector3f");

        inject(Component.class, "Component");
        inject(Optional.class, "Optional");
    }

    private void registerFunction(String name) {
        if (backend == null) return;
        final Object func = backend.getFunction(name);
        if (func != null) {
            functions.computeIfAbsent(name, k -> new ArrayList<>()).add(func);
            try {
                backend.removeGlobal(name);
            } catch (Exception ignored) {
            }
        }
    }

    public Future<?> callFunctionAsync(List<Object> functions, AbstractScriptContext scriptCtx,
                                       Runnable finishCallback, Object... args) {
        if (duringFailTimeout()) return null;
        if (backend == null) return null;
        if (!rte$engineBusy.compareAndSet(false, true)) {
            RTE_BUSY_SKIPPED.incrementAndGet();
            rte$enqueue(functions, scriptCtx, finishCallback, args);
            return null;
        }

        return rte$dispatch(functions, scriptCtx, finishCallback, args);
    }

    private static final class PendingCall {
        final List<Object> functions;
        final AbstractScriptContext scriptCtx;
        final Runnable finishCallback;
        final Object[] args;

        PendingCall(List<Object> functions, AbstractScriptContext scriptCtx,
                    Runnable finishCallback, Object[] args) {
            this.functions = functions;
            this.scriptCtx = scriptCtx;
            this.finishCallback = finishCallback;
            this.args = args;
        }
    }

    private final java.util.LinkedHashMap<AbstractScriptContext, PendingCall> rte$pending =
        new java.util.LinkedHashMap<>();

    private void rte$enqueue(List<Object> functions, AbstractScriptContext scriptCtx,
                             Runnable finishCallback, Object[] args) {
        synchronized (rte$pending) {
            if (rte$pending.size() >= RTE_PENDING_CAP && !rte$pending.containsKey(scriptCtx)) {
                return;
            }
            rte$pending.put(scriptCtx, new PendingCall(functions, scriptCtx, finishCallback, args));
            RTE_QUEUE_DEPTH.set(rte$pending.size());
        }
    }

    private void rte$drainNext() {
        final PendingCall next;
        synchronized (rte$pending) {
            final java.util.Iterator<java.util.Map.Entry<AbstractScriptContext, PendingCall>> it =
                rte$pending.entrySet().iterator();
            if (!it.hasNext()) {
                rte$engineBusy.set(false);
                return;
            }
            next = it.next().getValue();
            it.remove();
            RTE_QUEUE_DEPTH.set(rte$pending.size());
        }
        try {
            rte$dispatch(next.functions, next.scriptCtx, next.finishCallback, next.args);
        } catch (Throwable t) {
            rte$engineBusy.set(false);
        }
    }

    private Future<?> rte$dispatch(List<Object> functions, AbstractScriptContext scriptCtx,
                                   Runnable finishCallback, Object... args) {
        return SCRIPT_THREAD.submit(() -> {
            long start = System.nanoTime();
            try {
                if ("client".equals(side)) {
                    ClientDataCompat.ensureFresh();
                }
                TimingUtil.prepareForScript(scriptCtx);
                if (scriptCtx.state == null) {
                    scriptCtx.state = backend.newStateObject();
                }
                Object[] allArgs = new Object[3 + args.length];
                allArgs[0] = scriptCtx;
                allArgs[1] = scriptCtx.state;
                allArgs[2] = scriptCtx.getWrapperObject();
                System.arraycopy(args, 0, allArgs, 3, args.length);

                backend.invoke(functions, allArgs);

                if (finishCallback != null) finishCallback.run();

                failException = null;
                failTime = 0;
            } catch (Throwable ex) {
                RTE.LOGGER.error("Error in RTE Resource Pack JavaScript", ex);
                failTime = System.currentTimeMillis();
                failException = ex instanceof Exception e ? e : new RuntimeException(ex);
                rte$noteRuntimeFailure();
            } finally {
                scriptCtx.lastExecuteDuration = System.nanoTime() - start;
                RTE_SUBMITTED.incrementAndGet();
                RTE_EXEC_NANOS.addAndGet(scriptCtx.lastExecuteDuration);
                rte$drainNext();
            }
        });
    }

    private static final int RUNTIME_FAILURES_BEFORE_RHINO = 5;
    private ScriptEngineType rte$activeEngine = ScriptEngineType.GRAAL;
    private int rte$runtimeFailures = 0;
    private static final Set<String> RHINO_FORCED = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static final Set<String> RHINO_UNSUPPORTED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private synchronized void rte$noteRuntimeFailure() {
        if (engineType != ScriptEngineType.AUTO || rte$activeEngine != ScriptEngineType.GRAAL
            || key == null || RHINO_FORCED.contains(key) || RHINO_UNSUPPORTED.contains(key)) {
            return;
        }
        if (++rte$runtimeFailures < RUNTIME_FAILURES_BEFORE_RHINO) {
            return;
        }
        RHINO_FORCED.add(key);
        RTE.LOGGER.warn("[Script] {} failed {} times under GraalJS at runtime; "
            + "reloading resources to switch it to Rhino", name, rte$runtimeFailures);
        rte$requestReloadForRhino();
    }

    private static final java.util.concurrent.atomic.AtomicBoolean RELOAD_PENDING =
        new java.util.concurrent.atomic.AtomicBoolean(false);

    private static void rte$requestReloadForRhino() {
        if (!RELOAD_PENDING.compareAndSet(false, true)) {
            return;
        }
        try {
            final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft == null) {
                RELOAD_PENDING.set(false);
                return;
            }
            minecraft.execute(() -> {
                try {
                    cn.jstxjf_.rte.RTEClient.reloadResourcesWithFeedback(minecraft);
                } catch (Exception e) {
                    RTE.LOGGER.error("[Script] resource reload for Rhino fallback failed", e);
                } finally {
                    RELOAD_PENDING.set(false);
                }
            });
        } catch (Exception e) {
            RELOAD_PENDING.set(false);
        }
    }

    private static final int RTE_PENDING_CAP = 256;

    private static final java.util.concurrent.atomic.AtomicInteger RTE_QUEUE_DEPTH =
        new java.util.concurrent.atomic.AtomicInteger();

    private static final java.util.concurrent.atomic.AtomicLong RTE_SUBMITTED = new java.util.concurrent.atomic.AtomicLong();
    private static final java.util.concurrent.atomic.AtomicLong RTE_BUSY_SKIPPED = new java.util.concurrent.atomic.AtomicLong();
    private static final java.util.concurrent.atomic.AtomicLong RTE_EXEC_NANOS = new java.util.concurrent.atomic.AtomicLong();
    private static final java.util.Set<Object> RTE_ACTIVE_CONTEXTS =
        java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());
    private static long rte$nextThroughputReport = 0;

    private static void rte$reportThroughput() {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (rte$nextThroughputReport == 0) {
            rte$nextThroughputReport = now + 5000;
            return;
        }
        if (now < rte$nextThroughputReport) {
            return;
        }
        rte$nextThroughputReport = now + 5000;
        final long ran = RTE_SUBMITTED.getAndSet(0);
        final long skipped = RTE_BUSY_SKIPPED.getAndSet(0);
        final long nanos = RTE_EXEC_NANOS.getAndSet(0);
        final int contexts = RTE_ACTIVE_CONTEXTS.size();
        RTE_ACTIVE_CONTEXTS.clear();
        if (ran == 0 && skipped == 0) {
            return;
        }
        RTE.LOGGER.info("[ScriptThroughput] 5s: ran={} deferred={} ({}%) contexts={} queued={} scriptCpu={}ms busy={}% perContext={}Hz",
            ran, skipped,
            skipped + ran == 0 ? 0 : skipped * 100 / (skipped + ran),
            contexts, RTE_QUEUE_DEPTH.get(), nanos / 1000000L, nanos / 50000000L,
            contexts == 0 ? 0 : String.format("%.1f", ran / 5.0 / contexts));
    }

    public void tryCallFunctionAsync(String function, AbstractScriptContext scriptCtx, Runnable callback, Object... args) {
        RTE_ACTIVE_CONTEXTS.add(scriptCtx);
        rte$reportThroughput();
        if (!(scriptCtx.scriptStatus == null || scriptCtx.scriptStatus.isDone())) {
            RTE_BUSY_SKIPPED.incrementAndGet();
            return;
        }
        if (scriptCtx.disposed) return;
        final List<Object> bare = this.functions.get(function);
        final List<Object> suffixed = contextTypeName == null ? null : this.functions.get(function + contextTypeName);
        final List<Object> functions;
        if (bare != null && suffixed != null) {
            functions = new ArrayList<>(bare);
            functions.addAll(suffixed);
        } else if (bare != null) {
            functions = bare;
        } else if (suffixed != null) {
            functions = suffixed;
        } else {
            functions = new ArrayList<>();
        }
        scriptCtx.scriptStatus = callFunctionAsync(functions, scriptCtx, callback, args);
    }

    public void tryCallRenderFunctionAsync(AbstractScriptContext scriptCtx) {
        ScriptContextManager.trackContext(scriptCtx, this);
        if (!scriptCtx.created) {
            tryCallFunctionAsync("create", scriptCtx, () -> scriptCtx.created = true);
        } else {
            final long now = System.nanoTime();
            if (now < scriptCtx.nextRenderNanos) {
                return;
            }
            final long interval = Math.max(33_000_000L,
                Math.min(250_000_000L, scriptCtx.lastExecuteDuration * 2L));
            scriptCtx.nextRenderNanos = now + interval;
            tryCallFunctionAsync("render", scriptCtx, () -> scriptCtx.renderFunctionFinished(), true);
        }
    }

    public void tryCallDisposeFunctionAsync(AbstractScriptContext scriptCtx) {
        tryCallFunctionAsync("dispose", scriptCtx, () -> scriptCtx.created = false, false);
        scriptCtx.disposed = true;
    }

    public void tryCallUseFunctionAsync(AbstractScriptContext scriptCtx, Player player) {
        tryCallUseFunctionAsync(scriptCtx, player, null);
    }

    public void tryCallUseFunctionAsync(AbstractScriptContext scriptCtx, Player player, ScriptUseEvent event) {
        tryCallFunctionAsync("use", scriptCtx, null, true, new WrappedEntity(player), event);
    }

    private boolean duringFailTimeout() {
        return failTime > 0 && (System.currentTimeMillis() - failTime) < 4000;
    }

    public static void resetRunner() {
        SCRIPT_THREAD.shutdownNow();
        SCRIPT_THREAD = rte$newPool();
    }

    public void close() {
        if (backend != null) {
            try {
                backend.close();
            } catch (Exception ignored) {}
            backend = null;
        }
        functions.clear();
        synchronized (rte$pending) {
            rte$pending.clear();
        }
        rte$engineBusy.set(false);
    }
}
