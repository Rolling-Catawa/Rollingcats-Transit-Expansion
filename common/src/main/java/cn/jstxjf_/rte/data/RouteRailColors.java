package cn.jstxjf_.rte.data;

import org.mtr.core.data.Platform;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.SimplifiedRoute;
import org.mtr.core.data.SimplifiedRoutePlatform;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiConsumer;

public final class RouteRailColors {

    private static final int MAX_SEARCH_NODES = 20000;
    private static final int NODES_PER_FRAME = 900;

    private static final Map<String, Integer> RAIL_COLORS = new HashMap<>();
    private static final Map<String, Integer> WORKING_COLORS = new HashMap<>();
    private static final Deque<Leg> PENDING = new ArrayDeque<>();

    private static Leg activeLeg;
    private static PriorityQueue<Node> openQueue;
    private static Set<Position> openSet;
    private static Map<Position, Double> gScore;
    private static Map<Position, Position> cameFrom;
    private static Set<Position> closedSet;
    private static Set<Position> targetSet;
    private static Map<Position, Map<Position, Rail>> undirected;
    private static Map<Position, Map<Position, Rail>> directed;
    private static Position reachedTarget;
    private static int legExpanded;
    private static int legTotal;
    private static int legDone;
    private static int totalExpanded;
    private static int totalTagged;
    private static int failedLegs;
    private static int relaxedLegs;
    private static long jobStartMillis;
    private static long lastFinishMillis;
    private static boolean running;

    private static long lastBuildTime = 0;
    private static int lastRouteCount = -1;
    private static int lastRailCount = -1;

    private RouteRailColors() {
    }

    private static final class Leg {
        final String routeName;
        final int color;
        final List<Position> from;
        final List<Position> to;
        final int index;
        final int count;
        final boolean relaxed;

        Leg(String routeName, int color, List<Position> from, List<Position> to, int index, int count) {
            this(routeName, color, from, to, index, count, false);
        }

        Leg(String routeName, int color, List<Position> from, List<Position> to, int index, int count, boolean relaxed) {
            this.routeName = routeName;
            this.color = color;
            this.from = from;
            this.to = to;
            this.index = index;
            this.count = count;
            this.relaxed = relaxed;
        }

        Leg asRelaxed() {
            return new Leg(routeName, color, from, to, index, count, true);
        }
    }

    private static final class Node implements Comparable<Node> {
        final Position position;
        final double priority;

        Node(Position position, double priority) {
            this.position = position;
            this.priority = priority;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(priority, other.priority);
        }
    }

