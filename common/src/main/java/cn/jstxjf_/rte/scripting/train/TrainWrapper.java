package cn.jstxjf_.rte.scripting.train;

import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateTrainCustomConfigs;
import cn.jstxjf_.rte.scripting.compat.PlatformCompat;
import cn.jstxjf_.rte.scripting.compat.RouteCompat;
import cn.jstxjf_.rte.scripting.compat.StationCompat;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Station;
import org.mtr.core.data.TransportMode;
import org.mtr.core.data.Vehicle;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Utilities;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
public class TrainWrapper {

    public final boolean[] doorLeftOpen;
    public final boolean[] doorRightOpen;
    public final Matrix4f[] lastWorldPose;
    public final Vector3f[] lastCarPosition;
    public final Vector3f[] lastCarRotation;

    public boolean shouldRender;
    public boolean isInDetailDistance;
    public float previousDoorValue;
    public float currentDoorValue;

    private VehicleExtension train;

    public void rebind(VehicleExtension train) {
        this.train = train;
    }
    private final TrainExtraSupplier supplier;
    private List<PlatformInfo> allPlatforms = List.of();
    private List<PlatformInfo> thisRoutePlatforms = List.of();
    private List<PlatformInfo> nextRoutePlatforms = List.of();
    private double rte$scriptRailProgress = 0;

    public TrainWrapper(VehicleExtension train) {
        final int carCount = train.vehicleExtraData.immutableVehicleCars.size();
        this.doorLeftOpen = new boolean[carCount];
        this.doorRightOpen = new boolean[carCount];
        this.lastWorldPose = new Matrix4f[carCount];
        this.lastCarPosition = new Vector3f[carCount];
        this.lastCarRotation = new Vector3f[carCount];
        Arrays.setAll(lastWorldPose, ignored -> Matrix4f.translation(0, -10000, 0));
        Arrays.setAll(lastCarPosition, ignored -> new Vector3f(0, -10000, 0));
        Arrays.setAll(lastCarRotation, ignored -> new Vector3f(0, 0, 0));
        this.train = train;
        this.supplier = (TrainExtraSupplier) train;
        reset();
    }

    public void reset() {
        rte$pruneStaleCaches();
        rte$scriptRailProgress = ((TrainExtraSupplier) train).getVehicleRailProgress();
        final List<PlatformInfo> schedulePlatforms = buildSchedulePlatforms();
        long effectiveThisRouteId = train.vehicleExtraData.getThisRouteId();
        if (effectiveThisRouteId == 0 && !schedulePlatforms.isEmpty()) {
            effectiveThisRouteId = schedulePlatforms.get(0).routeId;
        }
        thisRoutePlatforms = buildRoutePlatforms(effectiveThisRouteId, train.vehicleExtraData.getNextRouteId());
        nextRoutePlatforms = buildRoutePlatforms(train.vehicleExtraData.getNextRouteId(), 0);
        rte$dedupePlatforms(thisRoutePlatforms);
        rte$dedupePlatforms(nextRoutePlatforms);
        if (!schedulePlatforms.isEmpty()) {
            allPlatforms = schedulePlatforms;
        } else if (nextRoutePlatforms.isEmpty()) {
            allPlatforms = thisRoutePlatforms;
        } else {
            final ArrayList<PlatformInfo> merged = new ArrayList<>(thisRoutePlatforms);
            merged.addAll(nextRoutePlatforms);
            allPlatforms = merged;
        }
        rte$dedupePlatforms(allPlatforms);
        final long cacheKey = train.getId();
        if (allPlatforms.isEmpty()) {
            final List<PlatformInfo> cachedAll = LAST_GOOD_ALL_PLATFORMS.get(cacheKey);
            if (cachedAll != null && !cachedAll.isEmpty()) {
                allPlatforms = new ArrayList<>(cachedAll);
                final List<PlatformInfo> cachedThisRoute = LAST_GOOD_ROUTE_PLATFORMS.get(cacheKey);
                if (thisRoutePlatforms.isEmpty() && cachedThisRoute != null) {
                    thisRoutePlatforms = new ArrayList<>(cachedThisRoute);
                }
            }
        } else {
            LAST_GOOD_ALL_PLATFORMS.put(cacheKey, new ArrayList<>(allPlatforms));
            if (!thisRoutePlatforms.isEmpty()) {
                LAST_GOOD_ROUTE_PLATFORMS.put(cacheKey, new ArrayList<>(thisRoutePlatforms));
            }
        }
        rte$canonicalizePlatforms();
        previousDoorValue = currentDoorValue;
        currentDoorValue = (float) train.persistentVehicleData.getDoorValue();
        rte$normalizePlatformDistances(allPlatforms);
        rte$normalizePlatformDistances(thisRoutePlatforms);
        rte$normalizePlatformDistances(nextRoutePlatforms);
        shouldRender = false;
        isInDetailDistance = false;
    }

