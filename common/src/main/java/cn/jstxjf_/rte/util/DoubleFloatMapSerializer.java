package cn.jstxjf_.rte.util;

import java.util.HashMap;
import java.util.Map;

public class DoubleFloatMapSerializer {

    public static String serializeToString(Map<Double, Float> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Double, Float> entry : map.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    public static Map<Double, Float> deserialize(String data) {
        Map<Double, Float> map = new HashMap<>();
        if (data == null || data.isEmpty()) return map;
        String[] entries = data.split(";");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                try {
                    map.put(Double.parseDouble(parts[0]), Float.parseFloat(parts[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }
}
