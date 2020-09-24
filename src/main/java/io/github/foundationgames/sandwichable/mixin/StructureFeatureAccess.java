package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;
import java.util.Locale;
@Mixin(StructureFeature.class)
public interface StructureFeatureAccess {
    @Invoker(value = "register")
    static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
        throw new AssertionError();
    }

    @Accessor(value = "JIGSAW_STRUCTURES")
    static void setJigsawStructures(List<StructureFeature<?>> list) {
        throw new AssertionError();
    }
}
