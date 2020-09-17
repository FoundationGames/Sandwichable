package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(VanillaLayeredBiomeSource.class)
public interface BiomeSourceAccess {
    @Accessor("BIOMES")
    static List<RegistryKey<Biome>> getBiomes() {
        throw new AssertionError();
    }
    @Accessor("BIOMES")
    static void setBiomes(List<RegistryKey<Biome>> biomes) {
        throw new AssertionError();
    }
}
