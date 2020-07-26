package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SandwichableGroupIconBuilder;
import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipeSerializer;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipeSerializer;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.villager.SandwichMakerProfession;
import io.github.foundationgames.sandwichable.worldgen.ExtraOreFeature;
import io.github.foundationgames.sandwichable.worldgen.ExtraOreFeatureConfig;
import io.github.foundationgames.sandwichable.worldgen.ShrubsFeature;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class Sandwichable implements ModInitializer {

    public static final ItemGroup SANDWICHABLE_ITEMS = FabricItemGroupBuilder.build(Util.id("sandwichable"), SandwichableGroupIconBuilder::getIcon);

    public static final Tag<Item> BREADS = TagRegistry.item(Util.id("breads"));
    public static final Tag<Item> METAL_ITEMS = TagRegistry.item(Util.id("metal_items"));

    private static final Feature<DefaultFeatureConfig> SHRUBS = Registry.register(
            Registry.FEATURE,
            Util.id("shrubs"),
            new ShrubsFeature(DefaultFeatureConfig::deserialize)
    );

    @Override
    public void onInitialize() {
        AutoConfig.register(SandwichableConfig.class, GsonConfigSerializer::new);

        BlocksRegistry.init();
        ItemsRegistry.init();
        SandwichMakerProfession.init();
        SpreadType.init();

        Registry.register(Registry.RECIPE_SERIALIZER, CuttingRecipeSerializer.ID, CuttingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(CuttingRecipe.Type.ID), CuttingRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, ToastingRecipeSerializer.ID, ToastingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(ToastingRecipe.Type.ID), ToastingRecipe.Type.INSTANCE);

        SandwichableConfig config = AutoConfig.getConfigHolder(SandwichableConfig.class).getConfig();

        Registry.BIOME.forEach(biome -> biome.addFeature(
            GenerationStep.Feature.RAW_GENERATION,
            new ShrubsFeature(DefaultFeatureConfig::deserialize).configure(
                new DefaultFeatureConfig()
            )
        ));

        Registry.BIOME.forEach(biome -> {
            if(biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.BEACH) {
                biome.addFeature(
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    new ExtraOreFeature(ExtraOreFeatureConfig::deserialize).configure(
                        new ExtraOreFeatureConfig(Blocks.SAND.getDefaultState(), BlocksRegistry.SALTY_SAND.getDefaultState(), config.saltySandGenOptions.veinSize)
                    ).createDecoratedFeature(
                        Decorator.COUNT_RANGE.configure(
                            new RangeDecoratorConfig(config.saltySandGenOptions.rarity, 0, 0, config.saltySandGenOptions.maxGenHeight)
                        )
                    )
                );
            }
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(Util.id("desalinator"), (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            return world.getBlockState(pos).createContainerFactory(player.world, pos).createMenu(syncId, player.inventory, player);
        });
    }
}
