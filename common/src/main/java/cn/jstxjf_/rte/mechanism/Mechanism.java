package cn.jstxjf_.rte.mechanism;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Mechanism {

    public final String group;
    public String scriptId = "";
    public final Map<String, RailPose> candidates = new LinkedHashMap<>();
    public String activeRailId = "";
    public String deckRailId = "";
    public String targetRailId = "";
    public MechanismTransform start = MechanismTransform.IDENTITY;
    public MechanismTransform target = MechanismTransform.IDENTITY;
    public long animStartMillis;
    public long animDurationMillis;
    public boolean easeInOut = true;
    public boolean locked;
    public String stateJson = "{}";
    public double approachRange = 300;

    public Mechanism(String group) {
        this.group = group;
    }

    public List<String> candidateIds() {
        return new ArrayList<>(candidates.keySet());
    }

    public boolean isMoving(long nowMillis) {
        return animDurationMillis > 0 && nowMillis < animStartMillis + animDurationMillis;
    }

    public double progress(long nowMillis) {
        if (animDurationMillis <= 0) {
            return 1;
        }
        final double t = Math.max(0, Math.min(1, (nowMillis - animStartMillis) / (double) animDurationMillis));
        return easeInOut ? t * t * (3 - 2 * t) : t;
    }

    public MechanismTransform sample(long nowMillis) {
        if (!isMoving(nowMillis)) {
            return target;
        }
        return start.lerp(target, progress(nowMillis));
    }

    public void setImmediate(MechanismTransform transform) {
        start = transform;
        target = transform;
        animStartMillis = 0;
        animDurationMillis = 0;
    }

    public void animate(MechanismTransform to, long nowMillis, long durationMillis, boolean easeInOut) {
        start = sample(nowMillis);
        target = to;
        animStartMillis = nowMillis;
        animDurationMillis = Math.max(0, durationMillis);
        this.easeInOut = easeInOut;
    }

    public void finishAnimation() {
        start = target;
        animDurationMillis = 0;
    }

    public long remainingMillis(long nowMillis) {
        return isMoving(nowMillis) ? animStartMillis + animDurationMillis - nowMillis : 0;
    }
}
