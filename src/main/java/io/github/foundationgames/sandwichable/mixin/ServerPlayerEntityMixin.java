package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onSlotUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/advancement/criterion/InventoryChangedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void sandwichable$triggerSandwichCollection(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() == ItemsRegistry.SANDWICH) {
            Sandwichable.COLLECT_SANDWICH.trigger((ServerPlayerEntity)(Object)this, stack);
        }
    }
}
