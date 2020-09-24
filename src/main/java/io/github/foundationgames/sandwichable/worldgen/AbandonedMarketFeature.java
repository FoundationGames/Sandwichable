package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class AbandonedMarketFeature extends JigsawFeature {
    public AbandonedMarketFeature(Codec<StructurePoolFeatureConfig> codec) { super(codec, 0, true, true); }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, StructurePoolFeatureConfig featureConfig) {
        return super.shouldStartAt(chunkGenerator, biomeSource, worldSeed, chunkRandom, chunkX, chunkZ, biome, chunkPos, featureConfig);
    }
}
