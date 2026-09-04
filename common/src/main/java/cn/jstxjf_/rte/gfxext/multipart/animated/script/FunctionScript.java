package cn.jstxjf_.rte.gfxext.multipart.animated.script;

import cn.jstxjf_.rte.gfxext.multipart.MultipartUpdateProp;
import cn.jstxjf_.rte.gfxext.util.Logging;

import java.util.Arrays;
import java.util.Locale;

public class FunctionScript {

    public static final FunctionScript DEFAULT = new FunctionScript("0");

    public Instructions[] InstructionSet;
    public double[] Constants;
    public double[] Stack;

    public FunctionScript(String Expression) {
        String PostfixExpression = Notation.GetPostfixNotationFromInfixNotation(Expression);
        String OptimizedExpression = Notation.GetOptimizedPostfixNotation(PostfixExpression);
        String[] Arguments = OptimizedExpression.split("\\s+");
        InstructionSet = new Instructions[16];
        Constants = new double[16];
        int i = 0;
        int c = 0;
        for (String Argument : Arguments) {
            if (i >= InstructionSet.length) {
                InstructionSet = Arrays.copyOf(InstructionSet, InstructionSet.length << 1);
            }
            if (c >= Constants.length) {
                Constants = Arrays.copyOf(Constants, Constants.length << 1);
            }
            if (!Argument.isEmpty()) {
                switch (Argument.toLowerCase(Locale.ROOT)) {
                    case "+": InstructionSet[i] = Instructions.MathPlus; break;
                    case "-": InstructionSet[i] = Instructions.MathSubtract; break;
                    case "minus": InstructionSet[i] = Instructions.MathMinus; break;
                    case "*": InstructionSet[i] = Instructions.MathTimes; break;
                    case "/": InstructionSet[i] = Instructions.MathDivide; break;
                    case "reciprocal": InstructionSet[i] = Instructions.MathReciprocal; break;
                    case "power": InstructionSet[i] = Instructions.MathPower; break;
                    case "++": InstructionSet[i] = Instructions.MathIncrement; break;
                    case "--": InstructionSet[i] = Instructions.MathDecrement; break;
                    case "fma": InstructionSet[i] = Instructions.MathFusedMultiplyAdd; break;
                    case "quotient": InstructionSet[i] = Instructions.MathQuotient; break;
                    case "mod": InstructionSet[i] = Instructions.MathMod; break;
                    case "floor": InstructionSet[i] = Instructions.MathFloor; break;
                    case "ceiling": InstructionSet[i] = Instructions.MathCeiling; break;
                    case "round": InstructionSet[i] = Instructions.MathRound; break;
                    case "min": InstructionSet[i] = Instructions.MathMin; break;
                    case "max": InstructionSet[i] = Instructions.MathMax; break;
                    case "abs": InstructionSet[i] = Instructions.MathAbs; break;
                    case "sign": InstructionSet[i] = Instructions.MathSign; break;
                    case "exp": InstructionSet[i] = Instructions.MathExp; break;
                    case "log": InstructionSet[i] = Instructions.MathLog; break;
                    case "sqrt": InstructionSet[i] = Instructions.MathSqrt; break;
                    case "sin": InstructionSet[i] = Instructions.MathSin; break;
                    case "cos": InstructionSet[i] = Instructions.MathCos; break;
                    case "tan": InstructionSet[i] = Instructions.MathTan; break;
                    case "arctan": InstructionSet[i] = Instructions.MathArcTan; break;
                    case "pi": InstructionSet[i] = Instructions.MathPi; break;
                    case "==": InstructionSet[i] = Instructions.CompareEqual; break;
                    case "!=": InstructionSet[i] = Instructions.CompareUnequal; break;
                    case "<": InstructionSet[i] = Instructions.CompareLess; break;
                    case ">": InstructionSet[i] = Instructions.CompareGreater; break;
                    case "<=": InstructionSet[i] = Instructions.CompareLessEqual; break;
                    case ">=": InstructionSet[i] = Instructions.CompareGreaterEqual; break;
                    case "?": InstructionSet[i] = Instructions.CompareConditional; break;
                    case "!": InstructionSet[i] = Instructions.LogicalNot; break;
                    case "&": InstructionSet[i] = Instructions.LogicalAnd; break;
                    case "|": InstructionSet[i] = Instructions.LogicalOr; break;
                    case "~&": InstructionSet[i] = Instructions.LogicalNand; break;
                    case "~|": InstructionSet[i] = Instructions.LogicalNor; break;
                    case "^": InstructionSet[i] = Instructions.LogicalXor; break;
                    case "halt": InstructionSet[i] = Instructions.SystemHalt; break;
                    case "value": InstructionSet[i] = Instructions.SystemValue; break;
                    case "delta": InstructionSet[i] = Instructions.SystemDelta; break;
                    case "copy": InstructionSet[i] = Instructions.StackCopy; break;
                    case "<>": InstructionSet[i] = Instructions.StackSwap; break;
                    case "random": InstructionSet[i] = Instructions.MathRandom; break;
                    case "randomint": InstructionSet[i] = Instructions.MathRandomInt; break;
                    case "time": InstructionSet[i] = Instructions.TimeSecondsSinceMidnight; break;
                    case "hour": InstructionSet[i] = Instructions.TimeHourDigit; break;
                    case "minute": InstructionSet[i] = Instructions.TimeMinuteDigit; break;
                    case "second": InstructionSet[i] = Instructions.TimeSecondDigit; break;
                    case "cameradistance": InstructionSet[i] = Instructions.CameraDistance; break;
                    case "cameraxdistance": InstructionSet[i] = Instructions.CameraXDistance; break;
                    case "cameraydistance": InstructionSet[i] = Instructions.CameraYDistance; break;
                    case "camerazdistance": InstructionSet[i] = Instructions.CameraZDistance; break;
                    case "cameraview": InstructionSet[i] = Instructions.CameraView; break;
                    case "cars": InstructionSet[i] = Instructions.TrainCars; break;
                    case "carnumber": InstructionSet[i] = Instructions.TrainCarNumber; break;
                    case "destination": InstructionSet[i] = Instructions.TrainDestination; break;
                    case "player": InstructionSet[i] = Instructions.PlayerTrain; break;
                    case "speed": InstructionSet[i] = Instructions.TrainSpeed; break;
                    case "speedometer": InstructionSet[i] = Instructions.TrainSpeedometer; break;
                    case "acceleration": InstructionSet[i] = Instructions.TrainAcceleration; break;
                    case "accelerationmotor": InstructionSet[i] = Instructions.TrainAccelerationMotor; break;
                    case "speedindex": InstructionSet[i] = Instructions.TrainSpeedOfCar; break;
                    case "speedometerindex": InstructionSet[i] = Instructions.TrainSpeedometerOfCar; break;
                    case "accelerationindex": InstructionSet[i] = Instructions.TrainAccelerationOfCar; break;
                    case "accelerationmotorindex": InstructionSet[i] = Instructions.TrainAccelerationMotorOfCar; break;
                    case "distance": InstructionSet[i] = Instructions.TrainDistance; break;
                    case "distanceindex": InstructionSet[i] = Instructions.TrainDistanceToCar; break;
                    case "trackdistance": InstructionSet[i] = Instructions.TrainTrackDistance; break;
                    case "trackdistanceindex": InstructionSet[i] = Instructions.TrainTrackDistanceToCar; break;
                    case "curveradius": InstructionSet[i] = Instructions.CurveRadius; break;
                    case "curveradiusindex": InstructionSet[i] = Instructions.CurveRadiusOfCar; break;
                    case "frontaxlecurveradius": InstructionSet[i] = Instructions.FrontAxleCurveRadius; break;
                    case "frontaxlecurveradiusindex": InstructionSet[i] = Instructions.FrontAxleCurveRadiusOfCar; break;
                    case "rearaxlecurveradius": InstructionSet[i] = Instructions.RearAxleCurveRadius; break;
                    case "rearaxlecurveradiusindex": InstructionSet[i] = Instructions.RearAxleCurveRadiusOfCar; break;
                    case "curvecant": InstructionSet[i] = Instructions.CurveCant; break;
                    case "curvecantindex": InstructionSet[i] = Instructions.CurveCantOfCar; break;
                    case "pitch": InstructionSet[i] = Instructions.Pitch; break;
                    case "pitchindex": InstructionSet[i] = Instructions.PitchOfCar; break;
                    case "odometer": InstructionSet[i] = Instructions.Odometer; break;
                    case "odometerindex": InstructionSet[i] = Instructions.OdometerOfCar; break;
                    case "brightnessindex": InstructionSet[i] = Instructions.BrightnessOfCar; break;
                    case "doors": InstructionSet[i] = Instructions.Doors; break;
                    case "doorsindex": InstructionSet[i] = Instructions.DoorsIndex; break;
                    case "leftdoors": InstructionSet[i] = Instructions.LeftDoors; break;
                    case "leftdoorsindex": InstructionSet[i] = Instructions.LeftDoorsIndex; break;
                    case "rightdoors": InstructionSet[i] = Instructions.RightDoors; break;
                    case "rightdoorsindex": InstructionSet[i] = Instructions.RightDoorsIndex; break;
                    case "leftdoorstarget": InstructionSet[i] = Instructions.LeftDoorsTarget; break;
                    case "leftdoorstargetindex": InstructionSet[i] = Instructions.LeftDoorsTargetIndex; break;
                    case "rightdoorstarget": InstructionSet[i] = Instructions.RightDoorsTarget; break;
                    case "rightdoorstargetindex": InstructionSet[i] = Instructions.RightDoorsTargetIndex; break;
                    case "leftdoorbutton": InstructionSet[i] = Instructions.LeftDoorButton; break;
                    case "rightdoorbutton": InstructionSet[i] = Instructions.RightDoorButton; break;
                    case "pilotlamp": InstructionSet[i] = Instructions.PilotLamp; break;
                    case "reversernotch": InstructionSet[i] = Instructions.ReverserNotch; break;
                    case "powernotch": InstructionSet[i] = Instructions.PowerNotch; break;
                    case "powernotches": InstructionSet[i] = Instructions.PowerNotches; break;
                    case "locobrakenotch": InstructionSet[i] = Instructions.LocoBrakeNotch; break;
                    case "locobrakenotches": InstructionSet[i] = Instructions.LocoBrakeNotches; break;
                    case "brakenotch": InstructionSet[i] = Instructions.BrakeNotch; break;
                    case "brakenotches": InstructionSet[i] = Instructions.BrakeNotches; break;
                    case "brakenotchlinear": InstructionSet[i] = Instructions.BrakeNotchLinear; break;
                    case "brakenotcheslinear": InstructionSet[i] = Instructions.BrakeNotchesLinear; break;
                    case "emergencybrake": InstructionSet[i] = Instructions.EmergencyBrake; break;
                    case "klaxon": InstructionSet[i] = Instructions.Klaxon; break;
                    case "primaryklaxon": InstructionSet[i] = Instructions.PrimaryKlaxon; break;
                    case "secondaryklaxon": InstructionSet[i] = Instructions.SecondaryKlaxon; break;
                    case "musicklaxon": InstructionSet[i] = Instructions.MusicKlaxon; break;
                    case "hasairbrake": InstructionSet[i] = Instructions.HasAirBrake; break;
                    case "holdbrake": InstructionSet[i] = Instructions.HoldBrake; break;
                    case "hasholdbrake": InstructionSet[i] = Instructions.HasHoldBrake; break;
                    case "constspeed": InstructionSet[i] = Instructions.ConstSpeed; break;
                    case "hasconstspeed": InstructionSet[i] = Instructions.HasConstSpeed; break;
                    case "mainreservoir": InstructionSet[i] = Instructions.BrakeMainReservoir; break;
                    case "mainreservoirindex": InstructionSet[i] = Instructions.BrakeMainReservoirOfCar; break;
                    case "equalizingreservoir": InstructionSet[i] = Instructions.BrakeEqualizingReservoir; break;
                    case "equalizingreservoirindex": InstructionSet[i] = Instructions.BrakeEqualizingReservoirOfCar; break;
                    case "brakepipe": InstructionSet[i] = Instructions.BrakeBrakePipe; break;
                    case "brakepipeindex": InstructionSet[i] = Instructions.BrakeBrakePipeOfCar; break;
                    case "brakecylinder": InstructionSet[i] = Instructions.BrakeBrakeCylinder; break;
                    case "brakecylinderindex": InstructionSet[i] = Instructions.BrakeBrakeCylinderOfCar; break;
                    case "straightairpipe": InstructionSet[i] = Instructions.BrakeStraightAirPipe; break;
                    case "straightairpipeindex": InstructionSet[i] = Instructions.BrakeStraightAirPipeOfCar; break;
                    case "safetypluginavailable": InstructionSet[i] = Instructions.SafetyPluginAvailable; break;
                    case "pluginstate": InstructionSet[i] = Instructions.SafetyPluginState; break;
                    case "passalarm": InstructionSet[i] = Instructions.PassAlarm; break;
                    case "stationadjustalarm": InstructionSet[i] = Instructions.StationAdjustAlarm; break;
                    case "timetablevisible": InstructionSet[i] = Instructions.TimetableVisible; break;
                    case "panel2timetable": InstructionSet[i] = Instructions.Panel2Timetable; break;
                    case "distancenextstation": InstructionSet[i] = Instructions.DistanceNextStation; break;
                    case "stopsnextstation": InstructionSet[i] = Instructions.StopsNextStation; break;
                    case "distancestation": InstructionSet[i] = Instructions.DistanceStation; break;
                    case "stopsstation": InstructionSet[i] = Instructions.StopsStation; break;
                    case "nextstation": InstructionSet[i] = Instructions.NextStation; break;
                    case "nextstationstop": InstructionSet[i] = Instructions.NextStationStop; break;
                    case "terminalstation": InstructionSet[i] = Instructions.TerminalStation; break;
                    case "routelimit": InstructionSet[i] = Instructions.RouteLimit; break;
                    case "sectionaspectnumber": InstructionSet[i] = Instructions.SectionAspectNumber; break;
                    case "state": InstructionSet[i] = Instructions.CurrentObjectState; break;
                    case "raindrop": InstructionSet[i] = Instructions.RainDrop; break;
                    case "snowflake": InstructionSet[i] = Instructions.SnowFlake; break;
                    case "wiperposition": InstructionSet[i] = Instructions.WiperPosition; break;
                    case "wheelradius": InstructionSet[i] = Instructions.WheelRadius; break;
                    case "wheelradiusindex": InstructionSet[i] = Instructions.WheelRadiusOfCar; break;
                    default:
                        if (CSUtil.tryParseDouble(Argument)) {
                            InstructionSet[i] = Instructions.SystemConstant;
                            Constants[c] = Double.parseDouble(Argument);
                            c++;
                        } else {
                            Logging.LOGGER.error("Unknown instruction: " + Argument);
                            InstructionSet[i] = Instructions.SystemConstant;
                            Constants[c] = 0;
                            c++;
                        }
                        break;
                }
                i++;
            }
        }
        
        // Optimization: Merge consecutive constant updates if possible
        // (Implementation note: Basic stack-based folding is handled in Notation.java)



        InstructionSet = Arrays.copyOf(InstructionSet, i);
        Constants = Arrays.copyOf(Constants, c);
        Stack = new double[256]; // Default size
    }

