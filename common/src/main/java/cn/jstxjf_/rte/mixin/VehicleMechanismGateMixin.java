package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.mechanism.Mechanism;
import cn.jstxjf_.rte.mechanism.MechanismGate;
import cn.jstxjf_.rte.mechanism.MechanismVehicles;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Vehicle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = Vehicle.class, remap = false)
public abstract class VehicleMechanismGateMixin {

    @Inject(method = "checkAndBlockSignal", at = @At("RETURN"), cancellable = true, require = 0)
    private void rte$blockAtMechanism(int index, ObjectArrayList<Object2ObjectAVLTreeMap<org.mtr.core.data.Position, Object2ObjectAVLTreeMap<org.mtr.core.data.Position, org.mtr.core.data.VehiclePosition>>> vehiclePositions,
                                      boolean reserveRail, boolean secondPass, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || RailScriptRuntime.GATE_INDEX.isEmpty()) {
            return;
        }
        final Vehicle self = (Vehicle) (Object) this;
        final List<PathData> path = self.vehicleExtraData.immutablePath;
        if (index < 0 || index >= path.size()) {
            return;
        }
        final long now = System.currentTimeMillis();
        final double progress = ((VehicleAccessor) (Object) self).getRailProgress();
        final double tail = progress - self.vehicleExtraData.getTotalVehicleLength();
        final Set<Mechanism> occupied = new HashSet<>();
        final int head = MechanismVehicles.indexAt(path, progress);
        for (int i = Math.max(0, MechanismVehicles.indexAt(path, tail)); i <= head && i < path.size(); i++) {
            final PathData data = path.get(i);
            if (data.getEndDistance() <= tail || data.getStartDistance() >= progress) {
                continue;
            }
            final Mechanism mechanism = RailScriptRuntime.GATE_INDEX.get(data.getRail().getHexId());
            if (mechanism != null) {
                occupied.add(mechanism);
            }
        }
        final String railId = path.get(index).getRail().getHexId();
        final Mechanism target = RailScriptRuntime.GATE_INDEX.get(railId);
        if (target != null) {
            if (!occupied.contains(target) && MechanismGate.blocks(target, railId, false, now)) {
                cir.setReturnValue(true);
            }
            return;
        }
        for (final Mechanism mechanism : occupied) {
            if (mechanism.locked || mechanism.isMoving(now) || !mechanism.sample(now).isAligned()) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
