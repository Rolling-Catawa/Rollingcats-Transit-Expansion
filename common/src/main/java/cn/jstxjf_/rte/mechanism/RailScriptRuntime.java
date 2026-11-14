package cn.jstxjf_.rte.mechanism;

//#if MC>=11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
import cn.jstxjf_.rte.compat.BuiltInRegistries;
//#endif
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.scripting.ScriptHolderServer;
import cn.jstxjf_.rte.scripting.engine.ScriptEngineType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.jstxjf_.rte.compat.McCompat;

public final class RailScriptRuntime {

    public static final Map<String, Mechanism> GATE_INDEX = new ConcurrentHashMap<>();
    public static final Map<String, Integer> SPEED_OVERRIDES = new ConcurrentHashMap<>();
    public static final Map<String, Integer> REDSTONE_OUTPUTS = new ConcurrentHashMap<>();

    private static final String[] SERVER_FUNCTIONS = {"create", "dispose", "__rteDispatch", "__rteSaveState", "__rteRestoreState"};
    private static final String PRELUDE = String.join("\n",
        "function __rteHandler(type) {",
        "  switch (String(type)) {",
        "    case 'approach': return typeof onTrainApproach === 'function' ? onTrainApproach : null;",
        "    case 'enter': return typeof onTrainEnter === 'function' ? onTrainEnter : null;",
        "    case 'leave': return typeof onTrainLeave === 'function' ? onTrainLeave : null;",
        "    case 'stop': return typeof onTrainStop === 'function' ? onTrainStop : null;",
        "    case 'redstone': return typeof onRedstone === 'function' ? onRedstone : null;",
        "    case 'use': return typeof onUse === 'function' ? onUse : null;",
        "    case 'message': case 'timer': return typeof onMessage === 'function' ? onMessage : null;",
        "    case 'aligned': return typeof onAligned === 'function' ? onAligned : null;",
        "  }",
        "  return null;",
        "}",
        "function __rteReport(ctx, err) { ctx.reportError(String(err), err && err.lineNumber ? err.lineNumber : -1); }",
        "function __rteDispatch(ctx, state, mech, events) {",
        "  for (var i = 0; i < events.size(); i++) {",
        "    var e = events.get(i); var fn = __rteHandler(e.type);",
        "    if (fn) { try { fn(ctx, state, mech, e); } catch (err) { __rteReport(ctx, err); } }",
        "  }",
        "  if (typeof tick === 'function') { try { tick(ctx, state, mech); } catch (err) { __rteReport(ctx, err); } }",
        "}",
        "function __rteSaveState(ctx, state) { try { ctx.storeState(JSON.stringify(state)); } catch (err) { __rteReport(ctx, err); } }",
        "function __rteRestoreState(ctx, state) { try { var o = JSON.parse(String(ctx.savedState())); for (var k in o) { state[k] = o[k]; } } catch (err) { } }");
    private static final Pattern LINE = Pattern.compile("(?:line |#|:)(\\d+)");
    private static final int SAVE_INTERVAL_TICKS = 6000;
    private static final int REBUILD_INTERVAL_TICKS = 40;
    private static final double SYNC_RANGE = 512;

    private static RailScriptRuntime instance;

    public final MinecraftServer server;
    public final RailScriptStore store = new RailScriptStore(RailScriptStore.defaultRoot());
    private final Map<String, ScriptHolderServer> holders = new HashMap<>();
    private final Map<String, Map<Integer, String>> errors = new ConcurrentHashMap<>();
    private final Map<String, LevelState> levels = new HashMap<>();
    public final MessageRateLimiter messageLimiter = new MessageRateLimiter(5, 1000);

    private RailScriptRuntime(MinecraftServer server) {
        this.server = server;
        RailScriptExamples.installIfMissing(store);
    }

    public static RailScriptRuntime get(MinecraftServer server) {
        if (instance == null || instance.server != server) {
            if (instance != null) {
                instance.shutdown();
            }
            instance = new RailScriptRuntime(server);
        }
        return instance;
    }

    public static RailScriptRuntime current() {
        return instance;
    }

