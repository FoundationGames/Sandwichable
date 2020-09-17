package io.github.foundationgames.sandwichable.items;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
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
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)user;
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (stack.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
        else {
            if (user instanceof PlayerEntity && !((PlayerEntity)user).abilities.creativeMode) {
                ItemStack stk = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity player = (PlayerEntity)user;
                if (!player.inventory.insertStack(stk)) player.dropItem(stk, false);
            }
            return stack;
        }
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
