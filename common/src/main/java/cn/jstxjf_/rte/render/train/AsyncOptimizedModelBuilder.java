package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mixin.MaterialGroupAccessor;
import cn.jstxjf_.rte.mixin.MaterialGroupWrapperAccessor;
import cn.jstxjf_.rte.mixin.ObjModelAccessor;
import cn.jstxjf_.rte.mixin.OptimizedModelAccessor;
import cn.jstxjf_.rte.mixin.OptimizedModelWrapperInvoker;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.Face;
import org.mtr.mapping.render.model.Mesh;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.object.IndexBuffer;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.object.VertexBuffer;
import org.mtr.mapping.render.vertex.CapturingVertexConsumer;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeSource;
import org.mtr.mapping.render.vertex.VertexAttributeType;
import org.mtr.mod.client.CustomResourceLoader;
import org.mtr.mod.resource.OptimizedModelWrapper;
import org.mtr.mod.resource.PartCondition;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AsyncOptimizedModelBuilder {

    private static final int INDEX_TYPE_UNSIGNED_INT = 5125;
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
    private static final int BUILDER_THREADS = Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2));
    private static final int MAX_IN_FLIGHT = BUILDER_THREADS * 2;
    private static final AtomicInteger IN_FLIGHT = new AtomicInteger();
    private static final Map<Object, OwnedUpload> OWNED_UPLOADS = new java.util.IdentityHashMap<>();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
        BUILDER_THREADS,
        runnable -> {
            final Thread thread = new Thread(runnable, "RTE Vehicle Model Builder-" + THREAD_COUNTER.incrementAndGet());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

    private AsyncOptimizedModelBuilder() {
    }

    public static Supplier<Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper>> supplier(
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper>> materialGroupsModel,
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper>> objModelsModel) {
        final VehicleBuildContext.Slot slot = VehicleMeshDiskCache.isEnabled() ? VehicleBuildContext.claimSlot() : null;
        final String structure = slot == null ? null : VehicleMeshDiskCache.structureFingerprint(materialGroupsModel, objModelsModel);
        final AtomicReference<CompletableFuture<Map<PartCondition, PreparedCondition>>> job = new AtomicReference<>();
        final AtomicReference<CompletableFuture<CacheLookup>> lookup = new AtomicReference<>();
        final boolean[] lookupFinished = {slot == null};
        final String[] cacheKey = {null};
        return () -> {
            if (!lookupFinished[0]) {
                final CompletableFuture<CacheLookup> pendingLookup = lookup.get();
                if (pendingLookup == null) {
                    if (IN_FLIGHT.get() >= MAX_IN_FLIGHT) {
                        return null;
                    }
                    IN_FLIGHT.incrementAndGet();
                    lookup.set(CompletableFuture.supplyAsync(() -> lookupCache(slot, structure), EXECUTOR)
                        .whenComplete((result, error) -> IN_FLIGHT.decrementAndGet()));
                    AsyncLoadGate.markPending();
                    return null;
                }
                if (!pendingLookup.isDone()) {
                    AsyncLoadGate.markPending();
                    return null;
                }
                lookup.set(null);
                lookupFinished[0] = true;
                final CacheLookup result = pendingLookup.getNow(null);
                cacheKey[0] = result == null ? null : result.key;
                if (result != null && result.prepared != null) {
                    lookupFinished[0] = slot == null;
                    return upload(result.prepared);
                }
            }
            final CompletableFuture<Map<PartCondition, PreparedCondition>> future = job.get();
            if (future == null) {
                if (IN_FLIGHT.get() >= MAX_IN_FLIGHT) {
                    return null;
                }
                final Map<PartCondition, CapturingVertexConsumer> captured = captureMaterialGroups(materialGroupsModel);
                final String keyToWrite = cacheKey[0];
                IN_FLIGHT.incrementAndGet();
                final CompletableFuture<Map<PartCondition, PreparedCondition>> started = CompletableFuture.supplyAsync(() -> prepare(captured, objModelsModel), EXECUTOR)
                    .whenComplete((result, error) -> IN_FLIGHT.decrementAndGet());
                if (keyToWrite != null) {
                    started.thenAcceptAsync(result -> VehicleMeshDiskCache.write(keyToWrite, result), EXECUTOR);
                }
                job.set(started);
                AsyncLoadGate.markPending();
                return null;
            }
            if (!future.isDone()) {
                AsyncLoadGate.markPending();
                return null;
            }
            job.set(null);
            lookupFinished[0] = slot == null;
            final Map<PartCondition, PreparedCondition> prepared;
            try {
                prepared = future.join();
            } catch (Throwable e) {
                RTE.LOGGER.warn("[AsyncVehicleModel] background build failed, building on render thread", e);
                return buildSynchronously(materialGroupsModel, objModelsModel);
            }
            return upload(prepared);
        };
    }

    private static CacheLookup lookupCache(VehicleBuildContext.Slot slot, String structure) {
        try {
            final String key = VehicleMeshDiskCache.cacheKey(slot, structure);
            return new CacheLookup(key, VehicleMeshDiskCache.read(key));
        } catch (Throwable e) {
            RTE.LOGGER.warn("[MeshCache] lookup failed for {}: {}", slot.vehicleResource().getId(), e.toString());
            return null;
        }
    }

    public static void releaseIfOwned(Object data) {
        final List<Mesh> meshes;
        final List<VertexArray> vertexArrays;
        synchronized (OWNED_UPLOADS) {
            final OwnedUpload owned = OWNED_UPLOADS.remove(data);
            if (owned == null) {
                return;
            }
            meshes = owned.meshes;
            vertexArrays = owned.vertexArrays;
        }
        final Runnable release = () -> {
            for (final VertexArray vertexArray : vertexArrays) {
                vertexArray.close();
            }
            for (final Mesh mesh : meshes) {
                mesh.vertexBuffer.close();
                mesh.indexBuffer.close();
            }
        };
        if (com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            release.run();
        } else {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(release::run);
        }
    }

    private static Map<PartCondition, CapturingVertexConsumer> captureMaterialGroups(
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper>> materialGroupsModel) {
        final Map<PartCondition, CapturingVertexConsumer> captured = new EnumMap<>(PartCondition.class);
        for (final PartCondition partCondition : PartCondition.values()) {
            final ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper> materialGroups = materialGroupsModel.get(partCondition);
            if (materialGroups != null) {
                captured.put(partCondition, captureMaterialGroup(materialGroups));
            }
        }
        return captured;
    }

    private static Map<PartCondition, PreparedCondition> prepare(
            Map<PartCondition, CapturingVertexConsumer> capturedMaterialGroups,
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper>> objModelsModel) {
        final VertexAttributeMapping mapping = OptimizedModelAccessor.rte$getDefaultMapping();
        final Map<PartCondition, PreparedCondition> result = new EnumMap<>(PartCondition.class);
        for (final PartCondition partCondition : PartCondition.values()) {
            final CapturingVertexConsumer captured = capturedMaterialGroups.get(partCondition);
            final ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper> objModels = objModelsModel.get(partCondition);
            final List<PreparedMesh> materialMeshes = captured == null ? null : prepareCapturedMaterialGroup(captured, mapping);
            final List<PreparedMesh> objMeshes = objModels == null ? null : prepareObjModels(objModels, mapping);
            result.put(partCondition, new PreparedCondition(materialMeshes, objMeshes));
        }
        return result;
    }

    private static CapturingVertexConsumer captureMaterialGroup(List<OptimizedModelWrapper.MaterialGroupWrapper> materialGroups) {
        final CapturingVertexConsumer capturingVertexConsumer = new CapturingVertexConsumer();
        for (final OptimizedModelWrapper.MaterialGroupWrapper wrapper : materialGroups) {
            final OptimizedModel.MaterialGroup materialGroup = ((MaterialGroupWrapperAccessor) (Object) wrapper).rte$getMaterialGroup();
            if (materialGroup == null) {
                continue;
            }
            final MaterialGroupAccessor accessor = (MaterialGroupAccessor) (Object) materialGroup;
            capturingVertexConsumer.beginStage(accessor.rte$getMaterialProperties());
            for (final Consumer<CapturingVertexConsumer> modelPartConsumer : accessor.rte$getModelPartConsumers()) {
                modelPartConsumer.accept(capturingVertexConsumer);
            }
        }
        return capturingVertexConsumer;
    }

    private static List<PreparedMesh> prepareCapturedMaterialGroup(CapturingVertexConsumer capturingVertexConsumer, VertexAttributeMapping mapping) {
        capturingVertexConsumer.rawModel.triangulate();
        final RawModel rawModel = new RawModel();
        capturingVertexConsumer.rawModel.iterateRawMeshList(rawModel::append);
        rawModel.distinct();
        return prepareRawModel(rawModel, mapping);
    }

    private static List<PreparedMesh> prepareObjModels(List<OptimizedModelWrapper.ObjModelWrapper> objModels, VertexAttributeMapping mapping) {
        final List<PreparedMesh> meshes = new ArrayList<>();
        for (final OptimizedModelWrapper.ObjModelWrapper wrapper : objModels) {
            if (wrapper == null || wrapper.objModel == null) {
                continue;
            }
            final RawModel rawModel = ((ObjModelAccessor) (Object) wrapper.objModel).rte$getRawModel();
            synchronized (rawModel) {
                rawModel.generateNormals();
                rawModel.distinct();
                meshes.addAll(prepareRawModel(rawModel, mapping));
            }
        }
        return meshes;
    }

    private static List<PreparedMesh> prepareRawModel(RawModel rawModel, VertexAttributeMapping mapping) {
        final List<PreparedMesh> meshes = new ArrayList<>();
        rawModel.iterateRawMeshList(rawMesh -> {
            if (rawMesh.hasFaces()) {
                meshes.add(prepareMesh(rawMesh, mapping));
            }
        });
        return meshes;
    }

    private static PreparedMesh prepareMesh(RawMesh rawMesh, VertexAttributeMapping mapping) {
        rawMesh.distinct();
        final int vertexCount = rawMesh.vertices.size();
        for (final Face face : rawMesh.faces) {
            for (final int vertexIndex : face.vertices) {
                if (vertexIndex < 0 || vertexIndex >= vertexCount) {
                    throw new IndexOutOfBoundsException("RawMesh contains invalid vertex index " + vertexIndex + " (should be 0 to " + (vertexCount - 1) + ")");
                }
            }
        }
        final boolean writePosition = writes(mapping, VertexAttributeType.POSITION);
        final boolean writeColor = writes(mapping, VertexAttributeType.COLOR);
        final boolean writeUv = writes(mapping, VertexAttributeType.UV_TEXTURE);
        final boolean writeLight = writes(mapping, VertexAttributeType.UV_LIGHTMAP);
        final boolean writeNormal = writes(mapping, VertexAttributeType.NORMAL);
        final ByteBuffer vertexData = ByteBuffer.allocateDirect(vertexCount * mapping.strideVertex).order(ByteOrder.nativeOrder());
        for (final Vertex vertex : rawMesh.vertices) {
            if (writePosition) {
                final Vector3f position = vertex.position;
                vertexData.putFloat(position.getX()).putFloat(position.getY()).putFloat(position.getZ());
            }
            if (writeColor) {
                vertexData.putInt(vertex.color);
            }
            if (writeUv) {
                vertexData.putFloat(vertex.u).putFloat(vertex.v);
            }
            if (writeLight) {
                vertexData.putInt(vertex.light);
            }
            if (writeNormal) {
                final float x = vertex.normal.getX();
                final float y = vertex.normal.getY();
                final float z = vertex.normal.getZ();
                final float inverseLength = (float) (1 / Math.sqrt(x * x + y * y + z * z));
                vertexData.put((byte) (int) (x * inverseLength * 127)).put((byte) (int) (y * inverseLength * 127)).put((byte) (int) (z * inverseLength * 127));
            }
            if (mapping.paddingVertex > 0) {
                vertexData.put((byte) 0);
            }
        }
        final ByteBuffer indexData = ByteBuffer.allocateDirect(rawMesh.faces.size() * 3 * 4).order(ByteOrder.nativeOrder());
        for (final Face face : rawMesh.faces) {
            for (final int vertexIndex : face.vertices) {
                indexData.putInt(vertexIndex);
            }
        }
        return new PreparedMesh(rawMesh.materialProperties, vertexData, indexData, rawMesh.faces.size());
    }

    private static boolean writes(VertexAttributeMapping mapping, VertexAttributeType type) {
        return mapping.sources.get(type) == VertexAttributeSource.VERTEX_BUFFER;
    }

    private static Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper> upload(Map<PartCondition, PreparedCondition> prepared) {
        final VertexAttributeMapping mapping = OptimizedModelAccessor.rte$getDefaultMapping();
        final Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper> optimizedModels = new Object2ObjectOpenHashMap<>();
        final OwnedUpload owned = new OwnedUpload(new ArrayList<>(), new ArrayList<>());
        CustomResourceLoader.OPTIMIZED_RENDERER_WRAPPER.beginReload();
        try {
            for (final PartCondition partCondition : PartCondition.values()) {
                final PreparedCondition preparedCondition = prepared.get(partCondition);
                final OptimizedModelWrapper materialModel = preparedCondition == null ? null : wrap(preparedCondition.materialMeshes, mapping, owned);
                final OptimizedModelWrapper objModel = preparedCondition == null ? null : wrap(preparedCondition.objMeshes, mapping, owned);
                optimizedModels.put(partCondition, new OptimizedModelWrapper(materialModel, objModel));
            }
        } finally {
            CustomResourceLoader.OPTIMIZED_RENDERER_WRAPPER.finishReload();
        }
        synchronized (OWNED_UPLOADS) {
            OWNED_UPLOADS.put(optimizedModels, owned);
        }
        return optimizedModels;
    }

    private static OptimizedModelWrapper wrap(List<PreparedMesh> meshes, VertexAttributeMapping mapping, OwnedUpload owned) {
        if (meshes == null) {
            return null;
        }
        final List<VertexArray> vertexArrays = new ArrayList<>(meshes.size());
        for (final PreparedMesh preparedMesh : meshes) {
            final Mesh mesh = new Mesh(new VertexBuffer(), new IndexBuffer(preparedMesh.faceCount, INDEX_TYPE_UNSIGNED_INT), preparedMesh.materialProperties);
            mesh.vertexBuffer.upload(preparedMesh.vertexData, VertexBuffer.USAGE_STATIC_DRAW);
            mesh.indexBuffer.upload(preparedMesh.indexData, VertexBuffer.USAGE_STATIC_DRAW);
            mesh.indexBuffer.setFaceCount(preparedMesh.faceCount);
            final VertexArray vertexArray = new VertexArray(mesh, mapping);
            vertexArrays.add(vertexArray);
            owned.meshes.add(mesh);
            owned.vertexArrays.add(vertexArray);
        }
        return OptimizedModelWrapperInvoker.rte$create(new OptimizedModel(vertexArrays));
    }

    private static Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper> buildSynchronously(
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper>> materialGroupsModel,
            Object2ObjectOpenHashMap<PartCondition, ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper>> objModelsModel) {
        final Object2ObjectOpenHashMap<PartCondition, OptimizedModelWrapper> optimizedModels = new Object2ObjectOpenHashMap<>();
        CustomResourceLoader.OPTIMIZED_RENDERER_WRAPPER.beginReload();
        try {
            for (final PartCondition partCondition : PartCondition.values()) {
                final ObjectArrayList<OptimizedModelWrapper.MaterialGroupWrapper> materialGroups = materialGroupsModel.get(partCondition);
                final ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper> objModels = objModelsModel.get(partCondition);
                optimizedModels.put(partCondition, new OptimizedModelWrapper(
                    materialGroups == null ? null : OptimizedModelWrapper.fromMaterialGroups(materialGroups),
                    objModels == null ? null : OptimizedModelWrapper.fromObjModels(objModels)));
            }
        } finally {
            CustomResourceLoader.OPTIMIZED_RENDERER_WRAPPER.finishReload();
        }
        return optimizedModels;
    }

    record PreparedMesh(MaterialProperties materialProperties, ByteBuffer vertexData, ByteBuffer indexData, int faceCount) {
    }

    record PreparedCondition(List<PreparedMesh> materialMeshes, List<PreparedMesh> objMeshes) {
    }

    private record CacheLookup(String key, Map<PartCondition, PreparedCondition> prepared) {
    }

    private record OwnedUpload(List<Mesh> meshes, List<VertexArray> vertexArrays) {
    }
}