    public static void onServerStopping(MinecraftServer server) {
        if (instance != null && instance.server == server) {
            instance.shutdown();
            instance = null;
        }
    }

    public static void tick(ServerLevel level) {
        try {
            get(level.getServer()).levelState(level).tick();
        } catch (Exception e) {
            RTE.LOGGER.error("[RailScript] tick failed", e);
        }
    }

    public LevelState levelState(ServerLevel level) {
        return levels.computeIfAbsent(level.dimension().location().toString(), key -> new LevelState(this, level));
    }

    public Map<Integer, String> errors(String scriptId) {
        return errors.getOrDefault(scriptId, Collections.emptyMap());
    }

    public void reportRuntimeError(String scriptId, int line, String message) {
        if (scriptId == null || scriptId.isEmpty()) {
            return;
        }
        final Map<Integer, String> map = errors.computeIfAbsent(scriptId, ignored -> new ConcurrentHashMap<>());
        final int key = Math.max(1, line);
        if (!message.equals(map.put(key, message))) {
            RTE.LOGGER.warn("[RailScript] {} line {}: {}", scriptId, key, message);
            cn.jstxjf_.rte.network.PacketRailScriptErrors.broadcast(server, scriptId, map);
        }
    }

    public synchronized ScriptHolderServer holder(String scriptId) {
        if (scriptId == null || scriptId.isEmpty()) {
            return null;
        }
        if (holders.containsKey(scriptId)) {
            return holders.get(scriptId);
        }
        ScriptHolderServer holder = null;
        final String source = store.read(scriptId, RailScriptStore.Part.SERVER).orElse(null);
        if (source != null) {
            final String meta = store.read(scriptId, RailScriptStore.Part.META).orElse("{}");
            ScriptEngineType engine = ScriptEngineType.AUTO;
            com.google.gson.JsonObject config = new com.google.gson.JsonObject();
            try {
                config = com.google.gson.JsonParser.parseString(meta).getAsJsonObject();
                if (config.has("engine")) {
                    engine = ScriptEngineType.valueOf(config.get("engine").getAsString().toUpperCase(java.util.Locale.ROOT));
                }
            } catch (Exception ignored) {
            }
            holder = new ScriptHolderServer(engine);
            final Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
            scripts.put(McCompat.id(RTE.MOD_ID, "rail_scripts/prelude.js"), PRELUDE);
            scripts.put(McCompat.id(RTE.MOD_ID, "rail_scripts/" + scriptId + ".server.js"), source);
            errors.remove(scriptId);
            try {
                holder.load("RailScript " + scriptId, "Mechanism", null, scripts, config, "rail_script:" + scriptId, SERVER_FUNCTIONS);
            } catch (Exception | Error e) {
                holder.close();
                holder = null;
                final Map<Integer, String> map = new ConcurrentHashMap<>();
                map.put(lineOf(e), String.valueOf(e.getMessage()));
                errors.put(scriptId, map);
                RTE.LOGGER.warn("[RailScript] failed loading {}: {}", scriptId, e.toString());
                cn.jstxjf_.rte.network.PacketRailScriptErrors.broadcast(server, scriptId, map);
            }
        }
        holders.put(scriptId, holder);
        return holder;
    }

    public synchronized void reload(String scriptId) {
        final ScriptHolderServer old = holders.remove(scriptId);
        for (final LevelState level : levels.values()) {
            level.disposeScript(scriptId);
        }
        if (old != null) {
            old.close();
        }
        if (errors.remove(scriptId) != null) {
            cn.jstxjf_.rte.network.PacketRailScriptErrors.broadcast(server, scriptId, Collections.emptyMap());
        }
        cn.jstxjf_.rte.network.PacketRailScriptSources.broadcastOne(server, scriptId, store.read(scriptId, RailScriptStore.Part.CLIENT).orElse(""));
    }

    public synchronized void reloadAll() {
        for (final String id : new ArrayList<>(holders.keySet())) {
            reload(id);
        }
        for (final String id : store.list()) {
            cn.jstxjf_.rte.network.PacketRailScriptSources.broadcastOne(server, id, store.read(id, RailScriptStore.Part.CLIENT).orElse(""));
        }
    }

