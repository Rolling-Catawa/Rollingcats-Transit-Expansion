package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.RTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DashboardMapCache {

    private static final long SAMPLE_INTERVAL_MS = 750;
    private static final long RESAMPLE_AGE_MS = 60000;
    private static final int SAMPLE_BUDGET = 192;
    private static final int MAX_TILES_PER_DIMENSION = 40000;
    private static final int REGION_CHUNKS = 8;
    private static final int REGION_BLOCKS = REGION_CHUNKS * 16;
    private static final int REGION_PIXELS = REGION_CHUNKS * 16;

    private static final int FILE_MAGIC = 0x52544D50;
    private static final int FILE_VERSION = 1;

    private static final Map<String, Map<Long, Tile>> DIMENSION_TILES = new HashMap<>();
    private static final Map<Long, RegionTexture> TEXTURES = new HashMap<>();
    private static final Set<String> LOADED_DIMENSIONS = new HashSet<>();
    private static String textureDimension = "";
    private static long lastSampleTime;
    private static String lastWorldId;

    private DashboardMapCache() {
    }

    public static void frameTick() {
        final long now = System.currentTimeMillis();
        if (now - lastSampleTime < SAMPLE_INTERVAL_MS) return;
        lastSampleTime = now;

        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        final String dim = dimensionKey(level);
        final String worldId = currentWorldId();
        if (worldId != null) {
            lastWorldId = worldId;
            ensureLoaded(worldId, dim);
        }
        final Map<Long, Tile> tiles = DIMENSION_TILES.computeIfAbsent(dim, k -> new HashMap<>());

        final int playerCx = mc.player.chunkPosition().x;
        final int playerCz = mc.player.chunkPosition().z;
        final int radius = Math.min(mc.options.renderDistance().get() + 2, 32);

        final List<long[]> missing = new ArrayList<>();
        final List<long[]> stale = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                final int cx = playerCx + dx;
                final int cz = playerCz + dz;
                final long key = ChunkPos.asLong(cx, cz);
                final Tile existing = tiles.get(key);
                if (existing == null && tiles.size() >= MAX_TILES_PER_DIMENSION) continue;
                if (existing != null && now - existing.sampledAt < RESAMPLE_AGE_MS) continue;
                final net.minecraft.world.level.chunk.LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, false);
                if (chunk == null || chunk instanceof net.minecraft.world.level.chunk.EmptyLevelChunk) continue;
                (existing == null ? missing : stale).add(new long[]{(long) dx * dx + (long) dz * dz, key});
            }
        }
        missing.sort((a, b) -> Long.compare(a[0], b[0]));
        stale.sort((a, b) -> Long.compare(a[0], b[0]));

        int budget = SAMPLE_BUDGET;
        for (int i = 0; i < missing.size() && budget > 0; i++, budget--) {
            sampleChunk(level, tiles, missing.get(i)[1], now, dim);
        }
        for (int i = 0; i < stale.size() && budget > 0; i++, budget--) {
            sampleChunk(level, tiles, stale.get(i)[1], now, dim);
        }
    }

    private static void sampleChunk(ClientLevel level, Map<Long, Tile> tiles, long key, long now, String dim) {
        final int cx = ChunkPos.getX(key);
        final int cz = ChunkPos.getZ(key);
        final int[] colors = new int[256];
        long sumR = 0, sumG = 0, sumB = 0;
        int coloredColumns = 0;

        for (int l = 0; l < 16; l++) {
            for (int k = 0; k < 16; k++) {
                final int bx = cx * 16 + k;
                final int bz = cz * 16 + l;
                final int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz) - 1;
                final BlockPos pos = new BlockPos(bx, top, bz);
                final BlockState state = level.getBlockState(pos);
                final int rgb = state.getMapColor(level, pos).col;
                if (rgb != 0) {
                    coloredColumns++;
                }
                final int r = (int) ((rgb >> 16 & 0xFF) / 1.5);
                final int g = (int) ((rgb >> 8 & 0xFF) / 1.5);
                final int b = (int) ((rgb & 0xFF) / 1.5);
                sumR += r;
                sumG += g;
                sumB += b;
                colors[l * 16 + k] = 0xFF000000 | b << 16 | g << 8 | r;
            }
        }

        if (coloredColumns == 0) {
            return;
        }

        final int avg = 0xFF000000 | (int) (sumR / 256) << 16 | (int) (sumG / 256) << 8 | (int) (sumB / 256);
        tiles.put(key, new Tile(colors, avg, now));

        if (dim.equals(textureDimension)) {
            final RegionTexture texture = TEXTURES.get(regionKey(cx, cz));
            if (texture != null) {
                texture.dirty = true;
            }
        }
    }

    private static long regionKey(int cx, int cz) {
        return ChunkPos.asLong(cx >> 3, cz >> 3);
    }

    public static void forEachRegion(double minWorldX, double minWorldZ, double maxWorldX, double maxWorldZ, boolean wantTexture, RegionConsumer consumer) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null) return;

        final String dim = dimensionKey(level);
        if (!dim.equals(textureDimension)) {
            disposeTextures();
            textureDimension = dim;
        }

        final Map<Long, Tile> tiles = DIMENSION_TILES.get(dim);
        if (tiles == null || tiles.isEmpty()) return;

        final int minRx = (int) Math.floor(minWorldX / REGION_BLOCKS) - 1;
        final int minRz = (int) Math.floor(minWorldZ / REGION_BLOCKS) - 1;
        final int maxRx = (int) Math.ceil(maxWorldX / REGION_BLOCKS) + 1;
        final int maxRz = (int) Math.ceil(maxWorldZ / REGION_BLOCKS) + 1;

        final Map<Long, long[]> regions = new HashMap<>();
        for (final Map.Entry<Long, Tile> entry : tiles.entrySet()) {
            final long key = entry.getKey();
            final int rx = ChunkPos.getX(key) >> 3;
            final int rz = ChunkPos.getZ(key) >> 3;
            if (rx < minRx || rx > maxRx || rz < minRz || rz > maxRz) continue;
            final int avg = entry.getValue().average;
            final long[] acc = regions.computeIfAbsent(ChunkPos.asLong(rx, rz), k -> new long[4]);
            acc[0] += avg >> 16 & 0xFF;
            acc[1] += avg >> 8 & 0xFF;
            acc[2] += avg & 0xFF;
            acc[3]++;
        }

        for (final Map.Entry<Long, long[]> entry : regions.entrySet()) {
            final long key = entry.getKey();
            final long[] acc = entry.getValue();
            final int average = 0xFF000000 | (int) (acc[0] / acc[3]) << 16 | (int) (acc[1] / acc[3]) << 8 | (int) (acc[2] / acc[3]);
            final ResourceLocation textureId = wantTexture ? getRegionTexture(key, tiles) : null;
            consumer.accept(ChunkPos.getX(key), ChunkPos.getZ(key), textureId, average);
        }
    }

    @Nullable
    private static ResourceLocation getRegionTexture(long key, Map<Long, Tile> tiles) {
        RegionTexture entry = TEXTURES.get(key);
        if (entry == null) {
            final DynamicTexture texture = new DynamicTexture(REGION_PIXELS, REGION_PIXELS, false);
            final ResourceLocation id = Minecraft.getInstance().getTextureManager().register("rte_dashboard_map", texture);
            entry = new RegionTexture(texture, id);
            entry.dirty = true;
            TEXTURES.put(key, entry);
        }
        if (entry.dirty) {
            final var image = entry.texture.getPixels();
            if (image == null) return null;
            final int baseCx = ChunkPos.getX(key) << 3;
            final int baseCz = ChunkPos.getZ(key) << 3;
            for (int dz = 0; dz < REGION_CHUNKS; dz++) {
                for (int dx = 0; dx < REGION_CHUNKS; dx++) {
                    final Tile tile = tiles.get(ChunkPos.asLong(baseCx + dx, baseCz + dz));
                    for (int l = 0; l < 16; l++) {
                        for (int k = 0; k < 16; k++) {
                            image.setPixelRGBA(dx * 16 + k, dz * 16 + l, tile == null ? 0 : tile.colors[l * 16 + k]);
                        }
                    }
                }
            }
            entry.texture.upload();
            entry.dirty = false;
        }
        return entry.id;
    }

    public static void flushAndClear() {
        saveAll();
        disposeTextures();
        DIMENSION_TILES.clear();
        LOADED_DIMENSIONS.clear();
        textureDimension = "";
        lastWorldId = null;
    }

    private static void disposeTextures() {
        for (final RegionTexture entry : TEXTURES.values()) {
            try {
                Minecraft.getInstance().getTextureManager().release(entry.id);
            } catch (Exception ignored) {
            }
        }
        TEXTURES.clear();
    }

    private static void ensureLoaded(String worldId, String dim) {
        final String marker = worldId + " " + dim;
        if (!LOADED_DIMENSIONS.add(marker)) return;
        loadDimension(worldId, dim);
    }

    private static void loadDimension(String worldId, String dim) {
        final Path file = cacheDir(worldId).resolve(sanitize(dim) + ".bin");
        if (!Files.exists(file)) return;
        final Map<Long, Tile> tiles = DIMENSION_TILES.computeIfAbsent(dim, k -> new HashMap<>());
        final long now = System.currentTimeMillis();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            if (in.readInt() != FILE_MAGIC || in.readInt() != FILE_VERSION) return;
            final int count = in.readInt();
            for (int i = 0; i < count; i++) {
                final long key = in.readLong();
                final int[] colors = new int[256];
                for (int j = 0; j < 256; j++) {
                    colors[j] = in.readInt();
                }
                final int avg = in.readInt();
                tiles.putIfAbsent(key, new Tile(colors, avg, now));
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[DashboardMapCache] load failed dim={}: {}", dim, e.toString());
        }
    }

    private static void saveAll() {
        if (lastWorldId == null) return;
        for (final Map.Entry<String, Map<Long, Tile>> dimEntry : DIMENSION_TILES.entrySet()) {
            saveDimension(lastWorldId, dimEntry.getKey(), dimEntry.getValue());
        }
    }

    private static void saveDimension(String worldId, String dim, Map<Long, Tile> tiles) {
        if (tiles.isEmpty()) return;
        final Path file = cacheDir(worldId).resolve(sanitize(dim) + ".bin");
        try {
            Files.createDirectories(file.getParent());
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
                out.writeInt(FILE_MAGIC);
                out.writeInt(FILE_VERSION);
                out.writeInt(tiles.size());
                for (final Map.Entry<Long, Tile> entry : tiles.entrySet()) {
                    out.writeLong(entry.getKey());
                    final Tile tile = entry.getValue();
                    for (int color : tile.colors) {
                        out.writeInt(color);
                    }
                    out.writeInt(tile.average);
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[DashboardMapCache] save failed dim={}: {}", dim, e.toString());
        }
    }

    @Nullable
    private static String currentWorldId() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            final var server = mc.getSingleplayerServer();
            if (server != null) {
                try {
                    return "sp_" + server.getWorldData().getLevelName();
                } catch (Exception ignored) {
                }
            }
        }
        final var current = mc.getCurrentServer();
        if (current != null && current.ip != null) {
            return "mp_" + current.ip;
        }
        return null;
    }

    private static Path cacheDir(String worldId) {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("rte_map_cache").resolve(sanitize(worldId));
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String dimensionKey(ClientLevel level) {
        return level.dimension().location().toString();
    }

    @FunctionalInterface
    public interface RegionConsumer {
        void accept(int regionX, int regionZ, @Nullable ResourceLocation texture, int averageArgb);
    }

    private static final class Tile {
        final int[] colors;
        final int average;
        final long sampledAt;

        Tile(int[] colors, int average, long sampledAt) {
            this.colors = colors;
            this.average = average;
            this.sampledAt = sampledAt;
        }
    }

    private static final class RegionTexture {
        final DynamicTexture texture;
        final ResourceLocation id;
        boolean dirty;

        RegionTexture(DynamicTexture texture, ResourceLocation id) {
            this.texture = texture;
            this.id = id;
        }
    }
}
