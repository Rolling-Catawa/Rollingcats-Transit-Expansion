package cn.jstxjf_.rte.network.util;

public class IntegerArraySerializer {

    private IntegerArraySerializer() {
    }

    public static String serialize(Integer[] array) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i] == null ? "null" : array[i]);
            if (i < array.length - 1) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    public static Integer[] deserialize(String value) {
        final String[] split = value.split(",");
        final Integer[] result = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            result[i] = "null".equals(split[i]) ? null : Integer.parseInt(split[i]);
        }
        return result;
    }
}
