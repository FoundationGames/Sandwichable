package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class SandwichableGroupIconBuilder {
    public static ItemStack getIcon() {
        DefaultedList<ItemStack> sandwichlist = DefaultedList.ofSize(128, ItemStack.EMPTY);
        ItemStack groupIcon = new ItemStack(BlocksRegistry.SANDWICH.asItem());
        sandwichlist.set(0, new ItemStack(ItemsRegistry.CHEESE_SLICE_REGULAR));
        sandwichlist.set(1, new ItemStack(ItemsRegistry.BACON_STRIPS));
        sandwichlist.set(2, new ItemStack(ItemsRegistry.LETTUCE_LEAF));
        sandwichlist.set(3, new ItemStack(ItemsRegistry.TOMATO_SLICE));
        sandwichlist.set(4, new ItemStack(ItemsRegistry.TOASTED_BREAD_SLICE));
        groupIcon.putSubTag("BlockEntityTag", Inventories.writeNbt(new NbtCompound(), sandwichlist));
        return groupIcon;
    }
}
