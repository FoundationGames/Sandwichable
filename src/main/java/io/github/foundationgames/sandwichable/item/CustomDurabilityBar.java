package io.github.foundationgames.sandwichable.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

public interface CustomDurabilityBar {
    @Environment(EnvType.CLIENT)
    float getBarLength(ItemStack stack);
    @Environment(EnvType.CLIENT)
    int getBarColor(ItemStack stack);
    @Environment(EnvType.CLIENT)
    boolean showBar(ItemStack stack);
}
