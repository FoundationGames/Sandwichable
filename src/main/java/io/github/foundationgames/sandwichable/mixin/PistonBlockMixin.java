package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addSyncedBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void tryEjectSandwich(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        Direction dir = state.get(PistonBlock.FACING);
        BlockPos tablePos = pos.offset(dir).down();
        if(world.getBlockState(tablePos).getBlock() == BlocksRegistry.SANDWICH_TABLE) {
            world.getBlockTickScheduler().schedule(tablePos, BlocksRegistry.SANDWICH_TABLE, 1);
        }
    }
}