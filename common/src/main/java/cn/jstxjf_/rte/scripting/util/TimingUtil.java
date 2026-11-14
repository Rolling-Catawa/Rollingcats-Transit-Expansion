package cn.jstxjf_.rte.scripting.util;

import cn.jstxjf_.rte.scripting.AbstractScriptContext;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class TimingUtil {

    public static volatile double runningSeconds = 0;

    private static final ThreadLocal<double[]> PER_THREAD =
        ThreadLocal.withInitial(() -> new double[2]);

    public static void prepareForScript(AbstractScriptContext scriptContext) {
        final double[] slot = PER_THREAD.get();
        final double now = runningSeconds;
        slot[0] = now;
        slot[1] = now - scriptContext.lastExecuteTime;
        scriptContext.lastExecuteTime = now;
    }

    public static double elapsed() {
        final double value = PER_THREAD.get()[0];
        return value == 0 ? runningSeconds : value;
    }

    public static double delta() {
        return PER_THREAD.get()[1];
    }

    public static String gameTime() {
        return Long.toString(Minecraft.getInstance().level.getGameTime());
    }
}
