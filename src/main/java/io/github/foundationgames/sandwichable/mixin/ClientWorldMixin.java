package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    private static BlockPos.Mutable cachedMutable = null;

    @Inject(method = "calculateColor", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void setWaterColorUnblended(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        ClientWorld self = (ClientWorld)(Object)this;
        if(self.getBlockState(pos.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS) && colorResolver == BiomeColors.WATER_COLOR) cir.setReturnValue(Util.getSaltyWaterColor());
    }

    @Inject(method = "calculateColor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/biome/Biome;DD)I", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void cacheMutable(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir, int i, int j, int k, int l, int m, CuboidBlockIterator it, BlockPos.Mutable mutable) {
        cachedMutable = mutable;
    }

    @ModifyVariable(method = "calculateColor", ordinal = 5, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/biome/Biome;DD)I", ordinal = 1, shift = At.Shift.AFTER))
    public int setWaterColorBlended(int old, BlockPos pos, ColorResolver colorResolver) {
        ClientWorld self = (ClientWorld)(Object)this;
        if(self.getBlockState(cachedMutable.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS) && colorResolver == BiomeColors.WATER_COLOR) return Util.getSaltyWaterColor();
        return old;
    }
}
