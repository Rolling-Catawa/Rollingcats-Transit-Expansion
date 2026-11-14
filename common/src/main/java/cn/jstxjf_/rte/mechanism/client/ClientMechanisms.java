package cn.jstxjf_.rte.mechanism.client;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.mechanism.MechanismTransform;
import cn.jstxjf_.rte.mechanism.RailPose;
import cn.jstxjf_.rte.network.PacketMechanismState;
import cn.jstxjf_.rte.scripting.ScriptHolderClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Rail;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import cn.jstxjf_.rte.compat.McCompat;

public final class ClientMechanisms {

    public static final class View {
        private final Entry entry;

        View(Entry entry) {
            this.entry = entry;
        }

        public String group() {
            return entry.state.group;
        }

        public String scriptId() {
            return entry.state.scriptId;
        }

        public String state() {
            return entry.state.state;
        }

        public String activeRail() {
            return entry.state.activeRail;
        }

        public String deckRail() {
            return entry.deckRail();
        }

        public String targetRail() {
            return entry.state.targetRail;
        }

        public boolean isMoving() {
            return entry.remaining(System.currentTimeMillis()) > 0;
        }

        public boolean isLocked() {
            return entry.state.locked;
        }

        public MechanismTransform getTransform() {
            return entry.current;
        }

        public List<String> candidates() {
            return new ArrayList<>(entry.state.candidates);
        }

        public String syncedState() {
            return entry.synced();
        }
    }

    public static final class Entry {
        PacketMechanismState state;
        long receivedAt;
        MechanismTransform start = MechanismTransform.IDENTITY;
        MechanismTransform target = MechanismTransform.IDENTITY;
        MechanismTransform current = MechanismTransform.IDENTITY;
        final Map<String, RailPose> poses = new HashMap<>();
        final View view = new View(this);
        MechanismClientContext context;

        long remaining(long now) {
            return Math.max(0, state.remainingMillis - (now - receivedAt));
        }

        MechanismTransform sample(long now) {
            final long remaining = remaining(now);
            if (remaining <= 0 || state.durationMillis <= 0) {
                return target;
            }
            double t = 1 - remaining / (double) state.durationMillis;
            t = Math.max(0, Math.min(1, t));
            if (state.easeInOut) {
                t = t * t * (3 - 2 * t);
            }
            return start.lerp(target, t);
        }

        String deckRail() {
            return state.deckRail;
        }

        RailPose origin() {
            return poses.get(state.deckRail);
        }

        RailPose pose(String railId) {
            return poses.get(railId);
        }

        String synced() {
            return state.synced;
        }
    }

    private record TrainMessage(String group, String message) {
    }

    private static final Map<String, Entry> ENTRIES = new ConcurrentHashMap<>();
    private static final Map<String, String> SOURCES = new ConcurrentHashMap<>();
    private static final Map<String, ScriptHolderClient> HOLDERS = new HashMap<>();
    private static final Map<Long, List<TrainMessage>> TRAIN_MESSAGES = new ConcurrentHashMap<>();
    private static final Map<String, Entry> BY_RAIL = new ConcurrentHashMap<>();
    private static Object lastLevel;
    private static long nextOrphanScan;
    private static final List<Rail> ORPHANS = new ArrayList<>();

    private ClientMechanisms() {
    }

