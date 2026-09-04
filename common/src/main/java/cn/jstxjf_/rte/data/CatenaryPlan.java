package cn.jstxjf_.rte.data;

import org.mtr.core.data.Position;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mod.Init;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class CatenaryPlan {

    public static final String[] NODE_BLOCKS = {
        "catenary_with_long_counterweight", "catenary_with_short_counterweight",
        "catenary_with_long", "catenary_with_long_top",
        "catenary_with_short", "catenary_with_short_top",
        "catenary_node", "catenary_node_style_2", "short_catenary_node", "short_catenary_node_style_2",
        "new_catenary_node", "rigid_catenary_node", "electric_node", "trans_catenary_node"
    };

    public static final double MAX_SPAN_METERS = 120;

    public static final String[] CATENARY_TYPES = {
        "CATENARY", "ELECTRIC", "RIGID_CATENARY", "RIGID_SOFT_CATENARY"
    };

    public static final class Params {
        public int nodeBlockIndex = 0;
        public int catenaryTypeIndex = 0;
        public double span = 50;
        public double stagger = 0.3;
        public double height = 5;
        public double lateral = 0;
        public boolean startPositive = true;
        public boolean curveTighten = true;
        public boolean poleOnRight = true;
        public boolean autoMirror = true;
        public double startOffset = 0;
        public double blockLateral = 0;
        public double blockVertical = 0;
        public double blockLongitudinal = 0;
        public double yawOffset = 0;
    }

    public static final class Node {
        public final double chainS;
        public final int blockX;
        public final int blockY;
        public final int blockZ;
        public final double wireX;
        public final double wireY;
        public final double wireZ;
        public final double offsetX;
        public final double offsetY;
        public final double offsetZ;
        public final boolean positive;
        public final String blockId;
        public final int facing;
        public final double rotationY;
        public double spanToNext;
        public boolean valid = true;

        Node(double chainS, int blockX, int blockY, int blockZ,
             double wireX, double wireY, double wireZ,
             double offsetX, double offsetY, double offsetZ, boolean positive,
             String blockId, int facing, double rotationY) {
            this.chainS = chainS;
            this.blockX = blockX;
            this.blockY = blockY;
            this.blockZ = blockZ;
            this.wireX = wireX;
            this.wireY = wireY;
            this.wireZ = wireZ;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.positive = positive;
            this.blockId = blockId;
            this.facing = facing;
            this.rotationY = rotationY;
        }
    }

    public static final class Plan {
        public final List<Node> nodes = new ArrayList<>();
        public final List<double[]> centerLine = new ArrayList<>();
        public double totalLength;
        public int invalidSpans;
        public String failure;
    }

    private CatenaryPlan() {
    }

    public static Plan build(BlockPos startBlockPos, BlockPos endBlockPos, Params params) {
        final Plan plan = new Plan();
        if (params.span < 4) {
            plan.failure = "span_too_small";
            return plan;
        }

        final MinecraftClientData data = MinecraftClientData.getInstance();
        final List<Position> chain = RailChainSmoother.walkChain(data,
            Init.blockPosToPosition(startBlockPos), Init.blockPosToPosition(endBlockPos));
        if (chain.size() < 2) {
            plan.failure = "no_chain";
            return plan;
        }

        final TracksidePlacer.ChainGeometry geometry = TracksidePlacer.buildGeometry(data, chain);
        if (geometry == null || geometry.totalLength() < 1) {
            plan.failure = "no_chain";
            return plan;
        }
        plan.totalLength = geometry.totalLength();

        for (double s = 0; s <= geometry.totalLength(); s += 1) {
            final TracksidePlacer.ChainPose pose = TracksidePlacer.poseAt(geometry, s);
            if (pose != null) {
                plan.centerLine.add(new double[]{pose.position().x, pose.position().y, pose.position().z});
            }
        }
        if (plan.centerLine.isEmpty()) {
            plan.failure = "no_chain";
            return plan;
        }

        boolean positive = params.startPositive;
        double s = Math.max(0, Math.min(params.startOffset, geometry.totalLength()));
        while (s <= geometry.totalLength() + 1e-6) {
            final TracksidePlacer.ChainPose pose = TracksidePlacer.poseAt(geometry, s);
            if (pose == null) {
                break;
            }
            final Node node = makeNode(pose, s, positive, params);
            if (node != null) {
                plan.nodes.add(node);
                positive = !positive;
            }

            final double factor = params.curveTighten ? TracksidePlacer.spacingFactor(geometry, s) : 1;
            final double step = Math.max(4, params.span * factor);
            if (s >= geometry.totalLength() - 1e-6) {
                break;
            }
            s = Math.min(s + step, geometry.totalLength());
        }

        for (int i = 0; i + 1 < plan.nodes.size(); i++) {
            final Node a = plan.nodes.get(i);
            final Node b = plan.nodes.get(i + 1);
            a.spanToNext = b.chainS - a.chainS;
            final double spanX = (b.blockX + b.offsetX) - (a.blockX + a.offsetX);
            final double spanY = (b.blockY + b.offsetY) - (a.blockY + a.offsetY);
            final double spanZ = (b.blockZ + b.offsetZ) - (a.blockZ + a.offsetZ);
            final double actualSpan = Math.sqrt(spanX * spanX + spanY * spanY + spanZ * spanZ);
            final boolean withinRange = actualSpan > 0.5 && actualSpan <= MAX_SPAN_METERS;
            final boolean ok = withinRange && MsdBridge.verifyPlannedSpan(
                a.blockX, a.blockY, a.blockZ, a.offsetX, a.offsetY, a.offsetZ,
                b.blockX, b.blockY, b.blockZ, b.offsetX, b.offsetY, b.offsetZ);
            if (!ok) {
                a.valid = false;
                plan.invalidSpans++;
            }
        }
        return plan;
    }

    private static Node makeNode(TracksidePlacer.ChainPose pose, double chainS, boolean positive, Params params) {
        final var center = pose.position();
        final double[] right = pose.right();
        final double sign = positive ? 1 : -1;
        final double lateralTotal = params.lateral + sign * params.stagger;

        final double wireX = center.x + right[0] * lateralTotal;
        final double wireY = center.y + params.height;
        final double wireZ = center.z + right[1] * lateralTotal;

        final double[] tangent = pose.tangent();
        final double poleSign = params.poleOnRight ? 1 : -1;
        final double anchorX = wireX + right[0] * params.blockLateral * poleSign + tangent[0] * params.blockLongitudinal;
        final double anchorY = wireY + params.blockVertical;
        final double anchorZ = wireZ + right[1] * params.blockLateral * poleSign + tangent[1] * params.blockLongitudinal;

        if (!Double.isFinite(wireX) || !Double.isFinite(wireY) || !Double.isFinite(wireZ)
            || !Double.isFinite(anchorX) || !Double.isFinite(anchorY) || !Double.isFinite(anchorZ)) {
            return null;
        }

        final int blockX = (int) Math.floor(anchorX);
        final int blockY = (int) Math.floor(anchorY);
        final int blockZ = (int) Math.floor(anchorZ);

        final double offsetX = wireX - (blockX + 0.5);
        final double offsetY = wireY - blockY;
        final double offsetZ = wireZ - (blockZ + 0.5);

        return new Node(chainS, blockX, blockY, blockZ, wireX, wireY, wireZ,
            offsetX, offsetY, offsetZ, positive,
            resolveBlockId(params, positive), resolveFacing(right, params.poleOnRight),
            resolveRotationY(right, params.poleOnRight) + params.yawOffset);
    }

    private static String resolveBlockId(Params params, boolean positive) {
        final String base = NODE_BLOCKS[clamp(params.nodeBlockIndex, NODE_BLOCKS.length)];
        if (params.autoMirror && !params.poleOnRight && base.endsWith("_counterweight")) {
            return base + "_mirror";
        }
        return base;
    }

    private static double idealYaw(double[] right, boolean poleOnRight) {
        final double dirX = poleOnRight ? -right[0] : right[0];
        final double dirZ = poleOnRight ? -right[1] : right[1];
        double degrees = Math.toDegrees(Math.atan2(-dirX, dirZ));
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees >= 360) {
            degrees -= 360;
        }
        return degrees;
    }

    private static double resolveRotationY(double[] right, boolean poleOnRight) {
        double delta = idealYaw(right, poleOnRight) - resolveFacing(right, poleOnRight) * 90.0;
        while (delta <= -180) {
            delta += 360;
        }
        while (delta > 180) {
            delta -= 360;
        }
        return delta;
    }

    private static int resolveFacing(double[] right, boolean poleOnRight) {
        final int index = (int) Math.round(idealYaw(right, poleOnRight) / 90.0);
        return ((index % 4) + 4) % 4;
    }

    public static void apply(Plan plan, Params params) {
        if (plan == null || plan.nodes.size() < 2) {
            return;
        }
        final List<long[]> placements = new ArrayList<>();
        final List<double[]> offsets = new ArrayList<>();
        final List<String> blockIds = new ArrayList<>();
        final List<Integer> facings = new ArrayList<>();
        final List<Double> rotations = new ArrayList<>();
        for (final Node node : plan.nodes) {
            placements.add(new long[]{net.minecraft.core.BlockPos.asLong(node.blockX, node.blockY, node.blockZ)});
            offsets.add(new double[]{node.offsetX, node.offsetY, node.offsetZ});
            blockIds.add(node.blockId);
            facings.add(node.facing);
            rotations.add(node.rotationY);
        }
        final List<long[]> links = new ArrayList<>();
        for (int i = 0; i + 1 < plan.nodes.size(); i++) {
            if (!plan.nodes.get(i).valid) {
                continue;
            }
            links.add(new long[]{placements.get(i)[0], placements.get(i + 1)[0]});
        }
        cn.jstxjf_.rte.network.PacketPlanCatenary.sendPlanC2S(
            CATENARY_TYPES[clamp(params.catenaryTypeIndex, CATENARY_TYPES.length)],
            placements, offsets, blockIds, facings, rotations, links);
    }

    private static int clamp(int value, int size) {
        return value < 0 ? 0 : value >= size ? size - 1 : value;
    }
}
