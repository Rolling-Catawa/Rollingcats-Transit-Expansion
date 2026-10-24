package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.client.Minecraft;
import org.mtr.legacy.resource.CustomResourcesConverter;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mod.client.CustomResourceLoader;
import org.mtr.mod.config.Config;
import org.mtr.mod.resource.CustomResources;
import org.mtr.mod.resource.LiftResource;
import org.mtr.mod.resource.ObjectResource;
import org.mtr.mod.resource.RailResource;
import org.mtr.mod.resource.SignResource;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

@Mixin(value = CustomResourceLoader.class, remap = false)
public class CustomResourceLoaderMixin {

    @Inject(method = "reload", at = @At("HEAD"))
    private static void rte$reloadHead(CallbackInfo ci) {
        cn.jstxjf_.rte.render.PreloadBudget.reset();
        cn.jstxjf_.rte.render.train.AsyncVehicleModelLoader.clear();
        cn.jstxjf_.rte.render.train.VehicleWarmup.clear();
        cn.jstxjf_.rte.render.train.VehicleMeshDiskCache.clearFingerprints();
        final Minecraft client = Minecraft.getInstance();
        if (client != null) {
            MtrModelRegistryUtil.resourceManager = client.getResourceManager();
        }
    }

    @Inject(method = "reload", at = @At("TAIL"))
    private static void rte$reloadTail(CallbackInfo ci) {
        final Minecraft client = Minecraft.getInstance();
        if (client != null) {
            try {
                rte$loadLegacyVehicleResources(client);
            } catch (Throwable e) {
                cn.jstxjf_.rte.RTE.LOGGER.error("[RTE] Failed loading legacy vehicle resources after MTR reload", e);
            }
            RTEClient.reloadResourcesAfterMtr(client.getResourceManager());
            rte$scheduleWarmup();
        }
    }

    private static void rte$scheduleWarmup() {
        final java.util.List<VehicleResource> vehicleResources = new java.util.ArrayList<>();
        rte$getVehiclesCache().values().forEach(vehicles -> vehicles.values().forEach(pair -> vehicleResources.add(pair.left())));
        cn.jstxjf_.rte.render.train.VehicleWarmup.schedule(vehicleResources);
        if (cn.jstxjf_.rte.render.train.VehicleMeshDiskCache.isEnabled()) {
            java.util.concurrent.CompletableFuture.runAsync(cn.jstxjf_.rte.render.train.VehicleMeshDiskCache::prune);
        }
    }

    private static void rte$loadLegacyVehicleResources(Minecraft client) {
        if (client.getResourceManager() == null) {
            return;
        }
        client.getResourceManager().getNamespaces().stream()
            .sorted()
            .forEach(namespace -> {
                rte$loadLegacyVehicles(client, namespace, CustomResourceLoader.CUSTOM_RESOURCES_ID + ".json");
                rte$loadLegacyVehicles(client, namespace, CustomResourceLoader.CUSTOM_RESOURCES_PENDING_MIGRATION_ID + ".json");
            });
    }

    private static void rte$loadLegacyVehicles(Minecraft client, String namespace, String path) {
        final ResourceLocation location = new ResourceLocation(namespace, path);
        for (Resource resource : client.getResourceManager().getResourceStack(location)) {
            try (InputStream inputStream = resource.open()) {
                rte$registerLegacyResources(location, CustomResourcesConverter.convert(rte$normalizeLegacyCustomResources(Config.readResource(inputStream).getAsJsonObject()), CustomResourceLoaderMixin::rte$readLegacyResource));
            } catch (Exception e) {
                org.mtr.mod.Init.LOGGER.error("Failed loading legacy vehicle resources from " + location, e);
            }
        }
    }

    private static JsonObject rte$normalizeLegacyCustomResources(JsonObject root) {
        if (root.has("custom_trains") && root.get("custom_trains").isJsonObject()) {
            root.getAsJsonObject("custom_trains").entrySet().forEach(entry -> {
                if (entry.getValue().isJsonObject()) {
                    final JsonObject train = entry.getValue().getAsJsonObject();
                    if (train.has("base_type") && !train.has("base_train_type")) {
                        train.add("base_train_type", train.get("base_type"));
                    }
                    rte$attachScriptOnlyBody(train);
                    if (train.has("bve_sound_base_id") && train.get("bve_sound_base_id").isJsonPrimitive()) {
                        final String soundBaseId = train.get("bve_sound_base_id").getAsString();
                        train.addProperty("bve_sound_base_id", soundBaseId.toLowerCase(java.util.Locale.ROOT));
                    }
                }
            });
        }
        return root;
    }

    private static final double RTE_LEGACY_CAR_SPACING_EXTRA = 1;
    private static final String RTE_SCRIPT_BODY_PREFIX = "legacy_script_body/";
    private static final String RTE_SCRIPT_BODY_MODEL = "rte:models/legacy_script_empty.obj";
    private static final java.util.regex.Pattern RTE_GENERIC_BASE = java.util.regex.Pattern.compile("(?:train|base)_(\\d+(?:\\.\\d+)?)_(\\d+(?:\\.\\d+)?)(?:_.*)?");

    private static boolean rte$isBlank(JsonObject train, String key) {
        return !train.has(key) || !train.get(key).isJsonPrimitive() || train.get(key).getAsString().isEmpty();
    }

