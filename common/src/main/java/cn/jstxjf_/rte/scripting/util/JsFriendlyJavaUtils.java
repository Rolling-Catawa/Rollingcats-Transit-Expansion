package cn.jstxjf_.rte.scripting.util;

import java.util.List;
import java.lang.reflect.Array;

public class JsFriendlyJavaUtils {
    public static <T> T[] asJavaArray(List<T> list, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(clazz, list.size());
        return list.toArray(array);
    }
}
