package cn.jstxjf_.rte.mechanism;

public final class MechanismGate {

    public static final String OK = "ok";
    public static final String TRAIN_CROSSING = "train_crossing";
    public static final String UNKNOWN_RAIL = "unknown_rail";

    private MechanismGate() {
    }

    public static boolean blocks(Mechanism mechanism, String railHexId, boolean vehicleOnDeck, long nowMillis) {
        if (mechanism == null || railHexId == null || !mechanism.candidates.containsKey(railHexId) || vehicleOnDeck) {
            return false;
        }
        return mechanism.locked || mechanism.isMoving(nowMillis) || !railHexId.equals(mechanism.activeRailId)
            || !railHexId.equals(mechanism.deckRailId) || !mechanism.sample(nowMillis).isAligned();
    }

    public static String decideRotate(boolean anyTrainCrossing, boolean railKnown) {
        if (!railKnown) {
            return UNKNOWN_RAIL;
        }
        return anyTrainCrossing ? TRAIN_CROSSING : OK;
    }

    public static String state(Mechanism mechanism, long nowMillis) {
        if (mechanism.isMoving(nowMillis)) {
            return "moving";
        }
        if (mechanism.locked) {
            return "locked";
        }
        return mechanism.sample(nowMillis).isAligned() && mechanism.activeRailId.equals(mechanism.deckRailId) ? "aligned" : "misaligned";
    }
}
