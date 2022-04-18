package io.github.foundationgames.sandwichable.worldgen;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;

import java.util.List;

public class SandwichableWorldgen {
    public static final Identifier SALTY_SAND = Util.id("salty_sand");
    public static final Identifier SHRUBS = Util.id("shrubs");

    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registry.FEATURE, SHRUBS, new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<OreFeatureConfig> SALTY_SAND_FEATURE = Registry.register(Registry.FEATURE, SALTY_SAND, new OreFeature(OreFeatureConfig.CODEC));
    public static final Feature<CascadeFeatureConfig> CASCADE_FEATURE = Registry.register(Registry.FEATURE, Util.id("cascade"), new CascadeFeature());

    public static final PlacedFeature SHRUBS_PLACED = new PlacedFeature(
            BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, SHRUBS, new ConfiguredFeature<>(SHRUBS_FEATURE, new DefaultFeatureConfig())), List.of()
    );
    public static final PlacedFeature SALTY_SAND_PLACED;
    public static PlacedFeature SALT_POOL_WATER = new PlacedFeature(
            BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_water"), new ConfiguredFeature<>(CASCADE_FEATURE, CascadeFeatureConfig.water())),
            List.of(RarityFilterPlacementModifier.of(426))
    );
    public static final PlacedFeature SALT_POOL_DRY = new PlacedFeature(
            BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_dry"), new ConfiguredFeature<>(CASCADE_FEATURE, CascadeFeatureConfig.dry())),
            List.of(RarityFilterPlacementModifier.of(442))
    );

    public static void init() {
        SandwichableConfig cfg = Util.getConfig();

        BiomeModifications.addFeature(ctx -> {
            var entry = ctx.getBiomeRegistryEntry();
            return entry.isIn(Sandwichable.SALT_WATER_BODIES);
        }, GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, SALTY_SAND));

        BiomeModifications.addFeature(ctx -> {
            var entry = ctx.getBiomeRegistryEntry();
            return !entry.isIn(Sandwichable.NO_SHRUBS);
        }, GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.PLACED_FEATURE_KEY, SHRUBS));

        if (cfg.saltPoolGenOptions.saltPools) {
            BiomeModifications.addFeature(ctx -> {
                var entry = ctx.getBiomeRegistryEntry();
                var biome = ctx.getBiome();
                return (!entry.isIn(Sandwichable.NO_SALT_POOLS)) && biome.getPrecipitation() == Biome.Precipitation.RAIN && !biome.hasHighHumidity();
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, Util.id("salt_pool_water")));
        }
        if (cfg.saltPoolGenOptions.drySaltPools) {
            BiomeModifications.addFeature(ctx -> {
                var entry = ctx.getBiomeRegistryEntry();
                var biome = ctx.getBiome();
                return (!entry.isIn(Sandwichable.NO_SALT_POOLS)) && biome.getPrecipitation() == Biome.Precipitation.NONE && biome.getTemperature() > 1.5f;
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, Util.id("salt_pool_dry")));
        }
    }
    
    static {
        BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, SHRUBS, SHRUBS_PLACED);

        SandwichableConfig config = Util.getConfig();
        
        SALTY_SAND_PLACED = new PlacedFeature(
                BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, SALTY_SAND,
                        new ConfiguredFeature<>(SALTY_SAND_FEATURE, new OreFeatureConfig(new BlockMatchRuleTest(Blocks.SAND), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize))
                ),
                List.of(CountPlacementModifier.of(config.saltySandGenOptions.rarity), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(config.saltySandGenOptions.maxGenHeight)))
        );
        BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, SALTY_SAND, SALTY_SAND_PLACED);

        BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, Util.id("salt_pool_water"), SALT_POOL_WATER);
        BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, Util.id("salt_pool_dry"), SALT_POOL_DRY);
    }
}
