package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow public HitResult crosshairTarget;
    @Shadow public ClientPlayerEntity player;
    @Shadow public ClientPlayerInteractionManager interactionManager;

    /*@ModifyVariable(method = "doItemPick", at = @At(value = "NEW", target = "Lnet/minecraft/item/ItemStack", shift = At.Shift.BEFORE, ordinal = 3), print = true)*/
    @Inject(method = "doItemPick", at = @At(value = "HEAD"), cancellable = true)
    private void pickEntitySandwichTableCart(CallbackInfo ci) {
        if(this.crosshairTarget != null) {
            HitResult.Type type = this.crosshairTarget.getType();
            if(type == HitResult.Type.ENTITY) {
                if(((EntityHitResult)this.crosshairTarget).getEntity() instanceof SandwichTableMinecartEntity) {
                    ItemStack stack = new ItemStack(ItemsRegistry.SANDWICH_TABLE_MINECART);
                    int i = this.player.getInventory().getSlotWithStack(stack);
                    if (this.player.isCreative()) {
                        this.player.getInventory().addPickBlock(stack);
                        this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + this.player.getInventory().selectedSlot);
                    } else if (i != -1) {
                        if (PlayerInventory.isValidHotbarIndex(i)) {
                            this.player.getInventory().selectedSlot = i;
                        } else {
                            this.interactionManager.pickFromInventory(i);
                        }
                    }
                    ci.cancel();
                }
            }
        }
    }
}