    public static void accept(PacketMechanismState packet) {
        final Level level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().location().toString().equals(packet.dimension)) {
            return;
        }
        final long now = System.currentTimeMillis();
        final Entry entry = ENTRIES.computeIfAbsent(packet.group, ignored -> new Entry());
        final Entry previous = entry.state == null ? null : entry;
        entry.state = packet;
        entry.receivedAt = now;
        entry.start = MechanismTransform.decode(packet.start);
        entry.target = MechanismTransform.decode(packet.target);
        if (previous == null) {
            entry.current = entry.sample(now);
        }
        for (final String id : packet.candidates) {
            BY_RAIL.put(id, entry);
        }
        refreshPoses(entry);
    }

    public static void acceptSources(boolean replaceAll, Map<String, String> sources) {
        Minecraft.getInstance().execute(() -> {
            if (replaceAll) {
                SOURCES.clear();
                HOLDERS.values().forEach(holder -> {
                    if (holder != null) {
                        holder.close();
                    }
                });
                HOLDERS.clear();
                ENTRIES.values().forEach(ClientMechanisms::disposeContext);
            }
            sources.forEach((id, source) -> {
                SOURCES.put(id, source);
                final ScriptHolderClient old = HOLDERS.remove(id);
                if (old != null) {
                    old.close();
                }
                ENTRIES.values().stream().filter(entry -> entry.state != null && entry.state.scriptId.equals(id))
                    .forEach(ClientMechanisms::disposeContext);
            });
        });
    }

    public static void deliverToTrain(String group, long trainId, String message) {
        final List<TrainMessage> list = TRAIN_MESSAGES.computeIfAbsent(trainId, ignored -> new ArrayList<>());
        synchronized (list) {
            if (list.size() < 64) {
                list.add(new TrainMessage(group, message));
            }
        }
    }

    public static List<Map<String, String>> pollTrainMessages(long trainId) {
        final List<TrainMessage> list = TRAIN_MESSAGES.remove(trainId);
        final List<Map<String, String>> result = new ArrayList<>();
        if (list != null) {
            synchronized (list) {
                for (final TrainMessage message : list) {
                    final Map<String, String> map = new LinkedHashMap<>();
                    map.put("group", message.group());
                    map.put("message", message.message());
                    result.add(map);
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> railScriptsAhead(List<PathData> path, double railProgress, double distance) {
        final List<Map<String, Object>> result = new ArrayList<>();
        if (path == null || path.isEmpty() || BY_RAIL.isEmpty()) {
            return result;
        }
        final long now = System.currentTimeMillis();
        final java.util.Set<String> seen = new java.util.HashSet<>();
        for (int i = cn.jstxjf_.rte.mechanism.MechanismVehicles.indexAt(path, railProgress); i < path.size(); i++) {
            final PathData data = path.get(i);
            final double ahead = data.getStartDistance() - railProgress;
            if (ahead > distance) {
                break;
            }
            final String railId = data.getRail().getHexId();
            final Entry entry = BY_RAIL.get(railId);
            if (entry == null || entry.state == null || !seen.add(entry.state.group)) {
                continue;
            }
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put("group", entry.state.group);
            row.put("scriptId", entry.state.scriptId);
            row.put("distance", Math.max(0, ahead));
            row.put("state", entry.remaining(now) > 0 ? "moving" : entry.state.state);
            row.put("yaw", entry.current.yaw);
            row.put("etaAlignedMillis", entry.remaining(now));
            row.put("requiredRail", railId);
            row.put("activeRail", entry.state.activeRail);
            result.add(row);
        }
        return result;
    }

    public static Iterable<Rail> renderRails() {
        final Level level = Minecraft.getInstance().level;
        if (level != lastLevel) {
            lastLevel = level;
            clear();
        }
        final long now = System.currentTimeMillis();
        final MinecraftClientData data = MinecraftClientData.getInstance();
        final List<Rail> rails = new ArrayList<>();
        for (final Entry entry : ENTRIES.values()) {
            if (entry.state == null) {
                continue;
            }
            entry.current = entry.sample(now);
            for (final String id : entry.state.candidates) {
                final Rail rail = data.railIdMap.get(id);
                if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                    continue;
                }
                final boolean deck = id.equals(entry.state.deckRail);
                extra.setMechanismTransform(deck ? entry.current : MechanismTransform.IDENTITY);
                extra.setMechanismHidden(!deck);
                rails.add(rail);
            }
            if (entry.poses.size() != entry.state.candidates.size()) {
                refreshPoses(entry);
            }
        }
        if (now >= nextOrphanScan) {
            nextOrphanScan = now + 1000;
            ORPHANS.clear();
            for (final Rail rail : data.railIdMap.values()) {
                if (((Object) rail) instanceof RailExtraSupplier extra && !extra.getMechanismGroup().isEmpty()
                    && !BY_RAIL.containsKey(rail.getHexId())) {
                    ORPHANS.add(rail);
                }
            }
        }
        rails.addAll(ORPHANS);
        return rails;
    }

    public static void renderScripts(Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix) {
        for (final Entry entry : ENTRIES.values()) {
            if (entry.state == null || entry.state.scriptId.isEmpty()) {
                continue;
            }
            final ScriptHolderClient holder = holder(entry.state.scriptId);
            final RailPose origin = entry.origin();
            if (holder == null || origin == null) {
                continue;
            }
            if (entry.context == null || entry.context.disposed) {
                entry.context = new MechanismClientContext(entry);
            }
            final Matrix4f fixedPose = viewMatrix.copy();
            fixedPose.translate((float) origin.midX(), (float) origin.midY(), (float) origin.midZ());
            final Matrix4f movingPose = viewMatrix.copy();
            movingPose.multiply(cn.jstxjf_.rte.render.rail.MechanismRailRenderer.toMatrix(entry.current));
            movingPose.translate((float) origin.midX(), (float) origin.midY(), (float) origin.midZ());
            final BlockPos lightPos = cn.jstxjf_.rte.compat.McCompat.blockPos(origin.midX(), origin.midY() + 1, origin.midZ());
            final int light = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, lightPos), level.getBrightness(LightLayer.SKY, lightPos));
            entry.context.commit(drawScheduler, movingPose, fixedPose, viewMatrix, light);
            holder.tryCallRenderFunctionAsync(entry.context);
        }
    }

    private static ScriptHolderClient holder(String scriptId) {
        if (HOLDERS.containsKey(scriptId)) {
            return HOLDERS.get(scriptId);
        }
        final String source = SOURCES.get(scriptId);
        ScriptHolderClient holder = null;
        if (source != null && !source.isBlank()) {
            holder = new ScriptHolderClient();
            final Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
            scripts.put(McCompat.id(RTE.MOD_ID, "rail_scripts/" + scriptId + ".client.js"), source);
            try {
                holder.load("RailScript " + scriptId + " (client)", "Mechanism", Minecraft.getInstance().getResourceManager(),
                    scripts, null, "rail_script_client:" + scriptId, "create", "render", "dispose");
            } catch (Exception | Error e) {
                RTE.LOGGER.warn("[RailScript] client half of {} failed to load: {}", scriptId, e.toString());
                holder.close();
                holder = null;
            }
        }
        HOLDERS.put(scriptId, holder);
        return holder;
    }

    private static void refreshPoses(Entry entry) {
        final MinecraftClientData data = MinecraftClientData.getInstance();
        MechanismTransform.SUPPRESS.set(true);
        try {
            for (final String id : entry.state.candidates) {
                final Rail rail = data.railIdMap.get(id);
                if (((Object) rail) instanceof RailExtraSupplier extra) {
                    final double length = extra.getLength();
                    final net.minecraft.world.phys.Vec3 a = extra.getPosition(0);
                    final net.minecraft.world.phys.Vec3 b = extra.getPosition(length);
                    entry.poses.put(id, new RailPose(a.x, a.y, a.z, b.x, b.y, b.z));
                }
            }
        } finally {
            MechanismTransform.SUPPRESS.set(false);
        }
    }

    private static void disposeContext(Entry entry) {
        if (entry.context != null) {
            entry.context.alive = false;
            entry.context.disposed = true;
            entry.context = null;
        }
    }

    public static Map<String, String> debugRows() {
        final Map<String, String> rows = new java.util.TreeMap<>();
        final long now = System.currentTimeMillis();
        ENTRIES.forEach((group, entry) -> {
            if (entry.state != null) {
                rows.put(group, entry.state.scriptId + " " + (entry.remaining(now) > 0 ? "moving" : entry.state.state)
                    + String.format(java.util.Locale.ROOT, " yaw=%.1f", entry.current.yaw) + " active=" + entry.state.activeRail
                    + (HOLDERS.get(entry.state.scriptId) == null && SOURCES.containsKey(entry.state.scriptId)
                    && !SOURCES.get(entry.state.scriptId).isBlank() ? " client-load-failed" : ""));
            }
        });
        return rows;
    }

    public static void clear() {
        ENTRIES.values().forEach(ClientMechanisms::disposeContext);
        ENTRIES.clear();
        BY_RAIL.clear();
        TRAIN_MESSAGES.clear();
        ORPHANS.clear();
    }
}
