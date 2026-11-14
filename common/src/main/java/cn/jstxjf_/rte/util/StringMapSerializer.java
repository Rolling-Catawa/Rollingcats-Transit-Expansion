package cn.jstxjf_.rte.util;

import java.util.HashMap;
import java.util.Map;

public class StringMapSerializer {
    public static String serializeToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) sb.append(",");
            sb.append(escape(entry.getKey())).append("=").append(escape(entry.getValue()));
        }
        return sb.toString();
    }

    public static Map<String, String> deserialize(String data) {
        Map<String, String> map = new HashMap<>();
        if (data == null || data.isEmpty()) return map;
        for (String pair : data.split(",")) {
            final int separator = pair.indexOf('=');
            if (separator > 0) {
                map.put(unescape(pair.substring(0, separator)), unescape(pair.substring(separator + 1)));
            }
        }
        return map;
    }

    private static String escape(String value) {
        if (value == null) return "";
        final StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '%' -> sb.append("%25");
                case ',' -> sb.append("%2C");
                case '=' -> sb.append("%3D");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescape(String value) {
        if (value.indexOf('%') < 0) return value;
        final StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == '%' && i + 2 < value.length()) {
                final String code = value.substring(i + 1, i + 3);
                final String decoded = switch (code.toUpperCase(java.util.Locale.ROOT)) {
                    case "25" -> "%";
                    case "2C" -> ",";
                    case "3D" -> "=";
                    default -> null;
                };
                if (decoded != null) {
                    sb.append(decoded);
                    i += 2;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
