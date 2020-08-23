package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.events.StructurePoolAddCallback;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.worldgen.ModifiableStructurePool;
import io.github.foundationgames.sandwichable.worldgen.StructurePoolHelper;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.BuiltinRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StructurePools.class)
public class StructurePoolsMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void inject(StructurePool spool, CallbackInfoReturnable<StructurePool> cir) {
        /*ModifiableStructurePool mpool = new ModifiableStructurePool(pool);
        for(Pair<Identifier, Pair<StructurePoolElement, Integer>> entry : StructurePoolHelper.ENTRIES) {
            Identifier targetPool = entry.getLeft();
            StructurePoolElement element = entry.getRight().getLeft();
            int weight = entry.getRight().getRight();
            if(pool.getId().equals(targetPool)) {
                System.out.println("Added "+element.toString()+" to "+pool.getId().toString());
                mpool.addStructurePoolElement(element, weight);
            }
        }
        StructurePool npool = mpool.getStructurePool();
        cir.setReturnValue(BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_POOL, npool.getId(), npool));*/

        StructurePool pool = spool;

        pool = Util.tryAddElementToPool(new Identifier("village/plains/houses"), pool, "sandwichable:village/plains/houses/plains_sandwich_stand", StructurePool.Projection.RIGID, 2);
        pool = Util.tryAddElementToPool(new Identifier("village/desert/houses"), pool, "sandwichable:village/desert/houses/desert_sandwich_stand", StructurePool.Projection.RIGID, 2);
        pool = Util.tryAddElementToPool(new Identifier("village/savanna/houses"), pool, "sandwichable:village/savanna/houses/savanna_sandwich_stand", StructurePool.Projection.RIGID, 2);
        pool = Util.tryAddElementToPool(new Identifier("village/taiga/houses"), pool, "sandwichable:village/taiga/houses/taiga_sandwich_stand", StructurePool.Projection.RIGID, 2);
        pool = Util.tryAddElementToPool(new Identifier("village/snowy/houses"), pool, "sandwichable:village/snowy/houses/snowy_sandwich_stand", StructurePool.Projection.RIGID, 2);

        cir.setReturnValue(BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_POOL, pool.getId(), pool));
        cir.cancel();
    }
}
