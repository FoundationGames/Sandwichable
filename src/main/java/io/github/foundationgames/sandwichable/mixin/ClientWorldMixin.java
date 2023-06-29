package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    private final BlockPos.Mutable sandwichable$mutable = new BlockPos.Mutable();
    private int sandwichable$lastY = 0;

    @Inject(method = "calculateColor", at = @At("HEAD"))
    private void sandwichable$cacheYValue(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        sandwichable$lastY = pos.getY();
    }

    @ModifyVariable(method = "calculateColor", index = 2, at = @At("HEAD"))
    private ColorResolver sandwichable$replaceColorResolver(ColorResolver old) {
        if (old == BiomeColors.WATER_COLOR) {
            ClientWorld self = (ClientWorld)(Object)this;

            return (biome, x, z) -> {
                sandwichable$mutable.set(x, sandwichable$lastY, z);
                if (self.getBlockState(sandwichable$mutable.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS)) {
                    return Util.getSaltyWaterColor();
                }

                return old.getColor(biome, x, z);
            };
        }

        return old;
    }

    /*
    private static BlockPos.Mutable cachedMutable = null;

    @Inject(method = "calculateColor", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void sandwichable$setWaterColorUnblended(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        ClientWorld self = (ClientWorld)(Object)this;
        if (self.getBlockState(pos.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS) && colorResolver == BiomeColors.WATER_COLOR) cir.setReturnValue(Util.getSaltyWaterColor());
    }

    @Inject(method = "calculateColor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/biome/Biome;DD)I", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void sandwichable$cacheMutable(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir, int i, int j, int k, int l, int m, CuboidBlockIterator it, int n, BlockPos.Mutable mutable) {
        cachedMutable = mutable;
    }

    @ModifyVariable(method = "calculateColor", ordinal = 5, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/biome/Biome;DD)I", ordinal = 1, shift = At.Shift.AFTER))
    public int sandwichable$setWaterColorBlended(int old, BlockPos pos, ColorResolver colorResolver) {
        ClientWorld self = (ClientWorld)(Object)this;
        if(self.getBlockState(cachedMutable.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS) && colorResolver == BiomeColors.WATER_COLOR) return Util.getSaltyWaterColor();
        return old;
    }

     */
}
