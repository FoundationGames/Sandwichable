package io.github.foundationgames.sandwichable.worldgen;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class SandwichableWorldgen {
    public static final Identifier SALTY_SAND = Util.id("salty_sand");
    public static final Identifier SHRUBS = Util.id("shrubs");

    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registries.FEATURE, SHRUBS, new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<CascadeFeatureConfig> CASCADE_FEATURE = Registry.register(Registries.FEATURE, Util.id("cascade"), new CascadeFeature());

    public static void init() {
        SandwichableConfig cfg = Util.getConfig();

        if (cfg.saltySandGenOptions.saltySand) {
            BiomeModifications.addFeature(ctx -> {
                var entry = ctx.getBiomeRegistryEntry();
                return entry.isIn(Sandwichable.SALT_WATER_BODIES);
            }, GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, SALTY_SAND));
        }

        BiomeModifications.addFeature(ctx -> {
            var entry = ctx.getBiomeRegistryEntry();
            return !entry.isIn(Sandwichable.NO_SHRUBS);
        }, GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(RegistryKeys.PLACED_FEATURE, SHRUBS));

        if (cfg.saltPoolGenOptions.saltPools) {
            BiomeModifications.addFeature(ctx -> {
                var entry = ctx.getBiomeRegistryEntry();
                var biome = ctx.getBiome();
                return (!entry.isIn(Sandwichable.NO_SALT_POOLS)) && biome.hasPrecipitation() && biome.getTemperature() > 0.15f && biome.weather.downfall() < 0.8;
            }, GenerationStep.Feature.LAKES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Util.id("salt_pool_water")));
        }
        if (cfg.saltPoolGenOptions.drySaltPools) {
            BiomeModifications.addFeature(ctx -> {
                var entry = ctx.getBiomeRegistryEntry();
                var biome = ctx.getBiome();
                return (!entry.isIn(Sandwichable.NO_SALT_POOLS)) && !biome.hasPrecipitation() && biome.getTemperature() > 1.5f;
            }, GenerationStep.Feature.LAKES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Util.id("salt_pool_dry")));
        }
    }
}
