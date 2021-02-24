package io.github.foundationgames.sandwichable.item;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Random;

public interface BottleCrateStorable {
    ItemStack bottleCrateRandomTick(Inventory inventory, ItemStack stack, Random random);
}
