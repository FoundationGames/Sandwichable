package io.github.foundationgames.sandwichable.items.spread;

import io.github.foundationgames.sandwichable.items.SpreadRegistry;
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
        if(user instanceof PlayerEntity && stack.getTag().getString("spreadType") != null) {
            SpreadType type = SpreadRegistry.INSTANCE.deserialize(stack.getTag().getString("spreadType"));
            ((PlayerEntity)user).getHungerManager().add(type.getHunger(), type.getSaturationModifier());
            for(StatusEffectInstance effect : type.getStatusEffects()) {
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
                return ("item.sandwichable.spread."+stack.getTag().getString("spreadType"));
            }
        }
        return super.getTranslationKey(stack);
    }
}
