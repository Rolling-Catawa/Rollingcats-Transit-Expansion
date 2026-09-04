package cn.jstxjf_.rte.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.mapping.holder.Box;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = VehicleResource.class, remap = false)
public class LegacyFloorMixin {

    private static final double FLOOR_THICKNESS = 0.5;
    private static final double END_MARGIN = 2.5;
    private static int rte$logged = 0;

    @ModifyArgs(
        method = "getCachedVehicleResource",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/mod/resource/VehicleResourceCache;<init>(Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectImmutableList;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectImmutableList;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;)V"
        ),
        require = 0
    )
    private void rte$synthesiseFloor(Args args) {
        try {
            if (!cn.jstxjf_.rte.ClientConfig.legacyWalkways) {
                return;
            }
            final ObjectImmutableList<Box> floors = args.get(0);
            final ObjectImmutableList<Box> doorways = args.get(1);
            if (floors == null || doorways == null || !floors.isEmpty() || doorways.isEmpty()) {
                return;
            }
            final Box floor = rte$slabFromDoorways(doorways);
            if (floor == null) {
                return;
            }
            final List<Box> replacement = new ArrayList<>();
            replacement.add(floor);
            args.set(0, new ObjectImmutableList<>(replacement));
            if (++rte$logged <= 10) {
                cn.jstxjf_.rte.RTE.LOGGER.info(
                    "[LegacyFloor] floor slab x[{}, {}] y[{}, {}] z[{}, {}] from {} doorways",
                    rte$round(floor.getMinXMapped()), rte$round(floor.getMaxXMapped()),
                    rte$round(floor.getMinYMapped()), rte$round(floor.getMaxYMapped()),
                    rte$round(floor.getMinZMapped()), rte$round(floor.getMaxZMapped()),
                    doorways.size());
            }
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyFloor] synthesis failed", e);
        }
    }

    private static double rte$round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private static Box rte$slabFromDoorways(ObjectImmutableList<Box> doorways) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double floorY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (final Box doorway : doorways) {
            minX = Math.min(minX, doorway.getMinXMapped());
            maxX = Math.max(maxX, doorway.getMaxXMapped());
            floorY = Math.min(floorY, doorway.getMinYMapped());
            minZ = Math.min(minZ, doorway.getMinZMapped());
            maxZ = Math.max(maxZ, doorway.getMaxZMapped());
        }
        if (minX > maxX || minZ > maxZ || floorY == Double.MAX_VALUE) {
            return null;
        }
        return new Box(
            minX, floorY - FLOOR_THICKNESS, minZ - END_MARGIN,
            maxX, floorY, maxZ + END_MARGIN);
    }
}
