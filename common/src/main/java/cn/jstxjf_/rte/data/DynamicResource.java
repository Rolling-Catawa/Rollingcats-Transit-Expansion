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
//#if MC>=11903
import net.minecraft.server.packs.resources.IoSupplier;
//#endif
import cn.jstxjf_.rte.compat.StreamSupplier;
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

    public static void addResourcesClient(ResourceLocation loc, StreamSupplier funGetStream) {
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
        private final Map<PackType, Map<ResourceLocation, StreamSupplier>> resources = new HashMap<>();
        private final Map<PackType, Set<String>> namespaces = new HashMap<>();

        public DynamicPack(String name, String packMcmeta) {
            this.name = name;
            this.packMcmeta = packMcmeta;
        }

        public void addResource(PackType packType, ResourceLocation loc, StreamSupplier funGetStream) {
            resources.computeIfAbsent(packType, k -> new HashMap<>()).put(loc, funGetStream);
            namespaces.computeIfAbsent(packType, k -> new HashSet<>()).add(loc.getNamespace());
        }

        //#if MC>=11903
        @Override
        public IoSupplier<InputStream> getRootResource(String... fileName) {
            return null;
        }

        @Override
        public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation loc) {
            final StreamSupplier supplier = lookup(packType, loc);
            return supplier == null ? null : supplier::get;
        }

        @Override
        public String packId() {
            return name;
        }

        //#if MC>=12101
        @Override
        public net.minecraft.server.packs.PackLocationInfo location() {
            return new net.minecraft.server.packs.PackLocationInfo(name, net.minecraft.network.chat.Component.literal(name), net.minecraft.server.packs.repository.PackSource.BUILT_IN, java.util.Optional.empty());
        }
        //#endif

        @Override
        public void listResources(PackType type, String namespace, String path, PackResources.ResourceOutput output) {
            Map<ResourceLocation, StreamSupplier> map = resources.get(type);
            if (map == null) return;
            for (Map.Entry<ResourceLocation, StreamSupplier> entry : map.entrySet()) {
                ResourceLocation rl = entry.getKey();
                if (!rl.getNamespace().equals(namespace)) continue;
                if (!rl.getPath().startsWith(path)) continue;
                final StreamSupplier supplier = entry.getValue();
                output.accept(rl, supplier::get);
            }
        }
        //#else
        @Override
        public InputStream getRootResource(String fileName) throws IOException {
            throw new java.io.FileNotFoundException(fileName);
        }

        @Override
        public InputStream getResource(PackType packType, ResourceLocation loc) throws IOException {
            final StreamSupplier supplier = lookup(packType, loc);
            if (supplier == null) {
                throw new java.io.FileNotFoundException(loc.toString());
            }
            return supplier.get();
        }

        @Override
        public boolean hasResource(PackType packType, ResourceLocation loc) {
            return lookup(packType, loc) != null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, java.util.function.Predicate<ResourceLocation> filter) {
            final List<ResourceLocation> found = new ArrayList<>();
            Map<ResourceLocation, StreamSupplier> map = resources.get(type);
            if (map == null) return found;
            for (ResourceLocation rl : map.keySet()) {
                if (!rl.getNamespace().equals(namespace)) continue;
                if (!rl.getPath().startsWith(path)) continue;
                if (filter.test(rl)) found.add(rl);
            }
            return found;
        }
        //#endif

        private StreamSupplier lookup(PackType packType, ResourceLocation loc) {
            Map<ResourceLocation, StreamSupplier> map = resources.get(packType);
            return map == null ? null : map.get(loc);
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
