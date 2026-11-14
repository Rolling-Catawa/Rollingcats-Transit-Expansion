package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Position;
import org.mtr.core.data.TransportMode;
import org.mtr.core.data.Rail;
import org.mtr.core.tool.Angle;
import org.mtr.core.generated.data.RailSchema;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RailSchema.class, remap = false)
public interface RailAccessor {
    @Accessor("transportMode")
    TransportMode rte$getTransportMode();

    @Accessor("position1")
    Position rte$getPosition1();

    @Accessor("position2")
    Position rte$getPosition2();

    @Accessor("angle1")
    Angle rte$getAngle1();

    @Accessor("angle2")
    Angle rte$getAngle2();

    @Accessor("shape")
    Rail.Shape rte$getShape();

    @Accessor("verticalRadius")
    double rte$getVerticalRadius();

    @Accessor("canTurnBack")
    boolean rte$getCanTurnBack();

    @Accessor("canHaveSignal")
    boolean rte$getCanHaveSignal();

    @Accessor("styles")
    ObjectArrayList<String> rte$getStyles();

    @Accessor("speedLimit1")
    long rte$getSpeedLimit1();

    @Accessor("speedLimit2")
    long rte$getSpeedLimit2();

    @Mutable
    @Accessor("speedLimit1")
    void rte$setSpeedLimit1(long speedLimit1);

    @Mutable
    @Accessor("speedLimit2")
    void rte$setSpeedLimit2(long speedLimit2);
}
