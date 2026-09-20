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
            basePose.translate((float) (pos.getX() + 0.5F + entity.getXOffset()),
                    (float) (pos.getY() + 0.5F + entity.getYOffset()),
                    (float) (pos.getZ() + 0.5F + entity.getZOffset()));

            if (!entity.isBound()) {
                if (verticalModel == null) continue;
                applyAttachment(basePose, entity.getAttachmentFace());
                final double radians = (System.currentTimeMillis() % 1000L) * (Math.PI * 2D / 1000D);
                basePose.rotateY((float) radians);
                RTEClient.drawScheduler.enqueue(verticalModel, basePose, light);
            } else {
                final boolean connected = entity.isConnected();
                final ModelCluster model = connected ? connectionModel : verticalModel;
                if (connected && ClientConfig.enableRail3D) continue;
                if (model == null) continue;
                final NodeOrientation orientation = computeNodeOrientation(pos, entity);
                basePose.rotateY(orientation.yaw);
                basePose.rotateX(-orientation.pitch);
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

    private static NodeOrientation computeNodeOrientation(BlockPos pos, BlockDirectNode.BlockEntityDirectNode entity) {
        final NodeOrientation fallback = new NodeOrientation(
                (float) (Math.PI / 2.0 - entity.getAngleRadians()),
                (float) Math.toRadians(entity.getPitchDegrees()));
        try {
            org.mtr.core.data.Position nodePos = new org.mtr.core.data.Position(pos.getX(), pos.getY(), pos.getZ());
            var railsAtPos = org.mtr.mod.client.MinecraftClientData.getInstance().positionsToRail.get(nodePos);
            if (railsAtPos == null || railsAtPos.isEmpty()) return fallback;
            org.mtr.core.data.Rail rail = railsAtPos.values().iterator().next();
            if (rail == null || !(((Object)rail) instanceof RailExtraSupplier extra)) return fallback;
            double length = extra.getLength();
            if (!Double.isFinite(length) || length < 0.01) return fallback;
            org.mtr.mapping.holder.Vector3d posAt0 = extra.getPosition(0);
            double nodeX = pos.getX() + 0.5 + entity.getXOffset();
            double nodeZ = pos.getZ() + 0.5 + entity.getZOffset();
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
                p0 = extra.getPosition(length);
                p1 = extra.getPosition(length - epsilon);
            }
            double dx = p1.getXMapped() - p0.getXMapped();
            double dy = p1.getYMapped() - p0.getYMapped();
            double dz = p1.getZMapped() - p0.getZMapped();
            final double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (horizontal < 1e-9 && Math.abs(dy) < 1e-9) return fallback;
            final float yaw = horizontal < 1e-9 ? fallback.yaw : (float) net.minecraft.util.Mth.atan2(dx, dz);
            return new NodeOrientation(yaw, (float) net.minecraft.util.Mth.atan2(dy, horizontal));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static void applyAttachment(Matrix4f pose, int faceId) {
        final org.mtr.mapping.holder.Direction face = org.mtr.mapping.holder.Direction.byId(faceId);
        if (face == org.mtr.mapping.holder.Direction.DOWN) pose.rotateX((float) Math.PI);
        else if (face == org.mtr.mapping.holder.Direction.NORTH) pose.rotateX((float) (-Math.PI / 2));
        else if (face == org.mtr.mapping.holder.Direction.SOUTH) pose.rotateX((float) (Math.PI / 2));
        else if (face == org.mtr.mapping.holder.Direction.EAST) pose.rotateZ((float) (-Math.PI / 2));
        else if (face == org.mtr.mapping.holder.Direction.WEST) pose.rotateZ((float) (Math.PI / 2));
    }

    private record NodeOrientation(float yaw, float pitch) {
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
