package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;

public final class RenderDiagnostics {

    private static final int BURST_LIMIT = 60;

    private static boolean armed;
    private static int emitted;
    private static long armedAt;
    private static boolean finished;

    private RenderDiagnostics() {
    }

    public static boolean allow() {
        if (!ClientConfig.enableRenderDiagnostics) {
            armed = false;
            return false;
        }
        if (!armed) {
            armed = true;
            emitted = 0;
            finished = false;
            armedAt = System.currentTimeMillis();
            RTE.LOGGER.info("[RteDiag] ARMED - capture starts in 2 seconds, close this screen and look at the problem");
        }
        if (System.currentTimeMillis() - armedAt < 2000) {
            return false;
        }
        if (emitted == 0) {
            logCamera("CAPTURE START");
        }
        if (emitted >= BURST_LIMIT) {
            if (!finished) {
                finished = true;
                logCamera("CAPTURE END");
                RTE.LOGGER.info("[RteDiag] CAPTURE COMPLETE - untick and re-tick the checkbox to capture again");
            }
            return false;
        }
        emitted++;
        return true;
    }

    public static void log(String format, Object... args) {
        RTE.LOGGER.info("[RteDiag] " + format, args);
    }

    public static void logCamera(String tag) {
        try {
            final net.minecraft.client.Camera cam =
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera();
            final net.minecraft.world.phys.Vec3 p = cam.getPosition();
            RTE.LOGGER.info("[RteDiag] === {} === camera=({}, {}, {}) yaw={} pitch={}",
                tag,
                String.format("%.2f", p.x), String.format("%.2f", p.y), String.format("%.2f", p.z),
                String.format("%.1f", cam.getYRot()), String.format("%.1f", cam.getXRot()));
        } catch (Throwable ignored) {
        }
    }

    public static String translationOf(Object graphicsHolder) {
        try {
            java.lang.reflect.Field f = null;
            Class<?> c = graphicsHolder.getClass();
            while (c != null && f == null) {
                try {
                    f = c.getDeclaredField("matrixStack");
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
            if (f == null) {
                return "no-matrixStack";
            }
            f.setAccessible(true);
            final com.mojang.blaze3d.vertex.PoseStack stack =
                (com.mojang.blaze3d.vertex.PoseStack) f.get(graphicsHolder);
            final org.joml.Matrix4f m = stack.last().pose();
            final double mag = Math.sqrt(m.m30() * m.m30() + m.m31() * m.m31() + m.m32() * m.m32());
            return String.format("(%.2f, %.2f, %.2f) dist=%.2f", m.m30(), m.m31(), m.m32(), mag);
        } catch (Throwable t) {
            return "error:" + t;
        }
    }
}
