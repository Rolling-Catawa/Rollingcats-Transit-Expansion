package cn.jstxjf_.rte.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class ImmersiveVehiclesCompat {

    private static final ThreadLocal<Set<Object>> MARKERS =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));
    private static volatile Accessors accessors;
    private static volatile boolean broken;

    private ImmersiveVehiclesCompat() {
    }

    public static void beginUpdate() {
        MARKERS.get().clear();
    }

    public static boolean isMarker(Object point) {
        return point != null && MARKERS.get().contains(point);
    }

    @SuppressWarnings("unchecked")
    public static void appendRailCollisions(Level level, Object box, List<AABB> collidingBoxes) {
        final Accessors handles = accessors(box);
        if (handles == null || level == null || collidingBoxes == null) {
            return;
        }
        try {
            final AABB bounds = handles.bounds(box, 0, 0, 0);
            final List<VoxelShape> shapes = RailCollisionManager.getShapes(level, bounds);
            if (shapes.isEmpty()) {
                return;
            }
            final List<Object> positions = (List<Object>) handles.collidingBlockPositions.invoke(box);
            final Set<Object> markers = MARKERS.get();
            for (final VoxelShape shape : shapes) {
                for (final AABB part : shape.toAabbs()) {
                    if (!part.intersects(bounds)) {
                        continue;
                    }
                    collidingBoxes.add(part);
                    final Object marker = handles.newPoint.invoke(
                        (part.minX + part.maxX) / 2, (part.minY + part.maxY) / 2, (part.minZ + part.maxZ) / 2);
                    markers.add(marker);
                    positions.add(marker);
                }
            }
        } catch (Throwable e) {
            broken = true;
        }
    }

    public static boolean collidesWithRail(Level level, Object box, Object offset) {
        final Accessors handles = accessors(box);
        if (handles == null || level == null) {
            return false;
        }
        try {
            final AABB bounds = offset == null
                ? handles.bounds(box, 0, 0, 0)
                : handles.bounds(box, (double) handles.pointX.invoke(offset), (double) handles.pointY.invoke(offset), (double) handles.pointZ.invoke(offset));
            for (final VoxelShape shape : RailCollisionManager.getShapes(level, bounds)) {
                for (final AABB part : shape.toAabbs()) {
                    if (part.intersects(bounds)) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            broken = true;
        }
        return false;
    }

    private static Accessors accessors(Object box) {
        if (broken || box == null) {
            return null;
        }
        Accessors current = accessors;
        if (current != null && current.boxClass == box.getClass()) {
            return current;
        }
        try {
            current = new Accessors(box.getClass());
            accessors = current;
            return current;
        } catch (Throwable e) {
            broken = true;
            return null;
        }
    }

    private static final class Accessors {
        final Class<?> boxClass;
        final MethodHandle globalCenter;
        final MethodHandle widthRadius;
        final MethodHandle heightRadius;
        final MethodHandle depthRadius;
        final MethodHandle collidingBlockPositions;
        final MethodHandle pointX;
        final MethodHandle pointY;
        final MethodHandle pointZ;
        final MethodHandle newPoint;

        Accessors(Class<?> boxClass) throws ReflectiveOperationException {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            this.boxClass = boxClass;
            final Class<?> pointClass = boxClass.getField("globalCenter").getType();
            globalCenter = lookup.unreflectGetter(boxClass.getField("globalCenter"));
            widthRadius = lookup.unreflectGetter(boxClass.getField("widthRadius"));
            heightRadius = lookup.unreflectGetter(boxClass.getField("heightRadius"));
            depthRadius = lookup.unreflectGetter(boxClass.getField("depthRadius"));
            collidingBlockPositions = lookup.unreflectGetter(boxClass.getField("collidingBlockPositions"));
            pointX = lookup.unreflectGetter(pointClass.getField("x"));
            pointY = lookup.unreflectGetter(pointClass.getField("y"));
            pointZ = lookup.unreflectGetter(pointClass.getField("z"));
            newPoint = lookup.findConstructor(pointClass, MethodType.methodType(void.class, double.class, double.class, double.class));
        }

        AABB bounds(Object box, double offsetX, double offsetY, double offsetZ) throws Throwable {
            final Object center = globalCenter.invoke(box);
            final double x = (double) pointX.invoke(center) + offsetX;
            final double y = (double) pointY.invoke(center) + offsetY;
            final double z = (double) pointZ.invoke(center) + offsetZ;
            final double width = (double) widthRadius.invoke(box);
            final double height = (double) heightRadius.invoke(box);
            final double depth = (double) depthRadius.invoke(box);
            return new AABB(x - width, y - height, z - depth, x + width, y + height, z + depth);
        }
    }
}
