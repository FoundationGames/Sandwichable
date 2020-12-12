package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Sandwich;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.FoodHelper;

import java.util.List;

@Mixin(value = FoodHelper.class, remap = false)
public class ASFoodHelperMixin {
    @Inject(method = "getDefaultFoodValues", at = @At("HEAD"), cancellable = true)
    private static void sandwichCompat(ItemStack stack, CallbackInfoReturnable<FoodHelper.BasicFoodValues> cir) {
        if(stack.getItem() instanceof SandwichBlockItem) {
            Sandwich.DisplayValues vals = ((SandwichBlockItem)stack.getItem()).getDisplayValues(stack);
            cir.setReturnValue(new FoodHelper.BasicFoodValues(vals.getHunger(), vals.getSaturation()));
        }
    }
}
