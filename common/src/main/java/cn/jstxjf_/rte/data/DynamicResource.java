package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unchecked")
public class DynamicResource {

    private static MultiPackResourceManager cachedMPRM = null;
    private static DynamicPack DYNAMIC_PACK = null;
    private static Set<String> ADDED_NAMESPACES = new HashSet<>();

    private static Field resourcesField;
    private static Field packsField;
    private static Field namespacedManagersField;

    static {
        resourcesField = resolveField(ReloadableResourceManager.class, "resources",
            field -> ResourceManager.class.isAssignableFrom(field.getType()));
        packsField = resolveField(MultiPackResourceManager.class, "packs",
            field -> field.getType() == List.class);
        namespacedManagersField = resolveField(MultiPackResourceManager.class, "namespacedManagers",
            field -> field.getType() == Map.class);
        if (resourcesField == null || packsField == null || namespacedManagersField == null) {
            RTE.LOGGER.error("Failed to init DynamicResource reflections: resources={} packs={} namespacedManagers={}",
                resourcesField != null, packsField != null, namespacedManagersField != null);
        }
    }

    private static Field resolveField(Class<?> owner, String mojangName, java.util.function.Predicate<Field> matcher) {
        try {
            final Field named = owner.getDeclaredField(mojangName);
            named.setAccessible(true);
            return named;
        } catch (NoSuchFieldException ignored) {
        }
        Field found = null;
        for (Field field : owner.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || !matcher.test(field)) {
                continue;
            }
            if (found != null) {
                return null;
            }
            found = field;
        }
        if (found != null) {
            found.setAccessible(true);
        }
        return found;
    }

    public static void addResourcesClient(ResourceLocation loc, IoSupplier<InputStream> funGetStream) {
        try {
            ReloadableResourceManager rrm = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            MultiPackResourceManager mprm = (MultiPackResourceManager) resourcesField.get(rrm);

            if (cachedMPRM == null || cachedMPRM != mprm) {
                cachedMPRM = mprm;
                List<PackResources> packs = (List<PackResources>) packsField.get(mprm);
                if (DYNAMIC_PACK != null && packs.contains(DYNAMIC_PACK)) {
                    packs.remove(DYNAMIC_PACK);
                }
                DYNAMIC_PACK = new DynamicPack("RTE Virtual Dynamic Pack",
                    "{\"pack\":{\"pack_format\":" + RtePackFormat.VALUE + ",\"description\":\"RTE Virtual Dynamic Pack\"}}");

                packs = new ArrayList<>(packs);
                packs.add(DYNAMIC_PACK);
                packsField.set(mprm, packs);
                ADDED_NAMESPACES = new HashSet<>();
            }

            DYNAMIC_PACK.addResource(PackType.CLIENT_RESOURCES, loc, funGetStream);

            Set<String> current = new HashSet<>(DYNAMIC_PACK.getNamespaces(PackType.CLIENT_RESOURCES));
            current.removeAll(ADDED_NAMESPACES);
            Map<String, FallbackResourceManager> nsMap = (Map<String, FallbackResourceManager>) namespacedManagersField.get(mprm);
            for (String ns : current) {
                if (!ns.isEmpty()) {
                    nsMap.computeIfAbsent(ns, k -> new FallbackResourceManager(PackType.CLIENT_RESOURCES, k))
                        .push(DYNAMIC_PACK);
                }
            }
            ADDED_NAMESPACES = new HashSet<>(DYNAMIC_PACK.getNamespaces(PackType.CLIENT_RESOURCES));
        } catch (Exception e) {
            RTE.LOGGER.error("Failed to add dynamic resource: " + loc, e);
        }
    }

    private static class DynamicPack implements PackResources {
        private final String name, packMcmeta;
        private final Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> resources = new HashMap<>();
        private final Map<PackType, Set<String>> namespaces = new HashMap<>();

        public DynamicPack(String name, String packMcmeta) {
            this.name = name;
            this.packMcmeta = packMcmeta;
        }

        public void addResource(PackType packType, ResourceLocation loc, IoSupplier<InputStream> funGetStream) {
            resources.computeIfAbsent(packType, k -> new HashMap<>()).put(loc, funGetStream);
            namespaces.computeIfAbsent(packType, k -> new HashSet<>()).add(loc.getNamespace());
        }

        @Override
        public IoSupplier<InputStream> getRootResource(String... fileName) {
            return null;
        }

        @Override
        public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation loc) {
            Map<ResourceLocation, IoSupplier<InputStream>> map = resources.get(packType);
            if (map == null) return null;
            return map.get(loc);
        }

        @Override
        public String packId() {
            return name;
        }

        @Override
        public void listResources(PackType type, String namespace, String path, PackResources.ResourceOutput output) {
            Map<ResourceLocation, IoSupplier<InputStream>> map = resources.get(type);
            if (map == null) return;
            for (Map.Entry<ResourceLocation, IoSupplier<InputStream>> entry : map.entrySet()) {
                ResourceLocation rl = entry.getKey();
                if (!rl.getNamespace().equals(namespace)) continue;
                if (!rl.getPath().startsWith(path)) continue;
                output.accept(rl, entry.getValue());
            }
        }

        @Override
        public Set<String> getNamespaces(PackType packType) {
            return namespaces.getOrDefault(packType, new HashSet<>());
        }

        @Override
        public void close() {
        }

        @Override
        public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
            if (packMcmeta == null) return null;
            return AbstractPackResources.getMetadataFromStream(deserializer, new ByteArrayInputStream(packMcmeta.getBytes()));
        }
    }
}
