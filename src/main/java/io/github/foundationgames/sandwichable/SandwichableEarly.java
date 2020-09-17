package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
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
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class SandwichableEarly implements EarlyInitializer {

    public static final Feature<DefaultFeatureConfig> SHRUBS_FEATURE = Registry.register(Registry.FEATURE, Util.id("shrubs"), new ShrubsFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<ExtraOreFeatureConfig> SALTY_SAND_FEATURE = Registry.register(Registry.FEATURE, Util.id("salty_sand"), new ExtraOreFeature(ExtraOreFeatureConfig.CODEC));
    public static final Feature<RandomTwoTallPatchFeatureConfig> RANDOM_TWO_TALL_PATCH = Registry.register(Registry.FEATURE, Util.id("random_two_tall_patch"), new RandomTwoTallPatchFeature(RandomTwoTallPatchFeatureConfig.CODEC));

    @Override
    public void onEarlyInitialization() {
        AutoConfig.register(SandwichableConfig.class, GsonConfigSerializer::new);

        SandwichableConfig config = AutoConfig.getConfigHolder(SandwichableConfig.class).getConfig();

        DynamicRegistryCallback.callback(Registry.BIOME_KEY).register((manager, key, biome) -> {
            if(biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.BEACH) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.UNDERGROUND_ORES, () -> SALTY_SAND_FEATURE.configure(
                        new ExtraOreFeatureConfig(Blocks.SAND.getDefaultState(), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize)
                ).method_30377(config.saltySandGenOptions.maxGenHeight).spreadHorizontally().repeat(config.saltySandGenOptions.rarity));
            }
            if(biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
                BiomesRegistry.registerFeature(manager, biome, GenerationStep.Feature.UNDERGROUND_ORES, () -> SHRUBS_FEATURE.configure(new DefaultFeatureConfig()));
            }
        });
    }
}
