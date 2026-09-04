package cn.jstxjf_.rte.render.block;

import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.block.BlockEyeCandy;
import cn.jstxjf_.rte.data.EyeCandyProperties;
import cn.jstxjf_.rte.data.EyeCandyRegistry;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import cn.jstxjf_.rte.scripting.eyecandy.EyeCandyScriptContext;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import com.mojang.math.Axis;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Items;

import java.util.HashSet;

public class BlockEntityEyeCandyRenderer extends BlockEntityRenderer<BlockEyeCandy.BlockEntityEyeCandy> {

    private static final ItemStack BARRIER_STACK = new ItemStack(net.minecraft.world.item.Items.BARRIER);
    private static ItemStack brushStack = null;

    private static ItemStack getBrushStack() {
        if (brushStack == null) {
            try {
                brushStack = new ItemStack(Items.BRUSH.get().asItem().data);
            } catch (Exception e) {
                brushStack = BARRIER_STACK;
            }
        }
        return brushStack;
    }
    private static HashSet<BlockEyeCandy.BlockEntityEyeCandy> entitysToRender = new HashSet<>();
    private static HashSet<BlockEyeCandy.BlockEntityEyeCandy> entitysToRenderWritting = new HashSet<>();

    public BlockEntityEyeCandyRenderer(BlockEntityRenderer.Argument ctx) {
        super(ctx);
    }

    @Override
    public void render(BlockEyeCandy.@NotNull BlockEntityEyeCandy blockEntity, float f, @NotNull GraphicsHolder graphicsHolder, int light, int overlay) {
        entitysToRenderWritting.add(blockEntity);
    }

    public static void exchange() {
        entitysToRender = entitysToRenderWritting;
        entitysToRenderWritting = new HashSet<>();
    }

    public static void commit(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers) {
        net.minecraft.world.phys.Vec3 camPos = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Matrix4f worldPose = Matrix4f.translation(-(float) camPos.x, -(float) camPos.y, -(float) camPos.z);
        HashSet<BlockEyeCandy.BlockEntityEyeCandy> temp = new HashSet<>(entitysToRender);
        for (BlockEyeCandy.BlockEntityEyeCandy blockEntity : temp) {
            if (blockEntity == null) continue;
            final Level world = blockEntity.getLevel();
            if (world == null) continue;
            BlockPos pos = blockEntity.getBlockPos();
            int light = LevelRenderer.getLightColor(world, pos);
            int lightToUse = blockEntity.fullLight ? LightTexture.pack(15, 15) : light;

            EyeCandyProperties prop = EyeCandyRegistry.getProperty(blockEntity.prefabId);
            if (prop == null || RailRenderDispatcher.isHoldingBrush) {
                matrices.pushPose();
                matrices.translate(pos.getX(), pos.getY(), pos.getZ());
                matrices.translate(0.5f, 0.5f, 0.5f);
                matrices.mulPose(Axis.YP.rotation((float) ((System.currentTimeMillis() % 1000) * (Math.PI * 2 / 1000))));
                ItemStack iconStack = (blockEntity.prefabId != null && prop == null) ? BARRIER_STACK : getBrushStack();
                Minecraft.getInstance().getItemRenderer().renderStatic(
                    iconStack, ItemDisplayContext.GROUND,
                    lightToUse, 0, matrices, vertexConsumers, world, 0);
                matrices.popPose();
            }
            if (prop == null) continue;

            Matrix4f candyPose = worldPose.copy();
            candyPose.mul(blockEntity.getBaseMatrix());
            if (prop.model != null) {
                RTEClient.drawScheduler.enqueue(prop.model, candyPose, lightToUse);
            }
            if (prop.script != null && (blockEntity.scriptContext == null || blockEntity.scriptContext.disposed || blockEntity.scriptContext.disposeForReload)) {
                blockEntity.scriptContext = new EyeCandyScriptContext(blockEntity);
            }
            if (prop.script != null && blockEntity.scriptContext != null
                    && !cn.jstxjf_.rte.util.JcmCompat.shouldDisableRteEyeCandyJs()
                    && !cn.jstxjf_.rte.util.JcmCompat.isEyeCandyTakenByJcm(
                        blockEntity.prefabId, pos.getX(), pos.getY(), pos.getZ())) {
                synchronized (blockEntity.scriptContext) {
                    blockEntity.scriptContext.commit(RTEClient.drawScheduler, candyPose, worldPose, lightToUse);
                }
                prop.script.tryCallRenderFunctionAsync(blockEntity.scriptContext);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEyeCandy.@NotNull BlockEntityEyeCandy blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(BlockEyeCandy.@NotNull BlockEntityEyeCandy blockEntity, @NotNull Vec3 cameraPos) {
        return true;
    }

}
