package cn.jstxjf_.rte.mixin;

import org.mtr.core.serializer.ReaderBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.generated.resource.ModelPropertiesPartSchema;
import org.mtr.mod.resource.ModelPropertiesPart;
import org.mtr.mod.resource.OptimizedModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(value = ModelPropertiesPart.class, remap = false)
public abstract class ObjDoorModelFixMixin extends ModelPropertiesPartSchema {

    @Unique private static final ThreadLocal<Double> RTE_MODEL_Y_OFFSET = new ThreadLocal<>();
    @Unique private static Field rte$detailX;
    @Unique private static Field rte$detailZ;
    @Unique private static Field rte$detailFlipped;
    @Unique private static boolean rte$detailFieldsFailed;

    @Unique private Object rte$currentDetail;
    @Unique private boolean rte$objPart;

    protected ObjDoorModelFixMixin(ReaderBase readerBase) {
        super(readerBase);
    }

    @Shadow
    private boolean isDoor() {
        throw new AssertionError();
    }

    @Inject(
        method = "writeCache(Ljava/util/Map;Lorg/mtr/mod/resource/PositionDefinitions;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;D)V",
        at = @At("HEAD"), require = 0
    )
    private void rte$rememberModelYOffset(java.util.Map<?, ?> objModels, org.mtr.mod.resource.PositionDefinitions positionDefinitions,
                                          org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<?, ?> openModels,
                                          org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<?, ?> closedModels,
                                          double modelYOffset, CallbackInfo ci) {
        RTE_MODEL_Y_OFFSET.set(modelYOffset);
        rte$objPart = true;
    }

    @Inject(
        method = "writeCache(Ljava/util/Map;Lorg/mtr/mod/resource/PositionDefinitions;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;D)V",
        at = @At("RETURN"), require = 0
    )
    private void rte$forgetModelYOffset(java.util.Map<?, ?> objModels, org.mtr.mod.resource.PositionDefinitions positionDefinitions,
                                        org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<?, ?> openModels,
                                        org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<?, ?> closedModels,
                                        double modelYOffset, CallbackInfo ci) {
        RTE_MODEL_Y_OFFSET.remove();
    }

    @Inject(method = "lambda$writeCache$11", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$buildUntransformedDoorModel(ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper> objModelWrappers,
                                                 CallbackInfoReturnable<OptimizedModelWrapper> cir) {
        if (!isDoor() || objModelWrappers == null) {
            return;
        }
        try {
            final Double yOffset = RTE_MODEL_Y_OFFSET.get();
            final ObjectArrayList<OptimizedModelWrapper.ObjModelWrapper> pristine = new ObjectArrayList<>();
            for (final OptimizedModelWrapper.ObjModelWrapper wrapper : objModelWrappers) {
                final OptimizedModel.ObjModel source = wrapper.objModel;
                final OptimizedModel.ObjModel copy = ObjModelAccessor.rte$create(
                    ((ObjModelAccessor) (Object) source).rte$getRawMeshes(), false,
                    source.getMinX(), source.getMinY(), source.getMinZ(),
                    source.getMaxX(), source.getMaxY(), source.getMaxZ());
                copy.addTransformation(renderStage.shaderType, 0, yOffset == null ? 0 : -yOffset, 0, false);
                pristine.add(new OptimizedModelWrapper.ObjModelWrapper(copy));
            }
            cir.setReturnValue(OptimizedModelWrapper.fromObjModels(pristine));
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[ObjDoorModel] failed to build untransformed door model", e);
        }
    }

    @Inject(method = "lambda$renderNormal$25", at = @At("HEAD"), require = 0)
    private void rte$captureDetail(boolean flashOn, VehicleExtension vehicle, ObjectArrayList<?> openDoorways,
                                   GraphicsHolder graphicsHolder, int light, ObjectIntImmutablePair<?> renderProperties,
                                   @Coerce Object partDetails, CallbackInfo ci) {
        rte$currentDetail = partDetails;
    }

    @Redirect(
        method = "lambda$renderNormal$25",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/mapper/GraphicsHolder;translate(DDD)V"),
        require = 0
    )
    private void rte$placeDoorLikeBakedBody(GraphicsHolder graphicsHolder, double x, double y, double z) {
        final Object detail = rte$currentDetail;
        rte$currentDetail = null;
        if (detail != null && rte$resolveDetailFields()) {
            try {
                if (rte$objPart && rte$detailFlipped.getBoolean(detail)) {
                    x -= rte$detailX.getDouble(detail) / 8;
                    z -= rte$detailZ.getDouble(detail) / 8;
                }
            } catch (Throwable ignored) {
            }
        }
        graphicsHolder.translate(x, y, z);
    }

    @Unique
    private static boolean rte$resolveDetailFields() {
        if (rte$detailX != null) {
            return true;
        }
        if (rte$detailFieldsFailed) {
            return false;
        }
        try {
            final Class<?> details = Class.forName("org.mtr.mod.resource.ModelPropertiesPart$PartDetails");
            final Field x = details.getDeclaredField("x");
            final Field z = details.getDeclaredField("z");
            final Field flipped = details.getDeclaredField("flipped");
            x.setAccessible(true);
            z.setAccessible(true);
            flipped.setAccessible(true);
            rte$detailZ = z;
            rte$detailFlipped = flipped;
            rte$detailX = x;
            return true;
        } catch (Throwable e) {
            rte$detailFieldsFailed = true;
            return false;
        }
    }
}
