package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;

public class SandwichableWorldgen {
    public static final Identifier SALTY_SAND = Util.id("salty_sand");
    public static final Identifier SHRUBS = Util.id("shrubs");

    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registry.FEATURE, SHRUBS, new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<OreFeatureConfig> SALTY_SAND_FEATURE = Registry.register(Registry.FEATURE, SALTY_SAND, new OreFeature(OreFeatureConfig.CODEC));
    public static final Feature<SaltPoolFeatureConfig> SALT_POOL_FEATURE = Registry.register(Registry.FEATURE, Util.id("salt_pool"), new SaltPoolFeature());

    public static PlacedFeature SHRUBS_PLACED = BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, SHRUBS,
            BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, SHRUBS, SHRUBS_FEATURE.configure(new DefaultFeatureConfig())).withPlacement()
    );
    public static PlacedFeature SALTY_SAND_PLACED;
    public static PlacedFeature SALT_POOL_WATER;
    public static PlacedFeature SALT_POOL_DRY;

    private static final List<Biome.Category> SALT_POOL_BLACKLIST = Lists.newArrayList(Biome.Category.BEACH, Biome.Category.RIVER, Biome.Category.OCEAN, Biome.Category.NETHER, Biome.Category.THEEND);

    public static void init() {
        SandwichableConfig cfg = Util.getConfig();
        BiomeModifications.addFeature(ctx -> {
            Biome biome = ctx.getBiome();
            return biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.BEACH;
        }, GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, SALTY_SAND));
        BiomeModifications.addFeature(ctx -> {
            Biome biome = ctx.getBiome();
            return biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND;
        }, GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.PLACED_FEATURE_KEY, SHRUBS));
        if (cfg.saltPoolGenOptions.saltPools) {
            BiomeModifications.addFeature(ctx -> {
                Biome biome = ctx.getBiome();
                return biome.getCategory() != Biome.Category.RIVER && biome.getCategory() != Biome.Category.OCEAN &&
                        biome.getPrecipitation() == Biome.Precipitation.RAIN && !biome.hasHighHumidity() && !SALT_POOL_BLACKLIST.contains(biome.getCategory());
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, Util.id("salt_pool_water")));
        }
        if (cfg.saltPoolGenOptions.drySaltPools) {
            BiomeModifications.addFeature(ctx -> {
                Biome biome = ctx.getBiome();
                return biome.getCategory() != Biome.Category.RIVER && biome.getCategory() != Biome.Category.OCEAN &&
                        biome.getPrecipitation() == Biome.Precipitation.NONE && biome.getTemperature() > 1.5f && !SALT_POOL_BLACKLIST.contains(biome.getCategory());
            }, GenerationStep.Feature.LAKES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, Util.id("salty_pool_dry")));
        }
    }
    
    static {
        SandwichableConfig config = Util.getConfig();
        
        SALTY_SAND_PLACED = BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, SALTY_SAND,
            BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, SALTY_SAND, SALTY_SAND_FEATURE.configure(
                    new OreFeatureConfig(new BlockMatchRuleTest(Blocks.SAND), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize)
            )).withPlacement(CountPlacementModifier.of(config.saltySandGenOptions.rarity), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(config.saltySandGenOptions.maxGenHeight)))
        );

        SALT_POOL_WATER = BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, Util.id("salt_pool_water"),
                BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_water"), SALT_POOL_FEATURE.configure(new SaltPoolFeatureConfig(true)))
                        .withPlacement(RarityFilterPlacementModifier.of(426))
        );
        SALT_POOL_DRY = BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, Util.id("salty_pool_dry"),
                BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salty_pool_dry"), SALT_POOL_FEATURE.configure(new SaltPoolFeatureConfig(false)))
                        .withPlacement(RarityFilterPlacementModifier.of(442))
        );
    }
}
