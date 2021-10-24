package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.util.CheeseRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class FermentingMilkBucketItem extends InfoTooltipItem {

    public FermentingMilkBucketItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder().hunger(3).saturationModifier(0.9F).alwaysEdible().build()).recipeRemainder(Items.BUCKET));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(stack.getNbt() != null) {
            if(stack.getNbt().contains("bucketData")) {
                NbtCompound tag = stack.getNbt().getCompound("bucketData");
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, tag.getInt("percentFermented") * 4, 5));
            }
        }
        return user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode ? super.finishUsing(stack, world, user) : new ItemStack(Items.BUCKET);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(stack.getNbt() != null) {
            if(stack.getNbt().getCompound("bucketData") != null) {
                NbtCompound tag = stack.getNbt().getCompound("bucketData").copy();
                int pct;
                CheeseType type;
                pct = tag.getInt("percentFermented");
                type = CheeseRegistry.INSTANCE.basinContentFromString(tag.getString("basinContent")).getCheeseType();
                tooltip.add(new TranslatableText("fermenting_milk_bucket.tooltip.pct_fermented", pct).formatted(Formatting.BLUE));
                if (type != null) {
                    tooltip.add(new TranslatableText("cheese.type." + type.toString()).formatted(Formatting.BLUE));
                }
            }
        } else {
            tooltip.add(new TranslatableText("fermenting_milk_bucket.tooltip.no_properties").formatted(Formatting.BLUE));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
