package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.block.BlockFreeNode;
import cn.jstxjf_.rte.block.RTEBlocks;
import cn.jstxjf_.rte.gui.DirectNodeScreen;
import cn.jstxjf_.rte.network.PacketFreeNodeAction;
import cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public final class FreeNodeEditor {

    private static final double MAX_DISTANCE = 32;
    private static final int HISTORY_LIMIT = 64;
    private static final double MIN_VISUAL_DISTANCE = 4;
    private static final double AXIS_LENGTH = 4;
    private static final double BOX_SIZE = 0.3;
    private static final double HIT_BOX_SIZE = 0.32;
    private static final double RING_RADIUS = 5;
    private static final double PLANE_NEAR = 5.5;
    private static final double PLANE_FAR = 8.5;
    private static final int RING_SEGMENTS = 32;
    private static final int CONE_SEGMENTS = 16;
    private static final int TANGENT_COLOUR = 0xFFFFD23C;
    private static final long LIVE_SYNC_INTERVAL_MS = 150;
    private static final double SELECT_THRESHOLD = Math.cos(Math.toRadians(1.5));
    private static final Handle[] AXES = {Handle.X, Handle.Y, Handle.Z};
    private static final Handle[] PLANES = {Handle.YZ, Handle.XZ, Handle.XY};
    private static final Handle[] RINGS = {Handle.YAW, Handle.PITCH};
    private static final Deque<Edit> undo = new ArrayDeque<>();
    private static final Deque<Edit> redo = new ArrayDeque<>();
    private static BlockPos selected;
    private static BlockPos hovered;
    private static Handle hoveredHandle = Handle.NONE;
    private static Handle activeHandle = Handle.NONE;
    private static boolean dragging;
    private static double dragStart;
    private static Vec3 dragOrigin;
    private static Vec3 dragStartPoint;
    private static Vec3 dragPlaneNormal;
    private static NodeState dragBefore;
    private static double dragAngleStart;
    private static double dragOriginalAngle;
    private static double dragOriginalPitch;
    private static double dragRotationDelta;
    private static double dragScale;
    private static double dragRotationAccumulated;
    private static double dragLastRingAngle;
    private static long lastLiveSync;
    private static NodeState lastSynced;
    private static BlockPos editLockAnchor;
    private static long editLockUntil;
    private static final long EDIT_LOCK_GRACE_MS = 600;

    static {
        cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode.clientEditLock = FreeNodeEditor::isEditLocked;
    }
    private static boolean plusX = true;
    private static boolean plusY = true;
    private static boolean plusZ = true;
    private static Clipboard clipboard;
    private static int selectionGrace;
    private static BlockPos deletePending;

    private FreeNodeEditor() {
    }

    private static boolean isEditLocked(org.mtr.mapping.holder.BlockPos pos) {
        final BlockPos anchor = dragging ? selected
                : net.minecraft.Util.getMillis() < editLockUntil ? editLockAnchor : null;
        return anchor != null && pos != null && anchor.getX() == pos.getX() && anchor.getY() == pos.getY() && anchor.getZ() == pos.getZ();
    }

    private static void extendEditLock(BlockPos anchor) {
        editLockAnchor = anchor;
        editLockUntil = net.minecraft.Util.getMillis() + EDIT_LOCK_GRACE_MS;
    }

    public static void tick() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isToolActive(minecraft)) {
            hovered = null;
            hoveredHandle = Handle.NONE;
            dragging = false;
            return;
        }
        if (selectionGrace > 0) {
            selectionGrace--;
        }
        if (selected != null && resolve(selected) == null && selectionGrace == 0) {
            selected = null;
            activeHandle = Handle.NONE;
            dragging = false;
        }
        if (!dragging) {
            updateHover();
        }
    }

    public static boolean onMouseButton(int button, int action, int modifiers) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isToolActive(minecraft)) {
            return false;
        }
        if (!dragging) {
            updateHover();
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && isConnectorActive(minecraft)) {
            if (action == GLFW.GLFW_PRESS && hovered != null) {
                return useConnector(minecraft, hovered);
            }
            return false;
        }
        if (!isEditorActive(minecraft)) {
            return false;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            if (selected != null && hoveredHandle != Handle.NONE) {
                return beginDrag();
            }
            if (hovered != null) {
                selectNextAtCursor();
                return true;
            }
            if (selected != null) {
                selected = null;
                activeHandle = Handle.NONE;
                return true;
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_RELEASE && dragging) {
            finishDrag();
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS && hovered != null
                && (selected == null || hoveredHandle == Handle.NONE)) {
            final BlockFreeNode.BlockEntityFreeNode hoveredNode = resolve(hovered);
            if (hoveredNode != null) {
                selected = hovered;
                activeHandle = Handle.NONE;
                minecraft.setScreen(DirectNodeScreen.createScreen(hoveredNode, () -> null));
                return true;
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS && selected != null) {
            final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
            if (hoveredHandle != Handle.NONE && node != null) {
                minecraft.setScreen(DirectNodeScreen.createScreen(node, () -> null));
                return true;
            }
            if (minecraft.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK && node != null) {
                final NodeState before = state(node);
                move(node, snapPosition(hit.getLocation(), true, true, true));
                node.setAttachmentFaceLocal(hit.getDirection().get3DDataValue());
                node.sendUpdateC2S();
                record(before, state(node));
                return true;
            }
            return true;
        }
        return false;
    }

    public static void selectPasted(BlockPos anchor) {
        selected = anchor;
        activeHandle = Handle.NONE;
        selectionGrace = 20;
    }

    public static boolean onScroll(double amount) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isEditorActive(minecraft) || selected == null || amount == 0) {
            return false;
        }
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        if (node == null) {
            return false;
        }
        final NodeState before = state(node);
        final Vec3 direction;
        if (hoveredHandle.axis()) {
            direction = hoveredHandle.vector;
        } else if (hoveredHandle == Handle.CENTER) {
            direction = center(node).subtract(viewOrigin()).normalize();
        } else {
            direction = viewDirection();
        }
        move(node, center(node).add(direction.scale(Math.signum(amount) * nudgeStep())));
        node.sendUpdateC2S();
        record(before, state(node));
        return true;
    }

    public static boolean onKey(int key, int action, int modifiers) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isEditorActive(minecraft) || action == GLFW.GLFW_RELEASE) {
            return false;
        }
        final boolean control = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        if (action == GLFW.GLFW_PRESS && control && key == GLFW.GLFW_KEY_C && selected != null) {
            final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
            if (node != null) {
                clipboard = new Clipboard(selected, center(node), node.getAngleDegrees(), node.getPitchDegrees(),
                        node.getAttachmentFace(), node.isFittingNode());
            }
            return true;
        }
        if (action == GLFW.GLFW_PRESS && control && key == GLFW.GLFW_KEY_V) {
            if (clipboard != null) {
                PacketFreeNodeAction.sendPaste(clipboard.anchor, clipboard.point.x, clipboard.point.y, clipboard.point.z,
                        clipboard.angle, clipboard.pitch, clipboard.face, clipboard.fitting);
            }
            return true;
        }
        if (action == GLFW.GLFW_PRESS && control && key == GLFW.GLFW_KEY_Z) {
            return undo();
        }
        if (action == GLFW.GLFW_PRESS && control && key == GLFW.GLFW_KEY_Y) {
            return redo();
        }
        if (selected == null) {
            return false;
        }
        if (action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE
                || key == GLFW.GLFW_KEY_KP_DECIMAL)) {
            if (deletePending == null) {
                deletePending = selected;
                PacketFreeNodeAction.send(PacketFreeNodeAction.DELETE, selected);
            }
            return true;
        }
        if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE) {
            selected = null;
            activeHandle = Handle.NONE;
            dragging = false;
            return true;
        }
        final Vec3 direction = switch (key) {
            case GLFW.GLFW_KEY_UP -> horizontalForward();
            case GLFW.GLFW_KEY_DOWN -> horizontalForward().scale(-1);
            case GLFW.GLFW_KEY_RIGHT -> horizontalRight();
            case GLFW.GLFW_KEY_LEFT -> horizontalRight().scale(-1);
            case GLFW.GLFW_KEY_PAGE_UP -> new Vec3(0, 1, 0);
            case GLFW.GLFW_KEY_PAGE_DOWN -> new Vec3(0, -1, 0);
            default -> null;
        };
        if (direction != null) {
            final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
            if (node != null) {
                final NodeState before = state(node);
                move(node, center(node).add(direction.scale(nudgeStep())));
                node.sendUpdateC2S();
                record(before, state(node));
            }
            return true;
        }
        return false;
    }

    public static void render(PoseStack poseStack) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isToolActive(minecraft)) {
            return;
        }
        if (dragging) {
            updateDrag();
        } else {
            updateHover();
        }
        final boolean editorActive = isEditorActive(minecraft);
        renderNodeMarkers(poseStack, editorActive);
        if (!editorActive) {
            return;
        }
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        final Vec3 point = center(node);
        if (point != null) {
            renderGizmo(poseStack, point, node);
        }
    }

    private static void renderNodeMarkers(PoseStack poseStack, boolean editorActive) {
        final BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        final Matrix4f matrix = poseStack.last().pose();
        for (final BlockFreeNode.BlockEntityFreeNode node : BlockEntityDirectNodeRenderer.getFreeNodes()) {
            if (editorActive && selected != null && selected.equals(node.getBlockPos())) {
                continue;
            }
            final Vec3 point = center(node);
            if (point == null) {
                continue;
            }
            final boolean hot = hovered != null && hovered.equals(node.getBlockPos());
            shadedBox(builder, matrix, point, hot ? 0.13F : 0.1F, hot ? 0xFFFFD23C : 0xB4FFFFFF);
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(builder.end());
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void renderGizmo(PoseStack poseStack, Vec3 point, BlockFreeNode.BlockEntityFreeNode node) {
        final double scale = gizmoScale(point);
        final float axisLength = (float) (AXIS_LENGTH * scale);
        final float boxSize = (float) (BOX_SIZE * scale);
        final float coneLength = axisLength / 5;
        final float coneRadius = coneLength / 3;
        final float ringRadius = (float) (RING_RADIUS * scale);
        final boolean idle = !dragging;
        final boolean showAxes = idle || activeHandle.axis() || activeHandle.plane();
        final boolean showPlanes = idle || activeHandle.plane();
        final boolean showRings = idle || activeHandle.rotation();
        final Vec3 eye = viewOrigin();

        final BufferBuilder builder = Tesselator.getInstance().getBuilder();
        final Matrix4f matrix = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        if (showAxes) {
            for (final Handle handle : AXES) {
                final Vec3 axis = signedAxis(handle);
                gizmoLine(builder, matrix, normal, point.add(axis.scale(boxSize)), point.add(axis.scale(axisLength)), axisColour(handle));
            }
        }
        if (showRings) {
            for (final Handle handle : RINGS) {
                if (!idle && handle != activeHandle) {
                    continue;
                }
                final Vec3[] basis = rotationBasis(node, handle);
                renderRing(builder, matrix, normal, point, eye, ringRadius, basis[0], basis[1], axisColour(handle), !idle);
            }
        }
        final Vec3 tangent = !idle && activeHandle.rotation() ? nodeTangent(node) : null;
        if (tangent != null) {
            gizmoLine(builder, matrix, normal, point.subtract(tangent.scale(axisLength * 0.85)),
                    point.add(tangent.scale(axisLength * 0.85)), TANGENT_COLOUR);
        }
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(3);
        BufferUploader.drawWithShader(builder.end());
        RenderSystem.lineWidth(1);

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        if (showAxes) {
            for (final Handle handle : AXES) {
                final Vec3 axis = signedAxis(handle);
                cone(builder, matrix, point.add(axis.scale(axisLength)), axis, coneLength, coneRadius, axisColour(handle));
            }
        }
        if (showPlanes) {
            for (final Handle handle : PLANES) {
                if (!idle && handle != activeHandle) {
                    continue;
                }
                planeHandle(builder, matrix, point, boxSize, handle, axisColour(handle));
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(builder.end());

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        if (!idle && activeHandle.rotation()) {
            final Vec3[] basis = rotationBasis(node, activeHandle);
            rotationSector(builder, matrix, point, ringRadius, basis[0], basis[1], dragAngleStart,
                    Math.toRadians(dragRotationDelta), (axisColour(activeHandle) & 0xFFFFFF) | 0x60000000);
        }
        final boolean centerHot = hoveredHandle == Handle.CENTER || activeHandle == Handle.CENTER;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(builder.end());
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        shadedBox(builder, matrix, point, boxSize, centerHot ? 0xFFFFFFFF : 0x80FFFFFF);
        RenderSystem.enableCull();
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    private static void renderRing(BufferBuilder builder, Matrix4f matrix, Matrix3f normal, Vec3 point, Vec3 eye,
                                   double radius, Vec3 u, Vec3 v, int colour, boolean full) {
        final Vec3 toTarget = point.subtract(eye);
        final double limit = toTarget.length() * 0.05;
        final Vec3 view = toTarget.normalize();
        for (int i = 0; i < RING_SEGMENTS; i++) {
            final Vec3 a = ringPoint(u, v, radius, Math.PI * 2 * i / RING_SEGMENTS);
            final Vec3 b = ringPoint(u, v, radius, Math.PI * 2 * (i + 1) / RING_SEGMENTS);
            if (full) {
                gizmoLine(builder, matrix, normal, point.add(a), point.add(b), colour);
                continue;
            }
            final double sideA = a.dot(view) - limit;
            final double sideB = b.dot(view) - limit;
            if (sideA < 0 && sideB < 0) {
                gizmoLine(builder, matrix, normal, point.add(a), point.add(b), colour);
            } else if (sideA < 0 || sideB < 0) {
                final Vec3 cut = a.add(b.subtract(a).scale(sideA / (sideA - sideB)));
                gizmoLine(builder, matrix, normal, point.add(sideA < 0 ? a : cut), point.add(sideA < 0 ? cut : b), colour);
            }
        }
    }

    private static void rotationSector(BufferBuilder builder, Matrix4f matrix, Vec3 point, double radius, Vec3 u, Vec3 v,
                                       double start, double sweep, int colour) {
        final int steps = Math.max(1, (int) Math.ceil(Math.abs(sweep) / (Math.PI * 2) * RING_SEGMENTS * 2));
        for (int i = 0; i < steps; i++) {
            final Vec3 a = point.add(ringPoint(u, v, radius, start + sweep * i / steps));
            final Vec3 b = point.add(ringPoint(u, v, radius, start + sweep * (i + 1) / steps));
            colourVertex(builder, matrix, point, colour);
            colourVertex(builder, matrix, a, colour);
            colourVertex(builder, matrix, b, colour);
        }
    }

    private static Vec3 ringPoint(Vec3 u, Vec3 v, double radius, double angle) {
        return u.scale(Math.cos(angle) * radius).add(v.scale(Math.sin(angle) * radius));
    }

    private static void cone(BufferBuilder builder, Matrix4f matrix, Vec3 base, Vec3 axis, double length, double radius, int colour) {
        final Vec3 first = Math.abs(axis.y) > 0.5 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        final Vec3 second = axis.cross(first).normalize();
        final Vec3 third = axis.cross(second).normalize();
        final Vec3 tip = base.add(axis.scale(length));
        for (int i = 0; i < CONE_SEGMENTS; i++) {
            final double angleA = Math.PI * 2 * i / CONE_SEGMENTS;
            final double angleB = Math.PI * 2 * (i + 1) / CONE_SEGMENTS;
            final Vec3 a = base.add(second.scale(Math.cos(angleA) * radius)).add(third.scale(Math.sin(angleA) * radius));
            final Vec3 b = base.add(second.scale(Math.cos(angleB) * radius)).add(third.scale(Math.sin(angleB) * radius));
            colourVertex(builder, matrix, tip, colour);
            colourVertex(builder, matrix, a, colour);
            colourVertex(builder, matrix, b, colour);
            colourVertex(builder, matrix, base, colour);
            colourVertex(builder, matrix, b, colour);
            colourVertex(builder, matrix, a, colour);
        }
    }

    private static void planeHandle(BufferBuilder builder, Matrix4f matrix, Vec3 point, double boxSize, Handle handle, int colour) {
        final Vec3 u = handle.u.scale(sign(handle.u) * boxSize);
        final Vec3 v = handle.v.scale(sign(handle.v) * boxSize);
        final Vec3 a = point.add(u.scale(PLANE_NEAR)).add(v.scale(PLANE_NEAR));
        final Vec3 b = point.add(u.scale(PLANE_NEAR)).add(v.scale(PLANE_FAR));
        final Vec3 c = point.add(u.scale(PLANE_FAR)).add(v.scale(PLANE_FAR));
        final Vec3 d = point.add(u.scale(PLANE_FAR)).add(v.scale(PLANE_NEAR));
        colourVertex(builder, matrix, a, colour);
        colourVertex(builder, matrix, b, colour);
        colourVertex(builder, matrix, c, colour);
        colourVertex(builder, matrix, a, colour);
        colourVertex(builder, matrix, c, colour);
        colourVertex(builder, matrix, d, colour);
    }

    private static void shadedBox(BufferBuilder builder, Matrix4f matrix, Vec3 center, double half, int colour) {
        final Vec3 p000 = center.add(-half, -half, -half);
        final Vec3 p001 = center.add(-half, -half, half);
        final Vec3 p010 = center.add(-half, half, -half);
        final Vec3 p011 = center.add(-half, half, half);
        final Vec3 p100 = center.add(half, -half, -half);
        final Vec3 p101 = center.add(half, -half, half);
        final Vec3 p110 = center.add(half, half, -half);
        final Vec3 p111 = center.add(half, half, half);
        boxFace(builder, matrix, center, p000, p100, p110, p010, shade(colour, 0.87F));
        boxFace(builder, matrix, center, p001, p101, p111, p011, shade(colour, 0.87F));
        boxFace(builder, matrix, center, p000, p001, p011, p010, shade(colour, 0.7F));
        boxFace(builder, matrix, center, p100, p101, p111, p110, shade(colour, 0.7F));
        boxFace(builder, matrix, center, p010, p110, p111, p011, colour);
        boxFace(builder, matrix, center, p000, p100, p101, p001, shade(colour, 0.6F));
    }

    private static void boxFace(BufferBuilder builder, Matrix4f matrix, Vec3 center, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int colour) {
        final Vec3 faceNormal = b.subtract(a).cross(c.subtract(a));
        final Vec3 outward = a.add(c).scale(0.5).subtract(center);
        if (faceNormal.dot(outward) < 0) {
            final Vec3 swap = b;
            b = d;
            d = swap;
        }
        colourVertex(builder, matrix, a, colour);
        colourVertex(builder, matrix, b, colour);
        colourVertex(builder, matrix, c, colour);
        colourVertex(builder, matrix, a, colour);
        colourVertex(builder, matrix, c, colour);
        colourVertex(builder, matrix, d, colour);
    }

    private static int shade(int colour, float factor) {
        final int r = (int) (((colour >> 16) & 0xFF) * factor);
        final int g = (int) (((colour >> 8) & 0xFF) * factor);
        final int b = (int) ((colour & 0xFF) * factor);
        return (colour & 0xFF000000) | r << 16 | g << 8 | b;
    }

    private static void colourVertex(BufferBuilder builder, Matrix4f matrix, Vec3 point, int colour) {
        builder.vertex(matrix, (float) point.x, (float) point.y, (float) point.z)
                .color((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, (colour >>> 24) & 0xFF).endVertex();
    }

    private static void gizmoLine(BufferBuilder builder, Matrix4f matrix, Matrix3f normal, Vec3 a, Vec3 b, int colour) {
        final Vec3 delta = b.subtract(a);
        final double length = delta.length();
        if (length < 1E-6) {
            return;
        }
        final Vec3 direction = delta.scale(1 / length);
        final int r = (colour >> 16) & 0xFF;
        final int g = (colour >> 8) & 0xFF;
        final int blue = colour & 0xFF;
        builder.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(r, g, blue, 255)
                .normal(normal, (float) direction.x, (float) direction.y, (float) direction.z).endVertex();
        builder.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(r, g, blue, 255)
                .normal(normal, (float) direction.x, (float) direction.y, (float) direction.z).endVertex();
    }

    private static int axisColour(Handle handle) {
        final boolean hot = handle == activeHandle || !dragging && handle == hoveredHandle;
        return hot ? handle.hotColour : handle.idleColour;
    }

    private static Vec3 nodeTangent(BlockFreeNode.BlockEntityFreeNode node) {
        if (node == null) {
            return null;
        }
        final double yaw = node.isBound() ? node.getAngleRadians() : 0;
        final double pitch = Math.toRadians(node.getPitchDegrees());
        final double horizontal = Math.cos(pitch);
        return new Vec3(Math.cos(yaw) * horizontal, Math.sin(pitch), Math.sin(yaw) * horizontal);
    }

    private static boolean beginDrag() {
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        final Vec3 point = center(node);
        if (node == null || point == null) {
            return false;
        }
        activeHandle = hoveredHandle;
        dragScale = gizmoScale(point);
        dragRotationDelta = 0;
        dragRotationAccumulated = 0;
        lastSynced = null;
        dragging = true;
        dragOrigin = point;
        dragBefore = state(node);
        if (activeHandle.rotation()) {
            final Vec3[] basis = rotationBasis(node, activeHandle);
            dragPlaneNormal = basis[0].cross(basis[1]).normalize();
            dragStartPoint = rayPlaneIntersection(viewOrigin(), viewDirection(), point, dragPlaneNormal);
            dragAngleStart = ringAngle(dragStartPoint, point, basis[0], basis[1]);
            dragLastRingAngle = dragAngleStart;
            dragOriginalAngle = node.isBound() ? node.getAngleDegrees() : 0;
            dragOriginalPitch = node.getPitchDegrees();
        } else if (activeHandle == Handle.CENTER) {
            dragPlaneNormal = viewDirection();
            dragStartPoint = rayPlaneIntersection(viewOrigin(), viewDirection(), point, dragPlaneNormal);
        } else if (activeHandle.plane()) {
            dragPlaneNormal = activeHandle.normal;
            dragStartPoint = rayPlaneIntersection(viewOrigin(), viewDirection(), point, dragPlaneNormal);
        } else {
            dragPlaneNormal = axisDragPlane(activeHandle.vector, viewDirection());
            dragStartPoint = rayPlaneIntersection(viewOrigin(), viewDirection(), point, dragPlaneNormal);
            dragStart = dragStartPoint == null
                    ? axisParameter(point, activeHandle.vector, viewOrigin(), viewDirection())
                    : dragStartPoint.subtract(point).dot(activeHandle.vector);
        }
        if ((!activeHandle.axis() || Double.isFinite(dragStart)) && (!activeHandle.rotation() || dragStartPoint != null)) {
            return true;
        }
        dragging = false;
        activeHandle = Handle.NONE;
        return false;
    }

    private static void finishDrag() {
        updateDrag();
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        final NodeState after = state(node);
        final boolean changed = dragBefore != null && after != null && !sameState(dragBefore, after);
        dragging = false;
        activeHandle = Handle.NONE;
        if (changed) {
            extendEditLock(node.getBlockPos());
            node.sendUpdateC2S();
            record(dragBefore, after);
        }
    }

    private static void updateHover() {
        final Vec3 origin = viewOrigin();
        final Vec3 direction = viewDirection();
        final List<NodeHit> hits = hits(origin, direction);
        hovered = hits.isEmpty() ? null : hits.get(0).anchor;
        hoveredHandle = Handle.NONE;
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        final Vec3 point = center(node);
        if (point == null) {
            return;
        }
        plusX = origin.x >= point.x;
        plusY = origin.y >= point.y;
        plusZ = origin.z >= point.z;
        final double scale = gizmoScale(point);
        final double axisLength = AXIS_LENGTH * scale;
        final double hitBox = HIT_BOX_SIZE * scale;
        if (rayHitsCube(origin, direction, point, hitBox)) {
            hoveredHandle = Handle.CENTER;
            return;
        }
        for (final Handle handle : PLANES) {
            if (hitsPlaneHandle(origin, direction, point, hitBox, handle)) {
                hoveredHandle = handle;
                return;
            }
        }
        double bestDot = SELECT_THRESHOLD;
        for (final Handle handle : AXES) {
            final Vec3 axis = signedAxis(handle);
            final double s = Math.max(0, Math.min(axisLength * 1.3, axisParameter(point, axis, origin, direction)));
            final double dot = viewDot(origin, direction, point.add(axis.scale(s)));
            if (dot >= bestDot) {
                bestDot = dot;
                hoveredHandle = handle;
            }
        }
        final Vec3 toTarget = point.subtract(origin);
        final double limit = toTarget.length() * 0.05;
        final Vec3 view = toTarget.normalize();
        final double ringRadius = RING_RADIUS * scale;
        for (final Handle handle : RINGS) {
            final Vec3[] basis = rotationBasis(node, handle);
            for (int i = 0; i < RING_SEGMENTS; i++) {
                final Vec3 a = ringPoint(basis[0], basis[1], ringRadius, Math.PI * 2 * i / RING_SEGMENTS);
                final Vec3 b = ringPoint(basis[0], basis[1], ringRadius, Math.PI * 2 * (i + 1) / RING_SEGMENTS);
                final Vec3 segment = b.subtract(a);
                final double segmentLength = segment.length();
                final Vec3 unit = segment.scale(1 / segmentLength);
                final double s = Math.max(0, Math.min(segmentLength, axisParameter(point.add(a), unit, origin, direction)));
                final Vec3 relative = a.add(unit.scale(s));
                if (relative.dot(view) >= limit) {
                    continue;
                }
                final double dot = viewDot(origin, direction, point.add(relative));
                if (dot > bestDot) {
                    bestDot = dot;
                    hoveredHandle = handle;
                }
            }
        }
    }

    private static double viewDot(Vec3 origin, Vec3 direction, Vec3 target) {
        final Vec3 delta = target.subtract(origin);
        final double length = delta.length();
        return length < 1E-9 ? -1 : delta.dot(direction) / length;
    }

    private static boolean rayHitsCube(Vec3 origin, Vec3 direction, Vec3 center, double half) {
        double near = 0;
        double far = Double.MAX_VALUE;
        final double[] o = {origin.x - center.x, origin.y - center.y, origin.z - center.z};
        final double[] d = {direction.x, direction.y, direction.z};
        for (int i = 0; i < 3; i++) {
            if (Math.abs(d[i]) < 1E-12) {
                if (Math.abs(o[i]) > half) {
                    return false;
                }
                continue;
            }
            double t1 = (-half - o[i]) / d[i];
            double t2 = (half - o[i]) / d[i];
            if (t1 > t2) {
                final double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            near = Math.max(near, t1);
            far = Math.min(far, t2);
            if (near > far) {
                return false;
            }
        }
        return true;
    }

    private static boolean hitsPlaneHandle(Vec3 origin, Vec3 direction, Vec3 point, double boxSize, Handle handle) {
        final Vec3 intersection = rayPlaneIntersection(origin, direction, point, handle.normal);
        if (intersection == null) {
            return false;
        }
        final Vec3 relative = intersection.subtract(point);
        final double a = relative.dot(handle.u) * sign(handle.u) / boxSize;
        final double b = relative.dot(handle.v) * sign(handle.v) / boxSize;
        return a >= PLANE_NEAR && a <= PLANE_FAR && b >= PLANE_NEAR && b <= PLANE_FAR;
    }

    private static double sign(Vec3 axis) {
        if (axis.x != 0) {
            return plusX ? 1 : -1;
        }
        if (axis.y != 0) {
            return plusY ? 1 : -1;
        }
        return plusZ ? 1 : -1;
    }

    private static Vec3 signedAxis(Handle handle) {
        return handle.vector.scale(sign(handle.vector));
    }

    private static double gizmoScale(Vec3 point) {
        if (dragging && dragScale > 0) {
            return dragScale;
        }
        return Math.max(MIN_VISUAL_DISTANCE, viewOrigin().distanceTo(point)) / 20;
    }

    private static void selectNextAtCursor() {
        final List<NodeHit> hits = hits(viewOrigin(), viewDirection());
        if (hits.isEmpty()) {
            return;
        }
        int index = -1;
        for (int i = 0; i < hits.size(); i++) {
            if (hits.get(i).anchor.equals(selected)) {
                index = i;
                break;
            }
        }
        selected = hits.get((index + 1) % hits.size()).anchor;
        activeHandle = Handle.NONE;
    }

    private static List<NodeHit> hits(Vec3 origin, Vec3 direction) {
        final List<NodeHit> result = new ArrayList<>();
        for (final BlockFreeNode.BlockEntityFreeNode node : BlockEntityDirectNodeRenderer.getFreeNodes()) {
            final Vec3 point = center(node);
            if (point == null) {
                continue;
            }
            final double distance = point.subtract(origin).dot(direction);
            if (distance < 0 || distance > MAX_DISTANCE) {
                continue;
            }
            final double radius = 0.22 + distance * 0.006;
            if (origin.add(direction.scale(distance)).distanceToSqr(point) <= radius * radius) {
                result.add(new NodeHit(node.getBlockPos(), distance));
            }
        }
        result.sort(Comparator.comparingDouble(NodeHit::distance));
        return result;
    }

    private static void updateDrag() {
        applyDrag();
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        if (!dragging || node == null) {
            return;
        }
        final long now = net.minecraft.Util.getMillis();
        if (now - lastLiveSync < LIVE_SYNC_INTERVAL_MS) {
            return;
        }
        final NodeState current = state(node);
        if (sameState(lastSynced == null ? dragBefore : lastSynced, current)) {
            return;
        }
        lastLiveSync = now;
        lastSynced = current;
        extendEditLock(node.getBlockPos());
        node.sendUpdateC2S();
    }

    private static void applyDrag() {
        final BlockFreeNode.BlockEntityFreeNode node = resolve(selected);
        if (node == null || dragOrigin == null || activeHandle == Handle.NONE) {
            dragging = false;
            return;
        }
        if (activeHandle.rotation()) {
            final Vec3[] basis = rotationBasis(node, activeHandle);
            final Vec3 current = rayPlaneIntersection(viewOrigin(), viewDirection(), dragOrigin, dragPlaneNormal);
            if (current != null && dragStartPoint != null) {
                final double ringAngle = ringAngle(current, dragOrigin, basis[0], basis[1]);
                double step = Math.toDegrees(ringAngle - dragLastRingAngle);
                while (step <= -180) step += 360;
                while (step > 180) step -= 360;
                dragLastRingAngle = ringAngle;
                dragRotationAccumulated += step;
                double delta = snapRotation(dragRotationAccumulated);
                if (activeHandle == Handle.YAW) {
                    node.setAngleLocal(dragOriginalAngle + delta);
                } else if (Math.abs(dragOriginalPitch) <= 90) {
                    dragRotationAccumulated = Math.max(-90 - dragOriginalPitch, Math.min(90 - dragOriginalPitch, dragRotationAccumulated));
                    final double pitch = Math.max(-90, Math.min(90, dragOriginalPitch + delta));
                    delta = pitch - dragOriginalPitch;
                    node.setPitchLocal(pitch);
                } else {
                    node.setPitchLocal(dragOriginalPitch + delta);
                }
                dragRotationDelta = delta;
            }
            return;
        }
        if (activeHandle == Handle.CENTER || activeHandle.plane()) {
            final Vec3 current = rayPlaneIntersection(viewOrigin(), viewDirection(), dragOrigin, dragPlaneNormal);
            if (current != null && dragStartPoint != null) {
                Vec3 delta = current.subtract(dragStartPoint);
                if (activeHandle.plane()) {
                    delta = delta.subtract(activeHandle.normal.scale(delta.dot(activeHandle.normal)));
                }
                final Vec3 target = dragOrigin.add(delta);
                move(node, activeHandle == Handle.CENTER
                        ? snapPosition(target, true, true, true)
                        : snapPosition(target, activeHandle != Handle.YZ, activeHandle != Handle.XZ, activeHandle != Handle.XY));
            }
            return;
        }
        final Vec3 currentPoint = rayPlaneIntersection(viewOrigin(), viewDirection(), dragOrigin, dragPlaneNormal);
        final double current = currentPoint == null
                ? axisParameter(dragOrigin, activeHandle.vector, viewOrigin(), viewDirection())
                : currentPoint.subtract(dragOrigin).dot(activeHandle.vector);
        if (Double.isFinite(current) && Double.isFinite(dragStart)) {
            final Vec3 target = dragOrigin.add(activeHandle.vector.scale(current - dragStart));
            move(node, snapPosition(target, activeHandle == Handle.X, activeHandle == Handle.Y, activeHandle == Handle.Z));
        }
    }

    private static Vec3[] rotationBasis(BlockFreeNode.BlockEntityFreeNode node, Handle handle) {
        if (handle == Handle.YAW) {
            return new Vec3[]{new Vec3(1, 0, 0), new Vec3(0, 0, 1)};
        }
        return new Vec3[]{pitchForward(node), new Vec3(0, 1, 0)};
    }

    private static Vec3 pitchForward(BlockFreeNode.BlockEntityFreeNode node) {
        final double radians = Math.toRadians(node != null && node.isBound() ? node.getAngleDegrees() : 0);
        return new Vec3(Math.cos(radians), 0, Math.sin(radians));
    }

    private static double ringAngle(Vec3 point, Vec3 center, Vec3 u, Vec3 v) {
        if (point == null) {
            return 0;
        }
        final Vec3 relative = point.subtract(center);
        return Math.atan2(relative.dot(v), relative.dot(u));
    }

    private static void move(BlockFreeNode.BlockEntityFreeNode node, Vec3 point) {
        final BlockPos anchor = node.getBlockPos();
        node.setOffsetsLocal(point.x - anchor.getX() - 0.5, point.y - anchor.getY() - 0.125, point.z - anchor.getZ() - 0.5);
    }

    private static double snap(double value) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            return value;
        }
        final double step = net.minecraft.client.gui.screens.Screen.hasControlDown() ? 1 : 0.0625;
        return Math.rint(value / step) * step;
    }

    private static Vec3 snapDelta(Vec3 value) {
        return new Vec3(snap(value.x), snap(value.y), snap(value.z));
    }

    private static Vec3 snapPosition(Vec3 value, boolean x, boolean y, boolean z) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            return value;
        }
        final double step = net.minecraft.client.gui.screens.Screen.hasControlDown() ? 1 : 0.0625;
        return new Vec3(x ? Math.rint(value.x / step) * step : value.x,
                y ? Math.rint(value.y / step) * step : value.y,
                z ? Math.rint(value.z / step) * step : value.z);
    }

    private static double nudgeStep() {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            return 0.0001;
        }
        return net.minecraft.client.gui.screens.Screen.hasControlDown() ? 1 : 0.0625;
    }

    private static double snapRotation(double value) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            return value;
        }
        final double step = net.minecraft.client.gui.screens.Screen.hasControlDown() ? 15 : 5;
        return Math.rint(value / step) * step;
    }

    private static void record(NodeState before, NodeState after) {
        if (before == null || after == null || sameState(before, after)) {
            return;
        }
        undo.addLast(new Edit(before, after));
        while (undo.size() > HISTORY_LIMIT) {
            undo.removeFirst();
        }
        redo.clear();
    }

    private static boolean undo() {
        if (undo.isEmpty()) {
            return true;
        }
        final Edit edit = undo.removeLast();
        if (apply(edit.before)) {
            redo.addLast(edit);
        }
        return true;
    }

    private static boolean redo() {
        if (redo.isEmpty()) {
            return true;
        }
        final Edit edit = redo.removeLast();
        if (apply(edit.after)) {
            undo.addLast(edit);
        }
        return true;
    }

    private static boolean apply(NodeState state) {
        final BlockFreeNode.BlockEntityFreeNode node = resolve(state.anchor);
        if (node == null) {
            return false;
        }
        move(node, state.point);
        node.setAttachmentFaceLocal(state.face);
        node.setAngleLocal(state.angle);
        node.setPitchLocal(state.pitch);
        node.sendUpdateC2S();
        selected = state.anchor;
        return true;
    }

    private static NodeState state(BlockFreeNode.BlockEntityFreeNode node) {
        return node == null ? null : new NodeState(node.getBlockPos(), center(node), node.getAttachmentFace(),
                node.getAngleDegrees(), node.getPitchDegrees());
    }

    private static boolean sameAngle(double a, double b) {
        return Double.isNaN(a) && Double.isNaN(b) || Math.abs(a - b) <= 1E-9;
    }

    private static boolean sameState(NodeState a, NodeState b) {
        return a.point.distanceToSqr(b.point) <= 1E-12 && a.face == b.face
                && sameAngle(a.angle, b.angle) && Math.abs(a.pitch - b.pitch) <= 1E-9;
    }

    private static boolean isToolActive(Minecraft minecraft) {
        if (minecraft == null || minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return false;
        }
        return isEditorActive(minecraft) || isConnectorActive(minecraft);
    }

    private static boolean isEditorActive(Minecraft minecraft) {
        if (minecraft == null || minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return false;
        }
        final net.minecraft.world.item.Item item = RTEBlocks.FREE_NODE.get().asItem().data;
        final net.minecraft.world.item.Item brush = org.mtr.mod.Items.BRUSH.get().data;
        return minecraft.player.getMainHandItem().is(item) || minecraft.player.getOffhandItem().is(item)
                || minecraft.player.getMainHandItem().is(brush) || minecraft.player.getOffhandItem().is(brush);
    }

    private static boolean isConnectorActive(Minecraft minecraft) {
        if (minecraft == null || minecraft.player == null) {
            return false;
        }
        return minecraft.player.getMainHandItem().getItem() instanceof org.mtr.mod.item.ItemNodeModifierBase
                || minecraft.player.getOffhandItem().getItem() instanceof org.mtr.mod.item.ItemNodeModifierBase;
    }

    private static boolean useConnector(Minecraft minecraft, BlockPos anchor) {
        if (minecraft.player == null || resolve(anchor) == null) {
            return false;
        }
        PacketFreeNodeAction.send(PacketFreeNodeAction.USE_CONNECTOR, anchor);
        return true;
    }

    private static BlockFreeNode.BlockEntityFreeNode resolve(BlockPos anchor) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (anchor == null || minecraft.level == null) {
            return null;
        }
        return minecraft.level.getBlockEntity(anchor) instanceof BlockFreeNode.BlockEntityFreeNode node ? node : null;
    }

    private static Vec3 center(BlockFreeNode.BlockEntityFreeNode node) {
        if (node == null) {
            return null;
        }
        final BlockPos anchor = node.getBlockPos();
        return new Vec3(anchor.getX() + 0.5 + node.getXOffset(), anchor.getY() + 0.125 + node.getYOffset(), anchor.getZ() + 0.5 + node.getZOffset());
    }

    private static Vec3 viewOrigin() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    private static Vec3 viewDirection() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameRenderer == null) {
            return new Vec3(0, 0, 1);
        }
        final org.joml.Vector3f look = minecraft.gameRenderer.getMainCamera().getLookVector();
        return new Vec3(look.x, look.y, look.z).normalize();
    }

    private static Vec3 horizontalForward() {
        final Vec3 view = viewDirection();
        final Vec3 horizontal = new Vec3(view.x, 0, view.z);
        return horizontal.lengthSqr() < 1E-8 ? new Vec3(0, 0, 1) : horizontal.normalize();
    }

    private static Vec3 horizontalRight() {
        final Vec3 forward = horizontalForward();
        return new Vec3(-forward.z, 0, forward.x);
    }

    private static double axisParameter(Vec3 axisOrigin, Vec3 axis, Vec3 rayOrigin, Vec3 rayDirection) {
        final Vec3 w = axisOrigin.subtract(rayOrigin);
        final double b = axis.dot(rayDirection);
        final double d = axis.dot(w);
        final double e = rayDirection.dot(w);
        final double denominator = 1 - b * b;
        return Math.abs(denominator) < 1E-6 ? 0 : (b * e - d) / denominator;
    }

    private static Vec3 axisDragPlane(Vec3 axis, Vec3 look) {
        Vec3 normal = axis.cross(look.cross(axis));
        if (normal.lengthSqr() < 1E-8) {
            normal = axis.cross(new Vec3(0, 1, 0));
        }
        if (normal.lengthSqr() < 1E-8) {
            normal = axis.cross(new Vec3(1, 0, 0));
        }
        return normal.normalize();
    }

    private static Vec3 rayPlaneIntersection(Vec3 rayOrigin, Vec3 rayDirection, Vec3 planePoint, Vec3 planeNormal) {
        final double denominator = rayDirection.dot(planeNormal);
        if (Math.abs(denominator) < 1E-6) {
            return null;
        }
        final double distance = planePoint.subtract(rayOrigin).dot(planeNormal) / denominator;
        return distance < 0 ? null : rayOrigin.add(rayDirection.scale(distance));
    }

    public static boolean isCapturingPointer() {
        final Minecraft minecraft = Minecraft.getInstance();
        return isEditorActive(minecraft) && (selected != null || hovered != null || dragging);
    }

    public static boolean suppressesAttack() {
        return isCapturingPointer();
    }

    public static void onDeleteResult(BlockPos anchor, boolean success) {
        if (deletePending == null || !deletePending.equals(anchor)) {
            return;
        }
        deletePending = null;
        final Minecraft minecraft = Minecraft.getInstance();
        if (success) {
            if (anchor.equals(selected)) {
                selected = null;
                activeHandle = Handle.NONE;
                dragging = false;
            }
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.translatable("gui.rte.free_node.deleted"), true);
            }
        } else if (minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.translatable("gui.rte.free_node.delete_failed"), true);
        }
    }

    public static void renderHud(GuiGraphics graphics) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!isEditorActive(minecraft)) {
            return;
        }
        final List<Component> lines = new ArrayList<>();
        if (selected == null) {
            lines.add(Component.translatable("gui.rte.free_node.hint.select"));
            lines.add(Component.translatable("gui.rte.free_node.hint.place"));
        } else {
            lines.add(Component.translatable(dragging ? "gui.rte.free_node.hint.dragging" : "gui.rte.free_node.hint.edit"));
            lines.add(Component.translatable("gui.rte.free_node.hint.move"));
            lines.add(Component.translatable("gui.rte.free_node.hint.nudge"));
            lines.add(Component.translatable("gui.rte.free_node.hint.clipboard"));
            lines.add(Component.translatable("gui.rte.free_node.hint.delete"));
        }
        int width = 0;
        for (final Component line : lines) {
            width = Math.max(width, minecraft.font.width(line));
        }
        final int lineHeight = minecraft.font.lineHeight + 2;
        final int x = graphics.guiWidth() - width - 12;
        final int y = graphics.guiHeight() - lines.size() * lineHeight - 42;
        graphics.fill(x - 6, y - 5, graphics.guiWidth() - 6, y + lines.size() * lineHeight + 3, 0xA0101010);
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(minecraft.font, lines.get(i), x, y + i * lineHeight, 0xFFF2F2F2, false);
        }
    }

    private record NodeHit(BlockPos anchor, double distance) {
    }

    private record NodeState(BlockPos anchor, Vec3 point, int face, double angle, double pitch) {
    }

    private record Edit(NodeState before, NodeState after) {
    }

    private record Clipboard(BlockPos anchor, Vec3 point, double angle, double pitch, int face, boolean fitting) {
    }

    private enum Handle {
        NONE(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFFFFFFFF, 0xFFFFFFFF),
        CENTER(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFFFFFFFF, 0xFFFFFFFF),
        X(new Vec3(1, 0, 0), Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFFBF0000, 0xFFFF9999),
        Y(new Vec3(0, 1, 0), Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFF00BF00, 0xFFBFFFBF),
        Z(new Vec3(0, 0, 1), Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFF0000BF, 0xFF8080FF),
        XY(Vec3.ZERO, new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1), 0xFF0000BF, 0xFF8080FF),
        XZ(Vec3.ZERO, new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vec3(0, 1, 0), 0xFF00BF00, 0xFFBFFFBF),
        YZ(Vec3.ZERO, new Vec3(0, 1, 0), new Vec3(0, 0, 1), new Vec3(1, 0, 0), 0xFFBF0000, 0xFFFF9999),
        YAW(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFF00BF00, 0xFFBFFFBF),
        PITCH(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0xFFBF0000, 0xFFFF9999);

        private final Vec3 vector;
        private final Vec3 u;
        private final Vec3 v;
        private final Vec3 normal;
        private final int idleColour;
        private final int hotColour;

        Handle(Vec3 vector, Vec3 u, Vec3 v, Vec3 normal, int idleColour, int hotColour) {
            this.vector = vector;
            this.u = u;
            this.v = v;
            this.normal = normal;
            this.idleColour = idleColour;
            this.hotColour = hotColour;
        }

        private boolean axis() {
            return this == X || this == Y || this == Z;
        }

        private boolean plane() {
            return this == XY || this == XZ || this == YZ;
        }

        private boolean rotation() {
            return this == YAW || this == PITCH;
        }
    }
}
