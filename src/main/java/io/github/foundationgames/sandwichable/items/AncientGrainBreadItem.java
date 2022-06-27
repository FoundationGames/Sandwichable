package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.util.AncientGrainType;
import net.minecraft.item.ItemStack;

public class AncientGrainBreadItem extends BiomeVariantItem implements DynamicFood {
    private final float foodValueMultiplier;

    public AncientGrainBreadItem(Settings settings, float foodValueMultiplier) {
        super(settings);
        this.foodValueMultiplier = foodValueMultiplier;
    }

    @Override
    public int getRestoredFood(ItemStack stack) {
        return (int) (AncientGrainType.get(getBiome(stack)).food * foodValueMultiplier);
    }

    @Override
    public float getRestoredSaturation(ItemStack stack) {
        return AncientGrainType.get(getBiome(stack)).saturation * foodValueMultiplier;
    }
}
