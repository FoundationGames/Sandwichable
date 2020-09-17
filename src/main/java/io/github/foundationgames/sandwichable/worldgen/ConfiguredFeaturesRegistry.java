package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.ibm.icu.impl.CollectionSet;
import io.github.foundationgames.sandwichable.SandwichableEarly;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;

import java.util.*;
import java.util.function.Supplier;

public class ConfiguredFeaturesRegistry {

    private static final Supplier<WeightedBlockStateProvider> CROPS_LIST_CREATOR = () -> {
        WeightedBlockStateProvider p = new WeightedBlockStateProvider();
        for(Block block : Registry.BLOCK) {
            if(block instanceof CropBlock) {
                CropBlock crop = (CropBlock)block;
                for (int i = 0; i < crop.getMaxAge() - (crop.getMaxAge() / 2); i++) {
                    p.addState(crop.getDefaultState().with(crop.getAgeProperty(), crop.getMaxAge() - i), 1);
                }
            }
        }
        p.addState(Blocks.AIR.getDefaultState(), 3);
        p.addState(BlocksRegistry.SHRUB.getDefaultState(), 2);
        p.addState(Blocks.OAK_LEAVES.getDefaultState(), 2);
        p.addState(Blocks.DARK_OAK_LEAVES.getDefaultState(), 1);
        p.addState(Blocks.BIRCH_LEAVES.getDefaultState(), 1);
        return p;
    };

    private static final Supplier<Set<BlockState>> BLACKLISTED_VEGETABLE_STATES = () -> {
        Set<BlockState> set = new HashSet<BlockState>();
        set.addAll(Blocks.CARVED_PUMPKIN.getStateManager().getStates());
        set.addAll(Blocks.JACK_O_LANTERN.getStateManager().getStates());
        set.add(Blocks.PUMPKIN.getDefaultState());
        set.add(Blocks.MOSSY_COBBLESTONE.getDefaultState());
        return ImmutableSet.copyOf(set);
    };

    public static final ConfiguredFeature<?, ?> COMMON_PUMPKIN_PATCH = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "sandwichable:forest_patch_pumpkin", Feature.RANDOM_PATCH.configure(
            new RandomPatchFeatureConfig.Builder(
                    new WeightedBlockStateProvider()
                    .addState(Blocks.PUMPKIN.getDefaultState(), 64)
                    .addState(Blocks.JACK_O_LANTERN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH), 1)
                    .addState(Blocks.JACK_O_LANTERN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.SOUTH), 1)
                    .addState(Blocks.JACK_O_LANTERN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST), 1)
                    .addState(Blocks.JACK_O_LANTERN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.WEST), 1)
                    .addState(Blocks.CARVED_PUMPKIN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH), 2)
                    .addState(Blocks.CARVED_PUMPKIN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.SOUTH), 2)
                    .addState(Blocks.CARVED_PUMPKIN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST), 2)
                    .addState(Blocks.CARVED_PUMPKIN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.WEST), 2),
                    SimpleBlockPlacer.INSTANCE
            ).tries(6).blacklist(BLACKLISTED_VEGETABLE_STATES.get()).whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK)).build()
    ).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE.repeat(3)));

    public static final ConfiguredFeature<?, ?> VEGETABLE_PATCH = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "sandwichable:patch_vegetable", SandwichableEarly.RANDOM_TWO_TALL_PATCH.configure(
            new RandomTwoTallPatchFeatureConfig.Builder(
                    CROPS_LIST_CREATOR.get(),
                    new SimpleBlockStateProvider(BlocksRegistry.FERTILE_SOIL.getDefaultState()),
                    SimpleBlockPlacer.INSTANCE
            ).yOffset(-1).canReplace().tries(100).whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK, Blocks.DIRT)).build()
    ).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE.repeat(2)));

    public static final ConfiguredFeature<?, ?> LEAF_PILE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "sandwichable_leaf_pile", Feature.BLOCK_PILE.configure(
            new BlockPileFeatureConfig(
                    new WeightedBlockStateProvider()
                            .addState(Blocks.OAK_LEAVES.getDefaultState(), 6)
                            .addState(Blocks.BIRCH_LEAVES.getDefaultState(), 4)
                            .addState(Blocks.DARK_OAK_LEAVES.getDefaultState(), 2)
            )
    ).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE.repeat(60)));


    public static final ConfiguredFeature<?, ?> SPARSE_DARK_OAK = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "sandwichable:sparse_dark_oak", Feature.TREE.configure(
            new TreeFeatureConfig.Builder(
                    new SimpleBlockStateProvider(Blocks.DARK_OAK_LOG.getDefaultState()),
                    new SimpleBlockStateProvider(Blocks.DARK_OAK_LEAVES.getDefaultState().with(Properties.PERSISTENT, false)),
                    new DarkOakFoliagePlacer(
                            UniformIntDistribution.of(0),
                            UniformIntDistribution.of(0)
                    ),
                    new DarkOakTrunkPlacer(6, 2, 1),
                    new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
            ).maxWaterDepth(2147483647).heightmap(Heightmap.Type.MOTION_BLOCKING).ignoreVines().build()
    ).withChance(0.06f).feature.get());

    public static void init() {
    }
}