    private void shutdown() {
        for (final LevelState level : levels.values()) {
            level.save();
            level.disposeAll();
        }
        levels.clear();
        holders.values().forEach(holder -> {
            if (holder != null) {
                holder.close();
            }
        });
        holders.clear();
        GATE_INDEX.clear();
        SPEED_OVERRIDES.clear();
        REDSTONE_OUTPUTS.clear();
    }

    static int lineOf(Throwable error) {
        final Matcher matcher = LINE.matcher(String.valueOf(error.getMessage()));
        int line = 1;
        while (matcher.find()) {
            line = Integer.parseInt(matcher.group(1));
        }
        return line;
    }

    public static String redstoneKey(String dimension, int x, int y, int z) {
        return dimension + "|" + x + "|" + y + "|" + z;
    }

    public static final class LevelState {

        private record Snapshot(Map<String, List<TrainProximityTracker.TrainSample>> samples,
                                Map<String, List<MechanismEventInfo>> approaching,
                                Map<String, Set<Long>> on,
                                Map<String, Boolean> crossing) {
        }

        public final RailScriptRuntime runtime;
        public final ServerLevel level;
        public final String dimension;
        final MechanismRegistry registry = new MechanismRegistry();
        private final Map<String, MechanismScriptContext> contexts = new HashMap<>();
        private final Map<String, TrainProximityTracker> trackers = new HashMap<>();
        private final Map<String, Boolean> alignedBefore = new HashMap<>();
        private final Map<String, Integer> redstoneBefore = new HashMap<>();
        private final Set<String> dirty = ConcurrentHashMap.newKeySet();
        private final Map<String, Long> lastBroadcast = new HashMap<>();
        private volatile List<MechanismRegistry.RailInfo> pendingRails;
        private volatile Snapshot snapshot = new Snapshot(Map.of(), Map.of(), Map.of(), Map.of());
        private final Map<String, Rail> serverRails = new ConcurrentHashMap<>();
        private final Set<String> ownedGateIds = new HashSet<>();
        private final Map<String, int[]> heldSignals = new ConcurrentHashMap<>();
        private long ticks;
        private boolean loaded;

        LevelState(RailScriptRuntime runtime, ServerLevel level) {
            this.runtime = runtime;
            this.level = level;
            this.dimension = level.dimension().location().toString();
        }

        private Path saveFile() {
            return level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                .resolve("data").resolve("rte_mechanisms_" + dimension.replace(':', '_').replace('/', '_') + ".json");
        }

