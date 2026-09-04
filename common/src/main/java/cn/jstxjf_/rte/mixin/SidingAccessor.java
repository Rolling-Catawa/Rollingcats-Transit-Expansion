package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Siding.class, remap = false)
public interface SidingAccessor {

    @Accessor("vehicles")
    ObjectArraySet<Vehicle> rte$getVehicles();

    @Accessor("pathMainRoute")
    org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<org.mtr.core.data.PathData> rte$getPathMainRoute();

    @Accessor("pathSidingToMainRoute")
    org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<org.mtr.core.data.PathData> rte$getPathSidingToMainRoute();

    @Accessor("pathMainRouteToSiding")
    org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<org.mtr.core.data.PathData> rte$getPathMainRouteToSiding();
}
