package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.items.DynamicFood;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Shadow public abstract void add(int food, float saturationModifier);

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private void sandwichable$modifyFoodValues(Item item, ItemStack stack, CallbackInfo ci) {
        if (item instanceof DynamicFood food) {
            this.add(food.getRestoredFood(stack), food.getRestoredSaturation(stack));
        }
    }
}
