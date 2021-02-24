package io.github.foundationgames.sandwichable.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BottledFoodItem extends InfoTooltipItem {
    private boolean useHoneySound;

    public BottledFoodItem(boolean useHoneySound, Settings settings) {
        super(settings);
        this.useHoneySound = useHoneySound;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if(user instanceof PlayerEntity) {
            return !((PlayerEntity)user).isCreative() ? new ItemStack(Items.GLASS_BOTTLE, 1) : super.finishUsing(stack, world, user);
        }
        return new ItemStack(Items.GLASS_BOTTLE, 1);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return useHoneySound ? SoundEvents.ITEM_HONEY_BOTTLE_DRINK : SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public SoundEvent getEatSound() {
        return useHoneySound ? SoundEvents.ITEM_HONEY_BOTTLE_DRINK : SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