    public boolean isStatic() {
        for (Instructions inst : InstructionSet) {
            if (inst == null) break;
            switch (inst) {
                case SystemConstant:
                case MathPlus: case MathSubtract: case MathMinus: case MathTimes: case MathDivide:
                case MathReciprocal: case MathPower: case MathIncrement: case MathDecrement:
                case MathFusedMultiplyAdd: case MathQuotient: case MathMod:
                case MathFloor: case MathCeiling: case MathRound: case MathMin: case MathMax:
                case MathAbs: case MathSign: case MathExp: case MathLog: case MathSqrt:
                case MathSin: case MathCos: case MathTan: case MathArcTan: case MathPi:
                case CompareEqual: case CompareUnequal: case CompareLess: case CompareGreater:
                case CompareLessEqual: case CompareGreaterEqual: case CompareConditional:
                case LogicalNot: case LogicalAnd: case LogicalOr: case LogicalNand: case LogicalNor: case LogicalXor:
                case StackCopy: case StackSwap:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public float update(MultipartUpdateProp prop, double elapsedTime, int currentState) {
        return (float) Executor.ExecuteFunctionScript(this, prop, elapsedTime, currentState, 0);
    }
    
    // Package-private access for Executor
    Instructions[] getInstructionSet() { return InstructionSet; }
    double[] getConstants() { return Constants; }
    double[] getStack() { return Stack; }
}
