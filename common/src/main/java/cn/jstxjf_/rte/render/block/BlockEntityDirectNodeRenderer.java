package cn.jstxjf_.rte.render.block;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.BlockFreeNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfx.object.VertArray;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.client.MinecraftClientData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import cn.jstxjf_.rte.compat.McCompat;

public class BlockEntityDirectNodeRenderer extends BlockEntityRenderer<BlockDirectNode.BlockEntityDirectNode> {

    private static final ResourceLocation VERTICAL_MODEL_LOCATION = McCompat.id(RTE.MOD_ID, "models/block/rail_node_vertical.obj");
    private static final ResourceLocation CONNECTION_MODEL_LOCATION = McCompat.id(RTE.MOD_ID, "models/block/rail_node_connection.obj");
    private static final int MODEL_COLOR = 0xAAAAFFFF;

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
            return;
        }
        verticalModel = loadModel(resourceManager, VERTICAL_MODEL_LOCATION);
        connectionModel = loadModel(resourceManager, CONNECTION_MODEL_LOCATION);
    }

    private static ModelCluster loadModel(ResourceManager resourceManager, ResourceLocation location) {
        try {
            if (resourceManager.getResource(location).isEmpty()) {
                return null;
            }
            final ModelCluster model = RTEClient.modelManager.uploadVertArrays(RTEClient.modelManager.loadRawModel(resourceManager, location, null));
            applyColor(model, MODEL_COLOR);
            return model;
        } catch (Exception e) {
            RTE.LOGGER.error("[DirectNode] Failed to load model {}", location, e);
            return null;
        }
    }

    public static void exchange() {
        entitiesToRender = entitiesToRenderWriting;
        entitiesToRenderWriting = new HashSet<>();
    }

    public static List<BlockFreeNode.BlockEntityFreeNode> getFreeNodes() {
        return entitiesToRender.stream()
                .filter(BlockFreeNode.BlockEntityFreeNode.class::isInstance)
                .map(BlockFreeNode.BlockEntityFreeNode.class::cast)
                .toList();
    }

    public static void commit(com.mojang.blaze3d.vertex.PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (verticalModel == null && connectionModel == null) {
            return;
        }
        final Vec3 camPos = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final Matrix4f worldPose = Matrix4f.translation(-(float) camPos.x, -(float) camPos.y, -(float) camPos.z);
        for (final BlockDirectNode.BlockEntityDirectNode entity : new HashSet<>(entitiesToRender)) {
            if (entity == null || entity instanceof BlockFreeNode.BlockEntityFreeNode || entity.getLevel() == null) {
                continue;
            }
            final BlockPos pos = entity.getBlockPos();
            final int light = LevelRenderer.getLightColor(entity.getLevel(), pos);
            final Matrix4f basePose = worldPose.copy();
            basePose.translate((float) (pos.getX() + 0.5F + entity.getXOffset()),
                    (float) (pos.getY() + 0.5F + entity.getYOffset()),
                    (float) (pos.getZ() + 0.5F + entity.getZOffset()));
            if (!entity.isBound()) {
                if (verticalModel == null) {
                    continue;
                }
                applyAttachment(basePose, entity.getAttachmentFace());
                basePose.rotateY((float) ((System.currentTimeMillis() % 1000L) * (Math.PI * 2D / 1000D)));
                RTEClient.drawScheduler.enqueue(verticalModel, basePose, light);
            } else {
                final boolean connected = entity.isConnected();
                if (connected && ClientConfig.enableRail3D) {
                    continue;
                }
                final ModelCluster model = connected ? connectionModel : verticalModel;
                if (model == null) {
                    continue;
                }
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
            final Map<Position, Rail> railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(new Position(pos.getX(), pos.getY(), pos.getZ()));
            if (railsAtPos == null || railsAtPos.isEmpty()) {
                return fallback;
            }
            final Rail rail = railsAtPos.values().iterator().next();
            if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
                return fallback;
            }
            final double length = extra.getLength();
            if (!Double.isFinite(length) || length < 0.01) {
                return fallback;
            }
            final Vec3 posAt0 = extra.getPosition(0);
            final double nodeX = pos.getX() + 0.5 + entity.getXOffset();
            final double nodeZ = pos.getZ() + 0.5 + entity.getZOffset();
            final boolean nodeIsAtStart = (posAt0.x - nodeX) * (posAt0.x - nodeX) + (posAt0.z - nodeZ) * (posAt0.z - nodeZ) < 1.0;
            final double epsilon = Math.max(0.001, Math.min(0.5, length * 0.01));
            final Vec3 p0 = nodeIsAtStart ? posAt0 : extra.getPosition(length);
            final Vec3 p1 = extra.getPosition(nodeIsAtStart ? epsilon : length - epsilon);
            final double dx = p1.x - p0.x;
            final double dy = p1.y - p0.y;
            final double dz = p1.z - p0.z;
            final double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (horizontal < 1e-9 && Math.abs(dy) < 1e-9) {
                return fallback;
            }
            final float yaw = horizontal < 1e-9 ? fallback.yaw : (float) Mth.atan2(dx, dz);
            return new NodeOrientation(yaw, (float) Mth.atan2(dy, horizontal));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static void applyAttachment(Matrix4f pose, int faceId) {
        final Direction face = Direction.from3DDataValue(faceId);
        if (face == Direction.DOWN) {
            pose.rotateX((float) Math.PI);
        } else if (face == Direction.NORTH) {
            pose.rotateX((float) (-Math.PI / 2));
        } else if (face == Direction.SOUTH) {
            pose.rotateX((float) (Math.PI / 2));
        } else if (face == Direction.EAST) {
            pose.rotateZ((float) (-Math.PI / 2));
        } else if (face == Direction.WEST) {
            pose.rotateZ((float) (Math.PI / 2));
        }
    }

    private record NodeOrientation(float yaw, float pitch) {
    }

    private static void applyColor(ModelCluster model, int color) {
        for (final VertArrays vertArrays : new VertArrays[]{model.uploadedOpaqueParts, model.uploadedTranslucentParts}) {
            for (final VertArray mesh : vertArrays.meshList) {
                mesh.materialProp.attrState.setColor(color);
            }
        }
    }
}
