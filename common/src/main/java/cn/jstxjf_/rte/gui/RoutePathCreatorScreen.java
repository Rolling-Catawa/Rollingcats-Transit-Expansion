package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.data.IRoute;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import cn.jstxjf_.rte.item.RoutePathCreator;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.data.Route;
import org.mtr.core.data.TransportMode;
import org.mtr.core.operation.UpdateDataRequest;
import org.mtr.mapping.holder.Hand;
import org.mtr.mod.InitClient;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.packet.PacketUpdateData;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RoutePathCreatorScreen extends Screen {

    private static final String MIXED_VALUE = "<mixed>";

    private final InteractionHand hand;
    private final Screen parent;

    private final List<PathData> pathData = new ArrayList<>();
    private final List<WidgetLabel> pathParts = new ArrayList<>();
    private final Set<Integer> selectedParts = new HashSet<>();

    private CompoundTag tag = new CompoundTag();
    private int currentPage = 0;
    private boolean refreshingFields = false;
    private int selectionAnchor = -1;

    private final imgui.type.ImString imRailType = new imgui.type.ImString(64);
    private final imgui.type.ImString imDwellTime = new imgui.type.ImString(16);
    private final imgui.type.ImString imRouteName = new imgui.type.ImString(128);
    private final imgui.type.ImString imRouteColor = new imgui.type.ImString(8);
    private boolean imRailTypeInvalid;
    private boolean imDwellInvalid;
    private boolean imFieldsInitialized;

    public RoutePathCreatorScreen(InteractionHand hand, Screen parent) {
        super(Component.literal("Route Path Creator Screen"));
        this.hand = hand;
        this.parent = parent;
        pullData();
    }

    public static void open(boolean mainHand) {
        Minecraft.getInstance().setScreen(new RoutePathCreatorScreen(
            mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
            null
        ));
    }

    @Override
    protected void init() {
        pullData();
        clearWidgets();

        for (final WidgetLabel label : pathParts) {
            addRenderableWidget(label);
        }

        imFieldsInitialized = false;
        refreshSelectedFields();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updatePageWidgets();
        layoutPathLabels();

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.beginFrame();
        buildImGuiPanel();
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.endFrame();
    }

    private void buildImGuiPanel() {
        if (!imFieldsInitialized) {
            syncImGuiFields();
            imRouteName.set(getRouteName());
            imRouteColor.set(String.format(Locale.ROOT, "%06X", getRouteColor()));
            imFieldsInitialized = true;
        }

        imgui.ImGui.setNextWindowPos(imgui.ImGui.getIO().getDisplaySizeX() - 360, 40, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.setNextWindowSize(340, 0, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.begin(title.getString() + "###rte_route_path_panel", imgui.flag.ImGuiWindowFlags.NoCollapse);

        final boolean hasPath = !pathData.isEmpty();
        if (!hasPath) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button("-###rpc_remove")) {
            removeLast();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("Clear###rpc_clear")) {
            clearPath();
        }
        if (!hasPath) {
            imgui.ImGui.endDisabled();
        }

        imgui.ImGui.separator();
        final boolean hasSelection = getSelectedIndex() != null;
        if (!hasSelection) {
            imgui.ImGui.beginDisabled();
        }
        if (imRailTypeInvalid) {
            imgui.ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 1f, 0.35f, 0.35f, 1f);
        }
        if (imgui.ImGui.inputText("Rail Type###rpc_rail_type", imRailType)) {
            imguiApplyRailType(imRailType.get());
        }
        if (imRailTypeInvalid) {
            imgui.ImGui.popStyleColor();
        }
        if (imDwellInvalid) {
            imgui.ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 1f, 0.35f, 0.35f, 1f);
        }
        if (imgui.ImGui.inputText("Dwell (s)###rpc_dwell", imDwellTime)) {
            imguiApplyDwell(imDwellTime.get());
        }
        if (imDwellInvalid) {
            imgui.ImGui.popStyleColor();
        }
        if (!hasSelection) {
            imgui.ImGui.endDisabled();
        }

        imgui.ImGui.separator();
        final boolean canPrev = currentPage > 0;
        if (!canPrev) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button("<###rpc_prev") && canPrev) {
            changePage(-1);
        }
        if (!canPrev) {
            imgui.ImGui.endDisabled();
        }
        imgui.ImGui.sameLine();
        imgui.ImGui.text(String.format(Locale.ROOT, "%d / %d", currentPage + 1, maxPage() + 1));
        imgui.ImGui.sameLine();
        final boolean canNext = currentPage < maxPage();
        if (!canNext) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button(">###rpc_next") && canNext) {
            changePage(1);
        }
        if (!canNext) {
            imgui.ImGui.endDisabled();
        }

        imgui.ImGui.separator();
        final Component validation = getValidationError();
        final double selectedLength = selectedParts.stream().mapToDouble(index -> pathData.get(index).getRailLength()).sum();
        final double selectedDwell = selectedParts.stream().mapToDouble(index -> pathData.get(index).getDwellTime() / 1000D).sum();
        if (validation == null) {
            imgui.ImGui.textColored(0.4f, 1f, 0.4f, 1f, "Path valid");
        } else {
            imgui.ImGui.textColored(1f, 0.45f, 0.45f, 1f, validation.getString());
        }
        imgui.ImGui.text(String.format(Locale.ROOT, "Selected %d | Segments %d", selectedParts.size(), pathData.size()));
        imgui.ImGui.text(String.format(Locale.ROOT, "Selected %.1fm | %.1fs", selectedLength, selectedDwell));

        final Integer selectedIndex = getSelectedIndex();
        if (selectedIndex != null && selectedIndex >= 0 && selectedIndex < pathData.size()) {
            final PathData selected = pathData.get(selectedIndex);
            imgui.ImGui.textDisabled("[" + (selectedIndex + 1) + "] " + RailRebuildHelper.getStoredOrInferredRailTypeName(selected.getRail()));
            imgui.ImGui.textDisabled(RoutePathCreator.formatPosition(selected.getOrderedPosition1()) + " -> " + RoutePathCreator.formatPosition(selected.getOrderedPosition2()));
            imgui.ImGui.textDisabled(String.format(Locale.ROOT, "Length %.1fm | Platform %d | Dwell %.1fs", selected.getRailLength(), selected.getSavedRailBaseId(), selected.getDwellTime() / 1000D));
        }

        imgui.ImGui.separator();
        if (imgui.ImGui.inputText(Component.translatable("gui.mtr.route_name").getString() + "###rpc_route_name", imRouteName)) {
            persistRouteSettings(imRouteName.get(), parseColor(imRouteColor.get()));
            pushTag();
        }
        final boolean colorValid = parseColor(imRouteColor.get()) != null;
        if (!colorValid) {
            imgui.ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 1f, 0.35f, 0.35f, 1f);
        }
        if (imgui.ImGui.inputText(Component.translatable("gui.mtr.route_color").getString() + "###rpc_route_color", imRouteColor) && parseColor(imRouteColor.get()) != null) {
            persistRouteSettings(imRouteName.get(), parseColor(imRouteColor.get()));
            pushTag();
        }
        if (!colorValid) {
            imgui.ImGui.popStyleColor();
        }
        final boolean canCreate = validation == null;
        if (!canCreate) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button("Create Route###rpc_create")) {
            persistRouteSettings(imRouteName.get(), parseColor(imRouteColor.get()));
            pushTag();
            createRoute();
        }
        if (!canCreate) {
            imgui.ImGui.endDisabled();
        }

        imgui.ImGui.separator();
        if (imgui.ImGui.button(net.minecraft.client.resources.language.I18n.get("gui.rte.imgui.back") + "###rpc_back")) {
            onClose();
        }
        imgui.ImGui.end();
    }

    private void syncImGuiFields() {
        final Integer selectedIndex = getSelectedIndex();
        final boolean hasSelection = selectedIndex != null;
        final String railTypeValue = hasSelection ? getSelectedRailTypeValue() : "";
        final String dwellValue = hasSelection ? getSelectedDwellValue() : "";
        imRailType.set(MIXED_VALUE.equals(railTypeValue) ? "" : railTypeValue);
        imDwellTime.set(MIXED_VALUE.equals(dwellValue) ? "" : dwellValue);
        imRailTypeInvalid = false;
        imDwellInvalid = false;
    }

    private void imguiApplyRailType(String value) {
        if (selectedParts.isEmpty()) {
            return;
        }
        final String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty() || !RailRebuildHelper.isRailTypeValid(normalized)) {
            imRailTypeInvalid = !normalized.isEmpty();
            return;
        }
        imRailTypeInvalid = false;
        boolean changed = false;
        for (final int index : selectedParts) {
            final PathData oldPathData = pathData.get(index);
            final org.mtr.core.data.Rail rail = CompoundCreatorLogic.RailModifierTask.copyRail(oldPathData.getRail());
            ((RailExtraSupplier) (Object) rail).setRailType(normalized);
            final org.mtr.core.data.Rail rebuilt = RailRebuildHelper.rebuildFromCurrentState(rail);
            pathData.set(index, new PathData(
                rebuilt,
                oldPathData.getSavedRailBaseId(),
                oldPathData.getDwellTime(),
                oldPathData.getStopIndex(),
                oldPathData.getOrderedPosition1(),
                oldPathData.getOrderedPosition2()
            ));
            changed = true;
        }
        if (changed) {
            pushData();
            rebuildPathParts();
            init();
        }
    }

    private void imguiApplyDwell(String value) {
        if (selectedParts.isEmpty()) {
            return;
        }
        final Long dwellTimeMillis = parseDwellTimeMillis(value);
        if (dwellTimeMillis == null) {
            imDwellInvalid = true;
            return;
        }
        imDwellInvalid = false;
        boolean changed = false;
        for (final int index : selectedParts) {
            final PathData oldPathData = pathData.get(index);
            if (oldPathData.getDwellTime() == dwellTimeMillis) {
                continue;
            }
            pathData.set(index, new PathData(
                oldPathData.getRail(),
                oldPathData.getSavedRailBaseId(),
                dwellTimeMillis,
                oldPathData.getStopIndex(),
                oldPathData.getOrderedPosition1(),
                oldPathData.getOrderedPosition2()
            ));
            changed = true;
        }
        if (changed) {
            pushData();
            rebuildPathParts();
            init();
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (imgui.ImGui.getIO().getWantCaptureMouse()) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    //#if MC>=12002
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
    //#else
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    //#endif
        if (imgui.ImGui.getIO().getWantCaptureMouse()) {
            return true;
        }
        //#if MC>=12002
        return super.mouseScrolled(mouseX, mouseY, scrollX, amount);
        //#else
        return super.mouseScrolled(mouseX, mouseY, amount);
        //#endif
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (imgui.ImGui.getIO().getWantCaptureKeyboard() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            moveSelectionToBoundary(true, hasShiftDown());
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            moveSelectionToBoundary(false, hasShiftDown());
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE && hasControlDown()) {
            clearPath();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            removeLast();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            changePage(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            changePage(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            moveSelection(-1, hasShiftDown());
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            moveSelection(1, hasShiftDown());
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_A && hasControlDown()) {
            selectAllParts();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !selectedParts.isEmpty()) {
            clearSelection();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void pullData() {
        if (!pullTag()) {
            pathData.clear();
            pathParts.clear();
            selectedParts.clear();
            currentPage = 0;
            return;
        }

        final org.mtr.mapping.holder.CompoundTag wrappedTag = new org.mtr.mapping.holder.CompoundTag(tag);
        RoutePathCreator.ensureDefaults(wrappedTag);

        pathData.clear();
        pathData.addAll(RoutePathCreator.readPath(wrappedTag));

        selectedParts.removeIf(index -> index < 0 || index >= pathData.size());
        if (selectionAnchor >= pathData.size()) {
            selectionAnchor = pathData.isEmpty() ? -1 : pathData.size() - 1;
        }
        rebuildPathParts();
        currentPage = Math.min(currentPage, maxPage());
    }

    private boolean pullTag() {
        final ItemStack stack = getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof RoutePathCreator)) {
            tag = new CompoundTag();
            return false;
        }
        tag = stack.getOrCreateTag().copy();
        return true;
    }

    private void pushData() {
        final org.mtr.mapping.holder.CompoundTag wrappedTag = new org.mtr.mapping.holder.CompoundTag(tag);
        RoutePathCreator.writePath(pathData, wrappedTag);
        if (pathData.isEmpty()) {
            wrappedTag.remove(RoutePathCreator.TAG_LAST_POS);
        } else {
            final Position lastPosition = pathData.get(pathData.size() - 1).getOrderedPosition2();
            tag.putLong(RoutePathCreator.TAG_LAST_POS, net.minecraft.core.BlockPos.containing(lastPosition.getX(), lastPosition.getY(), lastPosition.getZ()).asLong());
        }
        pushTag();
    }

    private boolean pushTag() {
        final ItemStack stack = getStack();
        if (stack.isEmpty()) {
            return false;
        }
        stack.setTag(tag.copy());
        syncHeldItem();
        return true;
    }

    private void rebuildPathParts() {
        pathParts.clear();
        for (int i = 0; i < pathData.size(); i++) {
            pathParts.add(createPathLabel(i));
        }
    }

    private WidgetLabel createPathLabel(int index) {
        final PathData data = pathData.get(index);
        final boolean oppositePrev = index > 0 && pathData.get(index - 1).isOppositeRail(data);
        final boolean oppositeNext = index < pathData.size() - 1 && pathData.get(index + 1).isOppositeRail(data);
        final int speedLimit = Math.max((int) data.getRail().getSpeedLimitKilometersPerHour(false), (int) data.getRail().getSpeedLimitKilometersPerHour(true));
        final String header = oppositePrev ? "TB<\n" : oppositeNext ? "\n" : "";
        final String footer = oppositeNext ? "\nTB>" : oppositePrev ? "\n " : "";
        final String text = String.format(
            Locale.ROOT,
            "%s%s:%dkm/h\n%.1fm %.1fs%s",
            header,
            RailRebuildHelper.getStoredOrInferredRailTypeName(data.getRail()),
            speedLimit,
            data.getRailLength(),
            data.getDwellTime() / 1000D,
            footer
        );
        final WidgetLabel label = new WidgetLabel(0, 0, 80, Component.literal(text), () -> handlePartSelection(index));
        label.backgroundColor = 0x22202020;
        label.hoverBackgroundColor = 0x665A5A5A;
        label.selectedBackgroundColor = 0x886534AA;
        return label;
    }

    private void selectPart(Integer... indexes) {
        selectedParts.clear();
        if (indexes != null) {
            for (final Integer index : indexes) {
                if (index != null && index >= 0 && index < pathData.size()) {
                    selectedParts.add(index);
                }
            }
        }
        if (indexes != null && indexes.length > 0 && indexes[0] != null) {
            selectionAnchor = indexes[0];
        } else if (selectedParts.isEmpty()) {
            selectionAnchor = -1;
        }
        refreshSelectedFields();
    }

    private void handlePartSelection(int index) {
        if (hasShiftDown() && selectionAnchor >= 0 && selectionAnchor < pathData.size()) {
            final int start = Math.min(selectionAnchor, index);
            final int end = Math.max(selectionAnchor, index);
            selectedParts.clear();
            for (int i = start; i <= end; i++) {
                selectedParts.add(i);
            }
            refreshSelectedFields();
            return;
        }

        if (hasControlDown()) {
            if (selectedParts.contains(index)) {
                selectedParts.remove(index);
                if (selectedParts.isEmpty()) {
                    selectionAnchor = -1;
                }
            } else {
                selectedParts.add(index);
                selectionAnchor = index;
            }
            refreshSelectedFields();
            return;
        }

        selectPart(index);
    }

    private void moveSelection(int delta, boolean extend) {
        if (pathData.isEmpty()) {
            return;
        }
        final Integer selectedIndex = getSelectedIndex();
        final int current = selectedIndex == null ? (delta > 0 ? 0 : pathData.size() - 1) : selectedIndex;
        final int target = Math.max(0, Math.min(pathData.size() - 1, current + delta));
        if (extend && selectionAnchor >= 0) {
            handlePartSelection(target);
        } else {
            selectPart(target);
        }
        currentPage = target / partPerPage();
        refreshSelectedFields();
    }

    private void moveSelectionToBoundary(boolean start, boolean extend) {
        if (pathData.isEmpty()) {
            return;
        }
        final int target = start ? 0 : pathData.size() - 1;
        if (extend && selectionAnchor >= 0) {
            handlePartSelection(target);
        } else {
            selectPart(target);
        }
        currentPage = target / partPerPage();
        refreshSelectedFields();
    }

    private void selectAllParts() {
        selectedParts.clear();
        for (int i = 0; i < pathData.size(); i++) {
            selectedParts.add(i);
        }
        selectionAnchor = pathData.isEmpty() ? -1 : 0;
        refreshSelectedFields();
    }

    private void clearSelection() {
        selectedParts.clear();
        selectionAnchor = -1;
        refreshSelectedFields();
    }

    private void refreshSelectedFields() {
        refreshingFields = true;
        imFieldsInitialized = false;

        final Integer selectedIndex = getSelectedIndex();
        final boolean hasSelection = selectedIndex != null;
        final String railTypeValue = hasSelection ? getSelectedRailTypeValue() : "";
        final String dwellValue = hasSelection ? getSelectedDwellValue() : "";

        refreshingFields = false;
    }

    private void changePage(int delta) {
        currentPage = Math.max(0, Math.min(maxPage(), currentPage + delta));
        refreshSelectedFields();
    }

    private void removeLast() {
        if (pathData.isEmpty()) {
            return;
        }

        pathData.remove(pathData.size() - 1);
        selectPart();
        pushData();
        pullData();
        init();
    }

    private void clearPath() {
        pathData.clear();
        selectPart();
        pushData();
        pullData();
        init();
    }

    private void createRoute() {
        if (getValidationError() != null) {
            return;
        }

        final Route route = new Route(TransportMode.TRAIN, MinecraftClientData.getDashboardInstance());
        route.setName(RoutePathCreator.getRouteName(new org.mtr.mapping.holder.CompoundTag(tag)));
        route.setColor(RoutePathCreator.getRouteColor(new org.mtr.mapping.holder.CompoundTag(tag)));
        ((IRoute) (Object) route).setPathData(pathData);
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateData(new UpdateDataRequest(MinecraftClientData.getDashboardInstance()).addRoute(route)));
    }

    private void persistRouteSettings(String routeName, Integer routeColor) {
        final org.mtr.mapping.holder.CompoundTag wrappedTag = new org.mtr.mapping.holder.CompoundTag(tag);
        RoutePathCreator.setRouteName(wrappedTag, routeName);
        if (routeColor != null) {
            RoutePathCreator.setRouteColor(wrappedTag, routeColor);
        }
    }

    private void updatePageWidgets() {
        currentPage = Math.max(0, Math.min(currentPage, maxPage()));
    }

    private void layoutPathLabels() {
        for (int i = 0; i <= maxPage(); i++) {
            for (int j = 0; j < partPerPage(); j++) {
                final int index = i * partPerPage() + j;
                if (index >= pathParts.size()) {
                    break;
                }
                final WidgetLabel label = pathParts.get(index);
                label.visible = i == currentPage;
                label.color = selectedParts.contains(index) ? 0xFF78CB : 0xFFFFFF;
                label.selected = selectedParts.contains(index);
                label.setX(0);
                label.setY(50 + 30 * j - label.getHeight() / 2);
                label.setWidth(80);
            }
        }
    }

    private int partPerPage() {
        return Math.max((height - 50 - 30) / 30, 1);
    }

    private int maxPage() {
        final int pages = (int) Math.ceil(pathParts.size() / (double) partPerPage());
        return Math.max(0, pages - 1);
    }

    private int getEditorX() {
        final int pathWidth = 80;
        final int editorWidth = getEditorWidth();
        return pathWidth + (width - pathWidth - editorWidth) / 2;
    }

    private int getEditorWidth() {
        return 100;
    }

    private Integer getSelectedIndex() {
        return selectedParts.stream().sorted().findFirst().orElse(null);
    }

    private ItemStack getStack() {
        return minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getItemInHand(hand);
    }

    private String getRouteName() {
        return RoutePathCreator.getRouteName(new org.mtr.mapping.holder.CompoundTag(tag));
    }

    private int getRouteColor() {
        return RoutePathCreator.getRouteColor(new org.mtr.mapping.holder.CompoundTag(tag));
    }

    private String getSelectedRailTypeValue() {
        String commonValue = null;
        for (final int index : selectedParts) {
            final String value = RailRebuildHelper.getStoredOrInferredRailTypeName(pathData.get(index).getRail());
            if (commonValue == null) {
                commonValue = value;
            } else if (!commonValue.equals(value)) {
                return MIXED_VALUE;
            }
        }
        return commonValue == null ? "" : commonValue;
    }

    private String getSelectedDwellValue() {
        Long commonValue = null;
        for (final int index : selectedParts) {
            final long dwell = pathData.get(index).getDwellTime();
            if (commonValue == null) {
                commonValue = dwell;
            } else if (commonValue != dwell) {
                return MIXED_VALUE;
            }
        }
        return commonValue == null ? "" : String.format(Locale.ROOT, "%.1f", commonValue / 1000D);
    }

    private void syncHeldItem() {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateHoldingItem(hand == InteractionHand.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND));
    }

    private Component getValidationError() {
        if (pathData.isEmpty()) {
            return Component.translatable("tooltip.rte.route_path_creator.empty");
        }
        if (pathData.size() < 3) {
            return Component.translatable("gui.rte.route_path_creator.action_screen.illegal_path");
        }
        if (pathData.get(0).getSavedRailBaseId() == 0) {
            return Component.translatable("gui.rte.route_path_creator.action_screen.illegal_path");
        }
        if (!pathData.get(pathData.size() - 1).getRail().isPlatform()) {
            return Component.translatable("gui.rte.route_path_creator.action_screen.illegal_path");
        }
        if (pathData.size() > 1 && pathData.get(pathData.size() - 2).isOppositeRail(pathData.get(pathData.size() - 1))) {
            return Component.translatable("gui.rte.route_path_creator.action_screen.illegal_path");
        }
        final RoutePathCreator.PathValidationError validationError = RoutePathCreator.validatePathProgress(pathData);
        if (validationError != null) {
            return Component.translatable(validationError.translationKey);
        }
        return null;
    }

    private static Integer parseColor(String value) {
        if (value == null) {
            return null;
        }
        final String normalized = value.trim().replace("#", "");
        if (normalized.length() != 6) {
            return null;
        }
        try {
            return Integer.parseInt(normalized, 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseDwellTimeMillis(String value) {
        try {
            final double seconds = Double.parseDouble(value);
            if (seconds < 0) {
                return null;
            }
            return Math.round(seconds * 2D) * 500L;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