        void save() {
            try {
                for (final MechanismScriptContext context : contexts.values()) {
                    final ScriptHolderServer holder = runtime.holder(context.mechanism.scriptId);
                    if (holder != null && context.created) {
                        holder.tryCallFunctionAsync("__rteSaveState", context, null);
                    }
                }
                final Path file = saveFile();
                Files.createDirectories(file.getParent());
                Files.writeString(file, registry.toJson(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailScript] failed saving mechanisms for {}: {}", dimension, e.toString());
            }
        }

        private void load() {
            loaded = true;
            try {
                final Path file = saveFile();
                if (Files.isRegularFile(file)) {
                    registry.loadJson(Files.readString(file, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailScript] failed loading mechanisms for {}: {}", dimension, e.toString());
            }
        }

        void tick() throws Exception {
            if (!loaded) {
                load();
            }
            final org.mtr.core.simulation.Simulator simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(level);
            if (simulator == null) {
                return;
            }
            ticks++;
            if (ticks % REBUILD_INTERVAL_TICKS == 1) {
                simulator.run(() -> pendingRails = collectRails(simulator));
            }
            final List<MechanismRegistry.RailInfo> rails = pendingRails;
            if (rails != null) {
                pendingRails = null;
                registry.rebuild(rails);
                refreshGateIndex();
            }
            if (registry.all().isEmpty()) {
                return;
            }
            simulator.run(() -> snapshot = scanVehicles(simulator));
            final Map<String, int[]> held = new HashMap<>(heldSignals);
            final long signalNow = System.currentTimeMillis();
            for (final Mechanism mechanism : registry.all()) {
                if (mechanism.locked || mechanism.isMoving(signalNow) || !MechanismGate.state(mechanism, signalNow).equals("aligned")) {
                    for (final String id : mechanism.candidateIds()) {
                        held.putIfAbsent(id, new int[0]);
                    }
                }
            }
            if (!held.isEmpty()) {
                simulator.run(() -> held.forEach((id, colors) -> {
                    final Rail rail = simulator.railIdMap.get(id);
                    if (rail != null) {
                        final org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList list = new org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList();
                        for (final int color : colors) {
                            list.add(color);
                        }
                        rail.blockRail(list);
                    }
                }));
            }
            final long now = System.currentTimeMillis();
            final Snapshot current = snapshot;
            for (final Mechanism mechanism : registry.all()) {
                advance(mechanism, current, now);
                applyToRails(mechanism, now);
                collectEvents(mechanism, current, now);
                runScript(mechanism, now);
                broadcastIfNeeded(mechanism, now);
            }
            pruneContexts();
            if (ticks % SAVE_INTERVAL_TICKS == 0) {
                save();
            }
        }

        private List<MechanismRegistry.RailInfo> collectRails(org.mtr.core.simulation.Simulator simulator) {
            final List<MechanismRegistry.RailInfo> result = new ArrayList<>();
            serverRails.clear();
            for (final Rail rail : simulator.railIdMap.values()) {
                if (!(((Object) rail) instanceof RailExtraSupplier extra) || extra.getMechanismGroup().isEmpty()) {
                    continue;
                }
                serverRails.put(rail.getHexId(), rail);
                MechanismTransform.SUPPRESS.set(true);
                try {
                    final double length = extra.getLength();
                    final net.minecraft.world.phys.Vec3 a = extra.getPosition(0);
                    final net.minecraft.world.phys.Vec3 b = extra.getPosition(length);
                    result.add(new MechanismRegistry.RailInfo(rail.getHexId(), extra.getMechanismGroup(), extra.getRailScriptId(),
                        new RailPose(a.x, a.y, a.z, b.x, b.y, b.z)));
                } finally {
                    MechanismTransform.SUPPRESS.set(false);
                }
            }
            return result;
        }

        private void refreshGateIndex() {
            ownedGateIds.forEach(GATE_INDEX::remove);
            ownedGateIds.clear();
            for (final Mechanism mechanism : registry.all()) {
                for (final String id : mechanism.candidateIds()) {
                    GATE_INDEX.put(id, mechanism);
                    ownedGateIds.add(id);
                }
            }
        }

        private Snapshot scanVehicles(org.mtr.core.simulation.Simulator simulator) {
            final Map<String, List<TrainProximityTracker.TrainSample>> samples = new HashMap<>();
            final Map<String, List<MechanismEventInfo>> approaching = new HashMap<>();
            final Map<String, Set<Long>> on = new HashMap<>();
            final Map<String, Boolean> crossing = new HashMap<>();
            for (final Mechanism mechanism : registry.all()) {
                samples.put(mechanism.group, new ArrayList<>());
                approaching.put(mechanism.group, new ArrayList<>());
                on.put(mechanism.group, new HashSet<>());
            }
            for (final Siding siding : simulator.sidings) {
                for (final Vehicle vehicle : cn.jstxjf_.rte.compat.SidingCompat.vehicles(siding)) {
                    scanVehicle(vehicle, samples, approaching, on, crossing);
                }
            }
            return new Snapshot(samples, approaching, on, crossing);
        }

        private void scanVehicle(Vehicle vehicle, Map<String, List<TrainProximityTracker.TrainSample>> samples,
                                 Map<String, List<MechanismEventInfo>> approaching, Map<String, Set<Long>> on, Map<String, Boolean> crossing) {
            final List<PathData> path = vehicle.vehicleExtraData.immutablePath;
            if (path.isEmpty()) {
                return;
            }
            final double progress = ((cn.jstxjf_.rte.mixin.VehicleAccessor) (Object) vehicle).getRailProgress();
            final double speed = ((cn.jstxjf_.rte.mixin.VehicleAccessor) (Object) vehicle).getSpeed();
            final double tail = progress - vehicle.vehicleExtraData.getTotalVehicleLength();
            final int head = MechanismVehicles.indexAt(path, progress);
            final Set<String> touched = new HashSet<>();
            boolean anyOutside = false;
            for (int i = Math.max(0, MechanismVehicles.indexAt(path, tail)); i <= head && i < path.size(); i++) {
                final PathData data = path.get(i);
                if (data.getEndDistance() <= tail || data.getStartDistance() >= progress) {
                    continue;
                }
                final Mechanism mechanism = GATE_INDEX.get(data.getRail().getHexId());
                if (mechanism != null && registry.get(mechanism.group) == mechanism) {
                    touched.add(mechanism.group);
                } else {
                    anyOutside = true;
                }
            }
            final Set<String> reported = new HashSet<>(touched);
            for (final String group : touched) {
                on.get(group).add(vehicle.getId());
                if (anyOutside) {
                    crossing.put(group, true);
                }
                final String rail = MechanismVehicles.railOn(path, head, group);
                samples.get(group).add(new TrainProximityTracker.TrainSample(vehicle.getId(), rail, 0, true, anyOutside, speed == 0));
            }
            for (int i = head; i < path.size(); i++) {
                final PathData data = path.get(i);
                final double distance = data.getStartDistance() - progress;
                if (distance > 2048) {
                    break;
                }
                final Mechanism mechanism = GATE_INDEX.get(data.getRail().getHexId());
                if (mechanism == null || reported.contains(mechanism.group) || registry.get(mechanism.group) == null || distance > mechanism.approachRange) {
                    continue;
                }
                reported.add(mechanism.group);
                final String rail = data.getRail().getHexId();
                samples.get(mechanism.group).add(new TrainProximityTracker.TrainSample(vehicle.getId(), rail, Math.max(0, distance), false, false, speed == 0));
                final MechanismEventInfo info = trainInfo("approach", vehicle, Math.max(0, distance), speed, rail);
                approaching.get(mechanism.group).add(info);
            }
        }

        private static MechanismEventInfo trainInfo(String type, Vehicle vehicle, double distance, double speed, String rail) {
            final MechanismEventInfo info = new MechanismEventInfo(type);
            info.train = vehicle.getId();
            info.distance = distance;
            info.eta = speed > 0 ? Math.round(distance / speed) : -1;
            info.requiredRail = rail;
            info.rail = rail;
            info.nextStation = vehicle.vehicleExtraData.getThisStationName();
            info.destination = vehicle.vehicleExtraData.getThisRouteDestination();
            info.routeName = vehicle.vehicleExtraData.getThisRouteName();
            return info;
        }

        private void advance(Mechanism mechanism, Snapshot current, long now) {
            synchronized (mechanism) {
                if (mechanism.isMoving(now)) {
                    return;
                }
                if (mechanism.animDurationMillis > 0) {
                    mechanism.finishAnimation();
                    markDirty(mechanism);
                }
                final boolean deckOccupied = !current.on().getOrDefault(mechanism.group, Set.of()).isEmpty();
                if (!mechanism.targetRailId.isEmpty() && !mechanism.targetRailId.equals(mechanism.activeRailId)) {
                    mechanism.activeRailId = mechanism.targetRailId;
                    markDirty(mechanism);
                }
                if (!deckOccupied && !mechanism.deckRailId.equals(mechanism.activeRailId)) {
                    mechanism.deckRailId = mechanism.activeRailId;
                    mechanism.setImmediate(MechanismTransform.IDENTITY);
                    markDirty(mechanism);
                }
            }
        }

        private void applyToRails(Mechanism mechanism, long now) {
            final MechanismTransform transform;
            final String deck;
            synchronized (mechanism) {
                transform = mechanism.sample(now);
                deck = mechanism.deckRailId;
            }
            for (final String id : mechanism.candidateIds()) {
                final Rail rail = serverRails.get(id);
                if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                    continue;
                }
                final boolean isDeck = id.equals(deck);
                extra.setMechanismTransform(isDeck ? transform : MechanismTransform.IDENTITY);
                extra.setMechanismHidden(!isDeck);
            }
        }

        private void collectEvents(Mechanism mechanism, Snapshot current, long now) {
            final MechanismScriptContext context = context(mechanism);
            if (context == null) {
                return;
            }
            final TrainProximityTracker tracker = trackers.computeIfAbsent(mechanism.group, ignored -> new TrainProximityTracker());
            final List<TrainProximityTracker.TrainSample> samples = current.samples().getOrDefault(mechanism.group, List.of());
            final Map<Long, MechanismEventInfo> approachInfo = new HashMap<>();
            for (final MechanismEventInfo info : current.approaching().getOrDefault(mechanism.group, List.of())) {
                approachInfo.put(info.train, info);
            }
            for (final MechanismEvent event : tracker.update(samples, mechanism.approachRange)) {
                final MechanismEventInfo info;
                switch (event.type()) {
                    case APPROACH -> info = approachInfo.getOrDefault(event.trainId(), new MechanismEventInfo("approach"));
                    case ENTER -> info = new MechanismEventInfo("enter");
                    case LEAVE -> info = new MechanismEventInfo("leave");
                    default -> info = new MechanismEventInfo("stop");
                }
                info.train = event.trainId();
                if (info.rail.isEmpty()) {
                    info.rail = event.rail();
                    info.requiredRail = event.rail();
                }
                context.queue(info);
            }
            final boolean aligned = MechanismGate.state(mechanism, now).equals("aligned");
            final Boolean before = alignedBefore.put(mechanism.group, aligned);
            if (before != null && before != aligned) {
                final MechanismEventInfo info = new MechanismEventInfo("aligned");
                info.aligned = aligned;
                info.rail = mechanism.activeRailId;
                context.queue(info);
            }
            for (final String id : mechanism.candidateIds()) {
                final Rail rail = serverRails.get(id);
                if (rail == null) {
                    continue;
                }
                for (final Position position : new Position[]{
                    ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition1(),
                    ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition2()}) {
                    if (position == null) {
                        continue;
                    }
                    final BlockPos pos = new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
                    if (!level.isLoaded(pos)) {
                        continue;
                    }
                    final int power = level.getBestNeighborSignal(pos);
                    final String key = redstoneKey(dimension, pos.getX(), pos.getY(), pos.getZ());
                    final Integer old = redstoneBefore.put(key, power);
                    if (old != null && old != power) {
                        final MechanismEventInfo info = new MechanismEventInfo("redstone");
                        info.x = pos.getX();
                        info.y = pos.getY();
                        info.z = pos.getZ();
                        info.oldPower = old;
                        info.newPower = power;
                        info.rail = id;
                        context.queue(info);
                    }
                }
            }
        }

        private MechanismScriptContext context(Mechanism mechanism) {
            if (mechanism.scriptId.isEmpty() || runtime.holder(mechanism.scriptId) == null) {
                return null;
            }
            return contexts.computeIfAbsent(mechanism.group, ignored -> new MechanismScriptContext(this, mechanism));
        }

        private void runScript(Mechanism mechanism, long now) {
            final MechanismScriptContext context = context(mechanism);
            if (context == null) {
                return;
            }
            final ScriptHolderServer holder = runtime.holder(mechanism.scriptId);
            if (holder == null || (context.scriptStatus != null && !context.scriptStatus.isDone())) {
                return;
            }
            if (!context.restored) {
                holder.tryCallFunctionAsync("__rteRestoreState", context, () -> context.restored = true);
            } else if (!context.created) {
                holder.tryCallFunctionAsync("create", context, () -> context.created = true);
            } else {
                holder.tryCallFunctionAsync("__rteDispatch", context, null, context.drainEvents(now));
            }
        }

        private void broadcastIfNeeded(Mechanism mechanism, long now) {
            final MechanismScriptContext context = contexts.get(mechanism.group);
            final String sync = context == null ? null : context.pendingSync;
            final boolean syncChanged = sync != null && !sync.equals(context.lastSync)
                && now - context.lastSyncMillis >= MechanismScriptContext.SYNC_INTERVAL_MILLIS;
            final long last = lastBroadcast.getOrDefault(mechanism.group, 0L);
            final boolean periodic = mechanism.isMoving(now) && now - last >= MechanismScriptContext.SYNC_INTERVAL_MILLIS;
            if (!dirty.remove(mechanism.group) && !syncChanged && !periodic && now - last < 5000) {
                return;
            }
            if (syncChanged) {
                context.lastSync = sync;
                context.lastSyncMillis = now;
            }
            lastBroadcast.put(mechanism.group, now);
            final cn.jstxjf_.rte.network.PacketMechanismState packet =
                cn.jstxjf_.rte.network.PacketMechanismState.of(dimension, mechanism, context == null ? "" : context.lastSync, now);
            final RailPose deck = mechanism.candidates.get(mechanism.deckRailId);
            for (final ServerPlayer player : level.players()) {
                if (deck == null || player.distanceToSqr(deck.midX(), deck.midY(), deck.midZ()) <= SYNC_RANGE * SYNC_RANGE) {
                    cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, packet);
                }
            }
        }

        public void sendAllTo(ServerPlayer player) {
            final long now = System.currentTimeMillis();
            for (final Mechanism mechanism : registry.all()) {
                final MechanismScriptContext context = contexts.get(mechanism.group);
                cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player,
                    cn.jstxjf_.rte.network.PacketMechanismState.of(dimension, mechanism, context == null ? "" : context.lastSync, now));
            }
        }

