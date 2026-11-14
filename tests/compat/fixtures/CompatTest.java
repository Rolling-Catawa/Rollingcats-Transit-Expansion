import cn.jstxjf_.rte.util.*;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import com.lx862.mtrscripting.mod.impl.mtr.MTRContentScripting;
import com.lx862.mtrscripting.mod.impl.mtr.MTRContentScripting.*;
import dev.architectury.platform.Platform;
import java.util.Map;

public class CompatTest {
    static int count;
    static void check(String name, boolean value) {
        if (!value) throw new AssertionError(name);
        count++;
        System.out.println("PASS " + name);
    }
    static boolean vehicle() {
        JcmCompat.beginFrame();
        return JcmCompat.isVehicleTakenByJcm("v1");
    }
    public static void main(String[] args) throws Exception {
        Platform.present = !args[0].equals("absent");
        if (!Platform.present) {
            CompatArbiter.restore(Map.of("scriptVehicle", "other", "scriptEyecandy", "other", "nodePlacement", "other"));
            check("absent/vehicle-enabled", !JcmCompat.shouldDisableRteJs());
            check("absent/eyecandy-enabled", !JcmCompat.shouldDisableRteEyeCandyJs());
            check("absent/node-enabled", CompatArbiter.owns(CompatArbiter.Domain.NODE_PLACEMENT));
            check("absent/no-dedupe", !vehicle());
            System.out.println("TOTAL " + count); return;
        }
        for (int legacy = 1; legacy <= 3; legacy++) {
            CompatArbiter.restore(Map.of());
            ClientConfig.jcmJsChoice = legacy;
            CompatArbiter.adoptLegacyVehicleChoice();
            check("legacy/" + legacy, CompatArbiter.ownerOf(CompatArbiter.Domain.SCRIPT_VEHICLE).equals(
                legacy == 1 ? "other" : legacy == 2 ? "both" : "dedupe"));
        }
        CompatArbiter.restore(Map.of("scriptVehicle", "both", "scriptVehicle@jsblock", "other", "scriptEyecandy@jsblock", "other"));
        ClientConfig.jcmJsChoice = 3;
        CompatArbiter.adoptLegacyVehicleChoice();
        check("modern/preserved", JcmCompat.shouldDisableRteJs());
        check("modern/legacy-mirror", ClientConfig.jcmJsChoice == 1);
        check("modern/eyecandy-override", JcmCompat.shouldDisableRteEyeCandyJs());
        var saved = CompatArbiter.snapshot();
        CompatArbiter.restore(saved);
        check("modern/round-trip", saved.equals(CompatArbiter.snapshot()));
        CompatArbiter.setOwner(CompatArbiter.Domain.SCRIPT_VEHICLE, "dedupe");
        check("modern/clear-override", !CompatArbiter.hasPerModChoice(CompatArbiter.Domain.SCRIPT_VEHICLE));
        CompatArbiter.restore(Map.of("scriptVehicle", "dedupe", "scriptEyecandy@jsblock", "dedupe"));
        var values = MTRContentScripting.manager.instances.values;
        var instance = new Instance();
        values.put(new Key("vehicle", "v1"), instance);
        values.put(new Key("eyecandy", "model", 1, 2, 3), instance);
        check("health/healthy", vehicle());
        int calls = MTRContentScripting.calls;
        check("eyecandy/key", JcmCompat.isEyeCandyTakenByJcm("model", 1, 2, 3));
        check("eyecandy/wrong-position", !JcmCompat.isEyeCandyTakenByJcm("model", 2, 2, 3));
        check("frame/single-scan", calls == MTRContentScripting.calls);
        instance.script.cooling = true;
        check("health/cooldown", !vehicle());
        instance.script.cooling = false; instance.script.error = "broken";
        check("health/exception", !vehicle());
        instance.script.error = null;
        check("health/recovered", vehicle());
        instance.script = null;
        check("health/null-script", !vehicle());
        values.clear();
        var other = new OtherInstance(); other.script.cooling = true;
        values.put(new Key("vehicle", "v1"), other);
        check("health/distinct-instance-class", !vehicle());
        other.script.cooling = false;
        check("health/distinct-recovered", vehicle());
        values.clear(); var broken = new BrokenInstance();
        values.put(new Key("vehicle", "v1"), broken);
        int warnings = RTE.LOGGER.warnings;
        check("health/unknown-keeps-dedupe", vehicle());
        vehicle();
        check("health/warn-once", RTE.LOGGER.warnings == warnings + 1);
        broken.fail = false; JcmCompat.reset();
        check("reload/retry-health", !vehicle());
        values.clear(); values.put(new Key("vehicle", "v1"), new Instance());
        MTRContentScripting.fail = true;
        warnings = RTE.LOGGER.warnings;
        check("bridge/failure-coexists", !vehicle());
        calls = MTRContentScripting.calls;
        check("bridge/backoff", !vehicle() && calls == MTRContentScripting.calls);
        check("bridge/warn-once", RTE.LOGGER.warnings == warnings + 1);
        var retry = JcmCompat.class.getDeclaredField("nextBridgeRetryNanos"); retry.setAccessible(true);
        check("bridge/five-second-delay", retry.getLong(null) - System.nanoTime() > 4_000_000_000L);
        retry.setLong(null, System.nanoTime() - 1);
        MTRContentScripting.fail = false;
        check("bridge/recovered-after-deadline", vehicle());
        values.clear(); JcmCompat.reset();
        check("disconnect/clears-taken", !vehicle());
        check("reset/preserves-choice", JcmCompat.shouldAutoDedupe());
        System.out.println("TOTAL " + count);
    }
}
