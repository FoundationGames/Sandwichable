package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.foundationgames.sandwichable.mixin.BiomeLayerAccess;
import io.github.foundationgames.sandwichable.mixin.BiomeSourceAccess;
import io.github.foundationgames.sandwichable.mixin.BuiltinBiomesAccess;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BiomesRegistry {

    private static final Supplier<Biome> VEGETABLE_FOREST_CREATOR = () -> {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        GenerationSettings.Builder genBuilder = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
        DefaultBiomeFeatures.addFarmAnimals(spawnBuilder);
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 20, 4, 6));
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(genBuilder);
        genBuilder.structureFeature(ConfiguredFeaturesRegistry.ABANDONED_MARKET);
        DefaultBiomeFeatures.addLandCarvers(genBuilder);
        DefaultBiomeFeatures.addDungeons(genBuilder);
        DefaultBiomeFeatures.addMossyRocks(genBuilder);
        DefaultBiomeFeatures.addMineables(genBuilder);
        DefaultBiomeFeatures.addDefaultOres(genBuilder);
        DefaultBiomeFeatures.addDefaultDisks(genBuilder);
        DefaultBiomeFeatures.addDefaultFlowers(genBuilder);
        genBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_TREES);
        DefaultBiomeFeatures.addDefaultGrass(genBuilder);
        DefaultBiomeFeatures.addDefaultMushrooms(genBuilder);
        DefaultBiomeFeatures.addDefaultVegetation(genBuilder);
        DefaultBiomeFeatures.addSprings(genBuilder);
        genBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.SPARSE_DARK_OAK);
        genBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.LEAF_PILE);
        genBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeaturesRegistry.VEGETABLE_PATCH);
        genBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.COMMON_PUMPKIN_PATCH);
        genBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_MELON);
        DefaultBiomeFeatures.addFrozenTopLayer(genBuilder);
        return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(0.1f).scale(0.12f).temperature(0.75f).downfall(0.8F).effects(new BiomeEffects.Builder().grassColor(0x89bd40).foliageColor(0xc79d12).waterColor(0x409ad6).waterFogColor(0x0fabff).fogColor(12638463).skyColor(0x7eb9ed).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(spawnBuilder.build()).generationSettings(genBuilder.build()).build();
    };

    public static final Biome VEGETABLE_FOREST = VEGETABLE_FOREST_CREATOR.get();
    public static final RegistryKey<Biome> VEGETABLE_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, Util.id("vegetable_forest"));

    public static void init() {
        registerTemperateBiome(VEGETABLE_FOREST_KEY, VEGETABLE_FOREST);
    }

    public static void registerTemperateBiome(RegistryKey<Biome> key, Biome biome) {
        Registry.register(BuiltinRegistries.BIOME, key.getValue(), biome);
        BuiltinBiomesAccess.getIdMap().put(BuiltinRegistries.BIOME.getRawId(biome), key);
        List<RegistryKey<Biome>> biomes = new ArrayList<>(BiomeSourceAccess.getBiomes());
        biomes.add(key);
        BiomeSourceAccess.setBiomes(biomes);
        BiomeLayerAccess.setTemperateBiomes(ArrayUtils.add(BiomeLayerAccess.getTemperateBiomes(), BuiltinRegistries.BIOME.getRawId(biome)));
    }
}
