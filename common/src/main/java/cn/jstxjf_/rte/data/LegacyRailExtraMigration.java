package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mixin.RailAccessor;
import cn.jstxjf_.rte.common.util.DoubleFloatMapSerializer;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.serializer.MessagePackReader;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.legacy.data.DataFixer;
import org.mtr.libraries.org.msgpack.core.MessagePack;
import org.mtr.libraries.org.msgpack.core.MessageUnpacker;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class LegacyRailExtraMigration {

    private LegacyRailExtraMigration() {
    }

    public static void apply(Path savePath, Collection<Rail> rails) {
        final Map<Long, Map<Long, LegacyExtra>> extras = read(savePath.resolve("rails"));
        if (extras.isEmpty()) {
            return;
        }
        int applied = 0;
        for (final Rail rail : rails) {
            if (!((Object) rail instanceof RailExtraSupplier supplier)) {
                continue;
            }
            final RailAccessor accessor = (RailAccessor) (Object) rail;
            final long position1 = asLong(accessor.rte$getPosition1());
            final long position2 = asLong(accessor.rte$getPosition2());
            LegacyExtra extra = find(extras, position1, position2);
            if (extra == null) {
                extra = find(extras, position2, position1);
            }
            if (extra != null && extra.applyTo(supplier)) {
                applied++;
            }
        }
        RTE.LOGGER.info("[LegacyRailMigration] restored NTE/ANTE rail data on {} of {} migrated rails in {}", applied, rails.size(), savePath);
    }

    private static LegacyExtra find(Map<Long, Map<Long, LegacyExtra>> extras, long start, long end) {
        final Map<Long, LegacyExtra> connections = extras.get(start);
        return connections == null ? null : connections.get(end);
    }

    private static long asLong(Position position) {
        return DataFixer.asLong(position);
    }

    private static Map<Long, Map<Long, LegacyExtra>> read(Path railsPath) {
        final Map<Long, Map<Long, LegacyExtra>> extras = new HashMap<>();
        if (!Files.isDirectory(railsPath)) {
            return extras;
        }
        try (final Stream<Path> folders = Files.list(railsPath)) {
            folders.filter(Files::isDirectory).forEach(folder -> {
                try (final Stream<Path> files = Files.list(folder)) {
                    files.filter(Files::isRegularFile).forEach(file -> readFile(file, extras));
                } catch (Exception e) {
                    RTE.LOGGER.warn("[LegacyRailMigration] cannot list {}", folder, e);
                }
            });
        } catch (Exception e) {
            RTE.LOGGER.warn("[LegacyRailMigration] cannot list {}", railsPath, e);
        }
        return extras;
    }

    private static void readFile(Path file, Map<Long, Map<Long, LegacyExtra>> extras) {
        try (final InputStream inputStream = Files.newInputStream(file); final MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputStream)) {
            final MessagePackReader reader = new MessagePackReader(unpacker);
            final long[] start = {Long.MIN_VALUE};
            reader.unpackLong("node_pos", value -> start[0] = value);
            if (start[0] == Long.MIN_VALUE) {
                return;
            }
            reader.iterateReaderArray("rail_connections", () -> {
            }, connection -> {
                final long[] end = {Long.MIN_VALUE};
                connection.unpackLong("node_pos", value -> end[0] = value);
                final LegacyExtra extra = LegacyExtra.read(connection);
                if (end[0] != Long.MIN_VALUE && extra != null) {
                    extras.computeIfAbsent(start[0], key -> new HashMap<>()).put(end[0], extra);
                }
            });
        } catch (Exception e) {
            RTE.LOGGER.debug("[LegacyRailMigration] skipped {}", file, e);
        }
    }

    private static final class LegacyExtra {

        private String modelKey = "";
        private boolean secondaryDirection;
        private boolean hasSecondaryDirection;
        private float verticalCurveRadius;
        private Map<String, String> customConfigs;
        private Map<Double, Float> rollAngleMap;
        private Float rollingOffset;
        private Integer openingDirection;

        private static LegacyExtra read(ReaderBase reader) {
            final LegacyExtra extra = new LegacyExtra();
            reader.unpackString("model_key", value -> extra.modelKey = value);
            reader.unpackBoolean("is_secondary_dir", value -> {
                extra.secondaryDirection = value;
                extra.hasSecondaryDirection = true;
            });
            reader.unpackDouble("vertical_curve_radius", value -> extra.verticalCurveRadius = (float) value);
            reader.unpackString("custom_configs", value -> {
                try {
                    extra.customConfigs = cn.jstxjf_.rte.network.util.StringMapSerializer.deserialize(value);
                } catch (Exception e) {
                    extra.customConfigs = null;
                }
            });
            reader.unpackString("roll_angle_map", value -> extra.rollAngleMap = DoubleFloatMapSerializer.deserialize(value));
            reader.unpackDouble("rolling_offset", value -> extra.rollingOffset = (float) value);
            reader.unpackInt("opening_direction", value -> extra.openingDirection = value);
            return extra.isEmpty() ? null : extra;
        }

        private boolean isEmpty() {
            return modelKey.isEmpty() && !secondaryDirection && verticalCurveRadius == 0 && (customConfigs == null || customConfigs.isEmpty())
                && (rollAngleMap == null || rollAngleMap.isEmpty()) && rollingOffset == null && openingDirection == null;
        }

        private boolean applyTo(RailExtraSupplier supplier) {
            boolean changed = false;
            if (!modelKey.isEmpty()) {
                supplier.setModelKey(modelKey);
                changed = true;
            }
            if (hasSecondaryDirection && secondaryDirection) {
                supplier.setRenderReversed(true);
                changed = true;
            }
            if (verticalCurveRadius != 0) {
                supplier.setVerticalCurveRadius(verticalCurveRadius);
                changed = true;
            }
            if (customConfigs != null && !customConfigs.isEmpty()) {
                supplier.setCustomConfigs(new HashMap<>(customConfigs));
                changed = true;
            }
            if (rollAngleMap != null && !rollAngleMap.isEmpty()) {
                supplier.setRollAngleMap(new HashMap<>(rollAngleMap));
                changed = true;
            }
            if (rollingOffset != null) {
                supplier.setRollingOffset(rollingOffset);
                changed = true;
            }
            if (openingDirection != null && openingDirection != 0) {
                supplier.setOpeningDirectionRaw(openingDirection);
                changed = true;
            }
            return changed;
        }
    }
}
