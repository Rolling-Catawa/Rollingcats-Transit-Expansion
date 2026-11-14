package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.Tree;
import cn.jstxjf_.rte.util.PinyinUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public class ImGuiSelectScreen extends ImGuiScreenBase {

    private record Row(String key, String label, Runnable onOpenBranch) {
    }

    private final Supplier<Screen> parentSupplier;
    private final String screenKey;
    private final String headerText;
    private final String instructionLink;
    private final Supplier<String> getSelected;
    private final ImGuiSelectScreen.SelectedCallback onSelected;
    private final TreeMap<String, Row> rows = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final ImString search = new ImString(128);
    private Runnable onMerge;

    public ImGuiSelectScreen(Supplier<Screen> parent, MutableComponent screenKey, Map<String, String> map, Supplier<String> getSelected, ImGuiSelectScreen.SelectedCallback onSelected, String instructionLink) {
        super(Component.literal("SelectScreen"), null);
        this.parentSupplier = parent;
        this.screenKey = screenKey.getString();
        this.headerText = screenKey.getString();
        this.instructionLink = instructionLink;
        this.getSelected = getSelected;
        this.onSelected = onSelected;
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            rows.put(entry.getKey(), new Row(entry.getKey(), Component.translatable(entry.getValue()).getString(), null));
        }
    }

    public ImGuiSelectScreen(Supplier<Screen> parent, MutableComponent screenKey, List<String> list, Supplier<String> getSelected, ImGuiSelectScreen.SelectedCallback onSelected, String instructionLink) {
        super(Component.literal("SelectScreen"), null);
        this.parentSupplier = parent;
        this.screenKey = screenKey.getString();
        this.headerText = screenKey.getString();
        this.instructionLink = instructionLink;
        this.getSelected = getSelected;
        this.onSelected = onSelected;
        for (final String value : list) {
            rows.put(value, new Row(value, Component.translatable(value).getString(), null));
        }
    }

    public <T> ImGuiSelectScreen(Supplier<Screen> parent, Tree.Branch<T> tree, Supplier<String> getSelected, ImGuiSelectScreen.SelectedCallback onSelected, String instructionLink) {
        super(Component.literal("SelectScreen"), null);
        this.parentSupplier = parent;
        this.screenKey = tree.getPathKey();
        this.headerText = tree.getPathName().getString();
        this.instructionLink = instructionLink;
        this.getSelected = getSelected;
        this.onSelected = onSelected;
        for (final Tree.Node<T> node : tree.getNodes().values()) {
            if (node instanceof Tree.Data<T> data) {
                rows.put(node.key, new Row(data.key, data.name.getString(), null));
            } else if (node instanceof Tree.Branch<T> branch) {
                rows.put(node.key, new Row(node.key, "▶ " + branch.name.getString(), () ->
                    minecraft.setScreen(new ImGuiSelectScreen(() -> this, branch, getSelected, onSelected, instructionLink))));
            }
        }
        if (tree.hasSubBranches()) {
            onMerge = () -> {
                final Map<String, Tree.Data<T>> leaves = tree.mergeLevel();
                final Map<String, String> map = new HashMap<>();
                for (final Map.Entry<String, Tree.Data<T>> entry : leaves.entrySet()) {
                    map.put(entry.getKey(), entry.getValue().name.getString());
                }
                minecraft.setScreen(new ImGuiSelectScreen(() -> this,
                    Component.translatable("gui.rte.select_screen.merged").append(tree.getPathName()), map, getSelected, onSelected, instructionLink));
            };
        }
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(520, 560, ImGuiCond.FirstUseEver);
        ImGui.begin(headerText + "###rte_select_" + screenKey);

        ImGui.inputTextWithHint("###select_search", t("gui.rte.imgui.search_hint"), search);
        if (onMerge != null) {
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.select_screen.merge") + "###select_merge")) {
                onMerge.run();
            }
        }

        final String selected = getSelected == null ? "" : (getSelected.get() == null ? "" : getSelected.get());
        final String filter = search.get().toLowerCase(Locale.ROOT);

        ImGui.beginChild("###select_list", 0, -60, true);
        for (final Row row : filteredRows(filter)) {
            final boolean isSelected = row.key().equals(selected);
            if (row.onOpenBranch() != null) {
                ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 0.95f, 0.85f, 0.35f, 1f);
                final boolean clicked = ImGui.selectable(row.label() + "###row_" + row.key());
                ImGui.popStyleColor();
                if (clicked) {
                    row.onOpenBranch().run();
                }
            } else {
                if (ImGui.selectable((isSelected ? "● " : "") + row.label() + "###row_" + row.key(), isSelected) && !isSelected) {
                    onSelected.accept(minecraft, null, row.key());
                }
            }
        }
        ImGui.endChild();

        if (instructionLink != null && !instructionLink.isEmpty()) {
            if (ImGui.button(t("tooltip.rte.select_screen.info") + "###select_link")) {
                final Screen self = this;
                minecraft.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) {
                        Util.getPlatform().openUri(instructionLink);
                    }
                    minecraft.setScreen(self);
                }, instructionLink, true));
            }
            ImGui.sameLine();
        }
        if (ImGui.button(t("gui.rte.imgui.back") + "###select_back")) {
            onClose();
        }
        ImGui.end();
    }

    private List<Row> filteredRows(String filter) {
        final List<Row> result = new ArrayList<>();
        for (final Row row : rows.values()) {
            if (filter.isEmpty()) {
                result.add(row);
                continue;
            }
            final String label = row.label().toLowerCase(Locale.ROOT);
            if (label.contains(filter)
                || PinyinUtils.getPinyin(label).toLowerCase(Locale.ROOT).contains(filter)
                || PinyinUtils.getPinyinInitials(label).toLowerCase(Locale.ROOT).contains(filter)) {
                result.add(row);
            }
        }
        return result;
    }

    @Override
    public void onClose() {
        ImGuiWorkspace.close(this);
        minecraft.setScreen(parentSupplier == null ? null : parentSupplier.get());
    }

    public static Screen create(Supplier<Screen> parent, MutableComponent screenKey, Map<String, String> map, Supplier<String> getSelected, SelectedCallback onSelected, String instructionLink) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent == null ? null : parent.get();
        }
        return new ImGuiSelectScreen(parent, screenKey, map, getSelected, onSelected, instructionLink);
    }

    public static <T> Screen create(Supplier<Screen> parent, Tree.Branch<T> tree, Supplier<String> getSelected, SelectedCallback onSelected, String instructionLink) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent == null ? null : parent.get();
        }
        return new ImGuiSelectScreen(parent, tree, getSelected, onSelected, instructionLink);
    }

    public interface SelectedCallback {
        void accept(net.minecraft.client.Minecraft mc, Screen screen, String selected);
    }
}
