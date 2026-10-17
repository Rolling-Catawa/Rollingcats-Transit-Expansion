package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import cn.jstxjf_.rte.block.BlockEyeCandy.BlockEntityEyeCandy;
import cn.jstxjf_.rte.block.RTEBlockEntityTypes;
import cn.jstxjf_.rte.data.EyeCandyProperties;
import cn.jstxjf_.rte.data.EyeCandyRegistry;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockItemExtension;

import java.util.List;
import java.util.function.Supplier;

public class BlockItemEyeCandy extends BlockItemExtension {

    public BlockItemEyeCandy(org.mtr.mapping.holder.Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Override
    public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            openEyeCandyScreen(hand == Hand.MAIN_HAND ? net.minecraft.world.InteractionHand.MAIN_HAND : net.minecraft.world.InteractionHand.OFF_HAND);
        }
    }

    private static void openEyeCandyScreen(InteractionHand hand) {
        cn.jstxjf_.rte.gui.EyeCandyScreen.open(hand, null);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag == null) return;
        if (tag.contains("prefabId")) {
            if (EyeCandyRegistry.ELEMENTS.containsKey(tag.getString("prefabId"))) {
                list.add(EyeCandyRegistry.ELEMENTS.get(tag.getString("prefabId")).name);
            } else {
                list.add(Component.literal(tag.getString("prefabId")));
            }
        }
    }

    public static class Client {
        public static void fillItemCategory(List<ItemStack> items) {
            final net.minecraft.world.item.Item eyeCandyItem = BuiltInRegistries.ITEM.get(new ResourceLocation("rte", "eye_candy"));
            for (EyeCandyProperties prop : EyeCandyRegistry.ELEMENTS.values()) {
                if (eyeCandyItem == net.minecraft.world.item.Items.AIR) {
                    return;
                }
                ItemStack stack = new ItemStack(eyeCandyItem);
                CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
                VirtualEyeCandy virtualEyeCandy = new VirtualEyeCandy(() -> stack.getOrCreateTagElement("BlockEntityTag"));
                virtualEyeCandy.setPrefabId(prop.key);
                virtualEyeCandy.doWriteTag();
                items.add(stack);
            }
        }

        private static class VirtualEyeCandy extends BlockEntityEyeCandy {
            private final Supplier<CompoundTag> tagSupplier;

            public VirtualEyeCandy(Supplier<CompoundTag> tagSupplier) {
                super(new org.mtr.mapping.holder.BlockPos(0, -1145141919, 0),
                    new org.mtr.mapping.holder.BlockState(cn.jstxjf_.rte.block.RTEBlocks.eyeCandy().defaultBlockState()));
                readCompoundTag(new org.mtr.mapping.holder.CompoundTag(tagSupplier.get()));
                this.tagSupplier = tagSupplier;
            }

            public void doWriteTag() {
                writeCompoundTag(new org.mtr.mapping.holder.CompoundTag(tagSupplier.get()));
            }

            @Override
            public void sendUpdateC2S() {
                doWriteTag();
            }
        }
    }
}
