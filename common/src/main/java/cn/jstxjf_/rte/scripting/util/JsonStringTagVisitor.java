package cn.jstxjf_.rte.scripting.util;

import com.google.common.collect.Lists;
import net.minecraft.nbt.*;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class JsonStringTagVisitor implements TagVisitor {
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    private final StringBuilder builder = new StringBuilder();

    public String visit(Tag tag) {
        tag.accept(this);
        return this.builder.toString();
    }

    public void visitString(StringTag tag) { builder.append(StringTag.quoteAndEscape(tag.getAsString())); }
    public void visitByte(ByteTag tag) { builder.append((Object) tag.getAsNumber()); }
    public void visitShort(ShortTag tag) { builder.append((Object) tag.getAsNumber()); }
    public void visitInt(IntTag tag) { builder.append((Object) tag.getAsNumber()); }
    public void visitLong(LongTag tag) { builder.append((Object) tag.getAsNumber()); }
    public void visitFloat(FloatTag tag) { builder.append(tag.getAsFloat()); }
    public void visitDouble(DoubleTag tag) { builder.append(tag.getAsDouble()); }

    public void visitByteArray(ByteArrayTag tag) {
        builder.append("[");
        byte[] abyte = tag.getAsByteArray();
        for (int i = 0; i < abyte.length; i++) {
            if (i != 0) builder.append(',');
            builder.append((int) abyte[i]);
        }
        builder.append(']');
    }

    public void visitIntArray(IntArrayTag tag) {
        builder.append("[");
        int[] aint = tag.getAsIntArray();
        for (int i = 0; i < aint.length; i++) {
            if (i != 0) builder.append(',');
            builder.append(aint[i]);
        }
        builder.append(']');
    }

    public void visitLongArray(LongArrayTag tag) {
        builder.append("[");
        long[] along = tag.getAsLongArray();
        for (int i = 0; i < along.length; i++) {
            if (i != 0) builder.append(',');
            builder.append(along[i]);
        }
        builder.append(']');
    }

    public void visitList(ListTag tag) {
        builder.append('[');
        for (int i = 0; i < tag.size(); i++) {
            if (i != 0) builder.append(',');
            builder.append((new JsonStringTagVisitor()).visit(tag.get(i)));
        }
        builder.append(']');
    }

    public void visitCompound(CompoundTag tag) {
        builder.append('{');
        List<String> list = Lists.newArrayList(tag.getAllKeys());
        Collections.sort(list);
        for (String s : list) {
            if (builder.length() != 1) builder.append(',');
            builder.append('"').append(handleEscape(s)).append("\":").append((new JsonStringTagVisitor()).visit(tag.get(s)));
        }
        builder.append('}');
    }

    protected static String handleEscape(String s) {
        return SIMPLE_VALUE.matcher(s).matches() ? s : StringTag.quoteAndEscape(s);
    }

    public void visitEnd(EndTag tag) { builder.append("END"); }
}
