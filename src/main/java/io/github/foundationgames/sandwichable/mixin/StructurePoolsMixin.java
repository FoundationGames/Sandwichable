package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.registry.Registerable;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructurePools.class)
public class StructurePoolsMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void sandwichable$inject(Registerable<StructurePool> structurePoolsRegisterable, String id, StructurePool pool, CallbackInfo ci) {
        Util.tryAddElementToPool("village/plains/houses", id, pool, "sandwichable:village/plains/houses/plains_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool("village/desert/houses", id, pool, "sandwichable:village/desert/houses/desert_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool("village/savanna/houses", id, pool, "sandwichable:village/savanna/houses/savanna_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool("village/taiga/houses", id, pool, "sandwichable:village/taiga/houses/taiga_sandwich_stand", StructurePool.Projection.RIGID, 2);
        Util.tryAddElementToPool("village/snowy/houses", id, pool, "sandwichable:village/snowy/houses/snowy_sandwich_stand", StructurePool.Projection.RIGID, 2);
    }
}
