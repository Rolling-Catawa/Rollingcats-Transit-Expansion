package cn.jstxjf_.rte.util;

import java.util.HashMap;
import java.util.Map;

public class StringMapSerializer {
    public static String serializeToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) sb.append(",");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public static Map<String, String> deserialize(String data) {
        Map<String, String> map = new HashMap<>();
        if (data == null || data.isEmpty()) return map;
        String[] pairs = data.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }
}

