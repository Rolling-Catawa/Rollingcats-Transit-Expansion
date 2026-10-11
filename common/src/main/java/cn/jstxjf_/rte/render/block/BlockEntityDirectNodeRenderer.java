package cn.jstxjf_.rte.render.block;

import cn.jstxjf_.rte.block.BlockDirectNode;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

public class BlockEntityDirectNodeRenderer extends BlockEntityRenderer<BlockDirectNode.BlockEntityDirectNode> {
    private static HashSet<BlockDirectNode.BlockEntityDirectNode> entitiesToRender = new HashSet<>();
    private static HashSet<BlockDirectNode.BlockEntityDirectNode> entitiesToRenderWriting = new HashSet<>();
    public BlockEntityDirectNodeRenderer(Argument dispatcher) {
        super(dispatcher);
    }

    public static void initGLModel(ResourceManager resourceManager) {
    }

    public static void exchange() {
        entitiesToRender = entitiesToRenderWriting;
        entitiesToRenderWriting = new HashSet<>();
    }

    public static List<cn.jstxjf_.rte.block.BlockFreeNode.BlockEntityFreeNode> getFreeNodes() {
        return entitiesToRender.stream()
                .filter(cn.jstxjf_.rte.block.BlockFreeNode.BlockEntityFreeNode.class::isInstance)
                .map(cn.jstxjf_.rte.block.BlockFreeNode.BlockEntityFreeNode.class::cast)
                .toList();
    }

    public static void commit(com.mojang.blaze3d.vertex.PoseStack matrices, MultiBufferSource vertexConsumers) {
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

}
