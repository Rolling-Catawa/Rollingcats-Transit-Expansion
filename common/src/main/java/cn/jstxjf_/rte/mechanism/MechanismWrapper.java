package cn.jstxjf_.rte.mechanism;

import java.util.ArrayList;
import java.util.List;

public final class MechanismWrapper {

    public static final class Candidate {
        public final String id;
        public final double x1;
        public final double y1;
        public final double z1;
        public final double x2;
        public final double y2;
        public final double z2;
        public final double yaw;

        Candidate(String id, RailPose pose) {
            this.id = id;
            this.x1 = pose.x1();
            this.y1 = pose.y1();
            this.z1 = pose.z1();
            this.x2 = pose.x2();
            this.y2 = pose.y2();
            this.z2 = pose.z2();
            this.yaw = pose.yawDegrees();
        }
    }

    public final class RailHandle {
        public final String id;

        RailHandle(String id) {
            this.id = id;
        }

        public void setSpeedLimit(int kmh) {
            level.setRailSpeedOverride(id, kmh);
        }

        public void clearSpeedLimit() {
            level.setRailSpeedOverride(id, -1);
        }

        public boolean setCollisionLayout(String layout) {
            return level.setRailCollisionLayout(id, layout);
        }

        public void holdRed() {
            level.holdSignal(id, new int[0]);
        }

        public void holdRed(int[] colors) {
            level.holdSignal(id, colors == null ? new int[0] : colors);
        }

        public void releaseSignal() {
            level.releaseSignal(id);
        }

        public boolean isHoldingRed() {
            return level.isHoldingSignal(id);
        }

        public int[] signalColors() {
            return level.signalColors(id);
        }
    }

    private static final double DEFAULT_DEGREES_PER_SECOND = 15;
    private static final double DEFAULT_BLOCKS_PER_SECOND = 2;

    private final RailScriptRuntime.LevelState level;
    private final Mechanism mechanism;
    private final MechanismScriptContext context;

    MechanismWrapper(RailScriptRuntime.LevelState level, Mechanism mechanism, MechanismScriptContext context) {
        this.level = level;
        this.mechanism = mechanism;
        this.context = context;
    }

    public String group() {
        return mechanism.group;
    }

    public String scriptId() {
        return mechanism.scriptId;
    }

    public MechanismTransform getTransform() {
        synchronized (mechanism) {
            return mechanism.sample(System.currentTimeMillis());
        }
    }

    public void setTransform(double yaw, double pitch, double roll, double dx, double dy, double dz) {
        synchronized (mechanism) {
            final RailPose deck = mechanism.candidates.get(mechanism.deckRailId);
            if (deck == null) {
                return;
            }
            mechanism.setImmediate(new MechanismTransform(deck.midX(), deck.midY(), deck.midZ(), yaw, pitch, roll, dx, dy, dz, deck.yawDegrees()));
            mechanism.targetRailId = mechanism.activeRailId;
        }
        level.markDirty(mechanism);
    }

    public String animateTo(double yaw, double pitch, double roll, double dx, double dy, double dz, double degreesPerSecond, double blocksPerSecond) {
        synchronized (mechanism) {
            final RailPose deck = mechanism.candidates.get(mechanism.deckRailId);
            if (deck == null) {
                return MechanismGate.UNKNOWN_RAIL;
            }
            final String decision = MechanismGate.decideRotate(level.anyTrainCrossing(mechanism), true);
            if (!decision.equals(MechanismGate.OK)) {
                return decision;
            }
            final MechanismTransform goal = new MechanismTransform(deck.midX(), deck.midY(), deck.midZ(), yaw, pitch, roll, dx, dy, dz, deck.yawDegrees());
            start(goal, degreesPerSecond, blocksPerSecond, true);
            mechanism.targetRailId = mechanism.activeRailId;
        }
        level.markDirty(mechanism);
        return MechanismGate.OK;
    }

    public String animateAround(double pivotX, double pivotY, double pivotZ, double yaw, double pitch, double roll,
                                double dx, double dy, double dz, double degreesPerSecond, double blocksPerSecond) {
        synchronized (mechanism) {
            final String decision = MechanismGate.decideRotate(level.anyTrainCrossing(mechanism), mechanism.candidates.containsKey(mechanism.deckRailId));
            if (!decision.equals(MechanismGate.OK)) {
                return decision;
            }
            final RailPose deck = mechanism.candidates.get(mechanism.deckRailId);
            start(new MechanismTransform(pivotX, pivotY, pivotZ, yaw, pitch, roll, dx, dy, dz, deck == null ? 0 : deck.yawDegrees()),
                degreesPerSecond, blocksPerSecond, true);
            mechanism.targetRailId = mechanism.activeRailId;
        }
        level.markDirty(mechanism);
        return MechanismGate.OK;
    }

