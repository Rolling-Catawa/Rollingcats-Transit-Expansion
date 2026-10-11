package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.LegacyDoorMetadata;
import cn.jstxjf_.rte.util.LegacyVisualDoorAdapter;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Box;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.mtr.mod.resource.ModelPropertiesPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Locale;

/** Restores the side information that MTR 4's MTR 3 converter discards. */
@Mixin(value = ModelPropertiesPart.class, remap = false)
public abstract class ModelPropertiesPartLegacyMixin implements LegacyDoorMetadata {

    @Shadow @Final
    private ObjectArrayList<?> partDetailsList;

    @Unique private String rte$legacyDoorSide = "";
    @Unique private String rte$legacyRenderCondition = "";
    @Unique private int rte$legacySide;
    @Unique private boolean rte$legacyWantsOpen;
    @Unique private static Field rte$doorwayField;

    @Inject(method = "<init>(Lorg/mtr/core/serializer/ReaderBase;)V", at = @At("RETURN"), require = 0)
    private void rte$readLegacyMetadata(ReaderBase reader, CallbackInfo ci) {
        rte$legacyDoorSide = reader.getString("rteLegacyDoorSide", "")
            .toUpperCase(Locale.ROOT);
        rte$legacyRenderCondition = reader.getString("rteLegacyRenderCondition", "")
            .toUpperCase(Locale.ROOT);
        rte$legacySide = rte$legacyRenderCondition.contains("LEFT") ? 1
            : rte$legacyRenderCondition.contains("RIGHT") ? 2 : 0;
        rte$legacyWantsOpen = rte$legacyRenderCondition.endsWith("_OPEN");
    }

    @Inject(method = "mapDoors", at = @At("RETURN"), require = 0)
    private void rte$mapExactLegacyDoorSide(ObjectArrayList<Box> doorways, CallbackInfo ci) {
        final Box marker = switch (rte$legacyDoorSide) {
            case "LEFT" -> LegacyVisualDoorAdapter.LEFT;
            case "RIGHT" -> LegacyVisualDoorAdapter.RIGHT;
            default -> null;
        };
        if (marker == null || partDetailsList == null) {
            return;
        }
        try {
            if (rte$doorwayField == null) {
                final Class<?> details = Class.forName(
                    "org.mtr.mod.resource.ModelPropertiesPart$PartDetails");
                rte$doorwayField = details.getDeclaredField("doorway");
                rte$doorwayField.setAccessible(true);
            }
            for (final Object detail : partDetailsList) {
                if (detail != null) {
                    rte$doorwayField.set(detail, marker);
                }
            }
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyDoorSide] failed to map render marker", e);
        }
    }

    @Inject(
        method = "render(Lorg/mtr/mapping/holder/Identifier;Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V",
        at = @At("HEAD"), cancellable = true, require = 0
    )
    private void rte$preserveSideSpecificCondition(
            org.mtr.mapping.holder.Identifier identifier,
            StoredMatrixTransformations transformations,
            VehicleExtension vehicle, int carNumber, int[] scrollingDisplayIndexTracker,
            int light, ObjectArrayList<?> openDoorways, boolean fromResourcePackCreator,
            CallbackInfo ci) {
        if (rte$legacySide == 0 || vehicle == null || fromResourcePackCreator) {
            return;
        }
        if (openDoorways.isEmpty()) {
            ci.cancel();
            return;
        }
        final org.mtr.mod.render.PositionAndRotation position =
            cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
                .getCarPositionAndRotation(vehicle, carNumber);
        final boolean[] sides = position == null ? new boolean[] {false, false}
            : cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
                .doorSidesOf(vehicle, carNumber, position);
        final boolean sideOpen = rte$legacySide == 1 ? sides[0] : sides[1];
        if (sideOpen != rte$legacyWantsOpen) {
            ci.cancel();
        }
    }

    @Inject(method = "isDoor", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$renderSideConditionPerFrame(CallbackInfoReturnable<Boolean> cir) {
        if (rte$legacySide != 0) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public String rte$getLegacyDoorSide() {
        return rte$legacyDoorSide;
    }

    @Override
    public String rte$getLegacyRenderCondition() {
        return rte$legacyRenderCondition;
    }
}
