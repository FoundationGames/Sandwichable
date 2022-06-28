package io.github.foundationgames.sandwichable.items;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface TintedParticle {
    int getParticleColor(World world, ItemStack stack);
}
