package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpreadItem extends Item {

    public SpreadItem() {
        super(new Settings().food(new FoodComponent.Builder().build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof PlayerEntity && stack.getTag().contains("spreadType")) {
            SpreadType type = SpreadRegistry.INSTANCE.fromString(stack.getTag().getString("spreadType"));
            if(!((PlayerEntity)user).isCreative()) ((PlayerEntity)user).getHungerManager().add(type.getHunger(), type.getSaturationModifier());
            for(StatusEffectInstance effect : type.getStatusEffects(stack)) {
                user.addStatusEffect(effect);
            }
            type.finishUsing(stack, world, user);
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if(stack.getTag() != null) {
            if(stack.getTag().getString("spreadType") != null) {
                String stype = stack.getTag().getString("spreadType");
                if(SpreadRegistry.INSTANCE.fromString(stype) != null) {
                    return SpreadRegistry.INSTANCE.fromString(stype).getTranslationKey(stype, stack);
                }
            }
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if(stack.getTag() != null) {
            if(stack.getTag().getString("spreadType") != null) {
                String stype = stack.getTag().getString("spreadType");
                if(SpreadRegistry.INSTANCE.fromString(stype) != null) {
                    return SpreadRegistry.INSTANCE.fromString(stype).hasGlint(stack);
                }
            }
        }
        return super.hasGlint(stack);
    }
}
