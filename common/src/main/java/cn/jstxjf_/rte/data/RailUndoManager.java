package cn.jstxjf_.rte.data;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RailUndoManager {

    public record RailOperation(String dimension, BlockPos posStart, BlockPos posEnd) {
    }

    private static final int MAX_HISTORY = 10;
    private static final Map<UUID, Deque<RailOperation>> HISTORY = new ConcurrentHashMap<>();

    public static void record(World world, BlockPos posStart, BlockPos posEnd, UUID playerId) {
        if (world == null || posStart == null || posEnd == null || playerId == null) {
            return;
        }
        final Deque<RailOperation> stack = HISTORY.computeIfAbsent(playerId, ignored -> new ArrayDeque<>());
        synchronized (stack) {
            stack.push(new RailOperation(dimensionOf(world), posStart, posEnd));
            while (stack.size() > MAX_HISTORY) {
                stack.removeLast();
            }
        }
    }

    public static RailOperation pop(World world, UUID playerId) {
        final Deque<RailOperation> stack = HISTORY.get(playerId);
        if (stack == null) {
            return null;
        }
        final String dimension = dimensionOf(world);
        synchronized (stack) {
            while (!stack.isEmpty()) {
                final RailOperation operation = stack.pop();
                if (operation.dimension().equals(dimension)) {
                    return operation;
                }
            }
        }
        return null;
    }

    private static String dimensionOf(World world) {
        return ((net.minecraft.world.level.Level) world.data).dimension().location().toString();
    }

    private RailUndoManager() {
    }
}
