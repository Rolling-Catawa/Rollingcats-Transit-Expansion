package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.compat.RailFactory;

//#if MC>=11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
import cn.jstxjf_.rte.compat.BuiltInRegistries;
//#endif
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Angle;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonParser;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CompoundCreatorLogic {

    public static List<Task> getTasks(ItemStack stack) {
        List<Task> tasks = new ArrayList<>();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag tasksTag = tag.getCompound("tasks");
        for (String key : tasksTag.getKeys()) {
            Task task = fromCompoundTag(tasksTag.getCompound(key));
            if (task != null) {
                tasks.add(task);
            }
        }
        tasks.sort(Comparator.comparingInt(task -> task.order));
        return tasks;
    }

    public static Task fromCompoundTag(CompoundTag compoundTag) {
        final String type = compoundTag.getString(Task.TAG_TYPE);
        if (SliceTask.TYPE.equals(type)) {
            return new SliceTask(compoundTag);
        } else if (RailModifierTask.TYPE.equals(type)) {
            return new RailModifierTask(compoundTag);
        } else if (type.isEmpty()) {
            return new Task(compoundTag);
        } else {
            return null;
        }
    }

    public static Task copyTask(Task task) {
        if (task instanceof SliceTask sliceTask) {
            return new SliceTask(sliceTask);
        } else if (task instanceof RailModifierTask railModifierTask) {
            return new RailModifierTask(railModifierTask);
        } else {
            return new Task(task.order, task.name);
        }
    }

    public static class Task {
        public static final String TAG_TYPE = "type";
        public static final String TAG_ORDER = "order";
        public static final String TAG_NAME = "name";

        public int order;
        public String name;

        public Task(int order, String name) {
            this.order = order;
            this.name = name;
        }

        public Task(CompoundTag compoundTag) {
            order = compoundTag.getInt(TAG_ORDER);
            name = compoundTag.getString(TAG_NAME);
        }

        public void copyFrom(Task other) {
            order = other.order;
            name = other.name;
        }

        public CompoundTag toCompoundTag() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt(TAG_ORDER, order);
            compoundTag.putString(TAG_NAME, name);
            return compoundTag;
        }
    }

    public static class SliceTask extends Task {
        public static final String TYPE = "Slice";

        public int width;
        public int height;
        public double start;
        public Double length;
        public Double interval;
        public double increment;
        public List<Lump> lumps;
        public boolean useYaw;
        public boolean usePitch;
        public boolean useRoll;
        public List<Segment> segments;
        public double pierInterval;
        public int pierDepth;

        public static final String TAG_HEIGHT = "height";
        public static final String TAG_LENGTH = "length";
        public static final String TAG_START = "start";
        public static final String TAG_WIDTH = "width";
        public static final String TAG_INTERVAL = "interval";
        public static final String TAG_INCREMENT = "increment";
        public static final String TAG_LUMPS = "lumps";
        public static final String TAG_USE_YAW = "use_yaw";
        public static final String TAG_USE_PITCH = "use_pitch";
        public static final String TAG_USE_ROLL = "use_roll";
        public static final String TAG_SEGMENTS = "segments";
        public static final String TAG_PIER_INTERVAL = "pier_interval";
        public static final String TAG_PIER_DEPTH = "pier_depth";

        public SliceTask() {
            super(0, TYPE);
            this.width = 11;
            this.height = 11;
            this.start = 0;
            this.length = null;
            this.interval = null;
            this.increment = 0.1;
            this.lumps = new ArrayList<>();
            for (int i = 0; i < this.width * this.height; i++) {
                lumps.add(new Lump(null, true));
            }
            this.useYaw = true;
            this.usePitch = true;
            this.useRoll = true;
            this.segments = new ArrayList<>();
            this.pierInterval = 0;
            this.pierDepth = 0;
        }

        public SliceTask(SliceTask other) {
            super(other.order, other.name);
            this.width = other.width;
            this.height = other.height;
            this.start = other.start;
            this.length = other.length;
            this.interval = other.interval;
            this.increment = other.increment;
            this.lumps = Lump.copyFrom(other.lumps);
            this.useYaw = other.useYaw;
            this.usePitch = other.usePitch;
            this.useRoll = other.useRoll;
            this.segments = Segment.copyFrom(other.segments);
            this.pierInterval = other.pierInterval;
            this.pierDepth = other.pierDepth;
        }

        public SliceTask(CompoundTag compoundTag) {
            super(compoundTag);
            this.width = compoundTag.getInt(TAG_WIDTH);
            this.height = compoundTag.getInt(TAG_HEIGHT);
            this.start = compoundTag.getDouble(TAG_START);
            if (compoundTag.contains(TAG_LENGTH)) {
                this.length = compoundTag.getDouble(TAG_LENGTH);
            } else {
                this.length = null;
            }
            this.increment = compoundTag.getDouble(TAG_INCREMENT);
            if (compoundTag.contains(TAG_INTERVAL)) {
                this.interval = compoundTag.getDouble(TAG_INTERVAL);
            } else {
                this.interval = null;
            }
            this.lumps = compoundTag.contains(TAG_LUMPS) ? Lump.fromByteArray(compoundTag.getByteArray(TAG_LUMPS)) : new ArrayList<>();
            final int expectedSize = this.width * this.height;
            while (this.lumps.size() < expectedSize) {
                this.lumps.add(new Lump(null, true));
            }
            if (this.lumps.size() > expectedSize) {
                this.lumps = new ArrayList<>(this.lumps.subList(0, expectedSize));
            }
            this.useYaw = compoundTag.getBoolean(TAG_USE_YAW);
            this.usePitch = compoundTag.getBoolean(TAG_USE_PITCH);
            this.useRoll = compoundTag.getBoolean(TAG_USE_ROLL);
            this.segments = compoundTag.contains(TAG_SEGMENTS)
                ? Segment.fromByteArray(compoundTag.getByteArray(TAG_SEGMENTS), expectedSize)
                : new ArrayList<>();
            this.pierInterval = compoundTag.getDouble(TAG_PIER_INTERVAL);
            this.pierDepth = compoundTag.getInt(TAG_PIER_DEPTH);
        }

        public boolean hasSegments() {
            return segments != null && segments.size() > 1;
        }

        public double segmentCycleLength() {
            if (segments == null || segments.isEmpty()) {
                return 0;
            }
            double total = 0;
            for (final Segment segment : segments) {
                total += Math.max(0.1, segment.length);
            }
            return total;
        }

        public List<Lump> lumpsAt(double offsetAlongRail) {
            if (!hasSegments()) {
                return lumps;
            }
            final double cycle = segmentCycleLength();
            if (cycle <= 0) {
                return lumps;
            }
            double position = offsetAlongRail % cycle;
            if (position < 0) {
                position += cycle;
            }
            for (final Segment segment : segments) {
                final double span = Math.max(0.1, segment.length);
                if (position < span) {
                    return segment.lumps;
                }
                position -= span;
            }
            return segments.get(segments.size() - 1).lumps;
        }

        public boolean setWidthAndHeight(int width, int height) {
            if (width < 1 || height < 1) {
                return false;
            }
            if (width % 2 != 1 || height % 2 != 1) {
                return false;
            }
            if (width == this.width && height == this.height) {
                return false;
            }

            final List<Lump> resized = new ArrayList<>();
            for (int i = 0; i < width * height; i++) {
                resized.add(new Lump(null, true));
            }

            final int newMidX = width / 2;
            final int newMidY = height / 2;
            final int oldMidX = this.width / 2;
            final int oldMidY = this.height / 2;
            for (int y = -newMidY; y <= newMidY; y++) {
                final int newY = newMidY + y;
                final int oldY = oldMidY + y;
                if (oldY < 0 || oldY >= this.height) {
                    continue;
                }
                for (int x = -newMidX; x <= newMidX; x++) {
                    final int newX = newMidX + x;
                    final int oldX = oldMidX + x;
                    if (oldX < 0 || oldX >= this.width) {
                        continue;
                    }
                    resized.set(newY * width + newX, this.lumps.get(oldY * this.width + oldX));
                }
            }

            this.width = width;
            this.height = height;
            this.lumps = resized;
            return true;
        }

        public void copyFrom(SliceTask other) {
            super.copyFrom(other);
            this.width = other.width;
            this.height = other.height;
            this.start = other.start;
            this.length = other.length;
            this.interval = other.interval;
            this.increment = other.increment;
            this.lumps = Lump.copyFrom(other.lumps);
            this.useYaw = other.useYaw;
            this.usePitch = other.usePitch;
            this.useRoll = other.useRoll;
            this.segments = Segment.copyFrom(other.segments);
            this.pierInterval = other.pierInterval;
            this.pierDepth = other.pierDepth;
        }

        @Override
        public CompoundTag toCompoundTag() {
            CompoundTag compoundTag = super.toCompoundTag();
            compoundTag.putString(TAG_TYPE, TYPE);
            compoundTag.putInt(TAG_WIDTH, width);
            compoundTag.putInt(TAG_HEIGHT, height);
            compoundTag.putDouble(TAG_START, start);
            if (length != null) compoundTag.putDouble(TAG_LENGTH, length);
            compoundTag.putDouble(TAG_INCREMENT, increment);
            if (interval != null) compoundTag.putDouble(TAG_INTERVAL, interval);
            compoundTag.putByteArray(TAG_LUMPS, Lump.toByteArray(lumps));
            compoundTag.putBoolean(TAG_USE_YAW, useYaw);
            compoundTag.putBoolean(TAG_USE_PITCH, usePitch);
            compoundTag.putBoolean(TAG_USE_ROLL, useRoll);
            if (segments != null && !segments.isEmpty()) {
                compoundTag.putByteArray(TAG_SEGMENTS, Segment.toByteArray(segments));
            }
            compoundTag.putDouble(TAG_PIER_INTERVAL, pierInterval);
            compoundTag.putInt(TAG_PIER_DEPTH, pierDepth);
            return compoundTag;
        }
    }

    public static class Segment {

        public List<Lump> lumps;
        public double length;

        public Segment(List<Lump> lumps, double length) {
            this.lumps = lumps;
            this.length = length;
        }

        public Segment(Segment other) {
            this(Lump.copyFrom(other.lumps), other.length);
        }

        public static List<Segment> copyFrom(List<Segment> source) {
            final List<Segment> copied = new ArrayList<>();
            if (source != null) {
                for (final Segment segment : source) {
                    copied.add(new Segment(segment));
                }
            }
            return copied;
        }

        public static byte[] toByteArray(List<Segment> segments) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
                dataOutputStream.writeInt(segments.size());
                for (final Segment segment : segments) {
                    dataOutputStream.writeDouble(segment.length);
                    final byte[] lumpBytes = Lump.toByteArray(segment.lumps);
                    dataOutputStream.writeInt(lumpBytes.length);
                    dataOutputStream.write(lumpBytes);
                }
            } catch (IOException e) {
                RTE.LOGGER.error("Serialising compound creator segments", e);
            }
            return byteArrayOutputStream.toByteArray();
        }

        public static List<Segment> fromByteArray(byte[] bytes, int expectedLumpCount) {
            final List<Segment> segments = new ArrayList<>();
            if (bytes == null || bytes.length == 0) {
                return segments;
            }
            try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
                final int count = dataInputStream.readInt();
                for (int i = 0; i < count; i++) {
                    final double length = dataInputStream.readDouble();
                    final int lumpByteCount = dataInputStream.readInt();
                    final byte[] lumpBytes = new byte[lumpByteCount];
                    dataInputStream.readFully(lumpBytes);
                    final List<Lump> lumps = Lump.fromByteArray(lumpBytes);
                    while (lumps.size() < expectedLumpCount) {
                        lumps.add(new Lump(null, true));
                    }
                    segments.add(new Segment(lumps, length));
                }
            } catch (IOException e) {
                RTE.LOGGER.error("Reading compound creator segments", e);
            }
            return segments;
        }
    }

    public static class Lump {
        public BlockState blockState;
        public boolean replacement;

        public Lump(BlockState blockState, boolean replacement) {
            this.blockState = blockState;
            this.replacement = replacement;
        }

        public Lump(Lump other) {
            this(other.blockState, other.replacement);
        }

        private static final int FORMAT_NAMED = -1;

        public static String describeState(BlockState blockState) {
            final net.minecraft.world.level.block.state.BlockState state = blockState.data;
            final StringBuilder builder = new StringBuilder(
                BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
            final java.util.Map<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> values = state.getValues();
            if (!values.isEmpty()) {
                builder.append('[');
                boolean first = true;
                for (final java.util.Map.Entry<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> entry : values.entrySet()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append(entry.getKey().getName()).append('=')
                        .append(describeValue(entry.getKey(), entry.getValue()));
                }
                builder.append(']');
            }
            return builder.toString();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static String describeValue(net.minecraft.world.level.block.state.properties.Property property, Comparable value) {
            return property.getName(value);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public static BlockState parseState(String description) {
            if (description == null || description.isEmpty()) {
                return null;
            }
            final int bracket = description.indexOf('[');
            final String blockId = bracket < 0 ? description : description.substring(0, bracket);
            final net.minecraft.resources.ResourceLocation location =
                net.minecraft.resources.ResourceLocation.tryParse(blockId);
            if (location == null || !BuiltInRegistries.BLOCK.containsKey(location)) {
                RTE.LOGGER.warn("[CompoundCreator] unknown block in template: {}", description);
                return null;
            }
            final net.minecraft.world.level.block.Block block =
                BuiltInRegistries.BLOCK.get(location);
            net.minecraft.world.level.block.state.BlockState state = block.defaultBlockState();
            if (bracket >= 0 && description.endsWith("]")) {
                final String body = description.substring(bracket + 1, description.length() - 1);
                for (final String pair : body.split(",")) {
                    final int equals = pair.indexOf('=');
                    if (equals < 0) {
                        continue;
                    }
                    final String name = pair.substring(0, equals).trim();
                    final String value = pair.substring(equals + 1).trim();
                    final net.minecraft.world.level.block.state.properties.Property<?> property =
                        block.getStateDefinition().getProperty(name);
                    if (property == null) {
                        continue;
                    }
                    final java.util.Optional parsed = property.getValue(value);
                    if (parsed.isPresent()) {
                        state = state.setValue((net.minecraft.world.level.block.state.properties.Property) property,
                            (Comparable) parsed.get());
                    }
                }
            }
            return new BlockState(state);
        }

        public static byte[] toByteArray(List<Lump> lumps) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
                dataOutputStream.writeInt(FORMAT_NAMED);
                dataOutputStream.writeInt(lumps.size());
                for (final Lump lump : lumps) {
                    final boolean hasBlockState = lump.blockState != null;
                    dataOutputStream.writeBoolean(hasBlockState);
                    if (hasBlockState) {
                        dataOutputStream.writeUTF(describeState(lump.blockState));
                    }
                    dataOutputStream.writeBoolean(lump.replacement);
                }
            } catch (IOException e) {
                RTE.LOGGER.error("Failed serializing compound creator lumps", e);
            }
            return byteArrayOutputStream.toByteArray();
        }

        public static List<Lump> fromByteArray(byte[] bytes) {
            final List<Lump> lumps = new ArrayList<>();
            if (bytes == null || bytes.length == 0) {
                return lumps;
            }

            try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
                int size = dataInputStream.readInt();
                final boolean named = size == FORMAT_NAMED;
                if (named) {
                    size = dataInputStream.readInt();
                }
                for (int i = 0; i < size; i++) {
                    final boolean hasBlockState = dataInputStream.readBoolean();
                    BlockState blockState = null;
                    if (hasBlockState) {
                        blockState = named
                            ? parseState(dataInputStream.readUTF())
                            : Block.getStateFromRawId(dataInputStream.readInt());
                    }
                    final boolean replacement = dataInputStream.readBoolean();
                    lumps.add(new Lump(blockState, replacement));
                }
            } catch (Exception e) {
                RTE.LOGGER.error("Failed deserializing compound creator lumps", e);
            }
            return lumps;
        }
        
        public static List<Lump> copyFrom(List<Lump> lumps) {
            List<Lump> result = new ArrayList<>();
            for (Lump lump : lumps) {
                result.add(new Lump(lump));
            }
            return result;
        }
    }

    public static class RailModifierTask extends Task {
        public static final String TYPE = "Modifier";
        public Rail rail;
        public boolean isOneWay;
        public boolean isReversed;

        public static final String TAG_RAIL = "rail";
        public static final String TAG_IS_ONE_WAY = "is_one_way";
        public static final String TAG_IS_REVERSED = "is_reversed";
        private static final String LEGACY_TAG_MODEL_KEY = "model_key";
        private static final String LEGACY_TAG_VERT_RADIUS = "vert_radius";
        private static final String LEGACY_TAG_RAIL_TYPE = "rail_type";
        private static final String LEGACY_TAG_SPEED_LIMIT_KPH = "speed_limit_kph";
        private static final String LEGACY_TAG_OPENING_DIRECTION = "opening_direction";

        public RailModifierTask() {
            super(0, TYPE);
            this.rail = createDefaultRail();
            this.isOneWay = false;
            this.isReversed = false;
        }

        public RailModifierTask(CompoundTag compoundTag) {
            super(compoundTag);
            this.rail = compoundTag.contains(TAG_RAIL) ? railFromJson(compoundTag.getString(TAG_RAIL)) : createLegacyRail(compoundTag);
            this.isOneWay = compoundTag.getBoolean(TAG_IS_ONE_WAY);
            this.isReversed = compoundTag.getBoolean(TAG_IS_REVERSED);
        }

        public RailModifierTask(RailModifierTask other) {
            super(other.order, other.name);
            this.rail = copyRail(other.rail);
            this.isOneWay = other.isOneWay;
            this.isReversed = other.isReversed;
        }

        public void copyFrom(RailModifierTask other) {
            super.copyFrom(other);
            this.rail = copyRail(other.rail);
            this.isOneWay = other.isOneWay;
            this.isReversed = other.isReversed;
        }

        @Override
        public CompoundTag toCompoundTag() {
            CompoundTag compoundTag = super.toCompoundTag();
            compoundTag.putString(TAG_TYPE, TYPE);
            compoundTag.putString(TAG_RAIL, Utilities.getJsonObjectFromData(rail).toString());
            compoundTag.putBoolean(TAG_IS_ONE_WAY, isOneWay);
            compoundTag.putBoolean(TAG_IS_REVERSED, isReversed);
            return compoundTag;
        }

        public static Rail copyRail(Rail rail) {
            return railFromJson(Utilities.getJsonObjectFromData(rail).toString());
        }

        private static Rail railFromJson(String railJson) {
            try {
                return new Rail(new JsonReader(JsonParser.parseString(railJson)));
            } catch (Exception ignored) {
                return createDefaultRail();
            }
        }

        private static Rail createDefaultRail() {
            return RailFactory.newRail(
                new Position(0, -1145, 0),
                Angle.N,
                new Position(0, -1145, 10),
                Angle.S,
                Rail.Shape.QUADRATIC,
                0,
                new ObjectArrayList<>(),
                80,
                80,
                false,
                false,
                true,
                false,
                true,
                TransportMode.TRAIN
            );
        }

        private static Rail createLegacyRail(CompoundTag compoundTag) {
            final Rail legacyRail = createDefaultRail();
            if ((Object) legacyRail instanceof RailExtraSupplier extra) {
                extra.setModelKey(compoundTag.getString(LEGACY_TAG_MODEL_KEY));
                extra.setVerticalCurveRadius(compoundTag.getFloat(LEGACY_TAG_VERT_RADIUS));
                extra.setRailType(compoundTag.getString(LEGACY_TAG_RAIL_TYPE));
                extra.setSpeedLimitKph(compoundTag.contains(LEGACY_TAG_SPEED_LIMIT_KPH) ? compoundTag.getInt(LEGACY_TAG_SPEED_LIMIT_KPH) : -1);
                extra.setOpeningDirection(compoundTag.contains(LEGACY_TAG_OPENING_DIRECTION) ? compoundTag.getInt(LEGACY_TAG_OPENING_DIRECTION) : 0);
            }
            return RailRebuildHelper.rebuildFromCurrentState(legacyRail);
        }
    }
}
