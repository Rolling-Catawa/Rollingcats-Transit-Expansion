package cn.jstxjf_.rte.network.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DoubleFloatMapSerializer {

    private static final Gson GSON = new GsonBuilder().create();

    public static String serializeToString(Map<Double, Float> map) {
        String json = GSON.toJson(map);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static Map<Double, Float> deserialize(String data) {
        if (data == null || data.isEmpty()) return new HashMap<>();
        String json = new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
        return GSON.fromJson(json, new TypeToken<Map<Double, Float>>(){}.getType());
    }
}
