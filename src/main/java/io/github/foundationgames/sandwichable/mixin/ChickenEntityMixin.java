package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(ChickenEntity.class)
public class ChickenEntityMixin {
    @Mutable @Shadow @Final private static Ingredient BREEDING_INGREDIENT;

    static {
        var items = new ArrayList<ItemConvertible>();
        for (var stack : BREEDING_INGREDIENT.getMatchingStacks()) {
            items.add(stack.getItem());
        }
        items.add(ItemsRegistry.TOMATO_SEEDS);
        items.add(ItemsRegistry.LETTUCE_SEEDS);
        items.add(ItemsRegistry.CUCUMBER_SEEDS);
        items.add(ItemsRegistry.ONION_SEEDS);
        items.add(ItemsRegistry.ANCIENT_GRAIN_SEEDS);

        var itemArray = new ItemConvertible[items.size()];
        items.toArray(itemArray);
        BREEDING_INGREDIENT = Ingredient.ofItems(itemArray);
    }
}
