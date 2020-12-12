package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addSyncedBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void tryEjectSandwich(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        Direction dir = state.get(PistonBlock.FACING);
        BlockPos tablePos = pos.offset(dir).down();
        if(world.getBlockState(tablePos).getBlock() == BlocksRegistry.SANDWICH_TABLE) {
            world.getBlockTickScheduler().schedule(tablePos, BlocksRegistry.SANDWICH_TABLE, 1);
        }
        List<SandwichTableMinecartEntity> list = world.getEntitiesByClass(SandwichTableMinecartEntity.class, new Box(tablePos), EntityPredicates.EXCEPT_SPECTATOR);
        if(list.size() > 0) {
            list.get(0).getSandwich().ejectSandwich(world, new Vec3d(tablePos.getX()+0.5, tablePos.getY(), tablePos.getZ()+0.5));
            list.get(0).sync();
        }
    }
}