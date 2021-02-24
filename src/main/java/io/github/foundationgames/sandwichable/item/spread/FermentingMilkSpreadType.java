package io.github.foundationgames.sandwichable.item.spread;

import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class FermentingMilkSpreadType extends SpreadType {
    public FermentingMilkSpreadType() {
        super(3, 0.9F, 0xC9C69D, ItemsRegistry.FERMENTING_MILK_BUCKET, Items.BUCKET);
    }

    @Override
    public void onPour(ItemStack container, ItemStack spread) {
        if(container.getTag() != null) {
            spread.getOrCreateTag().putInt("effectDuration", container.getTag().getInt("percentFermented")*4);
        }
    }

    @Override
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(stack.getTag() != null) {
            CompoundTag tag = stack.getTag();
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, tag.getInt("effectDuration"), 5));
        }
    }
}
