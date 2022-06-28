package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.util.AncientGrainType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AncientGrainItem extends BiomeVariantItem implements TintedParticle {
    public AncientGrainItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getParticleColor(World world, ItemStack stack) {
        return AncientGrainType.get(getBiome(world, stack)).color;
    }
}
