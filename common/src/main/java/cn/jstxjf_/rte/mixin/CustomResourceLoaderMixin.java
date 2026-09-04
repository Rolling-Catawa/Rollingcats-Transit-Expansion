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
        final Minecraft client = Minecraft.getInstance();
        if (client != null) {
            MtrModelRegistryUtil.resourceManager = client.getResourceManager();
        }
    }

    @Inject(method = "reload", at = @At("TAIL"))
    private static void rte$reloadTail(CallbackInfo ci) {
        final Minecraft client = Minecraft.getInstance();
        if (client != null) {
            rte$loadLegacyVehicleResources(client);
            RTEClient.reloadResources(client.getResourceManager());
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
                rte$registerLegacyResources(location, CustomResourcesConverter.convert(rte$normalizeLegacyCustomResources(Config.readResource(inputStream).getAsJsonObject()), CustomResourceLoaderMixin::rte$invokeReadResource));
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
                }
            });
        }
        return root;
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
