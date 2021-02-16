package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.ExtraDispenserBehaviorRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(method = "dispense", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void interrupt(ServerWorld serverWorld, BlockPos pos, CallbackInfo ci, BlockPointerImpl pointer, DispenserBlockEntity blockEntity, int slot, ItemStack stack) {
        List<DispenserBehavior> behaviors = ExtraDispenserBehaviorRegistry.ENTRIES.get(stack.getItem());
        if(behaviors == null) return;
        for(DispenserBehavior behavior : behaviors) {
            ItemStack d = behavior.dispense(pointer, stack);
            if(d != null) {
                blockEntity.setStack(slot, d);
                ci.cancel();
            }
        }
    }
}
