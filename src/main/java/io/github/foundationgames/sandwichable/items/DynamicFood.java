package io.github.foundationgames.sandwichable.items;

import net.minecraft.item.ItemStack;

public interface DynamicFood {
    int getRestoredFood(ItemStack stack);

    float getRestoredSaturation(ItemStack stack);
}
