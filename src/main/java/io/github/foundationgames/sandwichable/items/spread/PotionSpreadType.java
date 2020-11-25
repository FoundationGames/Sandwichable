package io.github.foundationgames.sandwichable.items.spread;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class PotionSpreadType extends SpreadType {
    public PotionSpreadType() {
        super(0, 0.0f, 0, Items.POTION, Items.GLASS_BOTTLE);
    }

    @Override
    public int getColor(ItemStack stack) {
        return PotionUtil.getColor(stack);
    }

    @Override
    public List<StatusEffectInstance> getStatusEffects(ItemStack stack) {
        return PotionUtil.getPotion(stack).getEffects();
    }

    @Override
    public void onPour(ItemStack container, ItemStack spread) {
        Potion potion = PotionUtil.getPotion(container);
        PotionUtil.setPotion(spread, potion);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
