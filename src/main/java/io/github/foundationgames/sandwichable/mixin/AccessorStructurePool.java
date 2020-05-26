package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructurePool.class)
public interface AccessorStructurePool {
    //Huge thanks to Draylar for help with this; https://github.com/Draylar/structurized
    @Accessor
    List<StructurePoolElement> getElements();
}
