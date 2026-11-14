package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.RTE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JcmCompat {

    public static final int CHOICE_UNASKED = 0;
    public static final int CHOICE_DISABLE_RTE_JS = 1;
    public static final int CHOICE_IGNORE = 2;
    public static final int CHOICE_AUTO_DEDUPE = 3;

    private static final String SCRIPTING_CLASS = "com.lx862.mtrscripting.mod.impl.mtr.MTRContentScripting";

    private static Boolean jcmLoaded;
    private static Method getScriptManager;
    private static Method getInstanceManager;
    private static Method getInstances;
    private static Field uniqueKeyObjs;
    private static boolean bridgeResolved;
    private static boolean bridgeUsable;
    private static boolean bridgeFailureLogged;
    private static long nextBridgeRetryNanos;
    private static final long BRIDGE_RETRY_NANOS = 5_000_000_000L;

    private static Set<String> takenThisFrame = Collections.emptySet();
    private static Set<String> takenEyeCandyThisFrame = Collections.emptySet();
    private static boolean scannedThisFrame;

    private JcmCompat() {
    }

    public static boolean isJcmLoaded() {
        if (jcmLoaded == null) {
            jcmLoaded = checkModLoaded("jsblock") || checkModLoaded("joban") || checkModLoaded("jcm");
        }
        return jcmLoaded;
    }

    public static boolean shouldDisableRteJs() {
        return isJcmLoaded() && CompatArbiter.OWNER_OTHER.equals(
            CompatArbiter.ownerOf(CompatArbiter.Domain.SCRIPT_VEHICLE, CompatArbiter.MOD_JCM));
    }

    public static boolean shouldAutoDedupe() {
        return isJcmLoaded() && CompatArbiter.OWNER_DEDUPE.equals(
            CompatArbiter.ownerOf(CompatArbiter.Domain.SCRIPT_VEHICLE, CompatArbiter.MOD_JCM));
    }

    public static void beginFrame() {
        scannedThisFrame = false;
    }

    public static boolean isEyeCandyTakenByJcm(String modelId, int x, int y, int z) {
        if (modelId == null || !shouldAutoDedupeEyeCandy()) {
            return false;
        }
        ensureScanned();
        return takenEyeCandyThisFrame.contains(eyeCandyKey(modelId, x, y, z));
    }

    public static boolean shouldAutoDedupeEyeCandy() {
        return isJcmLoaded() && cn.jstxjf_.rte.util.CompatArbiter.OWNER_DEDUPE.equals(
            cn.jstxjf_.rte.util.CompatArbiter.ownerOf(cn.jstxjf_.rte.util.CompatArbiter.Domain.SCRIPT_EYECANDY, CompatArbiter.MOD_JCM));
    }

    public static boolean shouldDisableRteEyeCandyJs() {
        return isJcmLoaded() && cn.jstxjf_.rte.util.CompatArbiter.OWNER_OTHER.equals(
            cn.jstxjf_.rte.util.CompatArbiter.ownerOf(cn.jstxjf_.rte.util.CompatArbiter.Domain.SCRIPT_EYECANDY, CompatArbiter.MOD_JCM));
    }

    private static String eyeCandyKey(String modelId, int x, int y, int z) {
        return modelId + "|" + x + "|" + y + "|" + z;
    }

    private static void ensureScanned() {
        if (scannedThisFrame) {
            return;
        }
        scannedThisFrame = true;
        scanTaken();
    }

    public static boolean isVehicleTakenByJcm(String hexId) {
        if (hexId == null || !shouldAutoDedupe()) {
            return false;
        }
        ensureScanned();
        return takenThisFrame.contains(hexId);
    }

    private static void scanTaken() {
        takenThisFrame = Collections.emptySet();
        takenEyeCandyThisFrame = Collections.emptySet();
        if (!resolveBridge()) {
            return;
        }
        try {
            final Object scriptManager = getScriptManager.invoke(null);
            if (scriptManager == null) {
                return;
            }
            final Object instanceManager = getInstanceManager.invoke(scriptManager);
            if (instanceManager == null) {
                return;
            }
            final Object raw = getInstances.invoke(instanceManager);
            if (!(raw instanceof Map<?, ?> instances) || instances.isEmpty()) {
                return;
            }
            final Set<String> taken = new HashSet<>();
            final Set<String> takenEyeCandy = new HashSet<>();
            for (final Map.Entry<?, ?> instanceEntry : instances.entrySet()) {
                final Object key = instanceEntry.getKey();
                if (key == null) {
                    continue;
                }
                if (!rte$instanceIsHealthy(instanceEntry.getValue())) {
                    continue;
                }
                if (uniqueKeyObjs == null) {
                    uniqueKeyObjs = key.getClass().getDeclaredField("objs");
                    uniqueKeyObjs.setAccessible(true);
                }
                final Object value = uniqueKeyObjs.get(key);
                if (!(value instanceof Object[] parts) || parts.length < 2) {
                    continue;
                }
                if ("vehicle".equals(parts[0]) && parts[1] != null) {
                    taken.add(String.valueOf(parts[1]));
                } else if ("eyecandy".equals(parts[0]) && parts.length >= 5 && parts[1] != null) {
                    takenEyeCandy.add(String.valueOf(parts[1]) + "|" + parts[2] + "|" + parts[3] + "|" + parts[4]);
                }
            }
            takenThisFrame = taken;
            takenEyeCandyThisFrame = takenEyeCandy;
        } catch (Throwable e) {
            bridgeUsable = false;
            reportBridgeFailure(e);
        }
    }

    private record HealthProbe(Method getScript, Method cooldown, Method exception) {}
    private static final Map<Class<?>, HealthProbe> HEALTH_PROBES = new java.util.HashMap<>();
    private static final Set<Class<?>> FAILED_HEALTH_PROBES = new HashSet<>();
    private static final Set<String> REPORTED_UNHEALTHY = new HashSet<>();

    public static void reset() {
        bridgeResolved = false;
        bridgeUsable = false;
        bridgeFailureLogged = false;
        nextBridgeRetryNanos = 0;
        getScriptManager = null;
        getInstanceManager = null;
        getInstances = null;
        uniqueKeyObjs = null;
        HEALTH_PROBES.clear();
        FAILED_HEALTH_PROBES.clear();
        REPORTED_UNHEALTHY.clear();
        takenThisFrame = Collections.emptySet();
        takenEyeCandyThisFrame = Collections.emptySet();
        scannedThisFrame = false;
    }

    private static boolean rte$instanceIsHealthy(Object instance) {
        if (instance == null) return false;
        final Class<?> type = instance.getClass();
        if (FAILED_HEALTH_PROBES.contains(type)) return true;
        try {
            HealthProbe probe = HEALTH_PROBES.get(type);
            if (probe == null) {
                final Method getScript = type.getMethod("getScript");
                final Class<?> parsed = getScript.getReturnType();
                probe = new HealthProbe(getScript, parsed.getMethod("duringFailCooldown"),
                    parsed.getMethod("getCapturedScriptException"));
                HEALTH_PROBES.put(type, probe);
            }
            final Object script = probe.getScript().invoke(instance);
            if (script == null) return false;
            final boolean cooling = Boolean.TRUE.equals(probe.cooldown().invoke(script));
            final Object captured = probe.exception().invoke(script);
            if (!cooling && captured == null) return true;
            rte$reportUnhealthy(script, captured);
            return false;
        } catch (ReflectiveOperationException | RuntimeException e) {
            if (FAILED_HEALTH_PROBES.add(type)) {
                RTE.LOGGER.warn("[JcmCompat] cannot read script health for {}; keeping dedupe until resource reload: {}",
                    type.getName(), String.valueOf(e));
            }
            return true;
        }
    }

    private static void rte$reportUnhealthy(Object parsedScript, Object captured) {
        final String key = String.valueOf(parsedScript);
        if (REPORTED_UNHEALTHY.size() < 256 && REPORTED_UNHEALTHY.add(key)) {
            RTE.LOGGER.info("[JcmCompat] JCM script is failing, RTE takes it back: {}",
                captured == null ? "in fail cooldown" : String.valueOf(captured));
        }
    }

    private static boolean resolveBridge() {
        if (bridgeResolved && (bridgeUsable || System.nanoTime() - nextBridgeRetryNanos < 0)) {
            return bridgeUsable;
        }
        bridgeResolved = true;
        try {
            final Class<?> scripting = Class.forName(SCRIPTING_CLASS);
            getScriptManager = scripting.getMethod("getScriptManager");
            final Class<?> managerClass = getScriptManager.getReturnType();
            getInstanceManager = managerClass.getMethod("getInstanceManager");
            getInstances = getInstanceManager.getReturnType().getMethod("getInstances");
            bridgeUsable = true;
            if (!bridgeFailureLogged) RTE.LOGGER.info("[JcmCompat] auto dedupe bridge resolved");
        } catch (Throwable e) {
            bridgeUsable = false;
            reportBridgeFailure(e);
        }
        return bridgeUsable;
    }

    private static void reportBridgeFailure(Throwable e) {
        nextBridgeRetryNanos = System.nanoTime() + BRIDGE_RETRY_NANOS;
        if (bridgeFailureLogged) {
            return;
        }
        bridgeFailureLogged = true;
        RTE.LOGGER.warn("[JcmCompat] auto dedupe unavailable, falling back to coexist: {}", String.valueOf(e));
    }

    private static boolean checkModLoaded(String modId) {
        try {
            final Class<?> platform = Class.forName("dev.architectury.platform.Platform");
            return (Boolean) platform.getMethod("isModLoaded", String.class).invoke(null, modId);
        } catch (Throwable e) {
            return false;
        }
    }
}
