package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.SneakInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @ModifyVariable(
            method = "interactBlock",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z", shift = At.Shift.AFTER)
    )
    private boolean sandwichable$modifyBlockInteractionCondition(boolean old, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit) {
        BlockState state = world.getBlockState(hit.getBlockPos());
        if (state.getBlock() instanceof SneakInteractable) {
            return !player.getMainHandStack().isEmpty() && player.shouldCancelInteraction();
        }
        return old;
    }
}
