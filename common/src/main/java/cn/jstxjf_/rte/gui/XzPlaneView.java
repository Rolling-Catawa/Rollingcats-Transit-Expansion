package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.mtr.mod.client.MinecraftClientData;

import javax.annotation.Nullable;
import java.util.Map;

class XzPlaneView {

    private final EditorCamera camera;

    XzPlaneView(EditorCamera camera) {
        this.camera = camera;
    }

    void renderEndpoints(GuiGraphics graphics, Rail rail) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
        double cx2 = p2.getX() + 0.5, cz2 = p2.getZ() + 0.5;
        int[] s1 = camera.xzToScreen(cx1, cz1);
        int[] s2 = camera.xzToScreen(cx2, cz2);

        GuiDrawUtil.drawCircle(graphics, s1[0], s1[1], 6, 0xFF00FF00);
        GuiDrawUtil.drawCircle(graphics, s2[0], s2[1], 6, 0xFFFF4444);

        int steps = 60;
        BezierCurve bezier = extra.getBezierCurve();
        double bezierLen = bezier != null ? bezier.getLength() : 0;
        boolean useBezier = bezier != null && Double.isFinite(bezierLen) && bezierLen > 0;
        double rmLen = rail.railMath.getLength();
        boolean useRailMath = !useBezier && Double.isFinite(rmLen) && rmLen > 0.01;
        int prevSx = s1[0], prevSy = s1[1];
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double wx, wz;
            if (useBezier) {
                BezierCurve.Vec3 pos = bezier.getPosition(t * bezierLen);
                wx = pos.x + 0.5;
                wz = pos.z + 0.5;
            } else if (useRailMath) {
                org.mtr.core.tool.Vector vec = rail.railMath.getPosition(t * rmLen, false);
                wx = vec.x;
                wz = vec.z;
            } else {
                EditorCamera.Basis basis = EditorCamera.basisOf(rail);
                wx = cx1 + basis.ax() * basis.len() * t;
                wz = cz1 + basis.az() * basis.len() * t;
            }
            int[] s = camera.xzToScreen(wx, wz);
            GuiDrawUtil.drawLine(graphics, prevSx, prevSy, s[0], s[1], 0x44FFFFFF);
            prevSx = s[0];
            prevSy = s[1];
        }
    }

    void renderDirectNodes(GuiGraphics graphics, Level level, BlockPos posStart, BlockPos posEnd, Rail rail) {
        if (level == null || posStart == null || posEnd == null) return;
        renderDirectNode(graphics, level, posStart, rail, 0xFF00DD00);
        renderDirectNode(graphics, level, posEnd, rail, 0xFFDD4444);
    }

    private void renderDirectNode(GuiGraphics graphics, Level level, BlockPos pos, Rail rail, int color) {
        try {
            final Object be = level.getBlockEntity(pos);
            if (be instanceof BlockDirectNode.BlockEntityDirectNode dn && dn.isBound()) {
                int[] s = camera.xzToScreen(pos.getX() + 0.5, pos.getZ() + 0.5);
                double angle = dn.getAngleRadians();
                double ppb = camera.pixelsPerBlock();
                double lineLen = Math.min(ppb * 2, 20);

                EditorCamera.Basis basis = EditorCamera.basisOf(rail);
                double dx = Math.cos(angle);
                double dz = Math.sin(angle);
                double screenDx = (dx * basis.nx() + dz * basis.nz()) * lineLen;
                double screenDy = -(dx * basis.ax() + dz * basis.az()) * lineLen;
                GuiDrawUtil.drawLine(graphics, s[0], s[1], s[0] + (int) screenDx, s[1] + (int) screenDy, color);

                GuiDrawUtil.drawCircle(graphics, s[0], s[1], 4, color);
            }
        } catch (Exception ignored) {
        }
    }

    @Nullable
    Rail renderExistingRails(GuiGraphics graphics, int mouseX, int mouseY, Rail rail, boolean deleteTool) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return null;
        Position p1 = extra.getExtraPosition1();
        if (p1 == null) return null;

        Rail hoveredRail = null;
        double minDist = 15;

        var clientData = MinecraftClientData.getInstance();
        var railsAtPos = clientData.positionsToRail.get(p1);
        if (railsAtPos == null) return null;

        for (Map.Entry<Position, Rail> entry : railsAtPos.entrySet()) {
            Rail r = entry.getValue();
            if (r == rail) continue;

            Position ep = entry.getKey();
            boolean hasBezier = (((Object) r) instanceof RailExtraSupplier rExtra)
                && RailExtraSupplier.usesBezierCurve(rExtra.getPathMode())
                && rExtra.getBezierCurve() != null;

            boolean hovered = false;

            if (hasBezier) {
                RailExtraSupplier rExtra = (RailExtraSupplier) (Object) r;
                double length = rExtra.getLength();
                int segs = Math.max(4, (int) Math.round(length * 2));
                int[] prevS = null;
                for (int i = 0; i <= segs; i++) {
                    double t = length * i / segs;
                    Vec3 pos = rExtra.getPosition(t);
                    int[] s = camera.xzToScreen(pos.x, pos.z);
                    if (prevS != null && deleteTool && !hovered) {
                        double dist = GuiDrawUtil.distPointToSegment(mouseX, mouseY, prevS[0], prevS[1], s[0], s[1]);
                        if (dist < minDist) {
                            minDist = dist;
                            hoveredRail = r;
                            hovered = true;
                        }
                    }
                    prevS = s;
                }
                int color = hovered ? 0xFFFF6666 : 0x66AAAAAA;
                prevS = null;
                for (int i = 0; i <= segs; i++) {
                    double t = length * i / segs;
                    Vec3 pos = rExtra.getPosition(t);
                    int[] s = camera.xzToScreen(pos.x, pos.z);
                    if (prevS != null) {
                        GuiDrawUtil.drawLine(graphics, prevS[0], prevS[1], s[0], s[1], color);
                    }
                    prevS = s;
                }
            } else {
                int[] sStart = camera.xzToScreen(p1.getX() + 0.5, p1.getZ() + 0.5);
                int[] sEnd = camera.xzToScreen(ep.getX() + 0.5, ep.getZ() + 0.5);
                if (deleteTool) {
                    double dist = GuiDrawUtil.distPointToSegment(mouseX, mouseY, sStart[0], sStart[1], sEnd[0], sEnd[1]);
                    if (dist < minDist) {
                        minDist = dist;
                        hoveredRail = r;
                        hovered = true;
                    }
                }
                int color = hovered ? 0xFFFF6666 : 0x66AAAAAA;
                GuiDrawUtil.drawLine(graphics, sStart[0], sStart[1], sEnd[0], sEnd[1], color);
            }
        }
        return hoveredRail;
    }

    void renderSnapIndicator(GuiGraphics graphics, int mouseX, int mouseY, Rail rail, double snapPixels) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
        double cx2 = p2.getX() + 0.5, cz2 = p2.getZ() + 0.5;
        int[] s1 = camera.xzToScreen(cx1, cz1);
        int[] s2 = camera.xzToScreen(cx2, cz2);
        double pd1sq = (mouseX - s1[0]) * (mouseX - s1[0]) + (mouseY - s1[1]) * (mouseY - s1[1]);
        double pd2sq = (mouseX - s2[0]) * (mouseX - s2[0]) + (mouseY - s2[1]) * (mouseY - s2[1]);

        if (pd1sq < snapPixels * snapPixels && pd1sq <= pd2sq) {
            GuiDrawUtil.drawCircle(graphics, s1[0], s1[1], 10, 0x6600FF00);
        } else if (pd2sq < snapPixels * snapPixels) {
            GuiDrawUtil.drawCircle(graphics, s2[0], s2[1], 10, 0x66FF4444);
        }
    }
}