    private Map<Long, Double> rte$realPlatformDistances() {
        final Map<Long, Double> result = new java.util.HashMap<>();
        try {
            for (final PathData pathData : train.vehicleExtraData.immutablePath) {
                final long savedRailBaseId = pathData.getSavedRailBaseId();
                if (savedRailBaseId != 0 && pathData.getDwellTime() != 0) {
                    result.putIfAbsent(savedRailBaseId, pathData.getEndDistance());
                }
            }
        } catch (Exception ignored) {
        }
        try {
            final List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> serverPath =
                cn.jstxjf_.rte.render.train.VehiclePathCache.get(train.getId());
            if (serverPath != null) {
                for (final cn.jstxjf_.rte.scripting.compat.PathDataCompat pathData : serverPath) {
                    if (pathData.savedRailBaseIdLong != 0 && pathData.dwellTimeMillis != 0) {
                        result.putIfAbsent(pathData.savedRailBaseIdLong, pathData.endDistance);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private boolean rte$applyRealDistances(List<PlatformInfo> platforms) {
        final Map<Long, Double> real = rte$realPlatformDistances();
        if (real.isEmpty()) {
            return false;
        }
        int matched = 0;
        for (final PlatformInfo info : platforms) {
            if (info.platform == null) {
                continue;
            }
            final Double distance = real.get(info.platform.idLong);
            if (distance != null) {
                info.distance = distance;
                matched++;
            }
        }
        return matched == platforms.size();
    }

    private void rte$normalizePlatformDistances(List<PlatformInfo> platforms) {
        if (platforms.isEmpty()) {
            return;
        }
        if (rte$applyRealDistances(platforms)) {
            return;
        }
        final double realProgress = rte$scriptRailProgress;
        final boolean dwelling = currentDoorValue > 0;
        final long thisPlatformId = train.vehicleExtraData.getThisPlatformId();
        final long nextPlatformId = train.vehicleExtraData.getNextPlatformId();

        int anchorIndex = -1;
        boolean arrived = false;
        if (dwelling && thisPlatformId != 0) {
            anchorIndex = indexOfPlatformId(platforms, thisPlatformId);
            arrived = anchorIndex >= 0;
        }
        if (anchorIndex < 0 && nextPlatformId != 0) {
            anchorIndex = indexOfPlatformId(platforms, nextPlatformId);
            arrived = false;
        }
        if (anchorIndex < 0 && thisPlatformId != 0) {
            anchorIndex = indexOfPlatformId(platforms, thisPlatformId);
            arrived = anchorIndex >= 0;
        }
        if (anchorIndex < 0) {
            final int geometric = findNearestPlatformIndexByPosition(platforms);
            if (geometric >= 0) {
                anchorIndex = geometric;
                arrived = dwelling || speedMetersPerMillisecond() < 0.001;
            }
        }
        if (anchorIndex < 0 && rte$lastAnchorIndex >= 0 && rte$lastAnchorIndex < platforms.size()) {
            anchorIndex = rte$lastAnchorIndex;
            arrived = dwelling;
        }
        if (anchorIndex < 0) {
            anchorIndex = 0;
            arrived = false;
        }
        rte$lastAnchorIndex = anchorIndex;

        final double step = rte$fallbackPlatformSpacing();
        final double anchorDistance = arrived ? realProgress : realProgress + step * 0.5;
        for (int i = 0; i < platforms.size(); i++) {
            platforms.get(i).distance = anchorDistance + (i - anchorIndex) * step;
        }
    }

    private int rte$lastAnchorIndex = -1;

    private double rte$fallbackPlatformSpacing() {
        try {
            final List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> serverPath =
                cn.jstxjf_.rte.render.train.VehiclePathCache.get(train.getId());
            if (serverPath != null && !serverPath.isEmpty()) {
                int stops = 0;
                double total = 0;
                for (final cn.jstxjf_.rte.scripting.compat.PathDataCompat pathData : serverPath) {
                    total = Math.max(total, pathData.endDistance);
                    if (pathData.dwellTimeMillis != 0) {
                        stops++;
                    }
                }
                if (stops > 1 && total > 0) {
                    return total / stops;
                }
            }
        } catch (Exception ignored) {
        }
        return 1000.0;
    }


    public List<PlatformInfo> getAllPlatforms() {
        return rte$clamped(allPlatforms);
    }

    private static List<PlatformInfo> rte$clamped(List<PlatformInfo> source) {
        if (source.isEmpty()) {
            return source;
        }
        return new java.util.AbstractList<>() {
            @Override
            public PlatformInfo get(int index) {
                final int size = source.size();
                if (index < 0) {
                    return source.get(0);
                }
                if (index >= size) {
                    return source.get(size - 1);
                }
                return source.get(index);
            }

            @Override
            public int indexOf(Object o) {
                return source.indexOf(o);
            }

            @Override
            public int size() {
                return source.size();
            }
        };
    }

    public int getAllPlatformsNextIndex() {

        final long dwellPlatformId = rte$dwellingPlatformId();
        if (dwellPlatformId != 0) {
            final int here = indexOfPlatformId(allPlatforms, dwellPlatformId);
            if (here >= 0) {
                rte$lastResolvedIndex = here;
                return here;
            }
        }

        final long nextPlatformId = train.vehicleExtraData.getNextPlatformId();
        if (nextPlatformId != 0) {
            final int index = indexOfPlatformId(allPlatforms, nextPlatformId);
            if (index >= 0) {
                rte$lastResolvedIndex = index;
                return index;
            }
            final int byPosition = findNearestPlatformIndexByPosition(allPlatforms);
            if (byPosition >= 0) {
                return byPosition;
            }
        } else if (isOnRoute() && !allPlatforms.isEmpty()) {
            final int geometric = findNextPlatformIndex(allPlatforms);
            if (geometric >= 0 && geometric < allPlatforms.size()) {
                rte$lastResolvedIndex = geometric;
                return geometric;
            }
            if (rte$hasUpcomingDwell() && rte$lastResolvedIndex >= 0 && rte$lastResolvedIndex < allPlatforms.size()) {
                return rte$lastResolvedIndex;
            }
            return allPlatforms.size();
        }
        final int fallback = findNextPlatformIndex(allPlatforms);
        return fallback;
    }

    private int rte$lastResolvedIndex = -1;

    private long rte$dwellingPlatformId() {
        if (currentDoorValue <= 0 && ((TrainExtraSupplier) train).getVehicleSpeed() > 0) {
            return 0;
        }
        return train.vehicleExtraData.getThisPlatformId();
    }

    private boolean rte$hasUpcomingDwell() {
        try {
            final double progress = rte$scriptRailProgress;
            for (final PathData pathData : train.vehicleExtraData.immutablePath) {
                if (pathData.getDwellTime() != 0 && pathData.getSavedRailBaseId() != 0
                        && pathData.getEndDistance() >= progress) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }



    public PlatformInfo getPlatformRelative(int offset, boolean allowDifferentRoute) {
        final int index = getAllPlatformsNextIndex() + offset;
        if (index < 0 || index >= allPlatforms.size()) {
            return null;
        }
        final int nextIndex = Math.min(getAllPlatformsNextIndex(), allPlatforms.size() - 1);
        if (!allowDifferentRoute && !allPlatforms.isEmpty() && !allPlatforms.get(index).routeId.equals(allPlatforms.get(nextIndex).routeId)) {
            return null;
        }
        return allPlatforms.get(index);
    }

    public List<PlatformInfo> getThisRoutePlatforms() {
        return rte$clamped(thisRoutePlatforms);
    }

    public List<PlatformInfo> getNextRoutePlatforms() {
        return rte$clamped(nextRoutePlatforms);
    }

    public int getThisRoutePlatformsNextIndex() {
        final long dwellPlatformId = rte$dwellingPlatformId();
        if (dwellPlatformId != 0) {
            final int here = indexOfPlatformId(thisRoutePlatforms, dwellPlatformId);
            if (here >= 0) {
                return here;
            }
        }
        final long nextPlatformId = train.vehicleExtraData.getNextPlatformId();
        if (nextPlatformId != 0) {
            final int index = indexOfPlatformId(thisRoutePlatforms, nextPlatformId);
            if (index >= 0) {
                return index;
            }
        }
        final int byProgress = findNextPlatformIndex(thisRoutePlatforms);
        if (byProgress < thisRoutePlatforms.size()) {
            return byProgress;
        }
        final int byPosition = findNearestPlatformIndexByPosition(thisRoutePlatforms);
        return byPosition >= 0 ? byPosition : byProgress;
    }

    public List<PlatformInfo> getDebugThisRoutePlatforms(int count) {
        if (count <= 0 || thisRoutePlatforms.isEmpty()) {
            return List.of();
        }
        final int fromIndex = Math.min(getThisRoutePlatformsNextIndex(), thisRoutePlatforms.size());
        final int toIndex = Math.min(fromIndex + count, thisRoutePlatforms.size());
        return List.copyOf(thisRoutePlatforms.subList(fromIndex, toIndex));
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public boolean shouldRenderDetail() {
        return shouldRender && (isReplayMod() || isInDetailDistance);
    }

    public boolean isClientPlayerRiding() {
        final Player player = Minecraft.getInstance().player;
        return player != null && org.mtr.mod.client.VehicleRidingMovement.isRiding(train.getId());
    }

    public Vehicle mtrTrain() {
        return train;
    }

    public long id() {
        return train.getId();
    }

    private static final java.util.Map<Long, Siding> LAST_KNOWN_SIDING = new java.util.concurrent.ConcurrentHashMap<>();

    public cn.jstxjf_.rte.scripting.compat.SidingCompat siding() {
        final MinecraftClientData data = MinecraftClientData.getInstance();
        final long sidingId = train.vehicleExtraData.getSidingId();
        final long vehicleId = train.getId();
        final Siding live = data.sidingIdMap.get(sidingId);
        if (live != null) {
            LAST_KNOWN_SIDING.put(vehicleId, live);
            return rte$exposeSiding(live);
        }
        final Siding cached = LAST_KNOWN_SIDING.get(vehicleId);
        if (cached != null) {
            return rte$exposeSiding(cached);
        }
        if (data.sidingIdMap.size() == 1) {
            final Siding only = data.sidingIdMap.values().iterator().next();
            LAST_KNOWN_SIDING.put(vehicleId, only);
            return rte$exposeSiding(only);
        }
        if (sidingId != 0) {
            rte$adoptSoleDepot(sidingId);
            return cn.jstxjf_.rte.scripting.compat.SidingCompat.stub(sidingId);
        }
        return null;
    }

    private static cn.jstxjf_.rte.scripting.compat.SidingCompat rte$exposeSiding(Siding siding) {
        final cn.jstxjf_.rte.scripting.compat.DataCacheCompat cache = cn.jstxjf_.rte.scripting.compat.ClientDataCompat.DATA_CACHE;
        final long key = siding.getId();
        if (!cache.sidingIdMap.containsKey(key)) {
            cache.sidingIdMap.put(key, new cn.jstxjf_.rte.scripting.compat.SidingCompat(siding));
        }
        if (siding.area != null) {
            cache.sidingIdToDepot.put(key, new cn.jstxjf_.rte.scripting.compat.DepotCompat(siding));
        } else {
            rte$adoptSoleDepot(key);
        }
        return new cn.jstxjf_.rte.scripting.compat.SidingCompat(siding);
    }

    private static void rte$adoptSoleDepot(long sidingId) {
        final cn.jstxjf_.rte.scripting.compat.DataCacheCompat cache = cn.jstxjf_.rte.scripting.compat.ClientDataCompat.DATA_CACHE;
        final cn.jstxjf_.rte.scripting.compat.DepotCompat existing = cache.sidingIdToDepot.get(sidingId);
        if (existing != null && existing.id != 0) {
            return;
        }
        for (final cn.jstxjf_.rte.scripting.compat.DepotCompat candidate : cache.sidingIdToDepot.values()) {
            if (candidate.id != 0) {
                cache.sidingIdToDepot.put(sidingId, candidate);
                return;
            }
        }
        if (existing == null) {
            cache.sidingIdToDepot.put(sidingId, cn.jstxjf_.rte.scripting.compat.DepotCompat.unknown());
        }
    }


    public String trainTypeId() {
        return train.vehicleExtraData.immutableVehicleCars.isEmpty() ? "" : train.vehicleExtraData.immutableVehicleCars.get(0).getVehicleId();
    }

    public String baseTrainType() {
        return trainTypeId();
    }

    public TransportMode transportMode() {
        return train.getTransportMode();
    }

    public double spacing() {
        final var cars = train.vehicleExtraData.immutableVehicleCars;
        if (cars.isEmpty()) {
            return 0;
        }
        final double length = cars.get(0).getLength();
        if (cars.size() < 2 || cn.jstxjf_.rte.ClientConfig.closeCouplingGap) {
            return length;
        }
        try {
            if (cn.jstxjf_.rte.data.GangwayPolicy.isLegacyFormat(cars.get(0).getVehicleId())) {
                return length + cn.jstxjf_.rte.ClientConfig.legacyCouplingGap;
            }
            final cn.jstxjf_.rte.mixin.VehicleCarAccessor here =
                (cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) cars.get(0);
            final cn.jstxjf_.rte.mixin.VehicleCarAccessor next =
                (cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) cars.get(1);
            return length + here.rte$getCouplingPadding2() + next.rte$getCouplingPadding1();
        } catch (Exception e) {
            return length;
        }
    }

    public double width() {
        return train.vehicleExtraData.immutableVehicleCars.isEmpty() ? 0 : train.vehicleExtraData.immutableVehicleCars.get(0).getWidth();
    }

    public int trainCars() {
        return train.vehicleExtraData.immutableVehicleCars.size();
    }

    public float accelerationConstant() {
        return (float) (train.vehicleExtraData.getAcceleration() * MILLIS_PER_TICK * MILLIS_PER_TICK);
    }

    public float accelerationMetersPerMillisecondSquared() {
        return (float) train.vehicleExtraData.getAcceleration();
    }

    public boolean manualAllowed() {
        return train.vehicleExtraData.getIsManualAllowed();
    }

    public int maxManualSpeed() {
        return (int) Math.round(train.vehicleExtraData.getMaxManualSpeed());
    }

    public int manualToAutomaticTime() {
        return (int) ((TrainExtraSupplier) train).getManualToAutomaticTime();
    }

    public Object path() {
        return rte$sharedSnapshot();
    }

    private java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> rte$sharedSnapshot() {
        final java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> live = rte$activePath();
        if (live.isEmpty() && !rte$handedPath.isEmpty()) {
            return rte$handedPath;
        }
        synchronized (rte$handedPath) {
            if (live.size() != rte$handedSize || rte$handedPath.size() != rte$handedSize) {
                if (rte$handedSize >= 0) {
                    rte$pathChangedSinceHandout = true;
                }
                rte$handedPath.clear();
                rte$handedPath.addAll(live);
                rte$handedSize = rte$handedPath.size();
            }
        }
        return rte$handedPath;
    }

    public void rte$invalidatePathSnapshot() {
        rte$handedSize = -1;
        rte$pathChangedSinceHandout = false;
    }

    private List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> rte$activePath() {
        if (rte$pathView == null) {
            rte$pathView = cn.jstxjf_.rte.scripting.compat.PathDataCompat.liveView(
                () -> train.vehicleExtraData.immutablePath);
        }
        final int localSize = train.vehicleExtraData.immutablePath.size();
        final long vehicleId = train.getId();
        final List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> serverPath =
            cn.jstxjf_.rte.render.train.VehiclePathCache.get(vehicleId);
        if (serverPath != null && serverPath.size() > localSize) {
            return serverPath;
        }
        if (localSize < MIN_TRUSTED_PATH_SIZE) {
            cn.jstxjf_.rte.render.train.VehiclePathCache.requestIfDue(vehicleId);
        }
        return rte$pathView;
    }

    private static final int MIN_TRUSTED_PATH_SIZE = 8;

    private final java.util.ArrayList<cn.jstxjf_.rte.scripting.compat.PathDataCompat> rte$handedPath =
        new java.util.ArrayList<>();

    private volatile boolean rte$pathChangedSinceHandout = false;

    public boolean rte$consumePathChanged() {
        final boolean changed = rte$pathChangedSinceHandout;
        rte$pathChangedSinceHandout = false;
        return changed;
    }

    private int rte$handedSize = -1;

    private java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> rte$indexBasis() {
        return rte$sharedSnapshot();
    }

    private List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> rte$pathView;

    public double railProgress() {
        return rte$scriptRailProgress;
    }

    public double getRailProgress(int car) {
        return railProgress() - car * spacing();
    }

    private long rte$nextEmptyPathReport = 0;

    private void rte$reportEmptyPath() {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextEmptyPathReport) {
            return;
        }
        rte$nextEmptyPathReport = now + 2000;
        cn.jstxjf_.rte.RTE.LOGGER.warn(
            "[PathProbe] vehicle {} has an EMPTY path; getRailIndex returns -1 and scripts that index "
                + "path data will throw (live={}, cachedSnapshot={})",
            train.getHexId(), rte$activePath().size(), rte$handedPath.size());
    }

    private long rte$nextConnProbe = 0;

    public void rte$probeConnAngles() {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextConnProbe) {
            return;
        }
        rte$nextConnProbe = now + 2000;
        try {
            final int cars = train.vehicleExtraData.immutableVehicleCars.size();
            final java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> path = rte$sharedSnapshot();
            if (path.isEmpty()) {
                cn.jstxjf_.rte.RTE.LOGGER.info("[ConnProbe] {} path empty", train.getHexId());
                return;
            }
            final double sp = spacing();
            final StringBuilder line = new StringBuilder();
            final double[] yaws = new double[cars];
            for (int c = 0; c < cars; c++) {
                final double prog = getRailProgress(c);
                final Vector3f a = rte$sampleAt(path, prog);
                final Vector3f b = rte$sampleAt(path, prog - sp);
                if (a == null || b == null) {
                    line.append("car").append(c).append("=nullSample ");
                    yaws[c] = Double.NaN;
                    continue;
                }
                yaws[c] = Math.atan2(a.x() - b.x(), a.z() - b.z());
            }
            for (int c = 0; c + 1 < cars; c++) {
                if (Double.isNaN(yaws[c]) || Double.isNaN(yaws[c + 1])) {
                    continue;
                }
                line.append(String.format(java.util.Locale.ROOT, "joint%d scriptYaw=%.1f  ",
                    c, Math.toDegrees(yaws[c + 1] - yaws[c])));
            }
            cn.jstxjf_.rte.RTE.LOGGER.info("[ConnProbe] {} spacing={} pathSize={} {}",
                train.getHexId(), String.format(java.util.Locale.ROOT, "%.2f", sp),
                path.size(), line.toString().trim());
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.info("[ConnProbe] failed: {}", String.valueOf(e));
        }
    }

    private Vector3f rte$sampleAt(java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> path,
                                  double progress) {
        final int idx = getRailIndex(progress, false);
        if (idx < 0 || idx >= path.size()) {
            return null;
        }
        final double local = progress - (idx > 0 ? path.get(idx - 1).endDistance : 0);
        final var pos = path.get(idx).rail.getPosition(local);
        return pos == null ? null : new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
    }

    public int getRailIndex(double railProgress, boolean roundDown) {
        final List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> path = rte$indexBasis();
        final int size = path.size();
        if (size == 0) {
            rte$reportEmptyPath();
            return -1;
        }
        final double lookup = roundDown ? railProgress - 1e-6 : railProgress;
        int low = 0;
        int high = size - 1;
        int found = -1;
        while (low <= high) {
            final int mid = (low + high) >>> 1;
            final cn.jstxjf_.rte.scripting.compat.PathDataCompat entry = path.get(mid);
            if (lookup < entry.startDistance) {
                high = mid - 1;
            } else if (lookup >= entry.endDistance) {
                low = mid + 1;
            } else {
                found = mid;
                break;
            }
        }
        if (found < 0) {
            found = Math.max(0, Math.min(low, size - 1));
        }
        return Math.min(found, size - 1);
    }

    public float getRailSpeed(int railIndex) {
        final var path = train.vehicleExtraData.immutablePath;
        if (railIndex < 0 || railIndex >= path.size()) {
            return 0;
        }
        final float direct = (float) path.get(railIndex).getSpeedLimitMetersPerMillisecond();
        if (direct > 0) {
            return direct;
        }
        final org.mtr.core.data.Rail rail = path.get(railIndex).getRail();
        if (rail != null) {
            final float reverse = (float) Math.max(
                rail.getSpeedLimitMetersPerMillisecond(false),
                rail.getSpeedLimitMetersPerMillisecond(true));
            if (reverse > 0) {
                return reverse;
            }
        }
        for (int offset = 1; offset < path.size(); offset++) {
            for (final int probe : new int[]{railIndex - offset, railIndex + offset}) {
                if (probe < 0 || probe >= path.size()) {
                    continue;
                }
                final float neighbour = (float) path.get(probe).getSpeedLimitMetersPerMillisecond();
                if (neighbour > 0) {
                    return neighbour;
                }
            }
        }
        return 0;
    }

    private static final double MILLIS_PER_TICK = 50;

    public float speed() {
        return (float) (((TrainExtraSupplier) train).getVehicleSpeed() * MILLIS_PER_TICK);
    }

    public float speedMetersPerMillisecond() {
        return (float) ((TrainExtraSupplier) train).getVehicleSpeed();
    }

    public float speedKilometersPerHour() {
        return (float) (((TrainExtraSupplier) train).getVehicleSpeed() * 3600);
    }

    public float doorValue() {
        return (float) train.persistentVehicleData.getDoorValue();
    }

    public boolean isCurrentlyManual() {
        return train.vehicleExtraData.getIsCurrentlyManual();
    }

    public boolean isReversed() {
        return ((TrainExtraSupplier) train).getVehicleReversed();
    }

    public boolean isOnRoute() {
        return train.getIsOnRoute();
    }

    public boolean justOpening() {
        return currentDoorValue > 0 && previousDoorValue <= 0;
    }

    public boolean justClosing(float doorCloseTime) {
        return currentDoorValue <= 0 && previousDoorValue > 0;
    }

    public boolean isDoorOpening() {
        return currentDoorValue > previousDoorValue;
    }

    public boolean doorTarget() {
        return train.vehicleExtraData.getDoorMultiplier() > 0;
    }

    public Map<String, String> getCustomConfigs() {
        return supplier.getCustomConfigs();
    }

    public void setCustomConfigs(Map<String, String> map) {
        supplier.setCustomConfigs(map);
    }

    public void sendCustomConfigsUpdateC2S() {
        PacketUpdateTrainCustomConfigs.sendUpdateC2S(train, supplier.getCustomConfigs());
    }

    public Map<String, ConfigResponder> getCustomResponders() {
        return supplier.getConfigResponders();
    }

    public void setConfigResponders(Map<String, ConfigResponder> map) {
        supplier.setConfigResponders(map);
    }

    public void registerCustomConfig(ConfigResponder responder) {
        responder.init(getCustomConfigs());
        getCustomResponders().put(responder.key(), responder);
    }

    public void removeCustomConfig(String key) {
        getCustomResponders().remove(key);
        getCustomConfigs().remove(key);
    }

    public void putCustomConfig(String key, String value) {
        getCustomConfigs().put(key, value);
    }

    public boolean isDoorOpen() {
        return currentDoorValue > 0;
    }

    public boolean isDoorFullyOpen() {
        return currentDoorValue >= 1.0f;
    }

    public boolean isDoorClosed() {
        return currentDoorValue <= 0;
    }

    public float speedKmh() {
        return speedKilometersPerHour();
    }

    public double distanceToNextPlatform() {
        final double stoppingPoint = rte$stoppingPoint();
        if (stoppingPoint > 0) {
            return Math.max(0, stoppingPoint - rte$scriptRailProgress);
        }
        final int idx = getAllPlatformsNextIndex();
        if (idx < 0 || idx >= allPlatforms.size()) return -1;
        return allPlatforms.get(idx).distance - railProgress();
    }

    private double rte$stoppingPoint() {
        try {
            return train.vehicleExtraData.getStoppingPoint();
        } catch (Exception e) {
            return -1;
        }
    }

    public double distanceToPrevPlatform() {
        final int idx = getAllPlatformsNextIndex() - 1;
        if (idx < 0 || idx >= allPlatforms.size()) return -1;
        return Math.max(0, rte$scriptRailProgress - allPlatforms.get(idx).distance);
    }

    public PlatformInfo nextPlatform() {
        return getPlatformRelative(0, true);
    }

    public PlatformInfo currentPlatform() {
        return getPlatformRelative(-1, true);
    }

    public StationCompat nextStation() {
        final PlatformInfo info = nextPlatform();
        return info != null ? info.station : null;
    }

    public StationCompat currentStation() {
        final PlatformInfo info = currentPlatform();
        return info != null ? info.station : null;
    }

    public String nextStationName() {
        final StationCompat station = nextStation();
        return station != null ? station.name : "";
    }

    public String currentStationName() {
        final StationCompat station = currentStation();
        return station != null ? station.name : "";
    }

    public String destinationName() {
        final PlatformInfo info = nextPlatform();
        return info != null ? info.destinationName : "";
    }

    public RouteCompat currentRoute() {
        final PlatformInfo info = nextPlatform();
        return info != null ? info.route : null;
    }

    public String routeName() {
        final RouteCompat route = currentRoute();
        return route != null ? route.name : "";
    }

    public String routeNumber() {
        final RouteCompat route = currentRoute();
        return route != null ? route.routeNumber : "";
    }

    public int routeColor() {
        final RouteCompat route = currentRoute();
        return route != null ? route.color : 0;
    }

    public int thisRouteStationCount() {
        return thisRoutePlatforms.size();
    }

    public int thisRouteCurrentIndex() {
        return getThisRoutePlatformsNextIndex() - 1;
    }

    public double totalRouteDistance() {
        if (thisRoutePlatforms.isEmpty()) return 0;
        return thisRoutePlatforms.get(thisRoutePlatforms.size() - 1).distance - thisRoutePlatforms.get(0).distance;
    }

    public double routeProgress() {
        if (thisRoutePlatforms.isEmpty()) return 0;
        final double firstDist = thisRoutePlatforms.get(0).distance;
        final double lastDist = thisRoutePlatforms.get(thisRoutePlatforms.size() - 1).distance;
        final double range = lastDist - firstDist;
        if (range <= 0) return 0;
        return Math.max(0, Math.min(1, (railProgress() - firstDist) / range));
    }

    public boolean isAtTerminus() {
        final int idx = getThisRoutePlatformsNextIndex();
        return idx >= thisRoutePlatforms.size();
    }

    public boolean isAtFirstStation() {
        return getThisRoutePlatformsNextIndex() <= 0 || (getThisRoutePlatformsNextIndex() == 1 && isDoorOpen());
    }

    public float pathSpeedLimit() {
        final java.util.List<cn.jstxjf_.rte.scripting.compat.PathDataCompat> basis = rte$indexBasis();
        final int idx = getRailIndex(railProgress(), false);
        if (idx < 0 || idx >= basis.size()) return 0;
        return (float) (basis.get(idx).data.getSpeedLimitMetersPerMillisecond() * 3600000.0);
    }

    public String doorSide() {
        if (currentDoorValue <= 0) return "none";
        boolean left = false;
        boolean right = false;
        for (boolean v : doorLeftOpen) if (v) { left = true; break; }
        for (boolean v : doorRightOpen) if (v) { right = true; break; }
        if (left && right) return "both";
        if (left) return "left";
        if (right) return "right";
        return "none";
    }

    public float headYaw() {
        if (lastCarPosition.length < 2) return 0;
        final Vector3f head = lastCarPosition[0];
        final Vector3f next = lastCarPosition.length > 1 ? lastCarPosition[1] : head;
        return (float) Math.toDegrees(Math.atan2(next.z() - head.z(), next.x() - head.x()));
    }

    public float headPitch() {
        if (lastCarRotation.length == 0) return 0;
        return lastCarRotation[0].x();
    }

    public double getCarLength(int car) {
        if (car < 0 || car >= train.vehicleExtraData.immutableVehicleCars.size()) return 0;
        return train.vehicleExtraData.immutableVehicleCars.get(car).getLength();
    }

    public double getCarBogieLength(int car) {
        if (car < 0 || car >= train.vehicleExtraData.immutableVehicleCars.size()) return 0;
        return train.vehicleExtraData.immutableVehicleCars.get(car).getBogie1Position();
    }

    public Vector3f getCarWorldPosition(int car) {
        if (car < 0 || car >= lastCarPosition.length) return new Vector3f(0, -10000, 0);
        return lastCarPosition[car];
    }

    public Vector3f getCarWorldRotation(int car) {
        if (car < 0 || car >= lastCarRotation.length) return new Vector3f(0, 0, 0);
        return lastCarRotation[car];
    }

    private List<PlatformInfo> buildRoutePlatforms(long routeId, long nextRouteId) {
        if (routeId == 0) {
            return List.of();
        }
        final Route route = MinecraftClientData.getInstance().routeIdMap.get(routeId);
        if (route != null) {
            final Route nextRoute = nextRouteId == 0 ? null : MinecraftClientData.getInstance().routeIdMap.get(nextRouteId);
            final List<PlatformInfo> result = new ArrayList<>(route.getRoutePlatforms().size());
            appendRoutePlatforms(route, nextRoute, result, 0);
            return result;
        }
        final org.mtr.core.data.SimplifiedRoute simplifiedRoute = MinecraftClientData.getInstance().simplifiedRouteIdMap.get(routeId);
        if (simplifiedRoute == null) {
            return List.of();
        }
        final org.mtr.core.data.SimplifiedRoute nextSimplifiedRoute = nextRouteId == 0 ? null : MinecraftClientData.getInstance().simplifiedRouteIdMap.get(nextRouteId);
        final List<PlatformInfo> result = new ArrayList<>(simplifiedRoute.getPlatforms().size());
        appendSimplifiedRoutePlatforms(simplifiedRoute, nextSimplifiedRoute, result, 0);
        return result;
    }

    private int appendSimplifiedRoutePlatforms(org.mtr.core.data.SimplifiedRoute route, org.mtr.core.data.SimplifiedRoute nextRoute, List<PlatformInfo> result, int pathSearchStart) {
        final RouteCompat routeCompat = new RouteCompat(route);
        final List<org.mtr.core.data.SimplifiedRoutePlatform> routePlatforms = route.getPlatforms();
        if (routePlatforms.isEmpty()) {
            return pathSearchStart;
        }
        final long thisLastPlatformId = routePlatforms.get(routePlatforms.size() - 1).getPlatformId();
        final long nextFirstPlatformId = nextRoute == null || nextRoute.getPlatforms().isEmpty() ? 0 : nextRoute.getPlatforms().get(0).getPlatformId();
        final boolean reverseAtPlatform = nextFirstPlatformId != 0 && thisLastPlatformId == nextFirstPlatformId;
        final org.mtr.core.data.SimplifiedRoutePlatform lastEntry = routePlatforms.get(routePlatforms.size() - 1);
        final String fallbackDestination = lastEntry.getStationName() == null ? "" : lastEntry.getStationName().split("\\|\\|")[0];
        for (int i = 0; i < routePlatforms.size(); i++) {
            final org.mtr.core.data.SimplifiedRoutePlatform simplifiedRoutePlatform = routePlatforms.get(i);
            final long platformId = simplifiedRoutePlatform.getPlatformId();
            String destinationName = simplifiedRoutePlatform.getDestination();
            if (destinationName == null || destinationName.isEmpty()) {
                destinationName = fallbackDestination;
            }
            final PathMatch pathMatch = findDistanceForPlatform(platformId, pathSearchStart);
            pathSearchStart = pathMatch.nextIndex;
            result.add(new PlatformInfo(
                route.getId(), routeCompat,
                rte$resolveStation(simplifiedRoutePlatform),
                rte$resolvePlatform(platformId),
                rte$resolveStation(lastEntry),
                destinationName,
                pathMatch.distance, reverseAtPlatform && i + 1 >= routePlatforms.size()
            ));
        }
        return pathSearchStart;
    }

    private static StationCompat rte$resolveStation(org.mtr.core.data.SimplifiedRoutePlatform simplifiedRoutePlatform) {
        final Platform platform = MinecraftClientData.getInstance().platformIdMap.get(simplifiedRoutePlatform.getPlatformId());
        if (platform != null && platform.area != null && platform.area.getName() != null && !platform.area.getName().isEmpty()) {
            return StationCompat.of(platform.area);
        }
        return StationCompat.ofFallback(simplifiedRoutePlatform.getStationId(),
            rte$bestStationName(simplifiedRoutePlatform.getPlatformId(), simplifiedRoutePlatform.getStationId(), simplifiedRoutePlatform.getStationName()));
    }

    private static StationCompat rte$resolveRouteStation(Platform platform) {
        if (platform == null) {
            return null;
        }
        if (platform.area != null && platform.area.getName() != null && !platform.area.getName().isEmpty()) {
            return StationCompat.of(platform.area);
        }
        final long stationId = platform.area != null ? platform.area.getId() : 0;
        return StationCompat.ofFallback(stationId, rte$bestStationName(platform.getId(), stationId, ""));
    }

    private static String rte$bestStationName(long platformId, long stationId, String hint) {
        if (hint != null && !hint.isEmpty()) {
            return hint;
        }
        final org.mtr.core.data.Station station = stationId != 0 ? MinecraftClientData.getInstance().stationIdMap.get(stationId) : null;
        if (station != null && station.getName() != null && !station.getName().isEmpty()) {
            return station.getName();
        }
        for (final org.mtr.core.data.SimplifiedRoute simplifiedRoute : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {
            for (final org.mtr.core.data.SimplifiedRoutePlatform srp : simplifiedRoute.getPlatforms()) {
                if (srp.getPlatformId() == platformId && srp.getStationName() != null && !srp.getStationName().isEmpty()) {
                    return srp.getStationName();
                }
            }
        }
        return "";
    }

    private static PlatformCompat rte$resolvePlatform(long platformId) {
        final Platform platform = MinecraftClientData.getInstance().platformIdMap.get(platformId);
        return platform != null ? new PlatformCompat(platform) : new PlatformCompat(platformId);
    }


    private List<PlatformInfo> buildSchedulePlatforms() {
        final cn.jstxjf_.rte.scripting.compat.SidingCompat sidingCompat = siding();
        final Siding siding = sidingCompat == null ? null : sidingCompat.getMtr4Siding();
        final org.mtr.core.data.Depot depot = siding == null ? null : siding.area;

        final List<Long> targetRouteIds = new ArrayList<>();
        final long vehicleId = train.getId();
        final Long persisted = LAST_KNOWN_ROUTE_ID.get(vehicleId);
        final java.util.Set<Long> pathPlatformIds = rte$pathPlatformIds();
        long chosen = 0;

        final long thisRouteId = train.vehicleExtraData.getThisRouteId();
        if (thisRouteId != 0 && rte$routeHasPlatforms(thisRouteId)) {
            chosen = thisRouteId;
        }
        if (chosen == 0) {
            final long nextRouteId = train.vehicleExtraData.getNextRouteId();
            if (nextRouteId != 0 && rte$routeHasPlatforms(nextRouteId)) {
                chosen = nextRouteId;
            }
        }
        final boolean confirmedByMtr = chosen != 0;
        if (chosen == 0 && persisted != null && rte$routeHasPlatforms(persisted)
                && (isOnRoute() || depot == null || depot.getRouteIds().isEmpty())) {
            chosen = persisted;
        }
        if (chosen == 0) {
            chosen = rte$bestMatchingRouteId(pathPlatformIds);
        }
        if (chosen != 0) {
            targetRouteIds.add(chosen);
            if (confirmedByMtr) {
                LAST_KNOWN_ROUTE_ID.put(vehicleId, chosen);
            }
        }
        if (targetRouteIds.isEmpty() && depot != null) {
            final java.util.Set<Long> candidates = new java.util.LinkedHashSet<>();
            for (final long routeId : depot.getRouteIds()) {
                if (routeId != 0 && rte$routeHasPlatforms(routeId)) {
                    candidates.add(routeId);
                }
            }
            for (final Route route : depot.routes) {
                if (route != null && !route.getRoutePlatforms().isEmpty()) {
                    candidates.add(route.getId());
                }
            }
            if (candidates.size() == 1) {
                targetRouteIds.add(candidates.iterator().next());
            }
        }
        if (targetRouteIds.isEmpty()) {
            final java.util.Set<Long> worldRoutes = new java.util.LinkedHashSet<>();
            for (final var entry : MinecraftClientData.getInstance().routeIdMap.long2ObjectEntrySet()) {
                final Route route = entry.getValue();
                if (route != null && !route.getRoutePlatforms().isEmpty() && !route.getHidden()) {
                    worldRoutes.add(entry.getLongKey());
                }
            }
            if (worldRoutes.isEmpty()) {
                for (final org.mtr.core.data.SimplifiedRoute simplifiedRoute
                        : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {
                    if (simplifiedRoute != null && !simplifiedRoute.getPlatforms().isEmpty()) {
                        worldRoutes.add(simplifiedRoute.getId());
                    }
                }
            }
            if (worldRoutes.size() == 1) {
                targetRouteIds.add(worldRoutes.iterator().next());
            }
        }
        if (targetRouteIds.isEmpty()) {
            return List.of();
        }

        final List<PlatformInfo> result = new ArrayList<>();
        int pathSearchStart = 0;
        for (int i = 0; i < targetRouteIds.size(); i++) {
            final long routeId = targetRouteIds.get(i);
            final long nextTargetId = i + 1 < targetRouteIds.size() ? targetRouteIds.get(i + 1) : 0;
            final Route fullRoute = MinecraftClientData.getInstance().routeIdMap.get(routeId);
            if (fullRoute != null && !fullRoute.getRoutePlatforms().isEmpty()) {
                final Route nextFull = nextTargetId == 0 ? null : MinecraftClientData.getInstance().routeIdMap.get(nextTargetId);
                pathSearchStart = appendRoutePlatforms(fullRoute, nextFull, result, pathSearchStart);
            } else {
                final org.mtr.core.data.SimplifiedRoute simplifiedRoute = MinecraftClientData.getInstance().simplifiedRouteIdMap.get(routeId);
                if (simplifiedRoute != null && !simplifiedRoute.getPlatforms().isEmpty()) {
                    final org.mtr.core.data.SimplifiedRoute nextSimplified = nextTargetId == 0 ? null : MinecraftClientData.getInstance().simplifiedRouteIdMap.get(nextTargetId);
                    pathSearchStart = appendSimplifiedRoutePlatforms(simplifiedRoute, nextSimplified, result, pathSearchStart);
                }
            }
        }
        rte$dedupePlatforms(result);
        return result;
    }

    private static void rte$dedupePlatforms(List<PlatformInfo> platforms) {
        final java.util.Set<Long> seen = new java.util.HashSet<>();
        final java.util.Iterator<PlatformInfo> iterator = platforms.iterator();
        while (iterator.hasNext()) {
            final PlatformInfo platformInfo = iterator.next();
            final long platformId = platformInfo.platform == null ? 0 : (long) platformInfo.platform.id;
            if (platformId != 0 && !seen.add(platformId)) {
                iterator.remove();
            }
        }
    }

    private boolean rte$routeHasPlatforms(long routeId) {
        final Route fullRoute = MinecraftClientData.getInstance().routeIdMap.get(routeId);
        if (fullRoute != null && !fullRoute.getRoutePlatforms().isEmpty()) {
            return true;
        }
        final org.mtr.core.data.SimplifiedRoute simplifiedRoute = MinecraftClientData.getInstance().simplifiedRouteIdMap.get(routeId);
        return simplifiedRoute != null && !simplifiedRoute.getPlatforms().isEmpty();
    }

    private static final java.util.Map<Long, Long> LAST_KNOWN_ROUTE_ID = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<Long, List<PlatformInfo>> LAST_GOOD_ALL_PLATFORMS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<Long, List<PlatformInfo>> LAST_GOOD_ROUTE_PLATFORMS = new java.util.concurrent.ConcurrentHashMap<>();

    private static volatile long rte$lastPruneMillis = 0;

    private static void rte$pruneStaleCaches() {
        final long now = System.currentTimeMillis();
        if (now - rte$lastPruneMillis < 30000) {
            return;
        }
        rte$lastPruneMillis = now;
        final java.util.Set<Long> alive = new java.util.HashSet<>();
        try {
            MinecraftClientData.getInstance().vehicles.forEach(vehicle -> alive.add(vehicle.getId()));
        } catch (Exception e) {
            return;
        }
        if (alive.isEmpty()) {
            return;
        }
        LAST_KNOWN_SIDING.keySet().retainAll(alive);
        LAST_KNOWN_ROUTE_ID.keySet().retainAll(alive);
        LAST_GOOD_ALL_PLATFORMS.keySet().retainAll(alive);
        LAST_GOOD_ROUTE_PLATFORMS.keySet().retainAll(alive);
        cn.jstxjf_.rte.render.train.VehiclePathCache.retainAll(alive);
    }

    private java.util.Set<Long> rte$pathPlatformIds() {
        final java.util.Set<Long> pathPlatformIds = new java.util.LinkedHashSet<>();
        for (final PathData pathData : train.vehicleExtraData.immutablePath) {
            if (pathData.getSavedRailBaseId() != 0) {
                pathPlatformIds.add(pathData.getSavedRailBaseId());
            }
        }
        return pathPlatformIds;
    }

    private long rte$bestMatchingRouteId(java.util.Set<Long> pathPlatformIds) {
        if (pathPlatformIds.isEmpty()) {
            return 0;
        }
        long bestRouteId = 0;
        int bestMatch = 0;
        int bestSizeGap = Integer.MAX_VALUE;
        final int pathSize = pathPlatformIds.size();
        for (final org.mtr.core.data.SimplifiedRoute simplifiedRoute : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {
            int match = 0;
            for (final org.mtr.core.data.SimplifiedRoutePlatform simplifiedRoutePlatform : simplifiedRoute.getPlatforms()) {
                if (pathPlatformIds.contains(simplifiedRoutePlatform.getPlatformId())) {
                    match++;
                }
            }
            if (match == 0) {
                continue;
            }
            final int sizeGap = Math.abs(simplifiedRoute.getPlatforms().size() - pathSize);
            final long routeId = simplifiedRoute.getId();
            final boolean better = match > bestMatch
                || (match == bestMatch && sizeGap < bestSizeGap)
                || (match == bestMatch && sizeGap == bestSizeGap && (bestRouteId == 0 || routeId < bestRouteId));
            if (better) {
                bestMatch = match;
                bestSizeGap = sizeGap;
                bestRouteId = routeId;
            }
        }
        return bestRouteId;
    }

    private int appendRoutePlatforms(Route route, Route nextRoute, List<PlatformInfo> result, int pathSearchStart) {
        final RouteCompat routeCompat = new RouteCompat(route);
        final List<RoutePlatformData> routePlatforms = route.getRoutePlatforms();
        final List<RoutePlatformData> nextRoutePlatforms = nextRoute == null ? List.of() : nextRoute.getRoutePlatforms();
        final Platform thisLastPlatform = routePlatforms.isEmpty() ? null : routePlatforms.get(routePlatforms.size() - 1).getPlatform();
        final Platform nextFirstPlatform = nextRoutePlatforms.isEmpty() ? null : nextRoutePlatforms.get(0).getPlatform();
        final boolean reverseAtPlatform = thisLastPlatform != null && nextFirstPlatform != null && thisLastPlatform.getId() == nextFirstPlatform.getId();
        final Platform destinationPlatform = routePlatforms.isEmpty() ? null : routePlatforms.get(routePlatforms.size() - 1).getPlatform();
        final Station destinationStation = destinationPlatform == null ? null : destinationPlatform.area;
        for (int i = 0; i < routePlatforms.size(); i++) {
            final RoutePlatformData routePlatformData = routePlatforms.get(i);
            final Platform platform = routePlatformData.getPlatform();
            if (platform == null) {
                continue;
            }
            String destinationName = route.getDestination(i);
            if (destinationName == null || destinationName.isEmpty()) {
                destinationName = destinationPlatform == null ? "" : rte$bestStationName(destinationPlatform.getId(),
                    destinationStation == null ? 0 : destinationStation.getId(), destinationStation == null ? "" : destinationStation.getName());
            }
            final PathMatch pathMatch = findDistanceForPlatform(platform, pathSearchStart);
            pathSearchStart = pathMatch.nextIndex;
            result.add(new PlatformInfo(
                (Long) route.getId(), routeCompat, rte$resolveRouteStation(platform),
                new PlatformCompat(platform),
                rte$resolveRouteStation(destinationPlatform), destinationName,
                pathMatch.distance, reverseAtPlatform && i + 1 >= routePlatforms.size()
            ));
        }
        return pathSearchStart;
    }

    private static boolean isReplayMod() {
        final Player player = Minecraft.getInstance().player;
        if (player != null && player.getClass().toGenericString().toLowerCase(Locale.ENGLISH).contains("replaymod")) {
            return true;
        }
        try {
            Class.forName("com.replaymod.replay.ReplayModReplay");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private PathMatch findDistanceForPlatform(Platform platform, int startIndex) {
        if (platform == null) {
            return new PathMatch(0, startIndex);
        }
        return findDistanceForPlatform(platform.getId(), startIndex);
    }

    private PathMatch findDistanceForPlatform(long platformId, int startIndex) {
        if (platformId == 0) {
            return new PathMatch(0, startIndex);
        }
        for (int i = Math.max(0, startIndex); i < train.vehicleExtraData.immutablePath.size(); i++) {
            final PathData pathData = train.vehicleExtraData.immutablePath.get(i);
            if (pathData.getSavedRailBaseId() == platformId) {
                return new PathMatch(pathData.getEndDistance(), i + 1);
            }
        }
        for (int i = 0; i < Math.max(0, startIndex); i++) {
            final PathData pathData = train.vehicleExtraData.immutablePath.get(i);
            if (pathData.getSavedRailBaseId() == platformId) {
                return new PathMatch(pathData.getEndDistance(), i + 1);
            }
        }
        return new PathMatch(0, startIndex);
    }

    private int findNextPlatformIndex(List<PlatformInfo> platforms) {
        final double progress = railProgress();
        for (int i = 0; i < platforms.size(); i++) {
            if (platforms.get(i).distance >= progress) {
                return i;
            }
        }
        return platforms.size();
    }

    private void rte$canonicalizePlatforms() {
        if (allPlatforms.isEmpty()) {
            return;
        }
        thisRoutePlatforms = rte$alignTo(allPlatforms, thisRoutePlatforms);
        nextRoutePlatforms = rte$alignTo(allPlatforms, nextRoutePlatforms);
    }

    private static List<PlatformInfo> rte$alignTo(List<PlatformInfo> canonical, List<PlatformInfo> list) {
        if (list.isEmpty() || list == canonical) {
            return list;
        }
        final List<PlatformInfo> result = new ArrayList<>(list.size());
        for (final PlatformInfo info : list) {
            PlatformInfo match = null;
            for (final PlatformInfo candidate : canonical) {
                if (rte$samePlatform(candidate, info)) {
                    match = candidate;
                    break;
                }
            }
            result.add(match != null ? match : info);
        }
        return result;
    }

    private static boolean rte$samePlatform(PlatformInfo a, PlatformInfo b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.platform == null || b.platform == null) {
            return false;
        }
        return a.platform.idLong != 0 && a.platform.idLong == b.platform.idLong
            && java.util.Objects.equals(a.routeId, b.routeId);
    }

    private static int indexOfPlatformId(List<PlatformInfo> platforms, long platformId) {
        if (platformId == 0) {
            return -1;
        }
        for (int i = 0; i < platforms.size(); i++) {
            if (platforms.get(i).platform != null && platforms.get(i).platform.idLong == platformId) {
                return i;
            }
        }
        return -1;
    }

    private int findNearestPlatformIndexByPosition(List<PlatformInfo> platforms) {
        final org.mtr.core.tool.Vector head;
        try {
            head = train.getHeadPosition();
        } catch (Exception e) {
            return -1;
        }
        if (head == null) {
            return -1;
        }
        int bestIndex = -1;
        double bestDistanceSquared = Double.MAX_VALUE;
        for (int i = 0; i < platforms.size(); i++) {
            final PlatformInfo platformInfo = platforms.get(i);
            if (platformInfo.platform == null) {
                continue;
            }
            final Platform platform = MinecraftClientData.getInstance().platformIdMap.get(platformInfo.platform.idLong);
            if (platform == null) {
                continue;
            }
            final org.mtr.core.data.Position mid = platform.getMidPosition();
            final double dx = mid.getX() + 0.5 - head.x;
            final double dz = mid.getZ() + 0.5 - head.z;
            final double distanceSquared = dx * dx + dz * dz;
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestDistanceSquared <= 200 * 200 ? bestIndex : -1;
    }

    private static final class PathMatch {
        private final double distance;
        private final int nextIndex;

        private PathMatch(double distance, int nextIndex) {
            this.distance = distance;
            this.nextIndex = nextIndex;
        }
    }

    public static final class PlatformInfo {
        public final Long routeId;
        public final RouteCompat route;
        public final StationCompat station;
        public final PlatformCompat platform;
        public final StationCompat destinationStation;
        public final String destinationName;
        public double distance;
        public final boolean reverseAtPlatform;

        public PlatformInfo(Long routeId, RouteCompat route, Station station, Platform platform,
                            Station destinationStation, String destinationName, double distance,
                            boolean reverseAtPlatform) {
            this(routeId, route, StationCompat.of(station), platform != null ? new PlatformCompat(platform) : null,
                StationCompat.of(destinationStation), destinationName, distance, reverseAtPlatform);
        }

        public PlatformInfo(Long routeId, RouteCompat route, StationCompat station, PlatformCompat platform,
                            StationCompat destinationStation, String destinationName, double distance,
                            boolean reverseAtPlatform) {
            this.routeId = routeId;
            this.route = route;
            this.station = station;
            this.platform = platform;
            this.destinationStation = destinationStation;
            this.destinationName = destinationName;
            this.distance = distance;
            this.reverseAtPlatform = reverseAtPlatform;
        }

        public String stationName() {
            return station != null ? station.name : "";
        }

        public String platformName() {
            return platform != null ? platform.name : "";
        }

        public String routeName() {
            return route != null ? route.name : "";
        }

        public int routeColor() {
            return route != null ? route.color : 0;
        }

        public String destinationStationName() {
            return destinationStation != null ? destinationStation.name : "";
        }
    }
}
