package cn.jstxjf_.rte.gfxext.multipart;

import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.gfxext.multipart.animated.AnimatedPartStates;
import org.mtr.core.data.Vehicle;
import org.mtr.core.data.TransportMode;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Set;

public class MultipartUpdateProp {

    public double railProgress;
    public float speed;
    public float acceleration;
    public int trainCars;
    public int carIndex;
    public boolean isReversed;
    public boolean trainOnRoute;
    public double trainSpeed;

    public float systemTimeSecMidnight;

    public float leftDoorState;
    public float rightDoorState;
    public int leftDoorTarget;
    public int rightDoorTarget;

    public int[] pluginState = new int[0];
    
    public AnimatedPartStates animatedPartStates = new AnimatedPartStates();
    public float miKeyframeTime;
    public Set<String> miHiddenParts;

    public MultipartUpdateProp() {
        this.systemTimeSecMidnight = LocalTime.now().get(ChronoField.MILLI_OF_DAY) / 1000F;
    }

    public MultipartUpdateProp(Vehicle vehicle) {
        this();
        TrainExtraSupplier extra = (TrainExtraSupplier) vehicle;
        this.railProgress = extra.getVehicleRailProgress();
        this.trainSpeed = extra.getVehicleSpeed();
        this.speed = (float) extra.getVehicleSpeed();
        this.acceleration = 0;
        this.isReversed = extra.getVehicleReversed();
        this.trainOnRoute = vehicle.getIsOnRoute();
        this.carIndex = 0;

        TransportMode mode = vehicle.getTransportMode(); // Use vehicle directly
        this.trainCars = (mode.hasPitchAscending || mode.hasPitchDescending) ? 1 : 1; 
    }
}
