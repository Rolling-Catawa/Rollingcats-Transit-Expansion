package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.network.PacketPlaceTrackside;
import cn.jstxjf_.rte.track.RailTrackPath;
import cn.jstxjf_.rte.track.TrackPose;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mod.Init;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.List;

public final class TracksidePlacer {

    private static final int ANCHOR_SEARCH_RADIUS = 10;
    private static final double ADAPTIVE_SPACING_MIN_INTERVAL = 20;

    public record ChainPose(Vec3 position, double[] tangent, double[] right) {
    }

    private record Anchor(net.minecraft.core.BlockPos blockPos, double chainS, double lateral, double height,
                          double yawRelative, net.minecraft.nbt.CompoundTag template) {
    }

    public record ChainGeometry(List<double[]> segmentInfos, List<RailTrackPath> paths, List<Boolean> forwardFlags, double totalLength) {
    }

    public static int arrayAlongChain(BlockPos startBlockPos, BlockPos endBlockPos, double interval) {
        final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || interval < 1) {
            return -1;
        }

        final MinecraftClientData data = MinecraftClientData.getInstance();
        final List<Position> chain = RailChainSmoother.walkChain(data,
            Init.blockPosToPosition(startBlockPos), Init.blockPosToPosition(endBlockPos));
        if (chain.size() < 2) {
            return -1;
        }

        final ChainGeometry geometry = buildGeometry(data, chain);
        if (geometry == null || geometry.totalLength() < interval) {
            return -1;
        }

