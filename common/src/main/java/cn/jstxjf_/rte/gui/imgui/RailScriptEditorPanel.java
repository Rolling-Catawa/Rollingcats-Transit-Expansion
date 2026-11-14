package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.mechanism.RailScriptStore;
import cn.jstxjf_.rte.network.PacketAssignRailScript;
import cn.jstxjf_.rte.network.PacketRailScriptContent;
import cn.jstxjf_.rte.network.PacketRequestRailScript;
import cn.jstxjf_.rte.network.PacketSaveRailScript;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RailScriptEditorPanel {

    private static final String[] KEYWORDS = {
        "break", "case", "catch", "class", "const", "continue", "default", "delete", "do", "else", "export", "extends",
        "finally", "for", "function", "if", "import", "in", "instanceof", "let", "new", "return", "switch", "this",
        "throw", "try", "typeof", "var", "void", "while", "with", "yield", "true", "false", "null", "undefined"
    };

    private static final ImString GROUP = new ImString(cn.jstxjf_.rte.mechanism.RailScriptPolicy.MAX_GROUP_LENGTH);
    private static final ImString NEW_ID = new ImString(64);
    private static final ImBoolean OPEN = new ImBoolean(false);
    private static final Map<String, Map<Integer, String>> ERRORS = new HashMap<>();
    private static List<String> ids = new ArrayList<>();
    private static String selectedId = "";
    private static String loadedId = "";
    private static TextEditor serverEditor;
    private static TextEditor clientEditor;
    private static TextEditor metaEditor;
    private static String lastRailId = "";
    private static boolean requested;

    private RailScriptEditorPanel() {
    }

    private static String t(String key) {
        return Component.translatable(key).getString();
    }

    public static void drawSection(Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        if (!rail.getHexId().equals(lastRailId)) {
            lastRailId = rail.getHexId();
            GROUP.set(extra.getMechanismGroup());
            selectedId = extra.getRailScriptId();
        }
        if (!requested) {
            requested = true;
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketRequestRailScript(""));
        }
        if (!ImGui.collapsingHeader(t("gui.rte.rail_script.header") + "###rte_rail_script")) {
            return;
        }
        ImGui.inputText(t("gui.rte.rail_script.group") + "###rte_rail_script_group", GROUP);
        if (ImGui.beginCombo(t("gui.rte.rail_script.script") + "###rte_rail_script_id", selectedId.isEmpty() ? "-" : selectedId)) {
            if (ImGui.selectable("-", selectedId.isEmpty())) {
                selectedId = "";
            }
            for (final String id : ids) {
                if (ImGui.selectable(id, id.equals(selectedId))) {
                    selectedId = id;
                }
            }
            ImGui.endCombo();
        }
        if (ImGui.button(t("gui.rte.rail_script.apply") + "###rte_rail_script_apply")) {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(
                new PacketAssignRailScript(List.of(rail.getHexId()), GROUP.get(), selectedId));
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.rail_script.clear") + "###rte_rail_script_clear_group")) {
            GROUP.set("");
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketAssignRailScript(List.of(rail.getHexId()), "", ""));
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.rail_script.edit") + "###rte_rail_script_edit")) {
            OPEN.set(true);
            if (!selectedId.isEmpty()) {
                cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketRequestRailScript(selectedId));
            }
        }
        ImGui.inputText(t("gui.rte.rail_script.new_id") + "###rte_rail_script_new_id", NEW_ID);
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.rail_script.create") + "###rte_rail_script_create")) {
            final String id = NEW_ID.get().trim();
            if (RailScriptStore.isValidId(id)) {
                selectedId = id;
                loadedId = id;
                editors();
                serverEditor.setText(RailScriptTemplates.SERVER);
                clientEditor.setText(RailScriptTemplates.CLIENT);
                metaEditor.setText(RailScriptTemplates.META);
                OPEN.set(true);
            }
        }
        if (!RailScriptStore.isValidId(NEW_ID.get().trim()) && !NEW_ID.get().isEmpty()) {
            ImGui.textColored(1F, 0.4F, 0.4F, 1F, t("gui.rte.rail_script.bad_id"));
        }
    }

    public static void drawWindow() {
        if (!OPEN.get() || loadedId.isEmpty()) {
            return;
        }
        editors();
        ImGui.setNextWindowSize(900, 640, ImGuiCond.FirstUseEver);
        if (!ImGui.begin(t("gui.rte.rail_script.editor_title") + " - " + loadedId + "###rte_rail_script_editor", OPEN)) {
            ImGui.end();
            return;
        }
        if (ImGui.button(t("gui.rte.rail_script.save") + "###rte_rail_script_save")) {
            PacketSaveRailScript.send(loadedId, RailScriptStore.Part.META, metaEditor.getText());
            PacketSaveRailScript.send(loadedId, RailScriptStore.Part.CLIENT, clientEditor.getText());
            PacketSaveRailScript.send(loadedId, RailScriptStore.Part.SERVER, serverEditor.getText());
            requested = false;
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.rail_script.reload") + "###rte_rail_script_reload")) {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketRequestRailScript(loadedId));
        }
        final Map<Integer, String> errors = ERRORS.getOrDefault(loadedId, Map.of());
        if (!errors.isEmpty()) {
            ImGui.sameLine();
            ImGui.textColored(1F, 0.4F, 0.4F, 1F, t("gui.rte.rail_script.errors") + " " + errors.size());
        }
        if (ImGui.beginTabBar("###rte_rail_script_tabs")) {
            if (ImGui.beginTabItem("server.js###rte_rail_script_tab_server")) {
                serverEditor.setErrorMarkers(errors);
                serverEditor.render("###rte_rail_script_server");
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("client.js###rte_rail_script_tab_client")) {
                clientEditor.render("###rte_rail_script_client");
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("json###rte_rail_script_tab_meta")) {
                metaEditor.render("###rte_rail_script_meta");
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

    private static void editors() {
        if (serverEditor != null) {
            return;
        }
        serverEditor = editor(true);
        clientEditor = editor(true);
        metaEditor = editor(false);
    }

    private static TextEditor editor(boolean javascript) {
        final TextEditor editor = new TextEditor();
        editor.setTabSize(2);
        editor.setShowWhitespaces(false);
        if (javascript) {
            final TextEditorLanguageDefinition language = new TextEditorLanguageDefinition();
            language.setName("JavaScript");
            language.setKeywords(KEYWORDS);
            language.setSingleLineComment("//");
            language.setCommentStart("/*");
            language.setCommentEnd("*/");
            language.setAutoIdentation(true);
            final Map<String, Integer> tokens = new java.util.LinkedHashMap<>();
            tokens.put("\"(\\\\.|[^\"])*\"", TextEditorPaletteIndex.String);
            tokens.put("'(\\\\.|[^'])*'", TextEditorPaletteIndex.String);
            tokens.put("`(\\\\.|[^`])*`", TextEditorPaletteIndex.String);
            tokens.put("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?", TextEditorPaletteIndex.Number);
            tokens.put("0[xX][0-9a-fA-F]+", TextEditorPaletteIndex.Number);
            tokens.put("[a-zA-Z_$][a-zA-Z0-9_$]*", TextEditorPaletteIndex.Identifier);
            tokens.put("[\\[\\]\\{\\}\\!\\%\\^\\&\\*\\(\\)\\-\\+\\=\\~\\|\\<\\>\\?\\/\\;\\,\\.\\:]", TextEditorPaletteIndex.Punctuation);
            language.setTokenRegexStrings(tokens);
            editor.setLanguageDefinition(language);
        }
        return editor;
    }

    public static void acceptContent(PacketRailScriptContent content) {
        net.minecraft.client.Minecraft.getInstance().execute(() -> {
            ids = new ArrayList<>(content.ids);
            if (content.id.isEmpty()) {
                return;
            }
            editors();
            loadedId = content.id;
            selectedId = content.id;
            serverEditor.setText(content.serverSource.isEmpty() ? RailScriptTemplates.SERVER : content.serverSource);
            clientEditor.setText(content.clientSource);
            metaEditor.setText(content.meta.isEmpty() ? RailScriptTemplates.META : content.meta);
        });
    }

    public static void acceptErrors(String scriptId, Map<Integer, String> errors) {
        net.minecraft.client.Minecraft.getInstance().execute(() -> {
            if (errors.isEmpty()) {
                ERRORS.remove(scriptId);
            } else {
                ERRORS.put(scriptId, new HashMap<>(errors));
            }
            if (!ids.contains(scriptId) && RailScriptStore.isValidId(scriptId)) {
                ids.add(scriptId);
                ids.sort(String::compareTo);
            }
        });
    }
}
