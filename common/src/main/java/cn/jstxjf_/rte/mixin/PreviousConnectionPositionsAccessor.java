package cn.jstxjf_.rte.mixin;

import org.mtr.core.tool.Vector;
import org.mtr.mod.render.RenderVehicles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RenderVehicles.PreviousConnectionPositions.class, remap = false)
public interface PreviousConnectionPositionsAccessor {

    @Accessor("position1")
    Vector rte$getPosition1();

    @Accessor("position2")
    Vector rte$getPosition2();

    @Accessor("position3")
    Vector rte$getPosition3();

    @Accessor("position4")
    Vector rte$getPosition4();
}