        private void pruneContexts() {
            contexts.entrySet().removeIf(entry -> {
                final Mechanism mechanism = registry.get(entry.getKey());
                if (mechanism != null && mechanism == entry.getValue().mechanism && mechanism.scriptId.equals(entry.getValue().mechanism.scriptId)) {
                    return false;
                }
                dispose(entry.getValue());
                return true;
            });
        }

        void disposeScript(String scriptId) {
            contexts.entrySet().removeIf(entry -> {
                if (!entry.getValue().mechanism.scriptId.equals(scriptId)) {
                    return false;
                }
                entry.getValue().mechanism.candidateIds().forEach(heldSignals::remove);
                dispose(entry.getValue());
                return true;
            });
        }

        void disposeAll() {
            heldSignals.clear();
            contexts.values().forEach(this::dispose);
            contexts.clear();
            ownedGateIds.forEach(GATE_INDEX::remove);
            ownedGateIds.clear();
        }

        private void dispose(MechanismScriptContext context) {
            context.alive = false;
            final ScriptHolderServer holder = runtime.holders.get(context.mechanism.scriptId);
            if (holder != null && context.created) {
                holder.tryCallFunctionAsync("__rteSaveState", context, null);
                holder.tryCallDisposeFunctionAsync(context);
            }
        }

