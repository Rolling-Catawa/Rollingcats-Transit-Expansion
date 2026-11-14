package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
//#if MC>=11903
import net.minecraft.server.packs.resources.IoSupplier;
//#endif

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public final class RawPackResources implements PackResources {

    private static final int PACK_FORMAT = RtePackFormat.VALUE;

    //#if MC>=11903
    @Override
    public IoSupplier<InputStream> getRootResource(String... segments) {
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        final RawPackAliases.Alias alias = selectedAlias(packType, location);
        return alias == null ? null : () -> RawPackAliases.open(alias);
    }

    @Override
    public void listResources(PackType packType, String namespace, String prefix, ResourceOutput output) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return;
        }
        final Set<String> selectedPackFileNames = RawPackAliases.selectedPackFileNames();
        for (final Map.Entry<ResourceLocation, RawPackAliases.Alias> entry : RawPackAliases.all().entrySet()) {
            final ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals(namespace) || !location.getPath().startsWith(prefix)) {
                continue;
            }
            final RawPackAliases.Alias alias = entry.getValue();
            if (!RawPackAliases.isFromSelectedPack(alias, selectedPackFileNames)) {
                continue;
            }
            output.accept(location, () -> RawPackAliases.open(alias));
        }
    }
    //#else
    @Override
    public InputStream getRootResource(String fileName) throws java.io.IOException {
        throw new java.io.FileNotFoundException(fileName);
    }

    @Override
    public InputStream getResource(PackType packType, ResourceLocation location) throws java.io.IOException {
        final RawPackAliases.Alias alias = selectedAlias(packType, location);
        if (alias == null) {
            throw new java.io.FileNotFoundException(location.toString());
        }
        return RawPackAliases.open(alias);
    }

    @Override
    public boolean hasResource(PackType packType, ResourceLocation location) {
        return selectedAlias(packType, location) != null;
    }

    @Override
    public java.util.Collection<ResourceLocation> getResources(PackType packType, String namespace, String prefix, java.util.function.Predicate<ResourceLocation> filter) {
        final java.util.List<ResourceLocation> found = new java.util.ArrayList<>();
        if (packType != PackType.CLIENT_RESOURCES) {
            return found;
        }
        final Set<String> selectedPackFileNames = RawPackAliases.selectedPackFileNames();
        for (final Map.Entry<ResourceLocation, RawPackAliases.Alias> entry : RawPackAliases.all().entrySet()) {
            final ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals(namespace) || !location.getPath().startsWith(prefix)) {
                continue;
            }
            if (RawPackAliases.isFromSelectedPack(entry.getValue(), selectedPackFileNames) && filter.test(location)) {
                found.add(location);
            }
        }
        return found;
    }
    //#endif

    private static RawPackAliases.Alias selectedAlias(PackType packType, ResourceLocation location) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return null;
        }
        final RawPackAliases.Alias alias = RawPackAliases.all().get(location);
        if (alias == null || !RawPackAliases.isFromSelectedPack(alias, RawPackAliases.selectedPackFileNames())) {
            return null;
        }
        return alias;
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? RawPackAliases.namespaces() : Set.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        //#if MC>=11903
        if (PackMetadataSection.TYPE.getMetadataSectionName().equals(serializer.getMetadataSectionName())) {
        //#else
        if (PackMetadataSection.SERIALIZER.getMetadataSectionName().equals(serializer.getMetadataSectionName())) {
        //#endif
            //#if MC>=12002
            return (T) new PackMetadataSection(Component.literal("RTE path recovery"), PACK_FORMAT, java.util.Optional.empty());
            //#else
            return (T) new PackMetadataSection(Component.literal("RTE path recovery"), PACK_FORMAT);
            //#endif
        }
        return null;
    }

    //#if MC>=12101
    @Override
    public net.minecraft.server.packs.PackLocationInfo location() {
        return new net.minecraft.server.packs.PackLocationInfo(RawPackAliases.PACK_ID, Component.literal("RTE Path Recovery"), PackSource.BUILT_IN, java.util.Optional.empty());
    }
    //#endif

    //#if MC>=11903
    @Override
    public String packId() {
        return RawPackAliases.PACK_ID;
    }

    //#if MC<12101
    @Override
    public boolean isBuiltin() {
        return true;
    }
    //#endif
    //#else
    @Override
    public String getName() {
        return RawPackAliases.PACK_ID;
    }
    //#endif

    @Override
    public void close() {
    }

    public static Pack createPack() {
        try {
            //#if MC>=12101
            final net.minecraft.server.packs.PackLocationInfo location = new net.minecraft.server.packs.PackLocationInfo(RawPackAliases.PACK_ID, Component.literal("RTE Path Recovery"), PackSource.BUILT_IN, java.util.Optional.empty());
            return Pack.readMetaAndCreate(
                location,
                new Pack.ResourcesSupplier() {
                    @Override
                    public net.minecraft.server.packs.PackResources openPrimary(net.minecraft.server.packs.PackLocationInfo info) {
                        return new RawPackResources();
                    }

                    @Override
                    public net.minecraft.server.packs.PackResources openFull(net.minecraft.server.packs.PackLocationInfo info, Pack.Metadata metadata) {
                        return new RawPackResources();
                    }
                },
                PackType.CLIENT_RESOURCES,
                new net.minecraft.server.packs.PackSelectionConfig(true, Pack.Position.BOTTOM, false));
            //#elseif MC>=11903
            return Pack.readMetaAndCreate(
                RawPackAliases.PACK_ID,
                Component.literal("RTE Path Recovery"),
                true,
                //#if MC>=12002
                new Pack.ResourcesSupplier() {
                    @Override
                    public net.minecraft.server.packs.PackResources openPrimary(String id) {
                        return new RawPackResources();
                    }

                    @Override
                    public net.minecraft.server.packs.PackResources openFull(String id, Pack.Info info) {
                        return new RawPackResources();
                    }
                },
                //#else
                ignored -> new RawPackResources(),
                //#endif
                PackType.CLIENT_RESOURCES,
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN);
            //#else
            return Pack.create(
                RawPackAliases.PACK_ID,
                true,
                RawPackResources::new,
                (id, title, required, supplier, metadata, position, source) -> new Pack(id, Component.literal("RTE Path Recovery"), required, supplier, metadata, PackType.CLIENT_RESOURCES, position, source),
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN);
            //#endif
        } catch (Exception e) {
            RTE.LOGGER.warn("[PathRecovery] could not create recovery pack: {}", e.toString());
            return null;
        }
    }
}
