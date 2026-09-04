package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.util.StringMapSerializer;
import org.mtr.core.generated.data.VehicleSchema;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.core.serializer.WriterBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = VehicleSchema.class, remap = false)
public abstract class VehicleSchemaMixin {

    @Inject(method = "updateData", at = @At("TAIL"))
    private void rte$updateData(ReaderBase reader, CallbackInfo ci) {
        if (!(this instanceof TrainExtraSupplier supplier)) {
            return;
        }
        reader.unpackString("custom_configs", value -> {
            try {
                supplier.setCustomConfigs(StringMapSerializer.deserialize(value));
            } catch (Exception ignored) {
                supplier.setCustomConfigs(new HashMap<>());
            }
        });
        reader.unpackString("rte_custom_configs", value -> {
            try {
                supplier.setCustomConfigs(StringMapSerializer.deserialize(value));
            } catch (Exception ignored) {
                supplier.setCustomConfigs(new HashMap<>());
            }
        });
    }

    @Inject(method = "serializeData", at = @At("TAIL"))
    private void rte$serializeData(WriterBase writer, CallbackInfo ci) {
        if (!(this instanceof TrainExtraSupplier supplier)) {
            return;
        }
        writer.writeString("custom_configs", StringMapSerializer.serializeToString(supplier.getCustomConfigs()));
        writer.writeString("rte_custom_configs", StringMapSerializer.serializeToString(supplier.getCustomConfigs()));
    }
}