        final List<BlockEyeCandy.BlockEntityEyeCandy> candidates = new ArrayList<>();
        final List<net.minecraft.core.BlockPos> candidatePositions = new ArrayList<>();
        final net.minecraft.core.BlockPos playerPos = ((net.minecraft.world.entity.player.Player) minecraft.player).blockPosition();
        for (int dx = -ANCHOR_SEARCH_RADIUS; dx <= ANCHOR_SEARCH_RADIUS; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -ANCHOR_SEARCH_RADIUS; dz <= ANCHOR_SEARCH_RADIUS; dz++) {
                    final net.minecraft.core.BlockPos probe = playerPos.offset(dx, dy, dz);
                    final net.minecraft.world.level.block.entity.BlockEntity blockEntity = minecraft.level.getBlockEntity(probe);
                    if (blockEntity instanceof BlockEyeCandy.BlockEntityEyeCandy eyeCandy && eyeCandy.prefabId != null && !eyeCandy.prefabId.isEmpty()) {
                        candidates.add(eyeCandy);
                        candidatePositions.add(probe);
                    }
                }
            }
        }
        if (candidates.isEmpty()) {
            return -2;
        }

        final List<Integer> order = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            order.add(i);
        }
        order.sort((a, b) -> Double.compare(candidatePositions.get(a).distSqr(playerPos), candidatePositions.get(b).distSqr(playerPos)));

        final List<Anchor> anchors = new ArrayList<>();
        for (final int i : order) {
            if (anchors.size() >= 2) {
                break;
            }
            final Anchor anchor = resolveAnchor(geometry, candidates.get(i), candidatePositions.get(i));
            if (anchor != null) {
                anchors.add(anchor);
            }
        }
        if (anchors.isEmpty()) {
            return -2;
        }
        anchors.sort((a, b) -> Double.compare(a.chainS(), b.chainS()));
        final Anchor primary = anchors.get(0);
        final Anchor secondary = anchors.size() > 1 ? anchors.get(1) : null;

        final List<double[]> msdTemplates = new ArrayList<>();
        final List<String> msdTemplateTags = new ArrayList<>();
        long msdSourceStatePos = 0;
        final List<double[]> msdTemplatesB = new ArrayList<>();
        final List<String> msdTemplateTagsB = new ArrayList<>();
        final List<List<long[]>> msdColumns = new ArrayList<>();
        if (MsdBridge.isLoaded()) {
            final List<Object[]> nodes = MsdBridge.findCatenaryNodesNear(minecraft.level, primary.blockPos(), 6);
            for (final Object[] node : nodes) {
                final net.minecraft.core.BlockPos nodePos = (net.minecraft.core.BlockPos) node[0];
                final double[] projected = projectToChain(geometry, nodePos.getX() + 0.5, nodePos.getY(), nodePos.getZ() + 0.5);
                if (projected == null) {
                    continue;
                }
                msdTemplates.add(new double[]{projected[0] - primary.chainS(), projected[1], projected[2]});
                msdTemplateTags.add((String) node[1]);
                if (msdSourceStatePos == 0) {
                    msdSourceStatePos = nodePos.asLong();
                }
                final List<long[]> column = new ArrayList<>();                column.add(new long[]{Math.round(projected[0] * 1000), nodePos.asLong()});
                msdColumns.add(column);
            }

            if (secondary != null && !msdTemplates.isEmpty()) {
                final List<Object[]> nodesB = MsdBridge.findCatenaryNodesNear(minecraft.level, secondary.blockPos(), 6);
                final List<double[]> rawB = new ArrayList<>();
                final List<String> rawTagsB = new ArrayList<>();
                final List<Long> rawPosB = new ArrayList<>();
                for (final Object[] node : nodesB) {
                    final net.minecraft.core.BlockPos nodePos = (net.minecraft.core.BlockPos) node[0];
                    final double[] projected = projectToChain(geometry, nodePos.getX() + 0.5, nodePos.getY(), nodePos.getZ() + 0.5);
                    if (projected == null) {
                        continue;
                    }
                    rawB.add(new double[]{projected[0] - secondary.chainS(), projected[1], projected[2]});
                    rawTagsB.add((String) node[1]);
                    rawPosB.add(nodePos.asLong());
                }
                for (int t = 0; t < msdTemplates.size(); t++) {
                    int bestMatch = -1;
                    double bestHeightGap = 2.0;
                    for (int b = 0; b < rawB.size(); b++) {
                        final double heightGap = Math.abs(rawB.get(b)[2] - msdTemplates.get(t)[2]);
                        if (heightGap < bestHeightGap) {
                            bestHeightGap = heightGap;
                            bestMatch = b;
                        }
                    }
                    if (bestMatch >= 0) {
                        msdTemplatesB.add(rawB.get(bestMatch));
                        msdTemplateTagsB.add(rawTagsB.get(bestMatch));
                        msdColumns.get(t).add(new long[]{Math.round((secondary.chainS() + rawB.get(bestMatch)[0]) * 1000), rawPosB.get(bestMatch)});
                    } else {
                        msdTemplatesB.add(msdTemplates.get(t));
                        msdTemplateTagsB.add(msdTemplateTags.get(t));
                    }
                }
            }
        }

        final boolean adaptive = interval >= ADAPTIVE_SPACING_MIN_INTERVAL;
        final List<Object[]> placements = new ArrayList<>();
        final List<long[]> msdPlacements = new ArrayList<>();
        final List<String> msdPlacementTags = new ArrayList<>();
        double s = primary.chainS() % interval;
        int index = 0;
        while (s <= geometry.totalLength() + 1e-6) {
            final double clamped = Math.min(s, geometry.totalLength());
            final boolean nearAnchor = Math.abs(clamped - primary.chainS()) < interval / 2
                || (secondary != null && Math.abs(clamped - secondary.chainS()) < interval / 2);
            if (!nearAnchor) {
                final boolean useSecondary = secondary != null && index % 2 == 1;
                final Anchor anchor = useSecondary ? secondary : primary;
                final Object[] placement = buildPlacement(geometry, anchor, clamped);
                if (placement != null) {
                    placements.add(placement);
                    final List<double[]> activeTemplates = useSecondary && !msdTemplatesB.isEmpty() ? msdTemplatesB : msdTemplates;
                    final List<String> activeTags = useSecondary && !msdTemplatesB.isEmpty() ? msdTemplateTagsB : msdTemplateTags;
                    for (int t = 0; t < activeTemplates.size(); t++) {
                        final double[] template = activeTemplates.get(t);
                        final double nodeS = Math.max(0, Math.min(geometry.totalLength(), clamped + template[0]));
                        final ChainPose nodePose = poseAt(geometry, nodeS);
                        if (nodePose == null) {
                            continue;
                        }
                        final double nx = nodePose.position().x + nodePose.right()[0] * template[1];
                        final double ny = nodePose.position().y + template[2];
                        final double nz = nodePose.position().z + nodePose.right()[1] * template[1];
                        final net.minecraft.core.BlockPos nodeBlockPos = new net.minecraft.core.BlockPos(
                            (int) Math.floor(nx), (int) Math.floor(ny + 1e-6), (int) Math.floor(nz));
                        msdPlacements.add(new long[]{nodeBlockPos.asLong()});
                        msdPlacementTags.add(activeTags.get(t));
                        msdColumns.get(t).add(new long[]{Math.round(nodeS * 1000), nodeBlockPos.asLong()});
                    }
                }
            }
            index++;
            s += adaptive ? interval * spacingFactor(geometry, clamped) : interval;
        }
        if (placements.isEmpty()) {
            return 0;
        }

        PacketPlaceTrackside.sendPlaceC2S(
            new BlockPos(primary.blockPos().getX(), primary.blockPos().getY(), primary.blockPos().getZ()).asLong(), placements);

        if (!msdPlacements.isEmpty() && msdSourceStatePos != 0) {
            final List<long[]> links = new ArrayList<>();
            for (final List<long[]> column : msdColumns) {
                column.sort((a, b) -> Long.compare(a[0], b[0]));
                for (int i = 0; i < column.size() - 1; i++) {
                    final long posA = column.get(i)[1];
                    final long posB = column.get(i + 1)[1];
                    if (posA != posB) {
                        links.add(new long[]{posA, posB});
                    }
                }
            }
            cn.jstxjf_.rte.network.PacketPlaceCatenary.sendPlaceC2S(
                msdSourceStatePos, catenaryTypeName, msdPlacements, msdPlacementTags, links);
        }
        return placements.size();
    }

    public static String catenaryTypeName = "ELECTRIC";

    private static double[] projectToChain(ChainGeometry geometry, double worldX, double worldY, double worldZ) {
        double bestS = -1;
        double bestDistance = Double.MAX_VALUE;
        double bestHorizontalSq = Double.MAX_VALUE;
        for (double s = 0; s <= geometry.totalLength(); s += 0.5) {
            final ChainPose pose = poseAt(geometry, s);
            if (pose == null) {
                continue;
            }
            final double dx = worldX - pose.position().x;
            final double dy = worldY - pose.position().y;
            final double dz = worldZ - pose.position().z;
            final double horizontalSq = dx * dx + dz * dz;
            final double distance = horizontalSq + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestHorizontalSq = horizontalSq;
                bestS = s;
            }
        }
        if (bestS < 0 || bestHorizontalSq > 20 * 20) {
            return null;
        }
        final ChainPose pose = poseAt(geometry, bestS);
        if (pose == null) {
            return null;
        }
        final double lateral = (worldX - pose.position().x) * pose.right()[0] + (worldZ - pose.position().z) * pose.right()[1];
        final double height = worldY - pose.position().y;
        return new double[]{bestS, lateral, height};
    }

    public static ChainGeometry buildGeometry(MinecraftClientData data, List<Position> chain) {
        final List<double[]> segmentInfos = new ArrayList<>();
        final List<RailTrackPath> paths = new ArrayList<>();
        final List<Boolean> forwardFlags = new ArrayList<>();
        double totalLength = 0;
        for (int i = 0; i < chain.size() - 1; i++) {
            final Rail rail = RailChainSmoother.railBetween(data, chain.get(i), chain.get(i + 1));
            if (rail == null) {
                continue;
            }
            final RailTrackPath path = new RailTrackPath(rail);
            final double length = path.length();
            if (!Double.isFinite(length) || length < 0.5) {
                continue;
            }
            final Vec3 sample0 = path.sampleByDistance(0).position();
            final Position nodeA = chain.get(i);
            final double dxA = sample0.x - nodeA.getX();
            final double dzA = sample0.z - nodeA.getZ();
            final Vec3 sampleEnd = path.sampleByDistance(length).position();
            final double dxB = sampleEnd.x - nodeA.getX();
            final double dzB = sampleEnd.z - nodeA.getZ();
            final boolean forward = dxA * dxA + dzA * dzA <= dxB * dxB + dzB * dzB;
            segmentInfos.add(new double[]{totalLength, length});
            paths.add(path);
            forwardFlags.add(forward);
            totalLength += length;
        }
        return paths.isEmpty() ? null : new ChainGeometry(segmentInfos, paths, forwardFlags, totalLength);
    }

    private static Anchor resolveAnchor(ChainGeometry geometry, BlockEyeCandy.BlockEntityEyeCandy eyeCandy, net.minecraft.core.BlockPos blockPos) {
        final double worldX = blockPos.getX() + 0.5 + eyeCandy.translateX;
        final double worldY = blockPos.getY() + eyeCandy.translateY;
        final double worldZ = blockPos.getZ() + 0.5 + eyeCandy.translateZ;

        double bestS = 0;
        double bestDistance = Double.MAX_VALUE;
        double bestHorizontalSq = Double.MAX_VALUE;
        for (double s = 0; s <= geometry.totalLength(); s += 0.5) {
            final ChainPose pose = poseAt(geometry, s);
            if (pose == null) {
                continue;
            }
            final double dx = worldX - pose.position().x;
            final double dy = worldY - pose.position().y;
            final double dz = worldZ - pose.position().z;
            final double horizontalSq = dx * dx + dz * dz;
            final double distance = horizontalSq + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestHorizontalSq = horizontalSq;
                bestS = s;
            }
        }
        if (bestHorizontalSq > 20 * 20) {
            return null;
        }
        final ChainPose pose = poseAt(geometry, bestS);
        if (pose == null) {
            return null;
        }
        final double lateral = (worldX - pose.position().x) * pose.right()[0] + (worldZ - pose.position().z) * pose.right()[1];
        final double height = worldY - pose.position().y;
        final double trackYaw = Math.atan2(pose.tangent()[1], pose.tangent()[0]);
        final double yawRelative = eyeCandy.rotateY - trackYaw;

        final CompoundTag template = new CompoundTag();
        eyeCandy.writeCompoundTag(template);
        return new Anchor(blockPos, bestS, lateral, height, yawRelative, template.data);
    }

    private static Object[] buildPlacement(ChainGeometry geometry, Anchor anchor, double chainS) {
        final ChainPose pose = poseAt(geometry, chainS);
        if (pose == null) {
            return null;
        }
        final double worldX = pose.position().x + pose.right()[0] * anchor.lateral();
        final double worldY = pose.position().y + anchor.height();
        final double worldZ = pose.position().z + pose.right()[1] * anchor.lateral();
        final int bx = (int) Math.floor(worldX);
        final int by = (int) Math.floor(worldY + 1e-6);
        final int bz = (int) Math.floor(worldZ);

        final net.minecraft.nbt.CompoundTag tag = anchor.template().copy();
        tag.putFloat("translateX", (float) (worldX - bx - 0.5));
        tag.putFloat("translateY", (float) (worldY - by));
        tag.putFloat("translateZ", (float) (worldZ - bz - 0.5));
        final double trackYaw = Math.atan2(pose.tangent()[1], pose.tangent()[0]);
        tag.putFloat("rotateY", (float) wrap(trackYaw + anchor.yawRelative()));
        return new Object[]{new BlockPos(bx, by, bz).asLong(), tag.toString()};
    }

    public static double spacingFactor(ChainGeometry geometry, double chainS) {
        final ChainPose before = poseAt(geometry, Math.max(0, chainS - 10));
        final ChainPose middle = poseAt(geometry, chainS);
        final ChainPose after = poseAt(geometry, Math.min(geometry.totalLength(), chainS + 10));
        if (before == null || middle == null || after == null) {
            return 1;
        }
        if (sagitta(before.position(), middle.position(), after.position()) < 0.05) {
            return 1;
        }
        final Double radius = RailGeometryAnalysis.circumradiusXZ(before.position(), middle.position(), after.position());
        if (radius == null || radius >= 800) {
            return 1;
        }
        if (radius >= 400) {
            return 0.8;
        }
        return 0.65;
    }

    private static double sagitta(cn.jstxjf_.rte.data.BezierCurve.Vec3 before,
                                 cn.jstxjf_.rte.data.BezierCurve.Vec3 middle,
                                 cn.jstxjf_.rte.data.BezierCurve.Vec3 after) {
        final double cx = after.x - before.x;
        final double cz = after.z - before.z;
        final double chord = Math.sqrt(cx * cx + cz * cz);
        if (chord < 1e-6) {
            return 0;
        }
        final double mx = middle.x - before.x;
        final double mz = middle.z - before.z;
        return Math.abs(mx * cz - mz * cx) / chord;
    }

    public static ChainPose poseAt(ChainGeometry geometry, double chainDistance) {
        for (int i = 0; i < geometry.paths().size(); i++) {
            final double start = geometry.segmentInfos().get(i)[0];
            final double length = geometry.segmentInfos().get(i)[1];
            if (chainDistance < start - 1e-6 || chainDistance > start + length + 1e-6) {
                continue;
            }
            final double local = Math.max(0, Math.min(length, chainDistance - start));
            final boolean forward = geometry.forwardFlags().get(i);
            final TrackPose pose = geometry.paths().get(i).sampleByDistance(forward ? local : length - local);
            final double sign = forward ? 1 : -1;
            final double tx = pose.tangent().x * sign;
            final double tz = pose.tangent().z * sign;
            final double horizontal = Math.sqrt(tx * tx + tz * tz);
            if (horizontal < 1e-6) {
                return null;
            }
            final double ntx = tx / horizontal;
            final double ntz = tz / horizontal;
            return new ChainPose(pose.position(), new double[]{ntx, ntz}, new double[]{-ntz, ntx});
        }
        return null;
    }

    private static double wrap(double rad) {
        rad = rad % (2 * Math.PI);
        if (rad <= -Math.PI) {
            rad += 2 * Math.PI;
        } else if (rad > Math.PI) {
            rad -= 2 * Math.PI;
        }
        return rad;
    }

    private TracksidePlacer() {
    }
}
