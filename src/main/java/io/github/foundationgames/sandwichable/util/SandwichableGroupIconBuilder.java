package io.github.foundationgames.sandwichable.util;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.compat.CompatModuleManager;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class SandwichableGroupIconBuilder {
    public static ItemStack getIcon() {
        Sandwich sandwich = new Sandwich();
        ItemStack groupIcon = new ItemStack(BlocksRegistry.SANDWICH.asItem());
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.TOASTED_BREAD_SLICE));
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.CHEESE_SLICE_REGULAR));
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.BACON_STRIPS));
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.TOMATO_SLICE));
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.LETTUCE_LEAF));
        sandwich.addTopFoodFrom(new ItemStack(ItemsRegistry.TOASTED_BREAD_SLICE));
        groupIcon.putSubTag("BlockEntityTag", sandwich.addToTag(new NbtCompound()));
        return groupIcon;
    }

    public static ItemStack getCompatIcon() {
        List<Item> candidates = new ArrayList<>();
        int seed = 0;
        for(Item i : Registry.ITEM) {
            if(i.getGroup() == CompatModuleManager.SANDWICHABLE_COMPAT) {
                candidates.add(i);
                seed += Registry.ITEM.getRawId(i);
            }
        }
        if(candidates.size() == 0) return new ItemStack(Items.BARRIER);
        return new ItemStack(candidates.get(new Random(seed).nextInt(candidates.size())));
    }
}
