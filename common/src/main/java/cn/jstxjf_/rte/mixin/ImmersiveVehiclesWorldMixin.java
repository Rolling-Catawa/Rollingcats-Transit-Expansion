package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.ImmersiveVehiclesCompat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "mcinterface1201.WrapperWorld", remap = false)
public abstract class ImmersiveVehiclesWorldMixin {

    @Shadow(remap = false)
    @Final
    protected Level world;

    @Shadow(remap = false)
    @Final
    private List<AABB> mutableCollidingAABBs;

    @Inject(method = "updateBoundingBoxCollisions", at = @At("HEAD"), require = 0, remap = false)
    private void rte$beginRailCollisions(@Coerce Object box, @Coerce Object motion, boolean ignoreIfGreater, CallbackInfo ci) {
        ImmersiveVehiclesCompat.beginUpdate();
    }

    @Inject(
        method = "updateBoundingBoxCollisions",
        at = @At(
            value = "FIELD",
            target = "Lminecrafttransportsimulator/baseclasses/BoundingBox;currentCollisionDepth:Lminecrafttransportsimulator/baseclasses/Point3D;",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        ),
        require = 0,
        remap = false
    )
    private void rte$addRailCollisions(@Coerce Object box, @Coerce Object motion, boolean ignoreIfGreater, CallbackInfo ci) {
        ImmersiveVehiclesCompat.appendRailCollisions(world, box, mutableCollidingAABBs);
    }

    @Inject(method = "checkForCollisions", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private void rte$checkRailCollisions(@Coerce Object box, @Coerce Object offset, boolean clearCache, boolean ignoreIfGreater,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && ImmersiveVehiclesCompat.collidesWithRail(world, box, offset)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void rte$railMarkerHardness(@Coerce Object point, CallbackInfoReturnable<Float> cir) {
        if (ImmersiveVehiclesCompat.isMarker(point)) {
            cir.setReturnValue(-1F);
        }
    }

    @Inject(method = "isBlockLiquid", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void rte$railMarkerLiquid(@Coerce Object point, CallbackInfoReturnable<Boolean> cir) {
        if (ImmersiveVehiclesCompat.isMarker(point)) {
            cir.setReturnValue(false);
        }
    }
}
