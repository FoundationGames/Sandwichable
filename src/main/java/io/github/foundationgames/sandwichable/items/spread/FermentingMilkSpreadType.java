package io.github.foundationgames.sandwichable.items.spread;

import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class FermentingMilkSpreadType extends SpreadType {
    public FermentingMilkSpreadType() {
        super(3, 0.9F, 0xC9C69D, ItemsRegistry.FERMENTING_MILK_BUCKET, Items.BUCKET);
    }

    @Override
    public void onPour(ItemStack container, ItemStack spread) {
        if(container.getNbt() != null) {
            spread.getOrCreateNbt().putInt("effectDuration", container.getNbt().getInt("percentFermented")*4);
        }
    }

    @Override
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(stack.getNbt() != null) {
            NbtCompound tag = stack.getNbt();
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, tag.getInt("effectDuration"), 5));
        }
    }
}
