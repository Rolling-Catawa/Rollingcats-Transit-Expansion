package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.ResourceWorkbench;
import cn.jstxjf_.rte.util.PinyinUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImDouble;
import imgui.type.ImString;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonElement;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.com.google.gson.JsonPrimitive;
import org.mtr.mod.resource.VehicleResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ImGuiResourceWorkbenchScreen extends ImGuiScreenBase {

    private List<VehicleResource> vehicles;
    private int selectedIndex = -1;
    private JsonObject editedJson;
    private String statusLine = "";

    private final ImString search = new ImString(128);
    private final ImString copyId = new ImString(128);
    private final ImString vehicleName = new ImString(256);
    private final ImString vehicleColor = new ImString(16);
    private final ImDouble length = new ImDouble();
    private final ImDouble width = new ImDouble();
    private final ImDouble bogie1 = new ImDouble();
    private final ImDouble bogie2 = new ImDouble();
    private final ImDouble coupling1 = new ImDouble();
    private final ImDouble coupling2 = new ImDouble();
    private final Map<String, ImDouble> numberHolders = new HashMap<>();
    private final Map<String, ImString> stringHolders = new HashMap<>();

    private static final Map<String, String[]> ENUM_OPTIONS = Map.of(
        "condition", new String[]{"NORMAL", "AT_DEPOT", "ON_ROUTE_FORWARDS", "ON_ROUTE_BACKWARDS", "DOORS_CLOSED", "DOORS_OPENED", "CHRISTMAS_LIGHT_RED", "CHRISTMAS_LIGHT_YELLOW", "CHRISTMAS_LIGHT_GREEN", "CHRISTMAS_LIGHT_BLUE"},
        "renderStage", new String[]{"LIGHT", "ALWAYS_ON_LIGHT", "INTERIOR", "INTERIOR_TRANSLUCENT", "EXTERIOR"},
        "type", new String[]{"NORMAL", "DISPLAY", "FLOOR", "DOORWAY", "SEAT"},
        "doorAnimationType", new String[]{"STANDARD", "STANDARD_SLOW", "CONSTANT", "PLUG_FAST", "PLUG_SLOW", "PLUG_SLOW_2", "PLUG_SLOW_3", "BOUNCY_1", "BOUNCY_2", "MLR", "R179", "R211"},
        "displayType", new String[]{"DESTINATION", "ROUTE_NUMBER", "DEPARTURE_INDEX", "NEXT_STATION", "NEXT_STATION_KCR", "NEXT_STATION_MTR", "NEXT_STATION_UK", "ROUTE_COLOR", "ROUTE_COLOR_ROUNDED"}
    );

    public ImGuiResourceWorkbenchScreen(Screen parent) {
        super(Component.translatable("gui.rte.workbench.title"), parent);
        ResourceWorkbench.ensureDirs();
        vehicles = ResourceWorkbench.listVehicles();
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(860, 560, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.workbench.title") + "###rte_workbench");

        ImGui.textDisabled(t("gui.rte.workbench.hint"));

        ImGui.beginChild("###wb_list", 300, -36, true);
        ImGui.inputTextWithHint("###wb_search", t("gui.rte.imgui.search_hint"), search);
        final String filter = search.get().toLowerCase(Locale.ROOT);
        for (int i = 0; i < vehicles.size(); i++) {
            final VehicleResource vehicle = vehicles.get(i);
            final String label = vehicle.getId() + "  " + vehicle.getName().getString();
            if (!filter.isEmpty()) {
                final String lower = label.toLowerCase(Locale.ROOT);
                if (!lower.contains(filter)
                    && !PinyinUtils.getPinyin(lower).toLowerCase(Locale.ROOT).contains(filter)
                    && !PinyinUtils.getPinyinInitials(lower).toLowerCase(Locale.ROOT).contains(filter)) {
                    continue;
                }
            }
            if (ImGui.selectable(label + "###wb_v_" + i, i == selectedIndex)) {
                select(i);
            }
        }
        ImGui.endChild();

        ImGui.sameLine();
        ImGui.beginChild("###wb_editor", 0, -36, true);
        if (selectedIndex >= 0 && selectedIndex < vehicles.size() && editedJson != null) {
            drawEditor();
        } else {
            ImGui.textDisabled(t("gui.rte.workbench.select_hint"));
        }
        ImGui.endChild();

        if (!statusLine.isEmpty()) {
            ImGui.textColored(0.6f, 0.85f, 1f, 1f, statusLine);
            ImGui.sameLine();
        }
        if (ImGui.button(t("gui.rte.workbench.open_folder") + "###wb_folder")) {
            net.minecraft.Util.getPlatform().openFile(ResourceWorkbench.exportDir().toFile());
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.back") + "###wb_back")) {
            onClose();
        }
        ImGui.end();
    }

    private void select(int index) {
        selectedIndex = index;
        numberHolders.clear();
        stringHolders.clear();
        final VehicleResource vehicle = vehicles.get(index);
        editedJson = ResourceWorkbench.snapshot(vehicle);
        if (editedJson == null) {
            statusLine = t("gui.rte.workbench.snapshot_failed");
            selectedIndex = -1;
            return;
        }
        copyId.set(vehicle.getId() + "_edit");
        vehicleName.set(getString("name"));
        vehicleColor.set(getString("color"));
        length.set(getDouble("length"));
        width.set(getDouble("width"));
        bogie1.set(getDouble("bogie1Position"));
        bogie2.set(getDouble("bogie2Position"));
        coupling1.set(getDouble("couplingPadding1"));
        coupling2.set(getDouble("couplingPadding2"));
    }

    private void drawEditor() {
        ImGui.text(vehicles.get(selectedIndex).getId());

        if (ImGui.inputText(t("gui.rte.workbench.name") + "###wb_name", vehicleName)) {
            editedJson.addProperty("name", vehicleName.get());
        }
        if (ImGui.inputText(t("gui.rte.workbench.color") + "###wb_color", vehicleColor)) {
            editedJson.addProperty("color", vehicleColor.get());
        }
        if (ImGui.inputDouble(t("gui.rte.workbench.length") + "###wb_length", length)) {
            editedJson.addProperty("length", length.get());
        }
        if (ImGui.inputDouble(t("gui.rte.workbench.width") + "###wb_width", width)) {
            editedJson.addProperty("width", width.get());
        }
        if (ImGui.inputDouble("Bogie 1###wb_bogie1", bogie1)) {
            editedJson.addProperty("bogie1Position", bogie1.get());
        }
        if (ImGui.inputDouble("Bogie 2###wb_bogie2", bogie2)) {
            editedJson.addProperty("bogie2Position", bogie2.get());
        }
        if (ImGui.inputDouble("Coupling 1###wb_coupling1", coupling1)) {
            editedJson.addProperty("couplingPadding1", coupling1.get());
        }
        if (ImGui.inputDouble("Coupling 2###wb_coupling2", coupling2)) {
            editedJson.addProperty("couplingPadding2", coupling2.get());
        }

        drawBooleanToggle("hasGangway1", "Gangway 1");
        ImGui.sameLine();
        drawBooleanToggle("hasGangway2", "Gangway 2");
        ImGui.sameLine();
        drawBooleanToggle("hasBarrier1", "Barrier 1");
        ImGui.sameLine();
        drawBooleanToggle("hasBarrier2", "Barrier 2");

        ImGui.separator();
        if (ImGui.collapsingHeader(t("gui.rte.workbench.preview") + "###wb_preview_hdr")) {
            drawModelPreview();
        }

        if (ImGui.collapsingHeader(t("gui.rte.workbench.models") + "###wb_models")) {
            drawModelsArray("models");
            drawModelsArray("bogie1Models");
            drawModelsArray("bogie2Models");
        }

        ImGui.separator();
        ImGui.setNextItemWidth(260);
        ImGui.inputText(t("gui.rte.workbench.copy_id") + "###wb_copy_id", copyId);
        if (ImGui.button(t("gui.rte.workbench.register_copy") + "###wb_register")) {
            if (copyId.get().isBlank()) {
                statusLine = t("gui.rte.workbench.id_empty");
            } else {
                final boolean ok = ResourceWorkbench.registerCopy(editedJson, copyId.get().trim());
                statusLine = t(ok ? "gui.rte.workbench.registered" : "gui.rte.workbench.register_failed");
                if (ok) {
                    ResourceWorkbench.saveAutoload(editedJson, copyId.get().trim());
                    vehicles = ResourceWorkbench.listVehicles();
                }
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.workbench.export") + "###wb_export")) {
            final boolean ok = ResourceWorkbench.export(editedJson, copyId.get().isBlank() ? vehicles.get(selectedIndex).getId() : copyId.get().trim());
            statusLine = t(ok ? "gui.rte.workbench.exported" : "gui.rte.workbench.export_failed");
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.workbench.export_pack") + "###wb_export_pack")) {
            final String packName = copyId.get().isBlank() ? vehicles.get(selectedIndex).getId() : copyId.get().trim();
            final java.nio.file.Path zip = ResourceWorkbench.exportPack(editedJson, packName);
            statusLine = zip == null ? t("gui.rte.workbench.export_failed")
                : t("gui.rte.workbench.pack_exported", zip.getFileName().toString());
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.workbench.reset") + "###wb_reset")) {
            select(selectedIndex);
            statusLine = "";
        }
        ImGui.textDisabled(t("gui.rte.workbench.preview_note"));

        final List<String> autoloads = ResourceWorkbench.listAutoload();
        if (!autoloads.isEmpty() && ImGui.collapsingHeader(t("gui.rte.workbench.autoload") + " (" + autoloads.size() + ")###wb_autoload")) {
            for (final String fileName : autoloads) {
                ImGui.text(fileName.replaceFirst("\\.json$", ""));
                ImGui.sameLine();
                if (ImGui.button(t("gui.rte.workbench.delete_autoload") + "###wb_al_" + fileName)) {
                    ResourceWorkbench.deleteAutoload(fileName);
                    statusLine = t("gui.rte.workbench.autoload_deleted");
                }
            }
        }
    }

    private float previewYaw = 0.6f;
    private float previewPitch = -0.4f;
    private float previewZoom = 1f;
    private final imgui.type.ImInt previewModelIndex = new imgui.type.ImInt(0);

    private void drawModelPreview() {
        final List<String> modelIds = new ArrayList<>();
        final List<org.mtr.libraries.com.google.gson.JsonObject> modelObjects = new ArrayList<>();
        for (final String arrayKey : new String[]{"models", "bogie1Models", "bogie2Models"}) {
            if (editedJson.has(arrayKey) && editedJson.get(arrayKey).isJsonArray()) {
                for (final JsonElement element : editedJson.getAsJsonArray(arrayKey)) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("modelResource")) {
                        final org.mtr.libraries.com.google.gson.JsonObject model = element.getAsJsonObject();
                        modelIds.add(model.get("modelResource").getAsString());
                        modelObjects.add(model);
                    }
                }
            }
        }
        if (modelIds.isEmpty()) {
            ImGui.textDisabled(t("gui.rte.workbench.preview_no_model"));
            return;
        }
        if (previewModelIndex.get() >= modelIds.size()) {
            previewModelIndex.set(0);
        }
        final String[] labels = new String[modelIds.size()];
        for (int i = 0; i < modelIds.size(); i++) {
            labels[i] = shorten(modelIds.get(i));
        }
        ImGui.setNextItemWidth(320);
        ImGui.combo("###wb_preview_model", previewModelIndex, labels);

        final String modelId = modelIds.get(previewModelIndex.get());
        final List<float[]> mesh = cn.jstxjf_.rte.data.ResourceWorkbench.loadModelMesh(modelId,
            modelObjects.get(previewModelIndex.get()));
        final String textureId = currentTextureFor(modelId);
        final int glId = textureId == null || textureId.isBlank() ? -1 : cn.jstxjf_.rte.data.ResourceWorkbench.textureGlId(textureId);
        if (mesh.isEmpty()) {
            ImGui.textDisabled(t("gui.rte.workbench.preview_parse_failed"));
        } else {
            drawMeshViewport(mesh, glId);
        }

        if (glId > 0 && ImGui.treeNode(t("gui.rte.workbench.preview_texture") + "###wb_preview_tex")) {
            ImGui.image(glId, 320, 320);
            ImGui.treePop();
        }
    }

    private String currentTextureFor(String modelId) {
        for (final String arrayKey : new String[]{"models", "bogie1Models", "bogie2Models"}) {
            if (editedJson.has(arrayKey) && editedJson.get(arrayKey).isJsonArray()) {
                for (final JsonElement element : editedJson.getAsJsonArray(arrayKey)) {
                    if (element.isJsonObject()) {
                        final JsonObject model = element.getAsJsonObject();
                        if (model.has("modelResource") && model.get("modelResource").getAsString().equals(modelId) && model.has("textureResource")) {
                            return model.get("textureResource").getAsString();
                        }
                    }
                }
            }
        }
        return null;
    }

    private void drawMeshViewport(List<float[]> mesh, int textureGlId) {
        final float viewWidth = Math.max(320, ImGui.getContentRegionAvailX() - 8);
        final float viewHeight = 340;
        final float originX = ImGui.getCursorScreenPosX();
        final float originY = ImGui.getCursorScreenPosY();

        final imgui.ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(originX, originY, originX + viewWidth, originY + viewHeight, 0xE0141418);
        drawList.addRect(originX, originY, originX + viewWidth, originY + viewHeight, 0xFF42423A);

        ImGui.invisibleButton("###wb_viewport", viewWidth, viewHeight);
        if (ImGui.isItemActive()) {
            if (ImGui.getIO().getMouseDown(1)) {
                previewZoom = Math.max(0.1f, Math.min(10f, previewZoom * (1 - ImGui.getIO().getMouseDeltaY() * 0.01f)));
            } else {
                previewYaw += ImGui.getIO().getMouseDeltaX() * 0.01f;
                previewPitch += ImGui.getIO().getMouseDeltaY() * 0.01f;
                previewPitch = Math.max(-1.5f, Math.min(1.5f, previewPitch));
            }
        }

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (final float[] quad : mesh) {
            for (int c = 0; c < 4; c++) {
                minX = Math.min(minX, quad[c * 3]);
                minY = Math.min(minY, quad[c * 3 + 1]);
                minZ = Math.min(minZ, quad[c * 3 + 2]);
                maxX = Math.max(maxX, quad[c * 3]);
                maxY = Math.max(maxY, quad[c * 3 + 1]);
                maxZ = Math.max(maxZ, quad[c * 3 + 2]);
            }
        }
        final float centerX = (minX + maxX) / 2f;
        final float centerY = (minY + maxY) / 2f;
        final float centerZ = (minZ + maxZ) / 2f;
        final float extent = Math.max(1, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
        final float scale = Math.min(viewWidth, viewHeight) * 0.8f / extent * previewZoom;

        final float cosYaw = (float) Math.cos(previewYaw);
        final float sinYaw = (float) Math.sin(previewYaw);
        final float cosPitch = (float) Math.cos(previewPitch);
        final float sinPitch = (float) Math.sin(previewPitch);
        final float screenCx = originX + viewWidth / 2f;
        final float screenCy = originY + viewHeight / 2f;

        final List<float[]> projected = new ArrayList<>(mesh.size());
        for (final float[] quad : mesh) {
            final float[] out = new float[14];
            float depth = 0;
            for (int c = 0; c < 4; c++) {
                final float wx = quad[c * 3] - centerX;
                final float wy = quad[c * 3 + 1] - centerY;
                final float wz = quad[c * 3 + 2] - centerZ;
                final float rx = wx * cosYaw - wz * sinYaw;
                final float rz = wx * sinYaw + wz * cosYaw;
                final float ry = wy * cosPitch - rz * sinPitch;
                final float rd = wy * sinPitch + rz * cosPitch;
                out[c * 2] = screenCx + rx * scale;
                out[c * 2 + 1] = screenCy - ry * scale;
                depth += rd;
            }
            out[8] = depth / 4f;
            out[9] = quad[12];
            out[10] = quad[13];
            out[11] = quad[14];
            out[12] = quad[15];
            out[13] = quad[16];
            projected.add(out);
        }
        projected.sort((a, b) -> Float.compare(a[8], b[8]));

        drawList.pushClipRect(originX, originY, originX + viewWidth, originY + viewHeight, true);
        for (final float[] q : projected) {
            final int dir = ((int) q[13]) % 6;
            final float shade = switch (dir) {
                case 4 -> 1.0f;
                case 0, 1 -> 0.85f;
                case 2, 3 -> 0.72f;
                default -> 0.55f;
            };
            final int gray = (int) (255 * shade);
            final int color = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
            if (textureGlId > 0 && q[13] < 6) {
                drawList.addImageQuad(textureGlId,
                    q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7],
                    q[9], q[10], q[11], q[10], q[11], q[12], q[9], q[12],
                    color);
            } else {
                drawList.addQuadFilled(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], color);
            }
        }
        drawList.popClipRect();

        final float[] zoomHolder = {previewZoom};
        ImGui.setNextItemWidth(220);
        if (ImGui.sliderFloat(t("gui.rte.workbench.preview_zoom") + "###wb_zoom", zoomHolder, 0.1f, 10f, "%.1fx")) {
            previewZoom = zoomHolder[0];
        }
        ImGui.sameLine();
        ImGui.textDisabled(t("gui.rte.workbench.preview_hint", mesh.size()));
    }

    private void drawModelsArray(String key) {
        if (!editedJson.has(key) || !editedJson.get(key).isJsonArray()) {
            return;
        }
        final JsonArray array = editedJson.getAsJsonArray(key);
        if (array.size() == 0) {
            return;
        }
        if (ImGui.treeNode(key + " (" + array.size() + ")###wbjson_" + key)) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).isJsonObject()) {
                    final JsonObject model = array.get(i).getAsJsonObject();
                    final String label = model.has("modelResource") ? model.get("modelResource").getAsString() : "#" + i;
                    if (ImGui.treeNode(shorten(label) + "###wbjson_" + key + "_" + i)) {
                        drawJsonObject(model, key + "." + i);
                        ImGui.treePop();
                    }
                }
            }
            ImGui.treePop();
        }
    }

    private void drawJsonObject(JsonObject obj, String path) {
        for (final String key : new ArrayList<>(obj.keySet())) {
            final JsonElement value = obj.get(key);
            final String childPath = path + "." + key;
            if (value == null || value.isJsonNull()) {
                continue;
            }
            if (value.isJsonPrimitive()) {
                drawJsonPrimitive(obj, key, value.getAsJsonPrimitive(), childPath);
            } else if (value.isJsonObject()) {
                if (ImGui.treeNode(key + "###wbjson_" + childPath)) {
                    drawJsonObject(value.getAsJsonObject(), childPath);
                    ImGui.treePop();
                }
            } else if (value.isJsonArray()) {
                final JsonArray array = value.getAsJsonArray();
                if (array.size() > 0 && ImGui.treeNode(key + " (" + array.size() + ")###wbjson_" + childPath)) {
                    for (int i = 0; i < array.size(); i++) {
                        final JsonElement element = array.get(i);
                        if (element.isJsonObject()) {
                            String label = "#" + (i + 1);
                            final JsonObject elementObject = element.getAsJsonObject();
                            for (final String probe : new String[]{"name", "id", "modelResource"}) {
                                if (elementObject.has(probe) && elementObject.get(probe).isJsonPrimitive()) {
                                    label = shorten(elementObject.get(probe).getAsString());
                                    break;
                                }
                            }
                            if (ImGui.treeNode(label + "###wbjson_" + childPath + "_" + i)) {
                                drawJsonObject(elementObject, childPath + "." + i);
                                ImGui.treePop();
                            }
                        } else if (element.isJsonPrimitive()) {
                            ImGui.textDisabled("- " + element.getAsString());
                        }
                    }
                    ImGui.treePop();
                }
            }
        }
    }

    private void drawJsonPrimitive(JsonObject owner, String key, JsonPrimitive primitive, String path) {
        final String id = "###wbjson_" + path;
        if (primitive.isBoolean()) {
            if (ImGui.checkbox(key + id, primitive.getAsBoolean())) {
                owner.addProperty(key, !primitive.getAsBoolean());
            }
            return;
        }
        if (primitive.isNumber()) {
            final ImDouble holder = numberHolders.computeIfAbsent(path, k -> new ImDouble(primitive.getAsDouble()));
            ImGui.setNextItemWidth(160);
            if (ImGui.inputDouble(key + id, holder)) {
                owner.addProperty(key, holder.get());
            }
            return;
        }
        final String current = primitive.getAsString();
        final String[] options = ENUM_OPTIONS.get(key);
        if (options != null) {
            int index = 0;
            for (int i = 0; i < options.length; i++) {
                if (options[i].equalsIgnoreCase(current)) {
                    index = i;
                    break;
                }
            }
            final imgui.type.ImInt holder = new imgui.type.ImInt(index);
            ImGui.setNextItemWidth(220);
            if (ImGui.combo(key + id, holder, options)) {
                owner.addProperty(key, options[holder.get()]);
            }
            return;
        }
        final ImString holder = stringHolders.computeIfAbsent(path, k -> new ImString(current, 512));
        ImGui.setNextItemWidth(320);
        if (ImGui.inputText(key + id, holder)) {
            owner.addProperty(key, holder.get());
        }
        if (key.equals("textureResource")) {
            drawLocalFilePicker(owner, key, path, ResourceWorkbench.listTextureFiles(), true);
        } else if (key.equals("modelResource")) {
            drawLocalFilePicker(owner, key, path, ResourceWorkbench.listModelFiles(), false);
        }
    }

    private void drawLocalFilePicker(JsonObject owner, String key, String path, List<String> files, boolean isTexture) {
        if (files.isEmpty()) {
            return;
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.workbench.local_file") + "###wbpick_" + path)) {
            ImGui.openPopup("###wbpick_popup_" + path);
        }
        if (ImGui.beginPopup("###wbpick_popup_" + path)) {
            for (final String file : files) {
                if (ImGui.menuItem(file + "###wbpick_item_" + path + "_" + file)) {
                    final String newId = isTexture
                        ? ResourceWorkbench.importLocalTexture(file)
                        : ResourceWorkbench.localModelId(file);
                    if (newId != null) {
                        owner.addProperty(key, newId);
                        stringHolders.remove(path);
                        statusLine = t("gui.rte.workbench.local_file_set", file);
                    } else {
                        statusLine = t("gui.rte.workbench.local_file_failed", file);
                    }
                }
            }
            ImGui.endPopup();
        }
    }

    private static String shorten(String value) {
        return value.length() > 42 ? "…" + value.substring(value.length() - 41) : value;
    }

    private void drawBooleanToggle(String key, String label) {
        final boolean current = editedJson.has(key) && !editedJson.get(key).isJsonNull() && editedJson.get(key).getAsBoolean();
        if (ImGui.checkbox(label + "###wb_" + key, current)) {
            editedJson.addProperty(key, !current);
        }
    }

    private String getString(String key) {
        try {
            return editedJson.has(key) && !editedJson.get(key).isJsonNull() ? editedJson.get(key).getAsString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private double getDouble(String key) {
        try {
            return editedJson.has(key) && !editedJson.get(key).isJsonNull() ? editedJson.get(key).getAsDouble() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
