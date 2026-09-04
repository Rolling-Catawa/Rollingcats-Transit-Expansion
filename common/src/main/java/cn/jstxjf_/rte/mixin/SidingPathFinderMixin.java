package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import cn.jstxjf_.rte.util.RteSidingPathHelper;
import org.mtr.core.path.SidingPathFinder;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SidingPathFinder.class, remap = false)
public abstract class SidingPathFinderMixin {

    @SuppressWarnings("rawtypes")
    @Shadow @Final public org.mtr.core.data.SavedRailBase startSavedRail;
    @Shadow @Final private TransportMode transportMode;
    @Shadow @Final private Object2ObjectOpenHashMap<Position, Object2ObjectOpenHashMap<Position, Rail>> positionsToRail;
    @Shadow @Final private Object2ObjectOpenHashMap<Position, Rail> runwaysInbound;
    @Shadow @Final private ObjectOpenHashSet<Position> runwaysOutbound;

    @Inject(
        method = "getConnections(JLjava/lang/Object;Ljava/lang/Long;)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void rte$getConnections(long elapsedTime, Object node, Long previousRouteId, CallbackInfoReturnable<ObjectArrayList> cir) {
        cir.setReturnValue(RteSidingPathHelper.getConnections(
            transportMode, positionsToRail, runwaysInbound, runwaysOutbound, node, rte$depotId()));
    }

    private long rte$depotId() {
        try {
            return startSavedRail != null && startSavedRail.area instanceof org.mtr.core.data.Depot depot
                ? depot.getId() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

}
