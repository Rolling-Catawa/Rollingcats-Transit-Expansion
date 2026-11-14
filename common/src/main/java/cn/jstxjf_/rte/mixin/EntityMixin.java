package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.RailCollisionManager;
import cn.jstxjf_.rte.util.TrainCollisionManager;
import net.minecraft.world.entity.Entity;
//#if MC>=11904
import net.minecraft.world.entity.Interaction;
//#endif
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB box, List<VoxelShape> shapes) {
        throw new AssertionError();
    }

    @Inject(method = "collideBoundingBox", at = @At("RETURN"), cancellable = true)
    private static void rte$collideWithRailBoxes(Entity entity, Vec3 movement, AABB box, Level level,
                                                  List<VoxelShape> entityShapes, CallbackInfoReturnable<Vec3> cir) {
        //#if MC>=11904
        if (entity == null || entity.noPhysics || entity.isSpectator() || entity instanceof Interaction) {
        //#else
        if (entity == null || entity.noPhysics || entity.isSpectator()) {
        //#endif
            return;
        }
        final Vec3 vanilla = cir.getReturnValue();
        if (vanilla == null) {
            return;
        }
        final AABB query = box.expandTowards(vanilla).inflate(0.001);
        final List<VoxelShape> railShapes = RailCollisionManager.getShapes(level, query);
        final List<VoxelShape> trainShapes = level != null && level.isClientSide()
            ? TrainCollisionManager.getClientShapes(entity, query)
            : List.of();
        if (railShapes.isEmpty() && trainShapes.isEmpty()) {
            return;
        }
        final List<VoxelShape> shapes = new ArrayList<>(railShapes.size() + trainShapes.size());
        shapes.addAll(railShapes);
        shapes.addAll(trainShapes);
        cir.setReturnValue(collideWithShapes(vanilla, box, shapes));
    }
}
