package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ScriptDebugOverlay {

    private static final int MAX_CONTEXTS = 12;
    private static final int MAX_ENTRIES = 10;
    private static final int MAX_LINES = 48;
    private static final int MAX_VALUE_LENGTH = 160;

    private ScriptDebugOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        if (!ClientConfig.scriptDebugOverlay) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options.hideGui || minecraft.player == null) {
            return;
        }

        final List<ContextSnapshot> contexts = snapshotContexts();
        final List<Line> lines = new ArrayList<>();
        lines.add(new Line("RTE JS Debug  contexts=" + ScriptContextManager.livingContexts.size(), 0xFF7FE7FF));
        if (contexts.isEmpty()) {
            lines.add(new Line("No active script debug data", 0xFFAAAAAA));
        } else {
            for (ContextSnapshot context : contexts) {
                if (lines.size() >= MAX_LINES) {
                    break;
                }
                lines.add(new Line(context.title, context.failed ? 0xFFFF7777 : 0xFFFFFF77));
                int count = 0;
                for (Map.Entry<String, Object> entry : context.entries) {
                    if (count++ >= MAX_ENTRIES || lines.size() >= MAX_LINES) {
                        break;
                    }
                    lines.add(new Line("  " + entry.getKey() + ": " + formatValue(entry.getValue()), 0xFFF2F2F2));
                }
                if (context.failed && lines.size() < MAX_LINES) {
                    lines.add(new Line("  error: " + formatValue(context.error), 0xFFFF7777));
                }
            }
        }

        final Font font = minecraft.font;
        int width = 0;
        for (Line line : lines) {
            width = Math.max(width, font.width(line.text));
        }
        final int x = 4;
        final int y = 4;
        final int lineHeight = font.lineHeight + 2;
        graphics.fill(x - 2, y - 2, x + width + 3, y + lines.size() * lineHeight + 1, 0xA0000000);
        for (int i = 0; i < lines.size(); i++) {
            final Line line = lines.get(i);
            graphics.drawString(font, line.text, x, y + i * lineHeight, line.color, false);
        }
    }

    private static List<ContextSnapshot> snapshotContexts() {
        final List<ContextSnapshot> result = new ArrayList<>();
        for (Map.Entry<AbstractScriptContext, ScriptHolderBase> living : ScriptContextManager.livingContexts.entrySet()) {
            final AbstractScriptContext context = living.getKey();
            final ScriptHolderBase holder = living.getValue();
            final List<Map.Entry<String, Object>> entries = new ArrayList<>(context.getDebugInfo().entryList());
            if (entries.isEmpty() && holder.failException == null) {
                continue;
            }
            final String name = holder.name == null || holder.name.isBlank() ? "<unnamed>" : truncate(holder.name);
            final String type = holder.contextTypeName == null || holder.contextTypeName.isBlank() ? "script" : truncate(holder.contextTypeName);
            final String state = context.scriptStatus != null && !context.scriptStatus.isDone() ? " running" : "";
            final double millis = context.lastExecuteDuration / 1_000_000.0;
            final String title = name + " [" + type + "/" + holder.activeEngineName() + "] "
                + String.format(java.util.Locale.ROOT, "%.2f ms", millis) + state;
            result.add(new ContextSnapshot(title, entries, holder.failException));
        }
        result.sort(Comparator.comparing(snapshot -> snapshot.title));
        return result.size() > MAX_CONTEXTS ? new ArrayList<>(result.subList(0, MAX_CONTEXTS)) : result;
    }

    private static String formatValue(Object value) {
        String text;
        try {
            text = String.valueOf(value);
        } catch (Throwable throwable) {
            text = "<" + throwable.getClass().getSimpleName() + ">";
        }
        return truncate(text.replace('\n', ' ').replace('\r', ' ').replace('\t', ' '));
    }

    private static String truncate(String text) {
        return text.length() > MAX_VALUE_LENGTH ? text.substring(0, MAX_VALUE_LENGTH - 3) + "..." : text;
    }
    private record ContextSnapshot(String title, List<Map.Entry<String, Object>> entries, Throwable error, boolean failed) {
        private ContextSnapshot(String title, List<Map.Entry<String, Object>> entries, Throwable error) {
            this(title, entries, error, error != null);
        }
    }

    private record Line(String text, int color) {
    }
}
