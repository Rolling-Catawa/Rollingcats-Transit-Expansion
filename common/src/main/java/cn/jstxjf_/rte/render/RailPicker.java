package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.mtr.mapping.holder.*;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.core.data.Rail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.mtr.mod.block.BlockNode;
import org.joml.Matrix4f;

import org.mtr.core.data.Position;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class RailPicker {

    public static Rail pickedRail;
    public static BlockPos pickedPosStart;
    public static BlockPos pickedPosEnd;
    private static long lastAngleSyncMillis = 0;

    public static void pick() {
        pickedRail = null;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        HitResult hitResult = minecraft.player.pick(20.0, 0.0f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        net.minecraft.core.BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
        pickedPosStart = new BlockPos(pos);
        net.minecraft.world.level.block.state.BlockState blockState = minecraft.level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof BlockNode)) return;

        Position posStartCore = new Position(pickedPosStart.getX(), pickedPosStart.getY(), pickedPosStart.getZ());
        var railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(posStartCore);
        if (railsAtPos == null || railsAtPos.isEmpty()) return;

        Optional<Map.Entry<Position, Rail>> closestEntry = railsAtPos.entrySet().stream().min(Comparator.comparingDouble(entry ->
                Mth.degreesDifferenceAbs((float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - pos.getX(), entry.getKey().getZ() - pos.getZ())), minecraft.player.getYRot())
        ));
        if (closestEntry.isEmpty()) return;

        Position endPos = closestEntry.get().getKey();
        pickedPosEnd = new BlockPos((int)endPos.getX(), (int)endPos.getY(), (int)endPos.getZ());
        pickedRail = closestEntry.get().getValue();
        syncPickedRailFromWorldNodeAngles(minecraft);
    }

    public record RayHit(Rail rail, Position from, Position to, double distance) {}

    public static RayHit rayPickRail(double maxDistance, double maxPerpendicular) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return null;
        }
        final var graph = MinecraftClientData.getInstance().positionsToRail;
        if (graph == null) {
            return null;
        }
        final net.minecraft.world.phys.Vec3 eye = minecraft.player.getEyePosition(1f);
        final net.minecraft.world.phys.Vec3 look = minecraft.player.getViewVector(1f).normalize();
        final double maxPerpendicularSq = maxPerpendicular * maxPerpendicular;
        final double cullRadiusSq = (maxDistance + 32) * (maxDistance + 32);

        RayHit best = null;
        double bestScore = Double.MAX_VALUE;
        for (final var entry : graph.entrySet()) {
            final Position from = entry.getKey();
            for (final var neighbour : entry.getValue().entrySet()) {
                final Position to = neighbour.getKey();
                final Rail rail = neighbour.getValue();
                if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
                    continue;
                }
                if (eye.distanceToSqr(from.getX() + 0.5, from.getY(), from.getZ() + 0.5) > cullRadiusSq
                    && eye.distanceToSqr(to.getX() + 0.5, to.getY(), to.getZ() + 0.5) > cullRadiusSq) {
                    continue;
                }
                final double length;
                try {
                    length = extra.getLength();
                } catch (Exception e) {
                    continue;
                }
                if (!Double.isFinite(length) || length <= 0) {
                    continue;
                }
                final int steps = (int) Math.max(1, Math.min(192, Math.ceil(length)));
                for (int i = 0; i <= steps; i++) {
                    final org.mtr.mapping.holder.Vector3d sample;
                    try {
                        sample = extra.getPosition(length * i / steps);
                    } catch (Exception e) {
                        break;
                    }
                    final double dx = sample.getXMapped() - eye.x;
                    final double dy = sample.getYMapped() + 0.5 - eye.y;
                    final double dz = sample.getZMapped() - eye.z;
                    final double along = dx * look.x + dy * look.y + dz * look.z;
                    if (along < 0.5 || along > maxDistance) {
                        continue;
                    }
                    final double perpX = dx - look.x * along;
                    final double perpY = dy - look.y * along;
                    final double perpZ = dz - look.z * along;
                    final double perpSq = perpX * perpX + perpY * perpY + perpZ * perpZ;
                    if (perpSq > maxPerpendicularSq) {
                        continue;
                    }
                    final double score = perpSq * 64 + along;
                    if (score < bestScore) {
                        bestScore = score;
                        best = new RayHit(rail, from, to, along);
                    }
                }
            }
        }
        return best;
    }

    public static boolean pickAlongLookRay() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return false;
        }
        final net.minecraft.world.phys.Vec3 eye = minecraft.player.getEyePosition(1f);
        final net.minecraft.world.phys.Vec3 look = minecraft.player.getViewVector(1f).normalize();

        Rail bestRail = null;
        Position bestFrom = null;
        Position bestTo = null;
        double bestScore = Double.MAX_VALUE;

        final var graph = MinecraftClientData.getInstance().positionsToRail;
        if (graph == null) {
            return false;
        }
        for (final var entry : graph.entrySet()) {
            final Position from = entry.getKey();
            for (final var neighbour : entry.getValue().entrySet()) {
                final Position to = neighbour.getKey();
                final Rail rail = neighbour.getValue();
                if (rail == null || !(((Object) rail) instanceof cn.jstxjf_.rte.data.RailExtraSupplier extra)) {
                    continue;
                }
                if (eye.distanceToSqr(from.getX() + 0.5, from.getY(), from.getZ() + 0.5) > 4096
                    && eye.distanceToSqr(to.getX() + 0.5, to.getY(), to.getZ() + 0.5) > 4096) {
                    continue;
                }
                final double length;
                try {
                    length = extra.getLength();
                } catch (Exception e) {
                    continue;
                }
                if (!Double.isFinite(length) || length <= 0) {
                    continue;
                }
                final int steps = (int) Math.max(1, Math.min(192, Math.ceil(length)));
                for (int i = 0; i <= steps; i++) {
                    final org.mtr.mapping.holder.Vector3d sample;
                    try {
                        sample = extra.getPosition(length * i / steps);
                    } catch (Exception e) {
                        break;
                    }
                    final double dx = sample.getXMapped() - eye.x;
                    final double dy = sample.getYMapped() + 0.5 - eye.y;
                    final double dz = sample.getZMapped() - eye.z;
                    final double along = dx * look.x + dy * look.y + dz * look.z;
                    if (along < 0.5 || along > 48) {
                        continue;
                    }
                    final double perpX = dx - look.x * along;
                    final double perpY = dy - look.y * along;
                    final double perpZ = dz - look.z * along;
                    final double perpSq = perpX * perpX + perpY * perpY + perpZ * perpZ;
                    if (perpSq > 2.25) {
                        continue;
                    }
                    final double score = perpSq * 64 + along;
                    if (score < bestScore) {
                        bestScore = score;
                        bestRail = rail;
                        bestFrom = from;
                        bestTo = to;
                    }
                }
            }
        }

        if (bestRail == null) {
            return false;
        }
        pickedRail = bestRail;
        pickedPosStart = new BlockPos((int) bestFrom.getX(), (int) bestFrom.getY(), (int) bestFrom.getZ());
        pickedPosEnd = new BlockPos((int) bestTo.getX(), (int) bestTo.getY(), (int) bestTo.getZ());
        syncPickedRailFromWorldNodeAngles(minecraft);
        return true;
    }

    private static void syncPickedRailFromWorldNodeAngles(Minecraft minecraft) {
        if (minecraft.level == null || pickedRail == null || pickedPosStart == null || pickedPosEnd == null) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now - lastAngleSyncMillis < 350) {
            return;
        }
        lastAngleSyncMillis = now;
        final World world = new World(minecraft.level);
        final Rail resolvedRail = RailRebuildHelper.resolveClientRail(pickedPosStart, pickedPosEnd, pickedRail);
        if (!RailRebuildHelper.needsWorldNodeAngleRebuild(world, pickedPosStart, pickedPosEnd, resolvedRail)) {
            return;
        }
        final Rail rebuilt = RailRebuildHelper.rebuildFromWorldNodeAngles(world, pickedPosStart, pickedPosEnd, resolvedRail);
        if (rebuilt != null && rebuilt != resolvedRail) {
            PacketUpdateRail.sendUpdateC2S(resolvedRail, rebuilt);
            pickedRail = rebuilt;
        }
    }

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (pickedRail == null || pickedPosStart == null || pickedPosEnd == null) return;

        drawLine(matrices, vertexConsumers,
                pickedPosStart.getX() + 0.5f, pickedPosStart.getY() + 0.2f, pickedPosStart.getZ() + 0.5f,
                pickedPosEnd.getX() + 0.5f, pickedPosEnd.getY() + 0.2f, pickedPosEnd.getZ() + 0.5f,
                255, 255, 180);
    }

    private static void drawLine(PoseStack matrices, MultiBufferSource vertexConsumers,
                                  float x1, float y1, float z1, float x2, float y2, float z2,
                                  int r, int g, int b) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.lines());
        Matrix4f mat = matrices.last().pose();
        org.joml.Matrix3f normal = matrices.last().normal();
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        buffer.vertex(mat, x1, y1, z1).color(r, g, b, 255).normal(normal, dx, dy, dz).endVertex();
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, 255).normal(normal, dx, dy, dz).endVertex();
    }
}
