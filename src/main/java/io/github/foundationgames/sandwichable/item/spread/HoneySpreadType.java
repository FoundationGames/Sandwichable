package io.github.foundationgames.sandwichable.item.spread;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class HoneySpreadType extends SpreadType{
    public HoneySpreadType() {
        super(6, 0.1F, 0xF08A1D, Items.HONEY_BOTTLE, Items.GLASS_BOTTLE);
    }

    @Override
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            user.removeStatusEffect(StatusEffects.POISON);
        }
    }
}
