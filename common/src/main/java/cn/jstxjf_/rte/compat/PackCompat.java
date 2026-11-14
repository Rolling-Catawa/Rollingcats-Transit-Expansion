package cn.jstxjf_.rte.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.io.InputStream;

public final class PackCompat {

    private PackCompat() {
    }

    public static String packId(PackResources pack) {
        //#if MC>=11903
        return pack.packId();
        //#else
        return pack.getName();
        //#endif
    }

    public static InputStream openRoot(PackResources pack, String... segments) throws IOException {
        //#if MC>=11903
        final net.minecraft.server.packs.resources.IoSupplier<InputStream> supplier = pack.getRootResource(segments);
        return supplier == null ? null : supplier.get();
        //#else
        try {
            return pack.getRootResource(String.join("/", segments));
        } catch (java.io.FileNotFoundException e) {
            return null;
        }
        //#endif
    }

    public static InputStream open(PackResources pack, PackType type, ResourceLocation location) throws IOException {
        //#if MC>=11903
        final net.minecraft.server.packs.resources.IoSupplier<InputStream> supplier = pack.getResource(type, location);
        return supplier == null ? null : supplier.get();
        //#else
        if (!pack.hasResource(type, location)) {
            return null;
        }
        return pack.getResource(type, location);
        //#endif
    }
}
