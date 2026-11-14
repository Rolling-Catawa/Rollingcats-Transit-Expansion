package cn.jstxjf_.rte.mechanism;

import org.mtr.core.data.PathData;

import java.util.List;

public final class MechanismVehicles {

    private MechanismVehicles() {
    }

    public static int indexAt(List<PathData> path, double distance) {
        int low = 0;
        int high = path.size() - 1;
        while (low < high) {
            final int mid = (low + high) >>> 1;
            if (path.get(mid).getEndDistance() <= distance) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return Math.max(0, low);
    }

    public static String railOn(List<PathData> path, int head, String group) {
        for (int i = Math.min(head, path.size() - 1); i >= 0; i--) {
            final String id = path.get(i).getRail().getHexId();
            final Mechanism mechanism = RailScriptRuntime.GATE_INDEX.get(id);
            if (mechanism != null && mechanism.group.equals(group)) {
                return id;
            }
        }
        return "";
    }
}