    private static void rte$attachScriptOnlyBody(JsonObject train) {
        if (!rte$isBlank(train, "model") || !rte$isBlank(train, "model_properties") || rte$isBlank(train, "base_train_type")) {
            return;
        }
        if (!train.has("script_files") && !train.has("script_texts")) {
            return;
        }
        final java.util.regex.Matcher matcher = RTE_GENERIC_BASE.matcher(train.get("base_train_type").getAsString().toLowerCase(java.util.Locale.ROOT));
        if (!matcher.matches()) {
            return;
        }
        train.addProperty("model", RTE_SCRIPT_BODY_MODEL);
        train.addProperty("model_properties", "rte:" + RTE_SCRIPT_BODY_PREFIX + matcher.group(1) + "_" + matcher.group(2) + ".json");
    }

    private static String rte$readLegacyResource(Identifier identifier) {
        if ("rte".equals(identifier.getNamespace()) && identifier.getPath().startsWith(RTE_SCRIPT_BODY_PREFIX)) {
            final String[] size = identifier.getPath().substring(RTE_SCRIPT_BODY_PREFIX.length()).replace(".json", "").split("_");
            if (size.length == 2) {
                final JsonObject body = new JsonObject();
                body.addProperty("transport_mode", "TRAIN");
                body.addProperty("length", Double.parseDouble(size[0]) + RTE_LEGACY_CAR_SPACING_EXTRA);
                body.addProperty("width", Double.parseDouble(size[1]));
                body.addProperty("door_max", 0);
                return body.toString();
            }
        }
        return rte$invokeReadResource(identifier);
    }

    private static final double RTE_PRELOAD_HEAP_LIMIT = 0.6;

    private static boolean rte$canKeepPreloading() {
        final Runtime runtime = Runtime.getRuntime();
        final long used = runtime.totalMemory() - runtime.freeMemory();
        return used < runtime.maxMemory() * RTE_PRELOAD_HEAP_LIMIT;
    }

    private static void rte$registerLegacyResources(ResourceLocation location, CustomResources customResources) {
        final int[] counts = new int[6];
        customResources.iterateVehicles(vehicleResource -> {
            final Object2ObjectAVLTreeMap<String, ObjectBooleanImmutablePair<VehicleResource>> vehicleCache = rte$getVehiclesCache().get(vehicleResource.getTransportMode());
            if (vehicleCache != null && vehicleCache.containsKey(vehicleResource.getId())) {
                return;
            }
            rte$invokeRegisterVehicle(vehicleResource, false);
            if (vehicleResource.shouldPreload) {
                counts[1]++;
            }
            counts[0]++;
        });
        customResources.iterateSigns(signResource -> {
            if (rte$getSignsCache().containsKey(signResource.signId)) {
                return;
            }
            if (signResource.isDefault) {
                rte$getSigns().add(0, signResource);
            } else {
                rte$getSigns().add(signResource);
            }
            rte$getSignsCache().put(signResource.signId, signResource);
            counts[2]++;
        });
        customResources.iterateRails(railResource -> {
            if (rte$getRailsCache().containsKey(railResource.getId())) {
                return;
            }
            rte$getRails().add(railResource);
            rte$getRailsCache().put(railResource.getId(), railResource);
            if (railResource.shouldPreload && rte$canKeepPreloading()) {
                railResource.preload();
            }
            counts[3]++;
        });
        customResources.iterateObjects(objectResource -> {
            if (rte$getObjectsCache().containsKey(objectResource.getId())) {
                return;
            }
            rte$getObjects().add(objectResource);
            rte$getObjectsCache().put(objectResource.getId(), objectResource);
            if (objectResource.shouldPreload && rte$canKeepPreloading()) {
                objectResource.preload();
            }
            counts[4]++;
        });
        customResources.iterateLifts(liftResource -> {
            if (rte$getLiftsCache().containsKey(liftResource.getId())) {
                return;
            }
            rte$getLifts().add(liftResource);
            rte$getLiftsCache().put(liftResource.getId(), liftResource);
            counts[5]++;
        });
    }

    @Invoker("registerVehicle")
    private static void rte$invokeRegisterVehicle(VehicleResource vehicleResource, boolean fromResourcePackCreator) {
        throw new AssertionError();
    }

    @Invoker("readResource")
    private static String rte$invokeReadResource(Identifier identifier) {
        throw new AssertionError();
    }

    @Accessor("SIGNS")
    private static ObjectArrayList<SignResource> rte$getSigns() {
        throw new AssertionError();
    }

    @Accessor("VEHICLES_CACHE")
    private static Object2ObjectAVLTreeMap<org.mtr.core.data.TransportMode, Object2ObjectAVLTreeMap<String, ObjectBooleanImmutablePair<VehicleResource>>> rte$getVehiclesCache() {
        throw new AssertionError();
    }

    @Accessor("SIGNS_CACHE")
    private static Object2ObjectAVLTreeMap<String, SignResource> rte$getSignsCache() {
        throw new AssertionError();
    }

    @Accessor("RAILS")
    private static ObjectArrayList<RailResource> rte$getRails() {
        throw new AssertionError();
    }

    @Accessor("RAILS_CACHE")
    private static Object2ObjectAVLTreeMap<String, RailResource> rte$getRailsCache() {
        throw new AssertionError();
    }

    @Accessor("OBJECTS")
    private static ObjectArrayList<ObjectResource> rte$getObjects() {
        throw new AssertionError();
    }

    @Accessor("OBJECTS_CACHE")
    private static Object2ObjectAVLTreeMap<String, ObjectResource> rte$getObjectsCache() {
        throw new AssertionError();
    }

    @Accessor("LIFTS")
    private static ObjectArrayList<LiftResource> rte$getLifts() {
        throw new AssertionError();
    }

    @Accessor("LIFTS_CACHE")
    private static Object2ObjectAVLTreeMap<String, LiftResource> rte$getLiftsCache() {
        throw new AssertionError();
    }
}
