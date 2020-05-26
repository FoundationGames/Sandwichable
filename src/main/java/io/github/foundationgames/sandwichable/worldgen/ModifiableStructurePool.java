package io.github.foundationgames.sandwichable.worldgen;

import io.github.foundationgames.sandwichable.mixin.AccessorStructurePool;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;

public class ModifiableStructurePool {
    //Huge thanks to Draylar for help with this; https://github.com/Draylar/structurized

    private final StructurePool pool;

    public ModifiableStructurePool(StructurePool pool) {
        this.pool = pool;
    }

    public void addStructurePoolElement(StructurePoolElement element) {
        ((AccessorStructurePool)pool).getElements().add(element);
    }

    public void addStructurePoolElement(StructurePoolElement element, int weight) {
        for (int i = 0; i < weight; i++) {
            ((AccessorStructurePool)pool).getElements().add(element);
        }
    }

    public StructurePool getStructurePool() {
        return pool;
    }
}
