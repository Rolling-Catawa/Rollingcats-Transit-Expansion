package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.IRoute;
import org.mtr.core.data.Depot;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.Station;
import org.mtr.core.path.SidingPathFinder;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Depot.class, remap = false)
public abstract class DepotMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rte$tick(CallbackInfo ci) {
        final DepotAccessor accessor = (DepotAccessor) (Object) this;
        if (!rte$shouldUseCustomRoutePath()) {
            return;
        }

        accessor.rte$getPath().clear();
        accessor.rte$getGeneratingSidingIds().clear();

        final ObjectArrayList<Platform> overallPlatforms = new ObjectArrayList<>();
        final ObjectArrayList<PathData> generatedPath = rte$buildCustomPath(overallPlatforms);
        if (generatedPath == null) {
            accessor.rte$getPath().clear();
            accessor.rte$getSidingPathFinders().clear();
            ci.cancel();
            return;
        }

        accessor.rte$getPath().addAll(generatedPath);
        accessor.rte$getSidingPathFinders().clear();
        ((DepotSchemaAccessor) (Object) this).rte$setLastGeneratedFailedSidingCount(0);
        PathData.writePathCache(accessor.rte$getPath(), ((NameColorDataBaseSchemaAccessor) (Object) this).rte$getData(), ((Depot) (Object) this).getTransportMode());
        rte$startSidingGeneration(overallPlatforms);
        ci.cancel();
    }

    private boolean rte$shouldUseCustomRoutePath() {
        final DepotAccessor accessor = (DepotAccessor) (Object) this;
        if (accessor.rte$getSidingPathFinders().isEmpty()) {
            return false;
        }

        for (Route route : accessor.rte$getRoutes()) {
            if (route != null && !((IRoute) (Object) route).getPathData().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private ObjectArrayList<PathData> rte$buildCustomPath(ObjectArrayList<Platform> overallPlatforms) {
        final ObjectArrayList<PathData> generatedPath = new ObjectArrayList<>();
        Platform previousTerminalPlatform = null;
        int stopIndexOffset = 0;

        for (Route route : ((DepotAccessor) (Object) this).rte$getRoutes()) {
            if (route == null) {
                continue;
            }

            final List<Platform> routePlatforms = rte$getSequentialPlatforms(route);
            if (routePlatforms.isEmpty()) {
                continue;
            }

            for (Platform routePlatform : routePlatforms) {
                if (overallPlatforms.isEmpty() || overallPlatforms.get(overallPlatforms.size() - 1).getId() != routePlatform.getId()) {
                    overallPlatforms.add(routePlatform);
                }
            }

            final Platform routeFirstPlatform = routePlatforms.get(0);
            if (previousTerminalPlatform != null && previousTerminalPlatform.getId() != routeFirstPlatform.getId()) {
                final ObjectArrayList<PathData> boundaryPath = rte$generateAutoPath(previousTerminalPlatform, routeFirstPlatform, stopIndexOffset);
                if (boundaryPath == null) {
                    rte$failPathGeneration(previousTerminalPlatform, routeFirstPlatform);
                    return null;
                }
                rte$appendPathChunk(generatedPath, boundaryPath);
                stopIndexOffset++;
            }

            final ObjectArrayList<PathData> routePath = rte$getRoutePath(route, routePlatforms, stopIndexOffset);
            if (routePath == null) {
                return null;
            }
            rte$appendPathChunk(generatedPath, routePath);
            stopIndexOffset += routePlatforms.size() - 1;
            previousTerminalPlatform = routePlatforms.get(routePlatforms.size() - 1);
        }

        return generatedPath.size() >= 2 ? generatedPath : null;
    }

    private ObjectArrayList<PathData> rte$getRoutePath(Route route, List<Platform> routePlatforms, int stopIndexOffset) {
        final List<PathData> manualPath = ((IRoute) (Object) route).getPathData();
        if (!manualPath.isEmpty() && rte$manualPathMatchesPlatforms(manualPath, routePlatforms)) {
            return rte$normalizeManualPath(manualPath, stopIndexOffset);
        }

        if (!manualPath.isEmpty()) {
            RTE.LOGGER.warn(
                "RTE route path data no longer matches route platforms, falling back to MTR4 auto path generation for depot {} route {}",
                ((Depot) (Object) this).getId(),
                route.getId()
            );
        }

        return rte$generateAutoPath(routePlatforms, stopIndexOffset);
    }

    private List<Platform> rte$getSequentialPlatforms(Route route) {
        final List<Platform> routePlatforms = new ArrayList<>();
        long lastPlatformId = 0;

        for (RoutePlatformData routePlatformData : route.getRoutePlatforms()) {
            final Platform platform = routePlatformData.getPlatform();
            if (platform != null && platform.getId() != lastPlatformId) {
                routePlatforms.add(platform);
                lastPlatformId = platform.getId();
            }
        }

        return routePlatforms;
    }

    private boolean rte$manualPathMatchesPlatforms(List<PathData> manualPath, List<Platform> routePlatforms) {
        final List<Long> manualPlatformIds = rte$getManualPlatformIds(manualPath);
        if (manualPlatformIds.size() != routePlatforms.size()) {
            return false;
        }

        for (int i = 0; i < routePlatforms.size(); i++) {
            if (manualPlatformIds.get(i) != routePlatforms.get(i).getId()) {
                return false;
            }
        }
        return true;
    }

    private List<Long> rte$getManualPlatformIds(List<PathData> manualPath) {
        final List<Long> platformIds = new ArrayList<>();
        PathData previous = null;
        long lastPlatformId = 0;

        for (PathData pathData : manualPath) {
            if (pathData == null) {
                continue;
            }

            final long platformId = pathData.getSavedRailBaseId();
            if (platformId != 0 && (previous == null || !previous.isOppositeRail(pathData)) && platformId != lastPlatformId) {
                platformIds.add(platformId);
                lastPlatformId = platformId;
            }
            previous = pathData;
        }

        return platformIds;
    }

    private ObjectArrayList<PathData> rte$normalizeManualPath(List<PathData> manualPath, int stopIndexOffset) {
        final ObjectArrayList<PathData> normalizedPath = new ObjectArrayList<>();
        PathData previous = null;
        long lastPlatformId = 0;
        int localStopIndex = 0;

        for (PathData pathData : manualPath) {
            if (pathData == null) {
                continue;
            }

            final long platformId = pathData.getSavedRailBaseId();
            if (platformId != 0 && (previous == null || !previous.isOppositeRail(pathData)) && platformId != lastPlatformId) {
                if (lastPlatformId != 0) {
                    localStopIndex++;
                }
                lastPlatformId = platformId;
            }

            normalizedPath.add(new PathData(
                pathData.getRail(),
                platformId,
                pathData.getDwellTime(),
                stopIndexOffset + localStopIndex,
                pathData.getOrderedPosition1(),
                pathData.getOrderedPosition2()
            ));
            previous = pathData;
        }

        return normalizedPath;
    }

    private ObjectArrayList<PathData> rte$generateAutoPath(List<Platform> routePlatforms, int stopIndexOffset) {
        final ObjectArrayList<PathData> generatedPath = new ObjectArrayList<>();

        for (int i = 0; i < routePlatforms.size() - 1; i++) {
            final Platform startPlatform = routePlatforms.get(i);
            final Platform endPlatform = routePlatforms.get(i + 1);
            final ObjectArrayList<PathData> segmentPath = rte$generateAutoPath(startPlatform, endPlatform, stopIndexOffset + i);
            if (segmentPath == null) {
                rte$failPathGeneration(startPlatform, endPlatform);
                return null;
            }
            rte$appendPathChunk(generatedPath, segmentPath);
        }

        return generatedPath;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ObjectArrayList<PathData> rte$generateAutoPath(Platform startPlatform, Platform endPlatform, int stopIndex) {
        final SidingPathFinder<Station, Platform, Station, Platform> sidingPathFinder =
            new SidingPathFinder<>(((NameColorDataBaseSchemaAccessor) (Object) this).rte$getData(), startPlatform, endPlatform, stopIndex);
        final ObjectArrayList<PathData> segmentPath = ((SidingPathFinderInvoker) (Object) sidingPathFinder).rte$invokeTick(((Depot) (Object) this).getCruisingAltitude());
        return segmentPath == null || segmentPath.size() < 2 ? null : segmentPath;
    }

    private void rte$appendPathChunk(ObjectArrayList<PathData> targetPath, ObjectArrayList<PathData> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        if (SidingPathFinder.overlappingPaths(targetPath, chunk)) {
            chunk.remove(0);
        }
        targetPath.addAll(chunk);
    }

    private void rte$startSidingGeneration(List<Platform> overallPlatforms) {
        if (overallPlatforms.isEmpty()) {
            return;
        }

        final Platform firstPlatform = overallPlatforms.get(0);
        final Platform lastPlatform = ((Depot) (Object) this).getRepeatInfinitely() ? null : overallPlatforms.get(overallPlatforms.size() - 1);
        final int platformCount = overallPlatforms.size();
        final long cruisingAltitude = ((Depot) (Object) this).getCruisingAltitude();

        final DepotAccessor accessor = (DepotAccessor) (Object) this;
        ((AreaBaseAccessor) (Object) this).rte$getSavedRails().forEach(siding -> {
            siding.generateRoute(firstPlatform, lastPlatform, platformCount, cruisingAltitude);
            accessor.rte$getGeneratingSidingIds().add(siding.getId());
        });
    }

    private void rte$failPathGeneration(Platform startPlatform, Platform endPlatform) {
        final DepotAccessor accessor = (DepotAccessor) (Object) this;
        accessor.rte$getPath().clear();
        accessor.rte$getSidingPathFinders().clear();
        accessor.rte$invokeUpdateGenerationStatus(Depot.GeneratedStatus.PATH_NOT_FOUND, startPlatform.getId(), endPlatform.getId(), "Path not found for %s");
    }
}
