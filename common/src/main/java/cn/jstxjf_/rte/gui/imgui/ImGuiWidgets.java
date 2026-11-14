package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.ConfigResponder;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImGuiWidgets {

    private static final Map<String, ImString> TEXT_BUFFERS = new HashMap<>();

    private ImGuiWidgets() {
    }

    public static ImString textBuffer(String id, String currentValue) {
        final ImString buffer = TEXT_BUFFERS.computeIfAbsent(id, k -> new ImString(currentValue == null ? "" : currentValue, 512));
        if (!ImGui.isAnyItemActive() && currentValue != null && !buffer.get().equals(currentValue)) {
            buffer.set(currentValue);
        }
        return buffer;
    }

    public static void clearTextBuffers(String idPrefix) {
        TEXT_BUFFERS.keySet().removeIf(key -> key.startsWith(idPrefix));
    }

    public static boolean drawResponders(String idPrefix, Map<String, String> configs, Map<String, ConfigResponder> responders) {
        if (configs == null || configs.isEmpty()) {
            return false;
        }
        boolean changed = false;
        final List<String> keys = new ArrayList<>(configs.keySet());
        Collections.sort(keys);
        boolean headerShown = false;
        for (final String key : keys) {
            final ConfigResponder responder = responders == null ? null : responders.get(key);
            if (responder == null) {
                continue;
            }
            if (!headerShown) {
                ImGui.text(t("gui.rte.custom_config.editable"));
                headerShown = true;
            }
            final String id = "###" + idPrefix + "_" + key;
            if (responder instanceof ConfigResponder.BooleanToggle toggle) {
                boolean now = toggle.defaultValue;
                try {
                    now = Boolean.parseBoolean(configs.get(key));
                } catch (Exception ignored) {
                }
                if (ImGui.checkbox(toggle.name.getString() + id, now)) {
                    final boolean next = !now;
                    configs.put(key, String.valueOf(next));
                    toggle.saveConsumer.accept(next);
                    changed = true;
                }
            } else if (responder instanceof ConfigResponder.DoubleSlider slider) {
                float now = (float) slider.defaultValue;
                try {
                    now = Float.parseFloat(configs.get(key));
                } catch (Exception ignored) {
                }
                final float[] holder = {now};
                if (ImGui.sliderFloat(slider.name.getString() + id, holder, (float) slider.min, (float) slider.max)) {
                    final double value = holder[0];
                    configs.put(key, String.valueOf(value));
                    slider.saveConsumer.accept(value);
                    changed = true;
                }
            } else if (responder instanceof ConfigResponder.CycleToggle cycle) {
                int now = cycle.defaultValue;
                try {
                    now = Integer.parseInt(configs.get(key));
                } catch (Exception ignored) {
                }
                final ImInt holder = new ImInt(Math.max(0, Math.min(cycle.values.size() - 1, now)));
                if (ImGui.combo(cycle.name.getString() + id, holder, cycle.values.toArray(new String[0]))) {
                    configs.put(key, String.valueOf(holder.get()));
                    cycle.saveConsumer.accept(holder.get());
                    changed = true;
                }
            } else if (responder instanceof ConfigResponder.TextField field) {
                final ImString buffer = textBuffer(idPrefix + "_" + key, field.transformer.apply(configs.get(key)));
                if (ImGui.inputText(field.name.getString() + id, buffer)) {
                    configs.put(key, buffer.get());
                    field.saveConsumer.accept(buffer.get());
                    changed = true;
                }
            }
        }
        for (final String key : keys) {
            if (responders == null || !responders.containsKey(key)) {
                ImGui.textDisabled(key + " : " + configs.get(key));
            }
        }
        return changed;
    }

    private static String t(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }
}
