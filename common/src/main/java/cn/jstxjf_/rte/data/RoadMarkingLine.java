package cn.jstxjf_.rte.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public record RoadMarkingLine(double offset, double width, double dash, double gap, int argb) {

    private static final double MIN_WIDTH = 0.02;
    private static final double MAX_WIDTH = 16;
    private static final double MAX_OFFSET = 16;
    private static final double MIN_DASH = 0.05;
    private static final int DEFAULT_COLOR = 0xFFF2F2F2;

    public boolean isSolid() {
        return dash <= 0;
    }

    public List<double[]> intervals(double length) {
        final List<double[]> result = new ArrayList<>();
        if (!(length > 0)) {
            return result;
        }
        if (isSolid()) {
            result.add(new double[]{0, length});
            return result;
        }
        final double period = dash + gap;
        final int count = (int) Math.floor((length + gap) / period + 1e-6);
        if (count <= 0) {
            return result;
        }
        final double used = count * period - gap;
        final double start = (length - used) / 2;
        for (int i = 0; i < count; i++) {
            final double from = start + i * period;
            result.add(new double[]{from, from + dash});
        }
        return result;
    }

    public static List<RoadMarkingLine> parseAll(JsonArray array) {
        final List<RoadMarkingLine> lines = new ArrayList<>();
        for (final JsonElement element : array) {
            if (element.isJsonObject()) {
                lines.add(parse(element.getAsJsonObject()));
            }
        }
        return lines;
    }

    public static RoadMarkingLine parse(JsonObject obj) {
        final double offset = clamp(number(obj, "offset", 0), -MAX_OFFSET, MAX_OFFSET);
        final double width = clamp(number(obj, "width", 0.15), MIN_WIDTH, MAX_WIDTH);
        double dash = number(obj, "dash", 0);
        double gap = number(obj, "gap", 0);
        if (dash > 0) {
            dash = Math.max(MIN_DASH, dash);
            gap = Math.max(0, gap);
        } else {
            dash = 0;
            gap = 0;
        }
        final int argb = obj.has("color") ? parseColor(obj.get("color").getAsString()) : DEFAULT_COLOR;
        return new RoadMarkingLine(offset, width, dash, gap, argb);
    }

    public static int parseColor(String text) {
        String hex = text.trim();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        } else if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        try {
            final long value = Long.parseLong(hex, 16);
            return hex.length() <= 6 ? (int) (0xFF000000L | value) : (int) value;
        } catch (NumberFormatException e) {
            return DEFAULT_COLOR;
        }
    }

    private static double number(JsonObject obj, String key, double fallback) {
        if (!obj.has(key)) {
            return fallback;
        }
        try {
            final double value = obj.get(key).getAsDouble();
            return Double.isFinite(value) ? value : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