        public void markDirty(Mechanism mechanism) {
            dirty.add(mechanism.group);
        }

        boolean anyTrainCrossing(Mechanism mechanism) {
            return snapshot.crossing().getOrDefault(mechanism.group, false);
        }

        boolean anyTrainOn(Mechanism mechanism) {
            return !snapshot.on().getOrDefault(mechanism.group, Set.of()).isEmpty();
        }

        Set<Long> trainsOn(Mechanism mechanism) {
            return snapshot.on().getOrDefault(mechanism.group, Set.of());
        }

        List<MechanismEventInfo> approaching(Mechanism mechanism) {
            return new ArrayList<>(snapshot.approaching().getOrDefault(mechanism.group, List.of()));
        }

        void setRailSpeedOverride(String railId, int kmh) {
            if (kmh > 0) {
                SPEED_OVERRIDES.put(railId, Math.min(kmh, 1000));
            } else {
                SPEED_OVERRIDES.remove(railId);
            }
        }

        boolean setRailCollisionLayout(String railId, String layout) {
            final Rail rail = serverRails.get(railId);
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                return false;
            }
            final String normalized = cn.jstxjf_.rte.util.RailCollisionManager.normalizeLayout(layout, extra.getLength());
            level.getServer().execute(() -> {
                final Map<String, String> configs = new HashMap<>(extra.getCustomConfigs());
                if (normalized.isEmpty()) {
                    configs.remove(cn.jstxjf_.rte.util.RailCollisionManager.CONFIG_LAYOUT);
                } else {
                    configs.put(cn.jstxjf_.rte.util.RailCollisionManager.CONFIG_LAYOUT, normalized);
                }
                extra.setCustomConfigs(configs);
                cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(level, rail);
            });
            return true;
        }

