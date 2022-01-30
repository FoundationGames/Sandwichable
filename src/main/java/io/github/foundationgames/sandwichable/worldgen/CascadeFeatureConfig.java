package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class CascadeFeatureConfig implements FeatureConfig {
    public final BlockStateProvider innerDisc;
    public final BlockStateProvider outerDisc;
    public final int minDiscSize;
    public final int maxDiscSize;
    public final BlockState pool;
    public final BlockState floor;
    public final BlockStateProvider base;
    public final BlockStateProvider rocks;

    public static final Codec<CascadeFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockStateProvider.TYPE_CODEC.fieldOf("inner_disc").forGetter(c -> c.innerDisc),
                    BlockStateProvider.TYPE_CODEC.fieldOf("outer_disc").forGetter(c -> c.outerDisc),
                    Codec.INT.fieldOf("min_disc_size").forGetter(c -> c.minDiscSize),
                    Codec.INT.fieldOf("max_disc_size").forGetter(c -> c.maxDiscSize),
                    BlockState.CODEC.fieldOf("pool").forGetter(c -> c.pool),
                    BlockState.CODEC.fieldOf("floor").forGetter(c -> c.floor),
                    BlockStateProvider.TYPE_CODEC.fieldOf("base").forGetter(c -> c.base),
                    BlockStateProvider.TYPE_CODEC.fieldOf("rocks").forGetter(c -> c.rocks)
            )
    .apply(instance, CascadeFeatureConfig::new));


    public CascadeFeatureConfig(BlockStateProvider innerDisc, BlockStateProvider outerDisc, int minDiscSize, int maxDiscSize, BlockState pool, BlockState floor, BlockStateProvider base, BlockStateProvider rocks) {
        this.innerDisc = innerDisc;
        this.outerDisc = outerDisc;
        this.minDiscSize = minDiscSize;
        this.maxDiscSize = maxDiscSize;
        this.pool = pool;
        this.floor = floor;
        this.base = base;
        this.rocks = rocks;
    }

    public static CascadeFeatureConfig water() {
        return new CascadeFeatureConfig(
                new SimpleBlockStateProvider(BlocksRegistry.SALTY_ROCKS.getDefaultState()),
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.SAND.getDefaultState(), 3).add(BlocksRegistry.SALTY_SAND.getDefaultState(), 1)),
                4, 6,
                Blocks.WATER.getDefaultState(), BlocksRegistry.SALTY_STONE.getDefaultState(),
                new SimpleBlockStateProvider(Blocks.SANDSTONE.getDefaultState()),
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.STONE.getDefaultState(), 1).add(Blocks.COBBLESTONE.getDefaultState(), 1))
        );
    }

    public static CascadeFeatureConfig dry() {
        return new CascadeFeatureConfig(
                new SimpleBlockStateProvider(BlocksRegistry.SALTY_ROCKS.getDefaultState()),
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.SAND.getDefaultState(), 3).add(BlocksRegistry.SALTY_SAND.getDefaultState(), 1)),
                4, 6,
                BlocksRegistry.SALTY_AIR.getDefaultState(), BlocksRegistry.SALTY_STONE.getDefaultState(),
                new SimpleBlockStateProvider(Blocks.SANDSTONE.getDefaultState()),
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.STONE.getDefaultState(), 1).add(Blocks.COBBLESTONE.getDefaultState(), 1))
        );
    }
}
