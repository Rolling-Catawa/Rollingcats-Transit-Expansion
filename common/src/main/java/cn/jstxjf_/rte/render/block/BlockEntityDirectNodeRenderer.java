package cn.jstxjf_.rte.render.block;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.object.VertArray;
import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class BlockEntityDirectNodeRenderer extends BlockEntityRenderer<BlockDirectNode.BlockEntityDirectNode> {

    private static final ResourceLocation VERTICAL_MODEL_LOCATION = new ResourceLocation(RTE.MOD_ID, "models/block/rail_node_vertical.obj");
    private static final ResourceLocation CONNECTION_MODEL_LOCATION = new ResourceLocation(RTE.MOD_ID, "models/block/rail_node_connection.obj");

    private static ModelCluster verticalModel;
    private static ModelCluster connectionModel;
    private static HashSet<BlockDirectNode.BlockEntityDirectNode> entitiesToRender = new HashSet<>();
    private static HashSet<BlockDirectNode.BlockEntityDirectNode> entitiesToRenderWriting = new HashSet<>();
    public BlockEntityDirectNodeRenderer(Argument dispatcher) {
        super(dispatcher);
    }

    public static void initGLModel(ResourceManager resourceManager) {
        verticalModel = null;
        connectionModel = null;
        if (resourceManager == null) {
            RTE.LOGGER.warn("[DirectNode] initGLModel: resourceManager is null");
            return;
        }

        try {
            if (resourceManager.getResource(VERTICAL_MODEL_LOCATION).isPresent()) {
                verticalModel = RTEClient.modelManager.uploadVertArrays(RTEClient.modelManager.loadRawModel(resourceManager, VERTICAL_MODEL_LOCATION, null));
                applyColor(verticalModel, 0xAAAAFFFF);
            }
        } catch (Exception e) {
            RTE.LOGGER.error("[DirectNode] Failed to load vertical model", e);
        }

        try {
            if (resourceManager.getResource(CONNECTION_MODEL_LOCATION).isPresent()) {
                connectionModel = RTEClient.modelManager.uploadVertArrays(RTEClient.modelManager.loadRawModel(resourceManager, CONNECTION_MODEL_LOCATION, null));
                applyColor(connectionModel, 0xAAAAFFFF);
            }
        } catch (Exception e) {
            RTE.LOGGER.error("[DirectNode] Failed to load connection model", e);
        }
    }

    public static void exchange() {
        entitiesToRender = entitiesToRenderWriting;
        entitiesToRenderWriting = new HashSet<>();
    }

    public static void commit(com.mojang.blaze3d.vertex.PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (verticalModel == null && connectionModel == null) {
            return;
        }

        net.minecraft.world.phys.Vec3 camPos = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final Matrix4f worldPose = Matrix4f.translation(-(float) camPos.x, -(float) camPos.y, -(float) camPos.z);
        final HashSet<BlockDirectNode.BlockEntityDirectNode> temp = new HashSet<>(entitiesToRender);
        for (final BlockDirectNode.BlockEntityDirectNode entity : temp) {
            if (entity == null || entity.getLevel() == null) {
                continue;
            }

            final BlockPos pos = entity.getBlockPos();
            final int light = LevelRenderer.getLightColor(entity.getLevel(), pos);
            final Matrix4f basePose = worldPose.copy();
            basePose.translate(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);

            if (!entity.isBound()) {
                if (verticalModel == null) continue;
                final double radians = (System.currentTimeMillis() % 1000L) * (Math.PI * 2D / 1000D);
                basePose.rotateY((float) radians);
                RTEClient.drawScheduler.enqueue(verticalModel, basePose, light);
            } else {
                final boolean connected = entity.isConnected();
                final ModelCluster model = connected ? connectionModel : verticalModel;
                if (connected && ClientConfig.enableRail3D) continue;
                if (model == null) continue;
                float yaw = computeNodeYaw(pos, entity.getAngleRadians());
                basePose.rotateY(yaw);
                RTEClient.drawScheduler.enqueue(model, basePose, light);
            }
        }
    }

    @Override
    public void render(BlockDirectNode.BlockEntityDirectNode entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
        entitiesToRenderWriting.add(entity);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockDirectNode.BlockEntityDirectNode blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(@NotNull BlockDirectNode.BlockEntityDirectNode blockEntity, @NotNull Vec3 cameraPos) {
        return true;
    }

    private static float computeNodeYaw(BlockPos pos, double storedMathAngle) {
        float fallbackYaw = (float)(Math.PI / 2.0 - storedMathAngle);
        try {
            org.mtr.core.data.Position nodePos = new org.mtr.core.data.Position(pos.getX(), pos.getY(), pos.getZ());
            var railsAtPos = org.mtr.mod.client.MinecraftClientData.getInstance().positionsToRail.get(nodePos);
            if (railsAtPos == null || railsAtPos.isEmpty()) return fallbackYaw;
            org.mtr.core.data.Rail rail = railsAtPos.values().iterator().next();
            if (rail == null || !(((Object)rail) instanceof RailExtraSupplier extra)) return fallbackYaw;
            if (extra.getBezierCurve() == null) return fallbackYaw;
            double length = extra.getLength();
            if (!Double.isFinite(length) || length < 0.01) return fallbackYaw;
            org.mtr.mapping.holder.Vector3d posAt0 = extra.getPosition(0);
            double nodeX = pos.getX() + 0.5;
            double nodeZ = pos.getZ() + 0.5;
            double distSqTo0 = (posAt0.getXMapped() - nodeX) * (posAt0.getXMapped() - nodeX)
                    + (posAt0.getZMapped() - nodeZ) * (posAt0.getZMapped() - nodeZ);
            boolean nodeIsAtStart = distSqTo0 < 1.0;
            double epsilon = Math.min(0.5, length * 0.01);
            if (epsilon < 0.001) epsilon = 0.001;
            org.mtr.mapping.holder.Vector3d p0, p1;
            if (nodeIsAtStart) {
                p0 = posAt0;
                p1 = extra.getPosition(epsilon);
            } else {
                p0 = extra.getPosition(length - epsilon);
                p1 = extra.getPosition(length);
            }
            double dx = p1.getXMapped() - p0.getXMapped();
            double dz = p1.getZMapped() - p0.getZMapped();
            if (Math.abs(dx) < 1e-9 && Math.abs(dz) < 1e-9) return fallbackYaw;
            return (float) net.minecraft.util.Mth.atan2(dx, dz);
        } catch (Exception e) {
            return fallbackYaw;
        }
    }

    private static void applyColor(ModelCluster model, int color) {
        final VertArrays[] vertArrays = new VertArrays[]{model.uploadedOpaqueParts, model.uploadedTranslucentParts};
        for (final VertArrays vertArray : vertArrays) {
            for (final VertArray mesh : vertArray.meshList) {
                mesh.materialProp.attrState.setColor(color);
            }
        }
    }
}
