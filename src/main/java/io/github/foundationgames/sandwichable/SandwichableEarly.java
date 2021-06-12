package io.github.foundationgames.sandwichable;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.worldgen.*;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.cloth.api.dynamic.registry.v1.BiomesRegistry;
import me.shedaniel.cloth.api.dynamic.registry.v1.DynamicRegistryCallback;
import me.shedaniel.cloth.api.dynamic.registry.v1.EarlyInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.List;

public class SandwichableEarly implements EarlyInitializer {

    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registry.FEATURE, Util.id("shrubs"), new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<ExtraOreFeatureConfig> SALTY_SAND_FEATURE = Registry.register(Registry.FEATURE, Util.id("salty_sand"), new ExtraOreFeature(ExtraOreFeatureConfig.CODEC));
    public static final Feature<SaltPoolFeatureConfig> SALT_POOL_FEATURE = Registry.register(Registry.FEATURE, Util.id("salt_pool"), new SaltPoolFeature());

    public static ConfiguredFeature<DefaultFeatureConfig, ?> SHRUBS_CONFIGURED = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("shrubs"), SHRUBS_FEATURE.configure(new DefaultFeatureConfig()));
    public static ConfiguredFeature<?, ?> SALTY_SAND_CONFIGURED;
    public static ConfiguredFeature<?, ?> SALT_POOL_WATER;
    public static ConfiguredFeature<?, ?> SALT_POOL_DRY;

    private static final List<Biome.Category> SALT_POOL_BLACKLIST = Lists.newArrayList(Biome.Category.BEACH, Biome.Category.RIVER, Biome.Category.OCEAN, Biome.Category.NETHER, Biome.Category.THEEND);

    @Override
    public void onEarlyInitialization() {


        SandwichableConfig config = Util.getConfig();
        var saltySandHeightRange = new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(config.saltySandGenOptions.maxGenHeight)));
        SALTY_SAND_CONFIGURED = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salty_sand"), SALTY_SAND_FEATURE.configure(
                new ExtraOreFeatureConfig(Blocks.SAND.getDefaultState(), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize)
        ).range(saltySandHeightRange).spreadHorizontally().repeat(config.saltySandGenOptions.rarity));

        SALT_POOL_WATER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salt_pool_water"), SALT_POOL_FEATURE.configure(new SaltPoolFeatureConfig(true)).decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(426))));
        SALT_POOL_DRY = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Util.id("salty_pool_dry"), SALT_POOL_FEATURE.configure(new SaltPoolFeatureConfig(false)).decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(442))));

        DynamicRegistryCallback.callback(Registry.BIOME_KEY).register((manager, key, biome) -> {
            if(biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.BEACH) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.UNDERGROUND_ORES, () -> SALTY_SAND_CONFIGURED);
            }
            if(biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.VEGETAL_DECORATION, () -> SHRUBS_CONFIGURED);
            }
            if(biome.getPrecipitation() == Biome.Precipitation.RAIN && !biome.hasHighHumidity() && biome.getScale() < 0.25 && !SALT_POOL_BLACKLIST.contains(biome.getCategory())) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.LAKES, () -> SALT_POOL_WATER);
            }
            else if(biome.getPrecipitation() == Biome.Precipitation.NONE && biome.getTemperature() > 1.5f && biome.getScale() < 0.25 && !SALT_POOL_BLACKLIST.contains(biome.getCategory())) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.LAKES, () -> SALT_POOL_DRY);
            }
        });
    }
}
