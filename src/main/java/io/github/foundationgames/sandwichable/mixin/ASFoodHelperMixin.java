package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SandwichBlockItem;
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
            List<ItemStack> sandwichList = ((SandwichBlockItem)stack.getItem()).getFoodList(stack);
            int h = 0;
            float s = 0;
            for(ItemStack f : sandwichList) {
                if(f.isFood()) {
                    h += f.getItem().getFoodComponent().getHunger();
                    s += f.getItem().getFoodComponent().getSaturationModifier();
                }
            }
            cir.setReturnValue(new FoodHelper.BasicFoodValues(h, s));
        }
    }
}
