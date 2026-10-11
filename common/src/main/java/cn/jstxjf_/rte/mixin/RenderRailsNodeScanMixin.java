package cn.jstxjf_.rte.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.RenderRails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = RenderRails.class, remap = false)
public abstract class RenderRailsNodeScanMixin {

    @Unique
    private static final int RTE_NODE_SCAN_RADIUS = 16;

    @ModifyConstant(method = "render", constant = @Constant(intValue = 16), require = 0)
    private static int rte$skipFullNodeScan(int original) {
        return -RTE_NODE_SCAN_RADIUS - 1;
    }

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private static void rte$renderNodesFromPalette(CallbackInfo ci) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        final ClientLevel level = minecraft.level;
        if (player == null || level == null || !RenderRails.isHoldingRailRelated(new ClientPlayerEntity(player))) {
            return;
        }
        final int centerX = player.getBlockX();
        final int centerY = player.getBlockY();
        final int centerZ = player.getBlockZ();
        final int light = MainRenderer.getFlashingLight();
        for (int sectionX = SectionPos.blockToSectionCoord(centerX - RTE_NODE_SCAN_RADIUS); sectionX <= SectionPos.blockToSectionCoord(centerX + RTE_NODE_SCAN_RADIUS); sectionX++) {
            for (int sectionZ = SectionPos.blockToSectionCoord(centerZ - RTE_NODE_SCAN_RADIUS); sectionZ <= SectionPos.blockToSectionCoord(centerZ + RTE_NODE_SCAN_RADIUS); sectionZ++) {
                final LevelChunk chunk = level.getChunkSource().getChunkNow(sectionX, sectionZ);
                if (chunk == null) {
                    continue;
                }
                final LevelChunkSection[] sections = chunk.getSections();
                for (int sectionY = SectionPos.blockToSectionCoord(centerY - RTE_NODE_SCAN_RADIUS); sectionY <= SectionPos.blockToSectionCoord(centerY + RTE_NODE_SCAN_RADIUS); sectionY++) {
                    final int index = level.getSectionIndexFromSectionY(sectionY);
                    if (index < 0 || index >= sections.length) {
                        continue;
                    }
                    final LevelChunkSection section = sections[index];
                    if (section == null || section.hasOnlyAir() || !section.getStates().maybeHas(state -> state.getBlock() instanceof BlockNode)) {
                        continue;
                    }
                    rte$renderSectionNodes(section, sectionX, sectionY, sectionZ, centerX, centerY, centerZ, light);
                }
            }
        }
    }

    @Unique
    private static void rte$renderSectionNodes(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, int centerX, int centerY, int centerZ, int light) {
        final int minX = Math.max(SectionPos.sectionToBlockCoord(sectionX), centerX - RTE_NODE_SCAN_RADIUS);
        final int maxX = Math.min(SectionPos.sectionToBlockCoord(sectionX) + 15, centerX + RTE_NODE_SCAN_RADIUS);
        final int minY = Math.max(SectionPos.sectionToBlockCoord(sectionY), centerY - RTE_NODE_SCAN_RADIUS);
        final int maxY = Math.min(SectionPos.sectionToBlockCoord(sectionY) + 15, centerY + RTE_NODE_SCAN_RADIUS);
        final int minZ = Math.max(SectionPos.sectionToBlockCoord(sectionZ), centerZ - RTE_NODE_SCAN_RADIUS);
        final int maxZ = Math.min(SectionPos.sectionToBlockCoord(sectionZ) + 15, centerZ + RTE_NODE_SCAN_RADIUS);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    final BlockState state = section.getBlockState(x & 15, y & 15, z & 15);
                    if (!(state.getBlock() instanceof BlockNode)) {
                        continue;
                    }
                    final org.mtr.mapping.holder.BlockState blockState = new org.mtr.mapping.holder.BlockState(state);
                    final org.mtr.mapping.holder.BlockPos blockPos = new org.mtr.mapping.holder.BlockPos(x, y, z);
                    RenderRailsNodeInvoker.rte$invokeRenderNode(blockState, blockPos, () -> rte$isOrphanConnectedNode(blockState, blockPos), light);
                }
            }
        }
    }

    @Unique
    private static boolean rte$isOrphanConnectedNode(org.mtr.mapping.holder.BlockState blockState, org.mtr.mapping.holder.BlockPos blockPos) {
        final Comparable<?> connected = blockState.get(new Property<>(BlockNode.IS_CONNECTED.data));
        return Boolean.TRUE.equals(connected) && !MinecraftClientData.getInstance().positionsToRail.containsKey(Init.blockPosToPosition(blockPos));
    }
}
