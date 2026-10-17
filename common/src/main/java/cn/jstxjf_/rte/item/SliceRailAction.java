package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.mtr.core.data.Rail;
import org.mtr.core.tool.Utilities;
import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.data.RailAction;
import org.mtr.mod.data.RailActionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SliceRailAction extends RailAction {

    private static final int COLOR = 0x47FEC4;

    private final CompoundCreatorLogic.SliceTask task;
    private final net.minecraft.server.level.ServerLevel nativeWorld;
    private final UUID uuid;
    private final String playerName;
    private final Rail rail;
    private final double railLength;
    private final double sectionLength;
    private final double increment;
    private final int width;
    private final int height;
    private final double[] starts;
    private final RailExtraSupplier railExtra;
    private final Set<Long> blacklistedPositions = new HashSet<>();

    private int startIndex;
    private double distance;
    private Vec3 last;

    public SliceRailAction(ServerWorld serverWorld, ServerPlayerEntity player, Rail rail, CompoundCreatorLogic.SliceTask task) {
        super(serverWorld, player, RailActionType.BRIDGE, rail, 0, 0, null);
        this.task = task;
        this.nativeWorld = (net.minecraft.server.level.ServerLevel) serverWorld.data;
        this.uuid = player.getUuid();
        this.playerName = player.getName().getString();
        this.rail = rail;
        this.railExtra = (RailExtraSupplier) (Object) rail;
        this.railLength = railExtra.getLength();
        this.sectionLength = task.length == null ? railLength : Math.max(0, task.length);
        this.increment = Math.max(0.1, task.increment);
        this.width = Math.max(1, task.width);
        this.height = Math.max(1, task.height);
        this.starts = buildStarts(task, railLength);
        this.startIndex = 0;
        this.distance = starts.length == 0 ? railLength : starts[0];
        this.last = starts.length == 0 ? railExtra.getPosition(0) : railExtra.getPosition(distance);
    }

    @Override
    public String getDescription() {
        return TextHelper.translatable("gui.mtr.rail_action_slice", playerName, Utilities.round(railLength, 1)).getString();
    }

    @Override
    public int getColor() {
        return COLOR;
    }

    @Override
    public boolean build() {
        if (starts.length == 0) {
            sendProgressMessage(100);
            return true;
        }

        final long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 2) {
            if (startIndex >= starts.length) {
                sendProgressMessage(100);
                return true;
            }

            final double sectionStart = starts[startIndex];
            final double sectionEnd = task.length == null ? railLength : Math.min(railLength, sectionStart + sectionLength);
            if (distance >= sectionEnd) {
                startIndex++;
                if (startIndex >= starts.length) {
                    sendProgressMessage(100);
                    return true;
                }
                distance = starts[startIndex];
                last = railExtra.getPosition(distance);
                continue;
            }

            distance += increment;
            if (distance >= railLength - 0.1) {
                sendProgressMessage(100);
                return true;
            }

            final Vec3 next = railExtra.getPosition(distance);
            final Matrix4f matrix = createTransform(last, next, distance - increment / 2);
            last = next;

            final java.util.List<CompoundCreatorLogic.Lump> activeLumps =
                task.lumpsAt(distance - increment / 2 - task.start);

            matrix.translate(width / 2F - 0.5F, height / 2F - 0.5F, 0);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final int lumpIndex = y * width + x;
                    if (lumpIndex < activeLumps.size()) {
                        placeLump(activeLumps.get(lumpIndex), matrix);
                    }
                    matrix.translate(-1, 0, 0);
                }
                matrix.translate(width, -1, 0);
            }

            if (rte$shouldPlacePier(distance - increment / 2)) {
                rte$placePier(next);
            }
        }

        final double denominator = Math.max(railLength, 0.1);
        sendProgressMessage((float) Utilities.round(100 * distance / denominator, 1));
        return false;
    }

    private boolean rte$shouldPlacePier(double along) {
        if (task.pierInterval <= 0 || task.pierDepth <= 0) {
            return false;
        }
        final double previous = along - increment;
        return Math.floor(along / task.pierInterval) > Math.floor(previous / task.pierInterval);
    }

    private void rte$placePier(Vec3 position) {
        final CompoundCreatorLogic.Lump pierLump = rte$pierLump();
        if (pierLump == null || pierLump.blockState == null) {
            return;
        }
        final int baseX = (int) Math.floor(position.x);
        final int baseZ = (int) Math.floor(position.z);
        int y = (int) Math.floor(position.y) - 1;
        for (int placed = 0; placed < task.pierDepth && y > nativeWorld.getMinBuildHeight(); placed++, y--) {
            final net.minecraft.core.BlockPos target = new net.minecraft.core.BlockPos(baseX, y, baseZ);
            if (!nativeWorld.getBlockState(target).isAir() && placed > 0) {
                break;
            }
            final Matrix4f pierMatrix = new Matrix4f();
            pierMatrix.translate(baseX + 0.5F, y + 0.5F, baseZ + 0.5F);
            placeLump(pierLump, pierMatrix);
        }
    }

    private CompoundCreatorLogic.Lump rte$pierLump() {
        final java.util.List<CompoundCreatorLogic.Lump> source = task.lumps;
        if (source == null || source.isEmpty()) {
            return null;
        }
        final int centre = (height / 2) * width + (width / 2);
        if (centre < source.size() && source.get(centre).blockState != null) {
            return source.get(centre);
        }
        for (final CompoundCreatorLogic.Lump lump : source) {
            if (lump.blockState != null) {
                return lump;
            }
        }
        return null;
    }

    private Matrix4f createTransform(Vec3 start, Vec3 end, double rollDistance) {
        final Matrix4f matrix = new Matrix4f();
        matrix.translate((float) start.x, (float) start.y, (float) start.z);

        if (task.useYaw) {
            final float yaw = (float) Math.atan2(end.x - start.x, end.z - start.z);
            matrix.rotateY(yaw);
        }

        if (task.usePitch) {
            final double dx = end.x - start.x;
            final double dz = end.z - start.z;
            final double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            final float pitch = (float) Math.atan2(end.y - start.y, horizontalDistance);
            matrix.rotateX(pitch);
        }

        if (task.useRoll) {
            final float roll = ((Object) rail) instanceof RailExtraSupplier ? RailExtraSupplier.getRollAngle(rail, rollDistance) : 0;
            matrix.rotateZ(roll);
        }

        return matrix;
    }

    private void placeLump(CompoundCreatorLogic.Lump lump, Matrix4f matrix) {
        if (lump == null || lump.blockState == null) {
            return;
        }

        final Vector3f pos = matrix.getTranslationPart();
        final net.minecraft.core.BlockPos blockPos = new net.minecraft.core.BlockPos(
                (int) Math.floor(pos.x()),
                (int) Math.floor(pos.y()),
                (int) Math.floor(pos.z())
        );
        final long posKey = blockPos.asLong();

        if (blacklistedPositions.contains(posKey)) {
            return;
        }
        if (!lump.replacement && !nativeWorld.getBlockState(blockPos).isAir()) {
            return;
        }
        if (!canPlace(blockPos)) {
            return;
        }

        net.minecraft.world.level.block.state.BlockState state = lump.blockState.data;
        state = rotateState(state, matrix);
        nativeWorld.setBlockAndUpdate(blockPos, state);
        blacklistedPositions.add(posKey);
    }

    private net.minecraft.world.level.block.state.BlockState rotateState(net.minecraft.world.level.block.state.BlockState state, Matrix4f matrix) {
        if (state.hasProperty(BlockStateProperties.FACING)) {
            return state.setValue(BlockStateProperties.FACING, rotateDirection(state.getValue(BlockStateProperties.FACING), matrix));
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotateDirection(state.getValue(BlockStateProperties.HORIZONTAL_FACING), matrix));
        } else if (state.hasProperty(BlockStateProperties.FACING_HOPPER)) {
            return state.setValue(BlockStateProperties.FACING_HOPPER, rotateDirection(state.getValue(BlockStateProperties.FACING_HOPPER), matrix));
        }
        return state;
    }

    private Direction rotateDirection(Direction direction, Matrix4f matrix) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return direction;
        }

        final Vector3f vector = switch (direction) {
            case NORTH -> new Vector3f(0, 0, -1);
            case SOUTH -> new Vector3f(0, 0, 1);
            case EAST -> new Vector3f(1, 0, 0);
            case WEST -> new Vector3f(-1, 0, 0);
            default -> new Vector3f(0, 0, -1);
        };
        final Vector3f transformed = matrix.transform3(vector);
        final double yaw = Math.toDegrees(Math.atan2(transformed.x(), -transformed.z()));
        return Direction.fromYRot(yaw);
    }

    private boolean canPlace(net.minecraft.core.BlockPos blockPos) {
        return nativeWorld.getBlockEntity(blockPos) == null && !(nativeWorld.getBlockState(blockPos).getBlock() instanceof BlockNode);
    }

    private void sendProgressMessage(float percentage) {
        final net.minecraft.world.entity.player.Player player = nativeWorld.getPlayerByUUID(uuid);
        if (player != null) {
            player.displayClientMessage(Component.translatable("gui.mtr.percentage_complete_slice", percentage), true);
        }
    }

    private static double[] buildStarts(CompoundCreatorLogic.SliceTask task, double railLength) {
        final List<Double> result = new ArrayList<>();
        if (task.start < railLength) {
            if (task.interval == null || task.length == null) {
                result.add(task.start);
            } else {
                final double step = task.length + task.interval;
                if (step > 0) {
                    double current = task.start;
                    while (current + step < railLength) {
                        result.add(current);
                        current += step;
                    }
                }
                if (result.isEmpty()) {
                    result.add(task.start);
                }
            }
        }
        return result.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
