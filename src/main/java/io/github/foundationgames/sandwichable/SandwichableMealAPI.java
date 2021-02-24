package io.github.foundationgames.sandwichable;

import io.github.foundationgames.mealapi.api.MealAPIInitializer;
import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.item.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Sandwich;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class SandwichableMealAPI implements MealAPIInitializer {
    @Override
    public void onInitialize() {
        MealItemRegistry.register(ItemsRegistry.SANDWICH, SandwichableMealAPI::calculateSandwichFullness);
    }

    private static int calculateSandwichFullness(PlayerEntity player, ItemStack stack) {
        if(stack.getItem() instanceof SandwichBlockItem) {
            Sandwich.DisplayValues vals = ((SandwichBlockItem)BlocksRegistry.SANDWICH.asItem()).getDisplayValues(stack);
            int mh = 20 - player.getHungerManager().getFoodLevel();
            float ms = 20.0f - player.getHungerManager().getSaturationLevel();
            float h = vals.getHunger();
            float s = h * vals.getSaturation() * 2;
            return (int)(((h + s) - (mh + ms)) * 1.25);
        } else {
            System.out.println(String.format("ID: {}, ITEMINST: {}", Registry.ITEM.getId(stack.getItem()), stack.getItem()));
        }
        return 0;
    }
}
