package io.github.foundationgames.sandwichable.plugin;

import io.github.foundationgames.sandwichable.items.DynamicFood;
import io.github.foundationgames.sandwichable.items.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Sandwich;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

public class SandwichableAppleSkin implements AppleSkinApi {
    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(event -> {
            var stack = event.itemStack;
            if (stack.getItem() instanceof SandwichBlockItem sandwich) {
                Sandwich.DisplayValues vals = sandwich.getDisplayValues(event.itemStack);
                event.modifiedFoodValues = new FoodValues(vals.getHunger(), vals.getSaturation());
            } else if (stack.getItem() instanceof DynamicFood food) {
                event.modifiedFoodValues = new FoodValues(food.getRestoredFood(stack), food.getRestoredSaturation(stack));
            }
        });
    }
}
