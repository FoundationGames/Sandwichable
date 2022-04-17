package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;

public class SandwichableWorldgen {
    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registry.FEATURE, Util.id("shrubs"), new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<ExtraOreFeatureConfig> SALTY_SAND_FEATURE = Registry.register(Registry.FEATURE, Util.id("salty_sand"), new ExtraOreFeature(ExtraOreFeatureConfig.CODEC));
    public static final Feature<CascadeFeatureConfig> CASCADE_FEATURE = Registry.register(Registry.FEATURE, Util.id("cascade"), new CascadeFeature());

    public static ConfiguredFeature<DefaultFeatureConfig, ?> SHRUBS_CONFIGURED = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("shrubs"), SHRUBS_FEATURE.configure(new DefaultFeatureConfig()));
    public static ConfiguredFeature<?, ?> SALTY_SAND_CONFIGURED;
    public static ConfiguredFeature<?, ?> SALT_POOL_WATER;
    public static ConfiguredFeature<?, ?> SALT_POOL_DRY;

    private static final List<Biome.Category> SALT_POOL_BLACKLIST = Lists.newArrayList(Biome.Category.NETHER, Biome.Category.THEEND);

    public static void init() {
        SandwichableConfig cfg = Util.getConfig();
        BiomeModifications.addFeature(ctx -> {
            Biome biome = ctx.getBiome();
            return biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.BEACH;
        }, GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Util.id("salty_sand")));
        BiomeModifications.addFeature(ctx -> {
            Biome biome = ctx.getBiome();
            return biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND;
        }, GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Util.id("shrubs")));
        if (cfg.saltPoolGenOptions.saltPools) {
            BiomeModifications.addFeature(ctx -> {
                Biome biome = ctx.getBiome();
                return biome.getPrecipitation() == Biome.Precipitation.RAIN && !biome.hasHighHumidity() && biome.getScale() < 0.25 && !SALT_POOL_BLACKLIST.contains(biome.getCategory());
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Util.id("salt_pool_water")));
        }
        if (cfg.saltPoolGenOptions.drySaltPools) {
            BiomeModifications.addFeature(ctx -> {
                Biome biome = ctx.getBiome();
                return biome.getPrecipitation() == Biome.Precipitation.NONE && biome.getTemperature() > 1.5f && biome.getScale() < 0.25 && !SALT_POOL_BLACKLIST.contains(biome.getCategory());
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Util.id("salt_pool_dry")));
        }
    }
    
    static {
        SandwichableConfig config = Util.getConfig();
        
        SALTY_SAND_CONFIGURED = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salty_sand"), SALTY_SAND_FEATURE.configure(
                new ExtraOreFeatureConfig(Blocks.SAND.getDefaultState(), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize)
        ).rangeOf(config.saltySandGenOptions.maxGenHeight).spreadHorizontally().repeat(config.saltySandGenOptions.rarity));

        SALT_POOL_WATER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_water"), CASCADE_FEATURE.configure(CascadeFeatureConfig.water())
                .decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(426)))
                .decorate(Decorator.HEIGHTMAP_WORLD_SURFACE.configure(DecoratorConfig.DEFAULT)));
        SALT_POOL_DRY = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_dry"), CASCADE_FEATURE.configure(CascadeFeatureConfig.dry())
                .decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(442)))
                .decorate(Decorator.HEIGHTMAP_WORLD_SURFACE.configure(DecoratorConfig.DEFAULT)));
    }
}
