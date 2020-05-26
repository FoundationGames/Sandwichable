package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.events.StructurePoolAddCallback;
import io.github.foundationgames.sandwichable.worldgen.ModifiableStructurePool;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructurePoolRegistry.class)
public class StructurePoolRegistryMixin {
    //Huge thanks to Draylar for help with this; https://github.com/Draylar/structurized
    @Inject(method = "add", at = @At("HEAD"))
    private void inject(StructurePool pool, CallbackInfo info) {
        StructurePoolAddCallback.EVENT.invoker().add(new ModifiableStructurePool(pool));
    }
}
