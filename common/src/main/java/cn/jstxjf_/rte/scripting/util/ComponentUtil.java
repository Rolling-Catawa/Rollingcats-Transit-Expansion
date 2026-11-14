package cn.jstxjf_.rte.scripting.util;

import net.minecraft.network.chat.Component;

public interface ComponentUtil {

    public static Component translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    public static Component literal(String text) {
        return Component.literal(text);
    }

    public static String getString(Component component) {
        return component.getString();
    }
}
