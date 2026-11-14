package cn.jstxjf_.rte.gfxext.multipart.animated.script;

import cn.jstxjf_.rte.gfxext.multipart.MultipartUpdateProp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Executor {

    private static final Random RANDOM_GENERATOR = new Random();

    public static double ExecuteFunctionScript(FunctionScript function, MultipartUpdateProp prop, double elapsedTime, int currentState, int lastResult) {
        int s = 0;
        int c = 0;
        for (int i = 0; i < function.InstructionSet.length; i++) {
            switch (function.InstructionSet[i]) {
                case SystemHalt:
                    i = function.InstructionSet.length;
                    break;
                case SystemConstant:
                    function.Stack[s++] = function.Constants[c++];
                    break;
                case SystemConstantArray: {
                    final int count = function.InstructionSet[i + 1].ordinal();
                    for (int j = 0; j < count; j++) {
                        function.Stack[s + j] = function.Constants[c + j];
                    }
                    s += count;
                    c += count;
                    i++;
                    break;
                }
                case SystemValue:
                    function.Stack[s++] = lastResult;
                    break;
                case SystemDelta:
                    function.Stack[s++] = elapsedTime;
                    break;
                case StackCopy:
                    function.Stack[s] = function.Stack[s - 1];
                    s++;
                    break;
                case StackSwap: {
                    final double value = function.Stack[s - 1];
                    function.Stack[s - 1] = function.Stack[s - 2];
                    function.Stack[s - 2] = value;
                    break;
                }
                case MathPlus:
                    function.Stack[s - 2] += function.Stack[s - 1];
                    s--;
                    break;
                case MathSubtract:
                    function.Stack[s - 2] -= function.Stack[s - 1];
                    s--;
                    break;
                case MathMinus:
                    function.Stack[s - 1] = -function.Stack[s - 1];
                    break;
                case MathTimes:
                    function.Stack[s - 2] *= function.Stack[s - 1];
                    s--;
                    break;
                case MathDivide:
                    function.Stack[s - 2] = function.Stack[s - 1] == 0D ? 0D : function.Stack[s - 2] / function.Stack[s - 1];
                    s--;
                    break;
                case MathReciprocal:
                    function.Stack[s - 1] = function.Stack[s - 1] == 0D ? 0D : 1D / function.Stack[s - 1];
                    break;
                case MathPower: {
                    final double a = function.Stack[s - 2];
                    final double b = function.Stack[s - 1];
                    if (b == 0D) {
                        function.Stack[s - 2] = 1D;
                    } else if (b < 0D) {
                        function.Stack[s - 2] = 0D;
                    } else {
                        function.Stack[s - 2] = Math.pow(a, b);
                    }
                    s--;
                    break;
                }
                case MathRandom: {
                    final double min = function.Stack[s - 2];
                    final double max = function.Stack[s - 1];
                    function.Stack[s - 2] = min + RANDOM_GENERATOR.nextDouble() * (max - min);
                    s--;
                    break;
                }
                case MathRandomInt: {
                    final int min = (int) function.Stack[s - 2];
                    final int max = (int) function.Stack[s - 1];
                    function.Stack[s - 2] = max <= min ? min : ThreadLocalRandom.current().nextInt(min, max);
                    s--;
                    break;
                }
                case MathIncrement:
                    function.Stack[s - 1] += 1D;
                    break;
                case MathDecrement:
                    function.Stack[s - 1] -= 1D;
                    break;
                case MathFusedMultiplyAdd:
                    function.Stack[s - 3] = function.Stack[s - 3] * function.Stack[s - 2] + function.Stack[s - 1];
                    s -= 2;
                    break;
                case MathQuotient:
                    function.Stack[s - 2] = function.Stack[s - 1] == 0D ? 0D : Math.floor(function.Stack[s - 2] / function.Stack[s - 1]);
                    s--;
                    break;
                case MathMod:
                    function.Stack[s - 2] = function.Stack[s - 1] == 0D ? 0D : function.Stack[s - 2] - function.Stack[s - 1] * Math.floor(function.Stack[s - 2] / function.Stack[s - 1]);
                    s--;
                    break;
                case MathFloor:
                    function.Stack[s - 1] = Math.floor(function.Stack[s - 1]);
                    break;
                case MathCeiling:
                    function.Stack[s - 1] = Math.ceil(function.Stack[s - 1]);
                    break;
                case MathRound:
                    function.Stack[s - 1] = Math.round(function.Stack[s - 1]);
                    break;
                case MathMin:
                    function.Stack[s - 2] = Math.min(function.Stack[s - 2], function.Stack[s - 1]);
                    s--;
                    break;
                case MathMax:
                    function.Stack[s - 2] = Math.max(function.Stack[s - 2], function.Stack[s - 1]);
                    s--;
                    break;
                case MathAbs:
                    function.Stack[s - 1] = Math.abs(function.Stack[s - 1]);
                    break;
                case MathSign:
                    function.Stack[s - 1] = Math.signum(function.Stack[s - 1]);
                    break;
                case MathExp:
                    function.Stack[s - 1] = Math.exp(function.Stack[s - 1]);
                    break;
                case MathLog:
                    function.Stack[s - 1] = safeLog(function.Stack[s - 1]);
                    break;
                case MathSqrt:
                    function.Stack[s - 1] = safeSqrt(function.Stack[s - 1]);
                    break;
                case MathSin:
                    function.Stack[s - 1] = Math.sin(function.Stack[s - 1]);
                    break;
                case MathCos:
                    function.Stack[s - 1] = Math.cos(function.Stack[s - 1]);
                    break;
                case MathTan:
                    function.Stack[s - 1] = safeTan(function.Stack[s - 1]);
                    break;
                case MathArcTan:
                    function.Stack[s - 1] = Math.atan(function.Stack[s - 1]);
                    break;
                case MathPi:
                    function.Stack[s++] = Math.PI;
                    break;
                case CompareEqual:
                    function.Stack[s - 2] = function.Stack[s - 2] == function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareUnequal:
                    function.Stack[s - 2] = function.Stack[s - 2] != function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareLess:
                    function.Stack[s - 2] = function.Stack[s - 2] < function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareGreater:
                    function.Stack[s - 2] = function.Stack[s - 2] > function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareLessEqual:
                    function.Stack[s - 2] = function.Stack[s - 2] <= function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareGreaterEqual:
                    function.Stack[s - 2] = function.Stack[s - 2] >= function.Stack[s - 1] ? 1D : 0D;
                    s--;
                    break;
                case CompareConditional:
                    function.Stack[s - 3] = function.Stack[s - 3] != 0D ? function.Stack[s - 2] : function.Stack[s - 1];
                    s -= 2;
                    break;
                case LogicalNot:
                    function.Stack[s - 1] = function.Stack[s - 1] != 0D ? 0D : 1D;
                    break;
                case LogicalAnd:
                    function.Stack[s - 2] = function.Stack[s - 2] != 0D && function.Stack[s - 1] != 0D ? 1D : 0D;
                    s--;
                    break;
                case LogicalOr:
                    function.Stack[s - 2] = function.Stack[s - 2] != 0D || function.Stack[s - 1] != 0D ? 1D : 0D;
                    s--;
                    break;
                case LogicalNand:
                    function.Stack[s - 2] = function.Stack[s - 2] != 0D && function.Stack[s - 1] != 0D ? 0D : 1D;
                    s--;
                    break;
                case LogicalNor:
                    function.Stack[s - 2] = function.Stack[s - 2] != 0D || function.Stack[s - 1] != 0D ? 0D : 1D;
                    s--;
                    break;
                case LogicalXor:
                    function.Stack[s - 2] = (function.Stack[s - 2] != 0D) ^ (function.Stack[s - 1] != 0D) ? 1D : 0D;
                    s--;
                    break;
                case CurrentObjectState:
                    function.Stack[s++] = currentState;
                    break;
                case TimeSecondsSinceMidnight:
                    function.Stack[s++] = prop.systemTimeSecMidnight;
                    break;
                case TimeHourDigit:
                    function.Stack[s++] = Math.floor(prop.systemTimeSecMidnight / 3600D);
                    break;
                case TimeMinuteDigit:
                    function.Stack[s++] = Math.floor(prop.systemTimeSecMidnight / 60D % 60D);
                    break;
                case TimeSecondDigit:
                    function.Stack[s++] = Math.floor(prop.systemTimeSecMidnight % 60D);
                    break;
                case CameraDistance:
                case CameraXDistance:
                case CameraYDistance:
                case CameraZDistance:
                case CameraView:
                case PlayerTrain:
                case TrainDestination:
                case TrainDistance:
                case TrainTrackDistance:
                case CurveRadius:
                case FrontAxleCurveRadius:
                case RearAxleCurveRadius:
                case CurveCant:
                case Pitch:
                case Odometer:
                case PassAlarm:
                case StationAdjustAlarm:
                case ReverserNotch:
                case PowerNotch:
                case LocoBrakeNotch:
                case BrakeNotch:
                case BrakeNotchLinear:
                case EmergencyBrake:
                case Klaxon:
                case PrimaryKlaxon:
                case SecondaryKlaxon:
                case MusicKlaxon:
                case HoldBrake:
                case ConstSpeed:
                case BrakeMainReservoir:
                case BrakeEqualizingReservoir:
                case BrakeBrakePipe:
                case BrakeBrakeCylinder:
                case BrakeStraightAirPipe:
                case TimetableVisible:
                case Panel2Timetable:
                case DistanceNextStation:
                case StopsNextStation:
                case NextStation:
                case NextStationStop:
                case TerminalStation:
                case RouteLimit:
                case SectionAspectNumber:
                case WiperPosition:
                    function.Stack[s++] = 0D;
                    break;
                case TrainCars:
                    function.Stack[s++] = prop.trainCars;
                    break;
                case TrainCarNumber:
                    function.Stack[s++] = prop.carIndex;
                    break;
                case TrainSpeed:
                case TrainSpeedometer:
                    function.Stack[s++] = prop.speed;
                    break;
                case TrainAcceleration:
                case TrainAccelerationMotor:
                    function.Stack[s++] = prop.acceleration;
                    break;
                case TrainSpeedOfCar:
                case TrainSpeedometerOfCar:
                    function.Stack[s - 1] = prop.speed;
                    break;
                case TrainAccelerationOfCar:
                case TrainAccelerationMotorOfCar:
                    function.Stack[s - 1] = prop.acceleration;
                    break;
                case TrainDistanceToCar:
                case TrainTrackDistanceToCar:
                case CurveRadiusOfCar:
                case FrontAxleCurveRadiusOfCar:
                case RearAxleCurveRadiusOfCar:
                case CurveCantOfCar:
                case PitchOfCar:
                case OdometerOfCar:
                case BrightnessOfCar:
                case DistanceStation:
                case StopsStation:
                case BrakeMainReservoirOfCar:
                case BrakeEqualizingReservoirOfCar:
                case BrakeBrakePipeOfCar:
                case BrakeBrakeCylinderOfCar:
                case BrakeStraightAirPipeOfCar:
                case WheelRadiusOfCar:
                case RainDrop:
                case SnowFlake:
                    function.Stack[s - 1] = 0D;
                    break;
                case Doors:
                    function.Stack[s++] = Math.max(prop.leftDoorState, prop.rightDoorState);
                    break;
                case DoorsIndex:
                    function.Stack[s - 1] = Math.max(prop.leftDoorState, prop.rightDoorState);
                    break;
                case LeftDoors:
                    function.Stack[s++] = prop.leftDoorState;
                    break;
                case LeftDoorsIndex:
                    function.Stack[s - 1] = prop.leftDoorState;
                    break;
                case RightDoors:
                    function.Stack[s++] = prop.rightDoorState;
                    break;
                case RightDoorsIndex:
                    function.Stack[s - 1] = prop.rightDoorState;
                    break;
                case LeftDoorsTarget:
                    function.Stack[s++] = prop.leftDoorTarget;
                    break;
                case LeftDoorsTargetIndex:
                    function.Stack[s - 1] = prop.leftDoorTarget;
                    break;
                case RightDoorsTarget:
                    function.Stack[s++] = prop.rightDoorTarget;
                    break;
                case RightDoorsTargetIndex:
                    function.Stack[s - 1] = prop.rightDoorTarget;
                    break;
                case LeftDoorButton:
                case RightDoorButton:
                    function.Stack[s++] = 0D;
                    break;
                case PilotLamp:
                    function.Stack[s++] = prop.leftDoorState == 0D && prop.rightDoorState == 0D ? 1D : 0D;
                    break;
                case PowerNotches:
                    function.Stack[s++] = 5D;
                    break;
                case LocoBrakeNotches:
                    function.Stack[s++] = 0D;
                    break;
                case BrakeNotches:
                case BrakeNotchesLinear:
                    function.Stack[s++] = 7D;
                    break;
                case HasAirBrake:
                case SafetyPluginAvailable:
                    function.Stack[s++] = 1D;
                    break;
                case HasHoldBrake:
                case HasConstSpeed:
                    function.Stack[s++] = 0D;
                    break;
                case SafetyPluginState: {
                    final int index = (int) Math.round(function.Stack[s - 1]);
                    if (prop.pluginState != null && index >= 0 && index < prop.pluginState.length) {
                        function.Stack[s - 1] = prop.pluginState[index];
                    } else {
                        function.Stack[s - 1] = 0D;
                    }
                    break;
                }
                case WheelRadius:
                    function.Stack[s++] = 0D;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction " + function.InstructionSet[i]);
            }
        }

        return s > 0 ? function.Stack[s - 1] : 0D;
    }

    private static double safeLog(double value) {
        return value <= 0D ? 0D : Math.log(value);
    }

    private static double safeSqrt(double value) {
        return value < 0D ? 0D : Math.sqrt(value);
    }

    private static double safeTan(double value) {
        final double cycle = value / Math.PI;
        final double offset = cycle - Math.floor(cycle) - 0.5D;
        final double epsilon = Math.floor(value >= 0D ? value : -value) * 1.38462643383279E-16D;
        return offset >= -epsilon && offset <= epsilon ? 0D : Math.tan(value);
    }
}