    public String rotateTo(String railId) {
        return rotateTo(railId, DEFAULT_DEGREES_PER_SECOND, DEFAULT_BLOCKS_PER_SECOND, true);
    }

    public String rotateTo(String railId, double degreesPerSecond) {
        return rotateTo(railId, degreesPerSecond, DEFAULT_BLOCKS_PER_SECOND, true);
    }

    public String rotateTo(String railId, double degreesPerSecond, double blocksPerSecond, boolean easeInOut) {
        synchronized (mechanism) {
            final RailPose deck = mechanism.candidates.get(mechanism.deckRailId);
            final RailPose goalPose = railId == null ? null : mechanism.candidates.get(railId);
            final String decision = MechanismGate.decideRotate(level.anyTrainCrossing(mechanism), deck != null && goalPose != null);
            if (!decision.equals(MechanismGate.OK)) {
                return decision;
            }
            start(MechanismTransform.between(deck, goalPose), degreesPerSecond, blocksPerSecond, easeInOut);
            mechanism.targetRailId = railId;
        }
        level.markDirty(mechanism);
        return MechanismGate.OK;
    }

    private void start(MechanismTransform goal, double degreesPerSecond, double blocksPerSecond, boolean easeInOut) {
        final long now = System.currentTimeMillis();
        final MechanismTransform from = mechanism.sample(now);
        final double angular = degreesPerSecond > 0 ? from.angularDistance(goal) / degreesPerSecond : 0;
        final double linear = blocksPerSecond > 0 ? from.linearDistance(goal) / blocksPerSecond : 0;
        mechanism.animate(goal, now, Math.round(Math.max(angular, linear) * 1000), easeInOut);
    }

    public void setActive(String railId) {
        synchronized (mechanism) {
            if (railId == null || !mechanism.candidates.containsKey(railId) || level.anyTrainOn(mechanism)) {
                return;
            }
            mechanism.activeRailId = railId;
            mechanism.deckRailId = railId;
            mechanism.targetRailId = railId;
            mechanism.setImmediate(MechanismTransform.IDENTITY);
        }
        level.markDirty(mechanism);
    }

    public boolean isMoving() {
        return mechanism.isMoving(System.currentTimeMillis());
    }

    public boolean isAligned() {
        final long now = System.currentTimeMillis();
        return MechanismGate.state(mechanism, now).equals("aligned");
    }

    public String state() {
        return MechanismGate.state(mechanism, System.currentTimeMillis());
    }

    public String activeRail() {
        return mechanism.activeRailId;
    }

    public String deckRail() {
        return mechanism.deckRailId;
    }

    public String targetRail() {
        return mechanism.targetRailId;
    }

    public List<Candidate> candidates() {
        final List<Candidate> result = new ArrayList<>();
        synchronized (mechanism) {
            mechanism.candidates.forEach((id, pose) -> result.add(new Candidate(id, pose)));
        }
        return result;
    }

    public List<RailHandle> rails() {
        final List<RailHandle> result = new ArrayList<>();
        for (final String id : mechanism.candidateIds()) {
            result.add(new RailHandle(id));
        }
        return result;
    }

    public void holdRed() {
        for (final String id : mechanism.candidateIds()) {
            level.holdSignal(id, new int[0]);
        }
    }

    public void releaseSignals() {
        for (final String id : mechanism.candidateIds()) {
            level.releaseSignal(id);
        }
    }

    public void lock() {
        mechanism.locked = true;
        level.markDirty(mechanism);
    }

    public void unlock() {
        mechanism.locked = false;
        level.markDirty(mechanism);
    }

    public boolean isLocked() {
        return mechanism.locked;
    }

    public void setApproachRange(double blocks) {
        mechanism.approachRange = Math.max(0, Math.min(2048, blocks));
    }

    public long[] trainsOn() {
        return level.trainsOn(mechanism).stream().mapToLong(Long::longValue).toArray();
    }

    public List<MechanismEventInfo> trainsApproaching() {
        return level.approaching(mechanism);
    }

    public void setRedstoneOutput(int x, int y, int z, int power) {
        level.setRedstoneOutput(x, y, z, power);
    }

    public void send(long trainId, String message) {
        level.sendToTrain(mechanism, trainId, message);
    }

    public void sendToMechanism(String group, String message) {
        level.sendToMechanism(mechanism.group, group, message);
    }

    MechanismScriptContext context() {
        return context;
    }
}