        void holdSignal(String railId, int[] colors) {
            if (railId != null && serverRails.containsKey(railId)) {
                heldSignals.put(railId, colors.clone());
            }
        }

        void releaseSignal(String railId) {
            if (railId != null) {
                heldSignals.remove(railId);
            }
        }

        boolean isHoldingSignal(String railId) {
            return railId != null && heldSignals.containsKey(railId);
        }

        int[] signalColors(String railId) {
            final Rail rail = railId == null ? null : serverRails.get(railId);
            return rail == null ? new int[0] : rail.getSignalColors().toIntArray();
        }

        void setRedstoneOutput(int x, int y, int z, int power) {
            final int clamped = Math.max(0, Math.min(15, power));
            final String key = redstoneKey(dimension, x, y, z);
            level.getServer().execute(() -> {
                final Integer old = clamped == 0 ? REDSTONE_OUTPUTS.remove(key) : REDSTONE_OUTPUTS.put(key, clamped);
                if (old == null ? clamped != 0 : old != clamped) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
                }
            });
        }

        void sendToTrain(Mechanism mechanism, long trainId, String message) {
            final cn.jstxjf_.rte.network.PacketMechanismMessage packet =
                cn.jstxjf_.rte.network.PacketMechanismMessage.toTrain(mechanism.group, trainId, message == null ? "" : message);
            level.getServer().execute(() -> {
                for (final ServerPlayer player : level.players()) {
                    cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, packet);
                }
            });
        }

        void sendToMechanism(String from, String group, String message) {
            final Mechanism target = registry.get(group);
            final MechanismScriptContext context = target == null ? null : contexts.get(group);
            if (context == null) {
                return;
            }
            final MechanismEventInfo info = new MechanismEventInfo("message");
            info.from = from;
            info.message = message == null ? "" : message;
            context.queue(info);
        }

        public boolean deliverFromTrain(String group, long trainId, String message) {
            final MechanismScriptContext context = contexts.get(group);
            if (context == null) {
                return false;
            }
            final MechanismEventInfo info = new MechanismEventInfo("message");
            info.train = trainId;
            info.from = "train";
            info.message = message;
            context.queue(info);
            return true;
        }

        public void deliverUse(String railId, ServerPlayer player, BlockPos pos) {
            final Mechanism mechanism = registry.byRail(railId);
            final MechanismScriptContext context = mechanism == null ? null : context(mechanism);
            if (context == null) {
                return;
            }
            final MechanismEventInfo info = new MechanismEventInfo("use");
            info.player = player.getGameProfile().getName();
            info.item = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
            info.x = pos.getX();
            info.y = pos.getY();
            info.z = pos.getZ();
            info.rail = railId;
            context.queue(info);
        }

        public Mechanism mechanismAt(BlockPos pos) {
            for (final Mechanism mechanism : registry.all()) {
                for (final String id : mechanism.candidateIds()) {
                    final Rail rail = serverRails.get(id);
                    if (rail == null) {
                        continue;
                    }
                    final Position p1 = ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition1();
                    final Position p2 = ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition2();
                    if (matches(p1, pos) || matches(p2, pos)) {
                        return mechanism;
                    }
                }
            }
            return null;
        }

        private static boolean matches(Position position, BlockPos pos) {
            return position != null && position.getX() == pos.getX() && position.getY() == pos.getY() && position.getZ() == pos.getZ();
        }

        public Map<String, Object> debugRows() {
            final Map<String, Object> rows = new TreeMap<>();
            final long now = System.currentTimeMillis();
            for (final Mechanism mechanism : registry.all()) {
                rows.put(mechanism.group, mechanism.scriptId + " " + MechanismGate.state(mechanism, now) + " active=" + mechanism.activeRailId);
            }
            return rows;
        }
    }
}
