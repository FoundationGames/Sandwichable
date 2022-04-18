package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructurePools.class)
public class StructurePoolsMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void sandwichable$inject(StructurePool pool, CallbackInfoReturnable<RegistryEntry<StructurePool>> cir) {
        Util.tryAddElementToPool(new Identifier("village/plains/houses"), pool, "sandwichable:village/plains/houses/plains_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool(new Identifier("village/desert/houses"), pool, "sandwichable:village/desert/houses/desert_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool(new Identifier("village/savanna/houses"), pool, "sandwichable:village/savanna/houses/savanna_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool(new Identifier("village/taiga/houses"), pool, "sandwichable:village/taiga/houses/taiga_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool(new Identifier("village/snowy/houses"), pool, "sandwichable:village/snowy/houses/snowy_sandwich_stand", StructurePool.Projection.RIGID, 2);
    }
}
