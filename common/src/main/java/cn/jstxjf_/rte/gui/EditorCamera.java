package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;

class EditorCamera {

    static final double MIN_CAM_HEIGHT = 10;
    static final double MAX_CAM_HEIGHT = 250;
    private static final long ANIM_DURATION_MS = 500;

    double currentY;
    double planeY;
    double panX;
    double panZ;
    double panY;
    double cameraHeight = 60;

    private float animProgress = 1.0f;
    private long animStartTime;
    private float animFromYaw, animFromPitch;
    private double animFromX, animFromY, animFromZ;

    private Vec3 savedPlayerPos;
    private float savedYaw;
    private float savedPitch;
    private boolean savedFlying;
    private boolean savedHideGui;

    private int viewWidth = 1;
    private int viewHeight = 1;

    void setViewport(int width, int height) {
        viewWidth = width;
        viewHeight = height;
    }

    boolean isAnimating() {
        return animProgress < 1.0f;
    }

    void capturePlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && savedPlayerPos == null) {
            savedPlayerPos = mc.player.position();
            savedYaw = mc.player.getYRot();
            savedPitch = mc.player.getXRot();
            savedFlying = mc.player.getAbilities().flying;
            savedHideGui = mc.options.hideGui;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().mayfly = true;
            mc.options.hideGui = true;
        }
    }

    void restorePlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && savedPlayerPos != null) {
            mc.player.setPos(savedPlayerPos.x, savedPlayerPos.y, savedPlayerPos.z);
            mc.player.xo = savedPlayerPos.x;
            mc.player.yo = savedPlayerPos.y;
            mc.player.zo = savedPlayerPos.z;
            mc.player.xOld = savedPlayerPos.x;
            mc.player.yOld = savedPlayerPos.y;
            mc.player.zOld = savedPlayerPos.z;
            mc.player.setYRot(savedYaw);
            mc.player.setXRot(savedPitch);
            mc.player.yRotO = savedYaw;
            mc.player.xRotO = savedPitch;
            mc.player.getAbilities().flying = savedFlying;
            mc.player.setDeltaMovement(0, 0, 0);
            mc.player.fallDistance = 0;
            mc.options.hideGui = savedHideGui;
            savedPlayerPos = null;
        }
    }

    void beginTransition() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            animFromYaw = mc.player.getYRot();
            animFromPitch = mc.player.getXRot();
            animFromX = mc.player.getX();
            animFromY = mc.player.getY();
            animFromZ = mc.player.getZ();
            animStartTime = System.currentTimeMillis();
            animProgress = 0;
        }
    }

    void resetPan() {
        panX = 0;
        panZ = 0;
        panY = 0;
    }

    void orient(boolean topDown, Rail rail) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        Basis basis = basisOf(rail);
        float targetYaw;
        float targetPitch;
        double targetX, targetY, targetZ;

        planeY = (p1.getY() + p2.getY()) / 2.0;

        if (topDown) {
            targetYaw = (float) -Math.toDegrees(Math.atan2(basis.ax, basis.az));
            targetPitch = 90;
            targetX = (p1.getX() + p2.getX()) / 2.0 + 0.5 + panX;
            targetZ = (p1.getZ() + p2.getZ()) / 2.0 + 0.5 + panZ;
            targetY = currentY + cameraHeight;
        } else {
            targetYaw = (float) -Math.toDegrees(Math.atan2(basis.ax, basis.az)) + 90;
            targetPitch = 0;
            double midX = (p1.getX() + p2.getX()) / 2.0 + 0.5;
            double midZ = (p1.getZ() + p2.getZ()) / 2.0 + 0.5;
            targetX = midX + basis.az * cameraHeight + panX;
            targetZ = midZ - basis.ax * cameraHeight + panZ;
            targetY = (p1.getY() + p2.getY()) / 2.0 + panY;
        }

        float yaw = targetYaw;
        float pitch = targetPitch;
        double camX = targetX;
        double camY = targetY;
        double camZ = targetZ;

        if (animProgress < 1.0f) {
            long elapsed = System.currentTimeMillis() - animStartTime;
            animProgress = Math.min(1.0f, elapsed / (float) ANIM_DURATION_MS);
            float t = GuiDrawUtil.smoothStep(animProgress);
            yaw = GuiDrawUtil.lerpAngle(animFromYaw, targetYaw, t);
            pitch = (float) GuiDrawUtil.lerp(animFromPitch, targetPitch, t);
            camX = GuiDrawUtil.lerp(animFromX, targetX, t);
            camY = GuiDrawUtil.lerp(animFromY, targetY, t);
            camZ = GuiDrawUtil.lerp(animFromZ, targetZ, t);
        }

        mc.player.setPos(camX, camY, camZ);
        mc.player.xo = camX;
        mc.player.yo = camY;
        mc.player.zo = camZ;
        mc.player.xOld = camX;
        mc.player.yOld = camY;
        mc.player.zOld = camZ;
        mc.player.setDeltaMovement(0, 0, 0);
        mc.player.fallDistance = 0;
        mc.player.setYRot(yaw);
        mc.player.setXRot(pitch);
        mc.player.yRotO = yaw;
        mc.player.xRotO = pitch;
        mc.player.yHeadRot = yaw;
        mc.player.yHeadRotO = yaw;
    }

    double[] screenToWorld(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return new double[]{0, planeY, 0};

        double eyeX = mc.player.getX();
        double eyeY = mc.player.getEyeY();
        double eyeZ = mc.player.getZ();
        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();

        double fovRad = Math.toRadians(mc.options.fov().get());
        double f = 1.0 / Math.tan(fovRad / 2.0);
        double aspect = (double) viewWidth / viewHeight;

        double ndcX = 2.0 * mouseX / viewWidth - 1.0;
        double ndcY = 1.0 - 2.0 * mouseY / viewHeight;

        double rcx = ndcX * aspect / f;
        double rcy = ndcY / f;
        double rcz = -1.0;

        double yawRad = Math.toRadians(yaw + 180);
        double pitchRad = Math.toRadians(pitch);
        double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
        double cosYr = Math.cos(yawRad), sinYr = Math.sin(yawRad);

        double ry1 = rcy * cosP + rcz * sinP;
        double rz1 = -rcy * sinP + rcz * cosP;

        double rxW = rcx * cosYr - rz1 * sinYr;
        double ryW = ry1;
        double rzW = rcx * sinYr + rz1 * cosYr;

        if (Math.abs(ryW) < 1E-8) return new double[]{eyeX, planeY, eyeZ};
        double t = (planeY - eyeY) / ryW;
        return new double[]{eyeX + rxW * t, planeY, eyeZ + rzW * t};
    }

    int[] xzToScreen(double wx, double wz) {
        return worldToScreen(wx, planeY, wz);
    }

    int[] worldToScreen(double wx, double wy, double wz) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return new int[]{viewWidth / 2, viewHeight / 2};

        double eyeX = mc.player.getX();
        double eyeY = mc.player.getEyeY();
        double eyeZ = mc.player.getZ();
        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();

        double dx = wx - eyeX;
        double dy = wy - eyeY;
        double dz = wz - eyeZ;

        double yawRad = Math.toRadians(yaw + 180);
        double cosY = Math.cos(yawRad), sinY = Math.sin(yawRad);
        double x1 = dx * cosY + dz * sinY;
        double y1 = dy;
        double z1 = -dx * sinY + dz * cosY;

        double pitchRad = Math.toRadians(pitch);
        double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
        double cx = x1;
        double cy = y1 * cosP - z1 * sinP;
        double cz = y1 * sinP + z1 * cosP;

        if (cz >= -0.01) return new int[]{-9999, -9999};

        double fovRad = Math.toRadians(mc.options.fov().get());
        double f = 1.0 / Math.tan(fovRad / 2.0);
        double aspect = (double) viewWidth / viewHeight;

        double ndcX = (cx * f) / (-cz * aspect);
        double ndcY = (cy * f) / (-cz);

        int screenX = (int) ((1.0 + ndcX) * viewWidth / 2.0);
        int screenY = (int) ((1.0 - ndcY) * viewHeight / 2.0);
        return new int[]{screenX, screenY};
    }

    double pixelsPerBlock() {
        Minecraft mc = Minecraft.getInstance();
        double fovDeg = mc.options.fov().get();
        double fovRad = Math.toRadians(fovDeg);
        double visibleHeight = 2.0 * cameraHeight * Math.tan(fovRad / 2.0);
        return (double) viewHeight / visibleHeight;
    }

    static Basis basisOf(Rail rail) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return new Basis(1, 0, 0, 1, 1);
        }
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return new Basis(1, 0, 0, 1, 1);
        double dx = (p2.getX() + 0.5) - (p1.getX() + 0.5);
        double dz = (p2.getZ() + 0.5) - (p1.getZ() + 0.5);
        double len = Math.max(1, Math.sqrt(dx * dx + dz * dz));
        double ax = dx / len;
        double az = dz / len;
        return new Basis(ax, az, -az, ax, len);
    }

    record Basis(double ax, double az, double nx, double nz, double len) {}
}
