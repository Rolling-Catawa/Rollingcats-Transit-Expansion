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
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public final class RawPackResources implements PackResources {

    private static final int PACK_FORMAT = RtePackFormat.VALUE;

    @Override
    public IoSupplier<InputStream> getRootResource(String... segments) {
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return null;
        }
        final RawPackAliases.Alias alias = RawPackAliases.all().get(location);
        return alias == null ? null : () -> RawPackAliases.open(alias);
    }

    @Override
    public void listResources(PackType packType, String namespace, String prefix, ResourceOutput output) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return;
        }
        for (final Map.Entry<ResourceLocation, RawPackAliases.Alias> entry : RawPackAliases.all().entrySet()) {
            final ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals(namespace) || !location.getPath().startsWith(prefix)) {
                continue;
            }
            final RawPackAliases.Alias alias = entry.getValue();
            output.accept(location, () -> RawPackAliases.open(alias));
        }
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? RawPackAliases.namespaces() : Set.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        if (PackMetadataSection.TYPE.getMetadataSectionName().equals(serializer.getMetadataSectionName())) {
            //#if MC>=12002
            return (T) new PackMetadataSection(Component.literal("RTE path recovery"), PACK_FORMAT, java.util.Optional.empty());
            //#else
            return (T) new PackMetadataSection(Component.literal("RTE path recovery"), PACK_FORMAT);
            //#endif
        }
        return null;
    }

    @Override
    public String packId() {
        return RawPackAliases.PACK_ID;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {
    }

    public static Pack createPack() {
        try {
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
        } catch (Exception e) {
            RTE.LOGGER.warn("[PathRecovery] could not create recovery pack: {}", e.toString());
            return null;
        }
    }
}
