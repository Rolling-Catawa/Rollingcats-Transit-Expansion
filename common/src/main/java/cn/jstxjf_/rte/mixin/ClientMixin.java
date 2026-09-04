package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.TrainExtraSupplier;
import org.mtr.core.data.Client;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Client.class, remap = false)
public abstract class ClientMixin {

    @Inject(method = "sendUpdates", at = @At("HEAD"))
    private void rte$queueChangedTrainConfigs(Simulator simulator, CallbackInfo ci) {
        final Client client = (Client) (Object) this;
        for (final Siding siding : simulator.sidings) {
            for (final Vehicle vehicle : ((SidingAccessor) (Object) siding).rte$getVehicles()) {
                if (vehicle instanceof TrainExtraSupplier supplier && supplier.isConfigsChanged()) {
                    client.update(vehicle, true, 0);
                    supplier.isConfigsChanged(false);
                }
            }
        }
    }
}
