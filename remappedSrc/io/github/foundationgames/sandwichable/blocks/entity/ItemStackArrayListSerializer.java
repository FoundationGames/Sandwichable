package io.github.foundationgames.sandwichable.blocks.entity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public class ItemStackArrayListSerializer {
    public static ListTag serializeItems(List<ItemStack> list) {
        ListTag listTag = new ListTag();
        for(ItemStack stack : list) {
            listTag.add(stack.toTag(new CompoundTag()));
        }
        return listTag;
    }
    public static List<ItemStack> deserializeItems(ListTag listTag) {
        List<ItemStack> list = Lists.newArrayList();
        for(Tag tag : listTag) {
            list.add(ItemStack.fromTag((CompoundTag)tag));
        }
        return list;
    }
}
