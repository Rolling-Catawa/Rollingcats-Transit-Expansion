package cn.jstxjf_.rte.mechanism;

public record MechanismEvent(Type type, long trainId, double distance, String rail) {

    public enum Type {
        APPROACH,
        ENTER,
        LEAVE,
        STOP
    }
}
