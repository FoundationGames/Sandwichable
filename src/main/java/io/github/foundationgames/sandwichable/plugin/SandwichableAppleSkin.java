package io.github.foundationgames.sandwichable.plugin;

import io.github.foundationgames.sandwichable.items.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Sandwich;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

public class SandwichableAppleSkin implements AppleSkinApi {
    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(event -> {
            if(event.itemStack.getItem() instanceof SandwichBlockItem) {
                Sandwich.DisplayValues vals = ((SandwichBlockItem)event.itemStack.getItem()).getDisplayValues(event.itemStack);
                event.modifiedFoodValues = new FoodValues(vals.getHunger(), vals.getSaturation());
            }
        });
    }
}
