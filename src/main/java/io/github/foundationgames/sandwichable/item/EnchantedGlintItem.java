package io.github.foundationgames.sandwichable.item;

import net.minecraft.item.ItemStack;

public class EnchantedGlintItem extends InfoTooltipItem {
    public EnchantedGlintItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }
}
