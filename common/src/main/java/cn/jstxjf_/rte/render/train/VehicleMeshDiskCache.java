package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.BuildStamp;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mixin.JsonReaderMapAccessor;
import cn.jstxjf_.rte.mixin.OptimizedModelAccessor;
import cn.jstxjf_.rte.mixin.VehicleModelJsonAccessor;
import cn.jstxjf_.rte.mixin.VehicleModelSourceAccessor;
import cn.jstxjf_.rte.mixin.VehicleResourceSchemaAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.mtr.core.serializer.JsonReader;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mod.resource.PartCondition;
import org.mtr.mod.resource.VehicleModel;
import org.mtr.mod.resource.VehicleResource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public final class VehicleMeshDiskCache {

    private static final int MAGIC = 0x52544D43;
    private static final int FORMAT_VERSION = 1;
    private static final byte NATIVE_ORDER = (byte) (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? 1 : 2);
    private static final Map<VehicleResource, String> SOURCE_FINGERPRINTS = new WeakHashMap<>();

    private VehicleMeshDiskCache() {
    }

    public static boolean isEnabled() {
        return ClientConfig.vehicleMeshDiskCache;
    }

    public static String structureFingerprint(Map<PartCondition, ? extends List<?>> materialGroupsModel, Map<PartCondition, ? extends List<?>> objModelsModel) {
        final StringBuilder builder = new StringBuilder();
        for (final PartCondition partCondition : PartCondition.values()) {
            final List<?> materialGroups = materialGroupsModel.get(partCondition);
            final List<?> objModels = objModelsModel.get(partCondition);
            builder.append(partCondition.name()).append(':')
                .append(materialGroups == null ? -1 : materialGroups.size()).append(',')
                .append(objModels == null ? -1 : objModels.size()).append(';');
        }
        return builder.toString();
    }

    public static String cacheKey(VehicleBuildContext.Slot slot, String structure) {
        final String source = sourceFingerprint(slot.vehicleResource());
        final StringBuilder builder = new StringBuilder()
            .append(FORMAT_VERSION).append('|')
            .append(BuildStamp.version()).append('|').append(BuildStamp.buildId()).append('|')
            .append(mtrVersion()).append('|')
            .append(OptimizedModelAccessor.rte$getDefaultMapping().strideVertex).append('|')
            .append(configSignature()).append('|')
            .append(slot.vehicleResource().getId()).append('|')
            .append(slot.carNumber()).append('|').append(slot.totalCars()).append('|').append(slot.index()).append('|')
            .append(structure).append('|')
            .append(source);
        return sha1(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String sourceFingerprint(VehicleResource vehicleResource) {
        synchronized (SOURCE_FINGERPRINTS) {
            final String cached = SOURCE_FINGERPRINTS.get(vehicleResource);
            if (cached != null) {
                return cached;
            }
        }
        final MessageDigest digest = newDigest();
        final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        for (final VehicleModel vehicleModel : ((VehicleResourceSchemaAccessor) (Object) vehicleResource).rte$getModels()) {
            final VehicleModelSourceAccessor source = (VehicleModelSourceAccessor) (Object) vehicleModel;
            update(digest, source.rte$getModelResource());
            update(digest, source.rte$getTextureResource());
            update(digest, source.rte$getModelPropertiesResource());
            update(digest, source.rte$getPositionDefinitionsResource());
            update(digest, Boolean.toString(source.rte$getFlipTextureV()));
            hashResource(digest, resourceManager, source.rte$getModelResource());
            hashResource(digest, resourceManager, source.rte$getModelPropertiesResource());
            hashResource(digest, resourceManager, source.rte$getPositionDefinitionsResource());
            final VehicleModelJsonAccessor json = (VehicleModelJsonAccessor) (Object) vehicleModel;
            hashJson(digest, json.rte$getModelPropertiesJsonReader());
            hashJson(digest, json.rte$getPositionDefinitionsJsonReader());
        }
        final String fingerprint = HexFormat.of().formatHex(digest.digest());
        synchronized (SOURCE_FINGERPRINTS) {
            SOURCE_FINGERPRINTS.put(vehicleResource, fingerprint);
        }
        return fingerprint;
    }

    public static void clearFingerprints() {
        synchronized (SOURCE_FINGERPRINTS) {
            SOURCE_FINGERPRINTS.clear();
        }
    }

    private static void hashJson(MessageDigest digest, JsonReader reader) {
        if (reader == null) {
            update(digest, "-");
            return;
        }
        update(digest, String.valueOf(((JsonReaderMapAccessor) (Object) reader).rte$getMap()));
    }

    private static void hashResource(MessageDigest digest, ResourceManager resourceManager, String location) {
        if (location == null || location.isEmpty()) {
            update(digest, "-");
            return;
        }
        final ResourceLocation resourceLocation = ResourceLocation.tryParse(location);
        if (resourceLocation == null) {
            update(digest, "?");
            return;
        }
        final Optional<Resource> resource = resourceManager.getResource(resourceLocation);
        if (resource.isEmpty()) {
            update(digest, "missing");
            return;
        }
        update(digest, resource.get().sourcePackId());
        try (InputStream inputStream = resource.get().open()) {
            final byte[] buffer = new byte[65536];
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        } catch (IOException e) {
            update(digest, "unreadable");
        }
    }

    private static String configSignature() {
        return ClientConfig.fixLegacyPlugDoors + "," + ClientConfig.legacyDoorways + "," + ClientConfig.legacyWalkways + ","
            + ClientConfig.mirrorReversedDoorways + "," + ClientConfig.legacyVariationCarFilter + "," + ClientConfig.acceptLegacyPackFormats + ","
            + ClientConfig.legacyDefaultTextureFallback + "," + ClientConfig.builtinGangway + "," + ClientConfig.forceGangwayOnAllCars + ","
            + ClientConfig.closeCouplingGap + "," + ClientConfig.legacyCouplingGap + "," + ClientConfig.legacyFlippedPositionFix + ","
            + ClientConfig.modelDedupeFaceLimit + "," + ClientConfig.mtrMeshDedupeFaceLimit + "," + ClientConfig.maxTextureSize;
    }

    private static String mtrVersion() {
        try {
            return dev.architectury.platform.Platform.getOptionalMod("mtr")
                .map(mod -> mod.getVersion())
                .orElse("none");
        } catch (Throwable e) {
            return "unknown";
        }
    }

    public static Map<PartCondition, AsyncOptimizedModelBuilder.PreparedCondition> read(String key) {
        final Path file = file(key);
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(Files.newInputStream(file), 1 << 16))) {
            if (input.readInt() != MAGIC || input.readInt() != FORMAT_VERSION || input.readByte() != NATIVE_ORDER) {
                return null;
            }
            final Map<PartCondition, AsyncOptimizedModelBuilder.PreparedCondition> result = new EnumMap<>(PartCondition.class);
            final int conditionCount = input.readInt();
            for (int i = 0; i < conditionCount; i++) {
                final PartCondition partCondition = PartCondition.valueOf(input.readUTF());
                final List<AsyncOptimizedModelBuilder.PreparedMesh> materialMeshes = readMeshes(input);
                final List<AsyncOptimizedModelBuilder.PreparedMesh> objMeshes = readMeshes(input);
                result.put(partCondition, new AsyncOptimizedModelBuilder.PreparedCondition(materialMeshes, objMeshes));
            }
            file.toFile().setLastModified(System.currentTimeMillis());
            return result;
        } catch (Exception e) {
            RTE.LOGGER.warn("[MeshCache] discarding unreadable cache entry {}: {}", file.getFileName(), e.toString());
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) {
            }
            return null;
        }
    }

    private static List<AsyncOptimizedModelBuilder.PreparedMesh> readMeshes(DataInputStream input) throws IOException {
        if (!input.readBoolean()) {
            return null;
        }
        final int count = input.readInt();
        final List<AsyncOptimizedModelBuilder.PreparedMesh> meshes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final OptimizedModel.ShaderType shaderType = OptimizedModel.ShaderType.valueOf(input.readUTF());
            final String texture = input.readUTF();
            final Integer color = input.readBoolean() ? input.readInt() : null;
            final int faceCount = input.readInt();
            final ByteBuffer vertexData = readBuffer(input);
            final ByteBuffer indexData = readBuffer(input);
            final int separator = texture.indexOf(':');
            final Identifier textureId = new Identifier(texture.substring(0, separator), texture.substring(separator + 1));
            meshes.add(new AsyncOptimizedModelBuilder.PreparedMesh(new MaterialProperties(shaderType, textureId, color), vertexData, indexData, faceCount));
        }
        return meshes;
    }

    private static ByteBuffer readBuffer(DataInputStream input) throws IOException {
        final int length = input.readInt();
        final byte[] bytes = new byte[length];
        input.readFully(bytes);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder());
        buffer.put(bytes);
        return buffer;
    }

    public static void write(String key, Map<PartCondition, AsyncOptimizedModelBuilder.PreparedCondition> prepared) {
        final Path file = file(key);
        final Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temporary), 1 << 16))) {
                output.writeInt(MAGIC);
                output.writeInt(FORMAT_VERSION);
                output.writeByte(NATIVE_ORDER);
                output.writeInt(prepared.size());
                for (final Map.Entry<PartCondition, AsyncOptimizedModelBuilder.PreparedCondition> entry : prepared.entrySet()) {
                    output.writeUTF(entry.getKey().name());
                    writeMeshes(output, entry.getValue().materialMeshes());
                    writeMeshes(output, entry.getValue().objMeshes());
                }
            }
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            RTE.LOGGER.warn("[MeshCache] failed writing cache entry {}: {}", file.getFileName(), e.toString());
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException ignored) {
            }
        }
    }

    private static void writeMeshes(DataOutputStream output, List<AsyncOptimizedModelBuilder.PreparedMesh> meshes) throws IOException {
        output.writeBoolean(meshes != null);
        if (meshes == null) {
            return;
        }
        output.writeInt(meshes.size());
        for (final AsyncOptimizedModelBuilder.PreparedMesh mesh : meshes) {
            final MaterialProperties materialProperties = mesh.materialProperties();
            final Identifier texture = materialProperties.getTexture();
            final Integer color = ((cn.jstxjf_.rte.mixin.MtrVertexAttributeStateAccessor) (Object) materialProperties.vertexAttributeState).rte$getColor();
            output.writeUTF(materialProperties.shaderType.name());
            output.writeUTF(texture.getNamespace() + ":" + texture.getPath());
            output.writeBoolean(color != null);
            if (color != null) {
                output.writeInt(color);
            }
            output.writeInt(mesh.faceCount());
            writeBuffer(output, mesh.vertexData());
            writeBuffer(output, mesh.indexData());
        }
    }

    private static void writeBuffer(DataOutputStream output, ByteBuffer buffer) throws IOException {
        final ByteBuffer view = buffer.duplicate();
        view.clear();
        final byte[] bytes = new byte[view.capacity()];
        view.get(bytes);
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    public static void prune() {
        final Path directory = directory();
        if (!Files.isDirectory(directory)) {
            return;
        }
        final long limit = (long) ClientConfig.vehicleMeshCacheMaxMb * 1024 * 1024;
        final List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(directory)) {
            stream.filter(Files::isRegularFile).forEach(files::add);
        } catch (IOException e) {
            return;
        }
        long total = 0;
        for (final Path path : files) {
            if (path.getFileName().toString().endsWith(".tmp")) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
                continue;
            }
            total += path.toFile().length();
        }
        if (total <= limit) {
            return;
        }
        files.removeIf(path -> path.getFileName().toString().endsWith(".tmp"));
        files.sort((a, b) -> Long.compare(a.toFile().lastModified(), b.toFile().lastModified()));
        final long target = limit * 9 / 10;
        int removed = 0;
        for (final Path path : files) {
            if (total <= target) {
                break;
            }
            final long size = path.toFile().length();
            try {
                Files.deleteIfExists(path);
                total -= size;
                removed++;
            } catch (IOException ignored) {
            }
        }
        RTE.LOGGER.info("[MeshCache] pruned {} entries, {} MB left", removed, total / 1048576);
    }

    private static Path directory() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("rte").resolve("mesh-cache");
    }

    private static Path file(String key) {
        return directory().resolve(key + ".bin");
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void update(MessageDigest digest, String value) {
        digest.update(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
        digest.update((byte) 0);
    }

    private static String sha1(byte[] bytes) {
        return HexFormat.of().formatHex(newDigest().digest(bytes));
    }
}
