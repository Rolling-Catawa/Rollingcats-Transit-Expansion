package cn.jstxjf_.rte.data;

import java.util.ArrayList;
import java.util.List;

public final class MsdBridge {

    private MsdBridge() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static List<Object[]> findCatenaryNodesNear(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos center, int radius) {
        return new ArrayList<>();
    }

    public static void serverPlaceAndLink(net.minecraft.server.level.ServerLevel level, List<long[]> nodePlacements,
                                          List<String> nodeTags, long sourceStatePos, List<long[]> links, String typeName) {
    }

    public static boolean verifyPlannedSpan(int ax, int ay, int az, double aox, double aoy, double aoz,
                                            int bx, int by, int bz, double box, double boy, double boz) {
        return false;
    }

    public static int serverPlacePlanned(net.minecraft.server.level.ServerLevel level,
                                         List<long[]> placements, List<double[]> offsets,
                                         List<String> blockIds, List<Integer> facings,
                                         List<Double> rotations, List<long[]> links, String typeName) {
        return 0;
    }
}
