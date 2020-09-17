package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetBaseBiomesLayer.class)
public interface BiomeLayerAccess {
    @Accessor("TEMPERATE_BIOMES")
    static int[] getTemperateBiomes() {
        throw new AssertionError();
    }

    @Accessor("TEMPERATE_BIOMES")
    static void setTemperateBiomes(int[] biomes) {
        throw new AssertionError();
    }
}
