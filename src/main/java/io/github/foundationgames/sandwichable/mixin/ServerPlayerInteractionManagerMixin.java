package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.SneakInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @ModifyVariable(
            method = "interactBlock",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE),
            index = 9
    )
    private boolean sandwichable$modifyBlockInteractionCondition(boolean old, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hit) {
        BlockState state = world.getBlockState(hit.getBlockPos());
        if (state.getBlock() instanceof SneakInteractable) {
            return !player.getMainHandStack().isEmpty() && player.shouldCancelInteraction();
        }
        return old;
    }
}
