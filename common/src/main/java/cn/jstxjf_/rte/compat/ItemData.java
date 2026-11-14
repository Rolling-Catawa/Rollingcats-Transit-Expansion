package cn.jstxjf_.rte.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ItemData {

    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

    private ItemData() {
    }

    @Nullable
    public static CompoundTag getTag(ItemStack stack) {
        //#if MC>=12101
        final net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data == null ? null : data.getUnsafe();
        //#else
        return stack.getTag();
        //#endif
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        //#if MC>=12101
        final net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        final net.minecraft.world.item.component.CustomData owned = net.minecraft.world.item.component.CustomData.of(data == null ? new CompoundTag() : data.copyTag());
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, owned);
        return owned.getUnsafe();
        //#else
        return stack.getOrCreateTag();
        //#endif
    }

    public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
        //#if MC>=12101
        if (tag == null) {
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        } else {
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        }
        //#else
        stack.setTag(tag);
        //#endif
    }

    @Nullable
    public static CompoundTag getTagElement(ItemStack stack, String key) {
        //#if MC>=12101
        if (BLOCK_ENTITY_TAG.equals(key)) {
            final net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
            return data == null ? null : data.getUnsafe();
        }
        final CompoundTag tag = getTag(stack);
        return tag != null && tag.contains(key, 10) ? tag.getCompound(key) : null;
        //#else
        return stack.getTagElement(key);
        //#endif
    }

    public static CompoundTag getOrCreateTagElement(ItemStack stack, String key) {
        //#if MC>=12101
        if (BLOCK_ENTITY_TAG.equals(key)) {
            final net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
            final CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
            if (!tag.contains("id")) {
                final net.minecraft.resources.ResourceLocation type = blockEntityTypeId(stack);
                if (type != null) {
                    tag.putString("id", type.toString());
                }
            }
            final net.minecraft.world.item.component.CustomData owned = net.minecraft.world.item.component.CustomData.of(tag);
            stack.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, owned);
            return owned.getUnsafe();
        }
        final CompoundTag tag = getOrCreateTag(stack);
        if (!tag.contains(key, 10)) {
            tag.put(key, new CompoundTag());
        }
        return tag.getCompound(key);
        //#else
        return stack.getOrCreateTagElement(key);
        //#endif
    }

    //#if MC>=12101
    @Nullable
    private static net.minecraft.resources.ResourceLocation blockEntityTypeId(ItemStack stack) {
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem && blockItem.getBlock() instanceof net.minecraft.world.level.block.EntityBlock entityBlock) {
            final net.minecraft.world.level.block.entity.BlockEntity blockEntity = entityBlock.newBlockEntity(net.minecraft.core.BlockPos.ZERO, blockItem.getBlock().defaultBlockState());
            if (blockEntity != null) {
                return net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
            }
        }
        return null;
    }
    //#endif
}