    public static Integer colorOf(Rail rail) {
        if (rail == null) {
            return null;
        }
        try {
            final String id = rail.getHexId();
            final Integer working = WORKING_COLORS.get(id);
            return working != null ? working : RAIL_COLORS.get(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getActiveColor() {
        return activeLeg == null ? 0xFFFFFFFF : activeLeg.color;
    }

    public static void forEachExploredNode(BiConsumer<Position, Boolean> consumer) {
        if (!running || closedSet == null) {
            return;
        }
        for (final Position position : closedSet) {
            consumer.accept(position, Boolean.FALSE);
        }
        if (openSet != null) {
            for (final Position position : openSet) {
                consumer.accept(position, Boolean.TRUE);
            }
        }
    }

    public static void refreshIfStale() {
        try {
            if (running) {
                advance();
                return;
            }
            final MinecraftClientData data = pickData();
            final int routeCount = data.simplifiedRouteIdMap.size();
            final int railCount = data.rails.size();
            final long now = System.currentTimeMillis();
            if (now - lastBuildTime < 5000) {
                return;
            }
            final boolean routesChanged = routeCount != lastRouteCount;
            final boolean railsChanged = Math.abs(railCount - lastRailCount) > Math.max(4, lastRailCount / 20);
            final boolean expired = now - lastBuildTime > 30000;
            if (!routesChanged && !railsChanged && !expired) {
                return;
            }
            lastRouteCount = routeCount;
            lastRailCount = railCount;
            lastBuildTime = now;
            startJob(data);
        } catch (Exception ignored) {
        }
    }

    public static void forceRefresh() {
        try {
            abortJob();
            lastBuildTime = System.currentTimeMillis();
            startJob(pickData());
        } catch (Exception ignored) {
        }
    }

    private static MinecraftClientData pickData() {
        final MinecraftClientData world = MinecraftClientData.getInstance();
        MinecraftClientData dashboard = null;
        try {
            dashboard = MinecraftClientData.getDashboardInstance();
        } catch (Exception ignored) {
        }
        if (dashboard == null || dashboard == world) {
            return world;
        }
        return dataScore(dashboard) > dataScore(world) ? dashboard : world;
    }

    private static int dataScore(MinecraftClientData data) {
        try {
            final var graph = data.positionsToRail;
            if (graph == null || graph.isEmpty()) {
                return 0;
            }
            return data.simplifiedRouteIdMap.isEmpty() ? 1 : 2;
        } catch (Exception e) {
            return 0;
        }
    }

    private static void startJob(MinecraftClientData data) {
        abortJob();
        final var graph = data.positionsToRail;
        if (graph == null || graph.isEmpty()) {
            return;
        }
        undirected = buildUndirected(graph);
        directed = buildDirected(graph);
        tagGeneratedPaths(data);

        int routeCount = 0;
        for (final SimplifiedRoute route : data.simplifiedRouteIdMap.values()) {
            if (route == null) {
                continue;
            }
            final int color = 0xFF000000 | (route.getColor() & 0xFFFFFF);
            final String name = routeName(route);
            final List<List<Position>> stops = new ArrayList<>();
            for (final SimplifiedRoutePlatform routePlatform : route.getPlatforms()) {
                final Platform platform = data.platformIdMap.get(routePlatform.getPlatformId());
                if (platform != null) {
                    final List<Position> ends = endpointsOf(platform);
                    if (!ends.isEmpty()) {
                        stops.add(ends);
                    }
                }
            }
            if (stops.size() < 2) {
                continue;
            }
            routeCount++;
            for (final List<Position> stop : stops) {
                tagPlatformRail(stop, color);
            }
            final int legCount = stops.size();
            for (int i = 0; i < legCount - 1; i++) {
                PENDING.add(new Leg(name, color, stops.get(i), stops.get(i + 1), i, legCount));
            }
            PENDING.add(new Leg(name, color, stops.get(stops.size() - 1), stops.get(0), legCount - 1, legCount));
        }

        legTotal = PENDING.size();
        if (legTotal == 0) {
            return;
        }
        running = true;
        jobStartMillis = System.currentTimeMillis();
        advance();
    }

    private static Map<Position, Map<Position, Rail>> buildUndirected(
        Object2ObjectOpenHashMap<Position, Object2ObjectOpenHashMap<Position, Rail>> graph) {
        final Map<Position, Map<Position, Rail>> result = new HashMap<>();
        for (final var entry : graph.entrySet()) {
            final Position from = entry.getKey();
            for (final var neighbour : entry.getValue().entrySet()) {
                final Position to = neighbour.getKey();
                final Rail rail = neighbour.getValue();
                result.computeIfAbsent(from, key -> new HashMap<>()).putIfAbsent(to, rail);
                result.computeIfAbsent(to, key -> new HashMap<>()).putIfAbsent(from, rail);
            }
        }
        return result;
    }

    private static Map<Position, Map<Position, Rail>> buildDirected(
        Object2ObjectOpenHashMap<Position, Object2ObjectOpenHashMap<Position, Rail>> graph) {
        final Map<Position, Map<Position, Rail>> result = new HashMap<>();
        for (final var entry : graph.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }

    private static void abortJob() {
        undirected = null;
        directed = null;
        PENDING.clear();
        WORKING_COLORS.clear();
        activeLeg = null;
        openQueue = null;
        openSet = null;
        gScore = null;
        cameFrom = null;
        closedSet = null;
        targetSet = null;
        reachedTarget = null;
        legExpanded = 0;
        legTotal = 0;
        legDone = 0;
        totalExpanded = 0;
        totalTagged = 0;
        failedLegs = 0;
        relaxedLegs = 0;
        running = false;
    }

    public static void advance() {
        if (!running) {
            return;
        }
        final Map<Position, Map<Position, Rail>> graph = undirected;
        if (graph == null || graph.isEmpty()) {
            abortJob();
            return;
        }
        int budget = NODES_PER_FRAME;
        while (budget > 0 && running) {
            if (activeLeg == null) {
                final Leg leg = PENDING.poll();
                if (leg == null) {
                    finishJob();
                    return;
                }
                if (!beginLeg(leg, graph)) {
                    continue;
                }
            }
            budget -= stepSearch(activeLeg.relaxed ? graph : directed, budget);
        }
    }

    private static boolean beginLeg(Leg leg, Map<Position, Map<Position, Rail>> graph) {
        final List<Position> starts = new ArrayList<>();
        for (final Position position : leg.from) {
            if (graph.containsKey(position)) {
                starts.add(position);
            }
        }
        targetSet = new HashSet<>();
        for (final Position position : leg.to) {
            if (graph.containsKey(position)) {
                targetSet.add(position);
            }
        }
        starts.removeAll(targetSet);
        if (starts.isEmpty() || targetSet.isEmpty()) {
            legDone++;
            failedLegs++;
            targetSet = null;
            return false;
        }
        activeLeg = leg;
        openQueue = new PriorityQueue<>();
        openSet = new HashSet<>();
        gScore = new HashMap<>();
        cameFrom = new HashMap<>();
        closedSet = new HashSet<>();
        legExpanded = 0;
        reachedTarget = null;
        for (final Position start : starts) {
            gScore.put(start, 0.0);
            openQueue.add(new Node(start, heuristic(start, targetSet)));
            openSet.add(start);
        }
        return true;
    }

    private static int stepSearch(Map<Position, Map<Position, Rail>> graph, int budget) {
        final Leg leg = activeLeg;
        int used = 0;
        while (used < budget) {
            final Node node = openQueue.poll();
            if (node == null) {
                completeLeg(false);
                return Math.max(1, used);
            }
            final Position current = node.position;
            openSet.remove(current);
            if (!closedSet.add(current)) {
                continue;
            }
            used++;
            legExpanded++;
            totalExpanded++;
            if (targetSet.contains(current)) {
                reachedTarget = current;
                completeLeg(true);
                return used;
            }
            if (legExpanded >= MAX_SEARCH_NODES) {
                completeLeg(false);
                return used;
            }
            final var neighbours = graph.get(current);
            if (neighbours == null) {
                continue;
            }
            final double currentG = gScore.getOrDefault(current, Double.MAX_VALUE);
            for (final Position next : neighbours.keySet()) {
                if (closedSet.contains(next)) {
                    continue;
                }
                final double tentative = currentG + edgeCost(neighbours.get(next), current, next);
                if (tentative < gScore.getOrDefault(next, Double.MAX_VALUE)) {
                    gScore.put(next, tentative);
                    cameFrom.put(next, current);
                    openQueue.add(new Node(next, tentative + heuristic(next, targetSet)));
                    openSet.add(next);
                }
            }
        }
        return used;
    }

    private static void completeLeg(boolean found) {
        final Leg leg = activeLeg;
        int tagged = 0;
        if (found) {
            Position cursor = reachedTarget;
            final Map<Position, Map<Position, Rail>> graph = undirected;
            while (cameFrom.containsKey(cursor)) {
                final Position previous = cameFrom.get(cursor);
                final var neighbours = graph.get(previous);
                if (neighbours != null) {
                    final Rail rail = neighbours.get(cursor);
                    if (rail != null) {
                        try {
                            if (WORKING_COLORS.putIfAbsent(rail.getHexId(), leg.color) == null) {
                                tagged++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                cursor = previous;
            }
            totalTagged += tagged;
            legDone++;
        } else if (!leg.relaxed) {
            relaxedLegs++;
            PENDING.addFirst(leg.asRelaxed());
        } else {
            failedLegs++;
            legDone++;
        }
        activeLeg = null;
        openQueue = null;
        openSet = null;
        gScore = null;
        cameFrom = null;
        closedSet = null;
        targetSet = null;
        reachedTarget = null;
    }

    private static void finishJob() {
        cn.jstxjf_.rte.RTE.LOGGER.info(
            "[RouteColors] legs={} failed={} relaxed={} tagged={} of {} rails, expanded={} in {}ms",
            legTotal, failedLegs, relaxedLegs, WORKING_COLORS.size(),
            undirected == null ? 0 : undirected.values().stream().mapToInt(java.util.Map::size).sum() / 2,
            totalExpanded, System.currentTimeMillis() - jobStartMillis);
        if (undirected != null) {
            final Set<String> seen = new HashSet<>();
            for (final Map.Entry<Position, Map<Position, Rail>> entry : undirected.entrySet()) {
                for (final Map.Entry<Position, Rail> neighbour : entry.getValue().entrySet()) {
                    try {
                        final String id = neighbour.getValue().getHexId();
                        if (!WORKING_COLORS.containsKey(id) && seen.add(id)) {
                            final Rail rail = neighbour.getValue();
                            cn.jstxjf_.rte.RTE.LOGGER.info(
                                "[RouteColors] uncoloured ({},{},{})->({},{},{}) platform={} siding={} turnBack={} shape={} oneWay={}",
                                entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ(),
                                neighbour.getKey().getX(), neighbour.getKey().getY(), neighbour.getKey().getZ(),
                                rail.isPlatform(), rail.isSiding(), rail.canTurnBack(),
                                rail.railMath.getShape(),
                                directed.containsKey(neighbour.getKey())
                                    && directed.get(neighbour.getKey()).containsKey(entry.getKey()) ? "no" : "yes");
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        RAIL_COLORS.clear();
        RAIL_COLORS.putAll(WORKING_COLORS);
        PENDING.clear();
        WORKING_COLORS.clear();
        activeLeg = null;
        running = false;
        lastFinishMillis = System.currentTimeMillis();
    }

    private static double edgeCost(Rail rail, Position from, Position to) {
        if (rail != null) {
            try {
                final double length = rail.railMath.getLength();
                if (Double.isFinite(length) && length > 0) {
                    return length;
                }
            } catch (Exception ignored) {
            }
        }
        return heuristic(from, to);
    }

    private static double heuristic(Position from, Position to) {
        final double dx = from.getX() - to.getX();
        final double dy = from.getY() - to.getY();
        final double dz = from.getZ() - to.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static double heuristic(Position from, Set<Position> targets) {
        double best = Double.MAX_VALUE;
        for (final Position target : targets) {
            best = Math.min(best, heuristic(from, target));
        }
        return best == Double.MAX_VALUE ? 0 : best;
    }

    private static final Map<String, Integer> GENERATED_PATH_COLORS = new HashMap<>();
    private static long lastPathRequestMillis = 0;

    public static void acceptGeneratedPathColors(Map<String, Integer> source) {
        GENERATED_PATH_COLORS.clear();
        GENERATED_PATH_COLORS.putAll(source);
        cn.jstxjf_.rte.RTE.LOGGER.info("[RouteColors] received {} generated path rails", source.size());
        forceRefresh();
    }

    private static void tagGeneratedPaths(MinecraftClientData data) {
        WORKING_COLORS.putAll(GENERATED_PATH_COLORS);
        final long now = System.currentTimeMillis();
        if (GENERATED_PATH_COLORS.isEmpty() && now - lastPathRequestMillis > 10000) {
            lastPathRequestMillis = now;
            try {
                org.mtr.mod.InitClient.REGISTRY_CLIENT.sendPacketToServer(
                    new cn.jstxjf_.rte.network.PacketRequestRoutePathColors());
            } catch (Exception ignored) {
            }
        }
        tagGeneratedPathsLocal(data);
    }

    private static void tagGeneratedPathsLocal(MinecraftClientData data) {
        int tagged = 0;
        int sidingsWithPath = 0;
        for (final org.mtr.core.data.Siding siding : data.sidings) {
            final org.mtr.core.data.Depot depot = siding.area;
            if (depot == null) {
                continue;
            }
            final Integer color = colorOfDepot(data, depot);
            if (color == null) {
                continue;
            }
            final cn.jstxjf_.rte.mixin.SidingAccessor accessor = (cn.jstxjf_.rte.mixin.SidingAccessor) (Object) siding;
            int fromThisSiding = 0;
            fromThisSiding += tagPathData(accessor.rte$getPathMainRoute(), color);
            fromThisSiding += tagPathData(accessor.rte$getPathSidingToMainRoute(), color);
            fromThisSiding += tagPathData(accessor.rte$getPathMainRouteToSiding(), color);
            if (fromThisSiding > 0) {
                sidingsWithPath++;
                tagged += fromThisSiding;
            }
        }
        cn.jstxjf_.rte.RTE.LOGGER.info("[RouteColors] generated paths: {} rails from {}/{} sidings",
            tagged, sidingsWithPath, data.sidings.size());
    }

    private static Integer colorOfDepot(MinecraftClientData data, org.mtr.core.data.Depot depot) {
        try {
            for (final long routeId : depot.getRouteIds()) {
                final SimplifiedRoute route = data.simplifiedRouteIdMap.get(routeId);
                if (route != null) {
                    return 0xFF000000 | (route.getColor() & 0xFFFFFF);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static int tagPathData(List<org.mtr.core.data.PathData> path, int color) {
        if (path == null) {
            return 0;
        }
        int tagged = 0;
        for (final org.mtr.core.data.PathData pathData : path) {
            try {
                final Rail rail = pathData.getRail();
                if (rail != null && WORKING_COLORS.putIfAbsent(rail.getHexId(), color) == null) {
                    tagged++;
                }
            } catch (Exception ignored) {
            }
        }
        return tagged;
    }

    private static void tagPlatformRail(List<Position> endpoints, int color) {
        if (endpoints.size() < 2 || undirected == null) {
            return;
        }
        final Map<Position, Rail> neighbours = undirected.get(endpoints.get(0));
        if (neighbours == null) {
            return;
        }
        final Rail rail = neighbours.get(endpoints.get(1));
        if (rail == null) {
            return;
        }
        try {
            WORKING_COLORS.putIfAbsent(rail.getHexId(), color);
        } catch (Exception ignored) {
        }
    }

    private static List<Position> endpointsOf(Platform platform) {
        final List<Position> result = new ArrayList<>();
        try {
            final Position first = platform.getRandomPosition();
            if (first != null) {
                result.add(first);
                final Position second = platform.getOtherPosition(first);
                if (second != null && !second.equals(first)) {
                    result.add(second);
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private static String routeName(SimplifiedRoute route) {
        try {
            final String name = route.getName();
            if (name != null && !name.isEmpty()) {
                return name.split("\\|")[0];
            }
        } catch (Exception ignored) {
        }
        return net.minecraft.network.chat.Component.translatable("gui.rte.route.unnamed").getString();
    }

}
