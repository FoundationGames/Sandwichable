package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
    /*
     * FIXME: Might silently cancel other injections
     */
    @Inject(method = "method_34740()Lcom/google/common/collect/BiMap;", at = @At("TAIL"), cancellable = true)
    private static void sandwichable$addOxidizables(CallbackInfoReturnable<BiMap<Block, Block>> cir) {
        var original = cir.getReturnValue();
        var map = ImmutableBiMap.<Block, Block>builder().putAll(original);
        BlocksRegistry.initOxidizables(map);
        cir.setReturnValue(map.build());
    }
}
