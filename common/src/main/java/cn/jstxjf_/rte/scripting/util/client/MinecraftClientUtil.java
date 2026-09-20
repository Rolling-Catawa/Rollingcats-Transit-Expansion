package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import cn.jstxjf_.rte.scripting.util.WrappedEntity;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.*;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.block.BlockPlatform;
import org.mtr.mod.block.BlockPSDAPGBase;
import org.mtr.mod.client.MinecraftClientData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class MinecraftClientUtil {

    private static Class<?> IBlockPlatformClass = Void.class;

    static {
        try {
            IBlockPlatformClass = Class.forName("team.dovecotmc.metropolis.block.interfaces.IBlockPlatform");
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static boolean worldIsRaining() {
        ClientLevel l = Minecraft.getInstance().level;
        return l != null && l.isRaining();
    }

    public static boolean worldIsRainingAt(Vector3f pos) {
        ClientLevel l = Minecraft.getInstance().level;
        return l != null && l.isRainingAt(new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()));
    }

    public static long worldDayTime() {
        ClientLevel l = Minecraft.getInstance().level;
        return l != null ? l.getDayTime() : 0;
    }

    public static void narrate(String message) {
        try { com.mojang.text2speech.Narrator.getNarrator().say(message, true); } catch (Exception ignored) {}
    }

    public static void displayMessage(String message, boolean actionBar) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(message), actionBar);
        }
    }

    public static void execute(Runnable runnable) { Minecraft.getInstance().execute(runnable); }

    public static void recordRenderCall(Runnable renderCall) { RenderSystem.recordRenderCall(renderCall::run); }

    public static boolean isOnRenderThreadOrInit() { return RenderSystem.isOnRenderThreadOrInit(); }

    public static Vector3f getCameraPos() {
        Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        return new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
    }

    public static float getCameraDistance(Vector3f from) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double dx = camPos.x - from.x(), dy = camPos.y - from.y(), dz = camPos.z - from.z();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static ClientLevel getLevel() { return Minecraft.getInstance().level; }

    public static WrappedEntity getCameraEntity() {
        Entity e = Minecraft.getInstance().getCameraEntity();
        return e != null ? new WrappedEntity(e) : null;
    }

    public static WrappedEntity getPlayer() {
        Entity e = Minecraft.getInstance().player;
        return e != null ? new WrappedEntity(e) : null;
    }

    public static int packLightTexture(int block, int sky) { return LightTexture.pack(block, sky); }

    public static int getLightColor(Vector3f pos) {
        ClientLevel l = Minecraft.getInstance().level;
        if (l == null) return 0;
        return LevelRenderer.getLightColor(l, new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()));
    }

    public static void setScreen(Screen screen) { Minecraft.getInstance().setScreen(screen); }
    public static Screen getPresentScreen() { return Minecraft.getInstance().screen; }

    public static void reloadResourcePacks() { Minecraft.getInstance().reloadResourcePacks(); }
    public static void markRendererAllChanged() { Minecraft.getInstance().levelRenderer.allChanged(); }

    public static double asin(double value) { return Math.asin(value); }

    public static void levelEvent(int id, Vector3f pos, int data) {
        ClientLevel l = Minecraft.getInstance().level;
        if (l != null) {
            l.levelEvent(id, new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()), data);
        }
    }

    public static Station getStationAt(Vector3f pos) {
        try {
            MinecraftClientData data = MinecraftClientData.getInstance();
            Position tscPos = new Position((long) pos.x(), (long) pos.y(), (long) pos.z());
            for (Station station : data.stations) {
                if (station.inArea(tscPos)) return station;
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static Platform getPlatformAt(Vector3f pos, int radius) {
        try {
            MinecraftClientData data = MinecraftClientData.getInstance();
            Position tscPos = new Position((long) pos.x(), (long) pos.y(), (long) pos.z());
            double closestDist = Double.MAX_VALUE;
            Platform closest = null;
            for (Platform platform : data.platforms) {
                Position mid = platform.getMidPosition();
                double dx = mid.getX() - tscPos.getX();
                double dy = mid.getY() - tscPos.getY();
                double dz = mid.getZ() - tscPos.getZ();
                double dist = dx * dx + dy * dy + dz * dz;
                if (dist < radius * radius && dist < closestDist) {
                    closestDist = dist;
                    closest = platform;
                }
            }
            return closest;
        } catch (Exception ignored) {}
        return null;
    }

    public static Platform getPlatformAt(Vector3f pos, int radius, int lower, int upper) {
        return getPlatformAt(pos, radius);
    }

    public static Vector3f getNodeAt(Vector3f vPos, float facing) {
        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos((int) Math.round(vPos.x()), (int) Math.round(vPos.y()), (int) Math.round(vPos.z()));
        Direction dir = Direction.fromYRot(facing);
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return null;
        final int[] checkDistance = {0, 1, -1, 2, -2, 3, -3, 4, -4};
        for (int z : checkDistance) {
            for (int x : checkDistance) {
                for (int y = -5; y <= 0; y++) {
                    net.minecraft.core.BlockPos checkPos = pos.above(y).relative(dir.getClockWise(), x).relative(dir, z);
                    if (world.getBlockState(checkPos).getBlock() instanceof BlockNode) {
                        return new Vector3f(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                    }
                }
            }
        }
        return null;
    }

    public static Vector3f getNodeAt(Vector3f vPos, Float facing) {
        return getNodeAt(vPos, facing == null ? 0 : facing);
    }

    public static int getOccupiedAspect(Vector3f vPos, float facing, int aspects) {
        try {
            MinecraftClientData data = MinecraftClientData.getInstance();
            if (data == null) return -1;

            Map<Position, Float> nodesToScan = new HashMap<>();
            Position startPos = new Position(Math.round(vPos.x()), Math.round(vPos.y()), Math.round(vPos.z()));
            nodesToScan.put(startPos, facing);

            int occupiedAspect = -1;

            for (int j = 1; j < aspects; j++) {
                final Map<Position, Float> newNodesToScan = new HashMap<>();

                for (Map.Entry<Position, Float> checkNode : nodesToScan.entrySet()) {
                    final Map<Position, Rail> railMap = data.positionsToRail.get(checkNode.getKey());

                    if (railMap != null) {
                        for (Position endPos : railMap.keySet()) {
                            float directionDeg = (float) Math.toDegrees(Math.atan2(
                                endPos.getZ() - checkNode.getKey().getZ(),
                                endPos.getX() - checkNode.getKey().getX()
                            ));

                            if (org.mtr.core.tool.Angle.similarFacing(checkNode.getValue(), directionDeg)) {
                                String hexId = TwoPositionsBase.getHexId(checkNode.getKey(), endPos);
                                if (data.blockedRailIds.contains(hexId)) {
                                    return j;
                                }
                                newNodesToScan.put(endPos, directionDeg + 180);
                                occupiedAspect = 0;
                            }
                        }
                    }
                }

                nodesToScan = newNodesToScan;
            }

            return occupiedAspect;
        } catch (Exception ignored) {}
        return -1;
    }

    public static boolean[] canOpenDoorsAt(Vector3f p1, Vector3f p2) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return new boolean[]{false, false};

        final Vec3 pos1 = new Vec3(p1.x(), p1.y(), p1.z());
        final Vec3 pos2 = new Vec3(p2.x(), p2.y(), p2.z());

        final double x = getAverage(pos1.x, pos2.x);
        final double y = getAverage(pos1.y, pos2.y) + 1;
        final double z = getAverage(pos1.z, pos2.z);

        final double realSpacing = pos2.distanceTo(pos1);
        final float yaw = (float) Math.atan2(pos2.x - pos1.x, pos2.z - pos1.z);
        final float pitch = realSpacing == 0 ? 0 : (float) Math.asin((pos2.y - pos1.y) / realSpacing);

        final boolean doorLeftOpen = scanDoors(world, x, y, z, (float) Math.PI + yaw, pitch, realSpacing / 2);
        final boolean doorRightOpen = scanDoors(world, x, y, z, yaw, pitch, realSpacing / 2);
        return new boolean[]{doorLeftOpen, doorRightOpen};
    }

    public static double getAverage(double a, double b) {
        return (a + b) / 2;
    }

    private static boolean scanDoors(ClientLevel world, double trainX, double trainY, double trainZ, float checkYaw, float pitch, double halfSpacing) {
        final Vec3 offsetVec = new Vec3(1, 0, 0).yRot(checkYaw).xRot(pitch);
        final Vec3 traverseVec = new Vec3(0, 0, 1).yRot(checkYaw).xRot(pitch);
        Set<BlockPos> checkedEyeCandy = new HashSet<>();
        for (int checkX = 1; checkX <= 3; checkX++) {
            for (int checkY = -2; checkY <= 3; checkY++) {
                for (double checkZ = -halfSpacing; checkZ <= halfSpacing; checkZ++) {
                    final BlockPos checkPos = new BlockPos(
                        (int) Math.floor(trainX + offsetVec.x * checkX + traverseVec.x * checkZ),
                        (int) Math.floor(trainY + checkY),
                        (int) Math.floor(trainZ + offsetVec.z * checkX + traverseVec.z * checkZ)
                    );
                    net.minecraft.world.level.block.Block block = world.getBlockState(checkPos).getBlock();
                    // Keep MTR 3's exact platform-block rule, then add MTR 4
                    // helpers and known add-on platform interfaces.
                    if (block instanceof BlockPlatform || block instanceof org.mtr.mod.block.PlatformHelper
                            || block instanceof BlockPSDAPGBase || IBlockPlatformClass.isInstance(block)) {
                        return true;
                    }
                    if (block instanceof BlockEyeCandy) {
                        if (checkEyeCandyPlatform(world, checkPos, checkedEyeCandy)) {
                            return true;
                        }
                        int[] dir = new int[]{1, -1};
                        int[] axis = new int[]{1, 0, 0, 1, 0, 0};
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 2; j++) {
                                for (int k = 1; k <= 40; k++) {
                                    int value = dir[j] * k;
                                    BlockPos pos = checkPos.offset(axis[i] * value, axis[i + 1] * value, axis[i + 2] * value);
                                    if (checkedEyeCandy.contains(pos)) break;
                                    if (checkEyeCandyPlatform(world, pos, checkedEyeCandy)) {
                                        return true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkEyeCandyPlatform(ClientLevel world, BlockPos pos, Set<BlockPos> checkedEyeCandy) {
        checkedEyeCandy.add(pos);
        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof BlockEyeCandy.BlockEntityEyeCandy eyeCandy && eyeCandy.isPlatform();
    }

    private interface Void {
    }
}
