package cn.jstxjf_.rte.render;

import org.mtr.libraries.com.logisticscraft.occlusionculling.cache.OcclusionCache;

import java.util.Arrays;

public final class SparseOcclusionCache implements OcclusionCache {

    private static final int CAPACITY = 1 << 21;
    private static final int MASK = CAPACITY - 1;
    private static final int MAX_USED = CAPACITY / 2;
    private static final int MAX_PROBES = 32;

    private final long[] keys = new long[CAPACITY];
    private final byte[] states = new byte[CAPACITY];
    private final int[] generations = new int[CAPACITY];
    private int generation = 1;
    private int used;
    private long lastKey;
    private int lastSlot = -1;

    @Override
    public void resetCache() {
        generation++;
        if (generation == 0) {
            Arrays.fill(generations, 0);
            generation = 1;
        }
        used = 0;
        lastSlot = -1;
    }

    @Override
    public void setVisible(int x, int y, int z) {
        mark(slot(key(x, y, z), true), 1);
    }

    @Override
    public void setHidden(int x, int y, int z) {
        mark(slot(key(x, y, z), true), 2);
    }

    @Override
    public int getState(int x, int y, int z) {
        lastKey = key(x, y, z);
        lastSlot = slot(lastKey, false);
        return lastSlot < 0 ? 0 : states[lastSlot];
    }

    @Override
    public void setLastHidden() {
        mark(lastSlot >= 0 ? lastSlot : slot(lastKey, true), 2);
    }

    @Override
    public void setLastVisible() {
        mark(lastSlot >= 0 ? lastSlot : slot(lastKey, true), 1);
    }

    private void mark(int slot, int bit) {
        if (slot >= 0) {
            states[slot] |= (byte) bit;
        }
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x1FFFFF) << 42) | ((long) (y & 0x1FFFFF) << 21) | (long) (z & 0x1FFFFF);
    }

    private int slot(long key, boolean create) {
        long hash = key * 0x9E3779B97F4A7C15L;
        int index = (int) (hash ^ (hash >>> 32)) & MASK;
        for (int probe = 0; probe < MAX_PROBES; probe++) {
            if (generations[index] != generation) {
                if (!create || used >= MAX_USED) {
                    return -1;
                }
                generations[index] = generation;
                keys[index] = key;
                states[index] = 0;
                used++;
                return index;
            }
            if (keys[index] == key) {
                return index;
            }
            index = (index + 1) & MASK;
        }
        return -1;
    }
}
