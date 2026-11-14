package cn.jstxjf_.rte.gfxext.multipart.mi;

import net.minecraft.client.Minecraft;
import org.mtr.mapping.holder.MinecraftClient;

public class MiScheduleHelper {

    private long lastRenderedMillis;

    public float currentFrameTime;
    private float targetFrameTime;

    public void elapse() {
        final long currentMillis = System.currentTimeMillis();
        final float secondsElapsed = Minecraft.getInstance().isPaused() || lastRenderedMillis == 0 ? 0 : (currentMillis - lastRenderedMillis) / 1000F;
        currentFrameTime = Math.min(currentFrameTime + secondsElapsed, targetFrameTime);
        lastRenderedMillis = currentMillis;
    }

    public void play(float begin, float end) {
        if (targetFrameTime != end) {
            targetFrameTime = end;
            currentFrameTime = begin;
        } else {
            currentFrameTime = Math.min(Math.max(currentFrameTime, begin), targetFrameTime);
        }
    }

    public void pause() {
        targetFrameTime = currentFrameTime;
    }

    public void stop() {
        targetFrameTime = 0;
        currentFrameTime = 0;
    }
}
