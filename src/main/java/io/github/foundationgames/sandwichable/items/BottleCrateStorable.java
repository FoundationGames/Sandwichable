package io.github.foundationgames.sandwichable.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

public interface BottleCrateStorable {
    ItemStack bottleCrateRandomTick(Inventory inventory, ItemStack stack, Random random);
}
