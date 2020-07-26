package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtraOreFeatureConfig implements FeatureConfig {
    public final BlockState target;
    public final int size;
    public final BlockState state;

    public ExtraOreFeatureConfig(BlockState target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap(ImmutableMap.of(ops.createString("size"), ops.createInt(this.size), ops.createString("target"), BlockState.serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
    }

    public static ExtraOreFeatureConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("size").asInt(0);
        BlockState target = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new ExtraOreFeatureConfig(target, blockState, i);
    }
}
