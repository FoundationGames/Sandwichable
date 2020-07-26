package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ExtraOreFeatureConfig implements FeatureConfig {

    public static final Codec<ExtraOreFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
            BlockState.CODEC.fieldOf("state").forGetter((oreFeatureConfig) -> oreFeatureConfig.target),
            BlockState.CODEC.fieldOf("state").forGetter((oreFeatureConfig) -> oreFeatureConfig.state),
            Codec.INT.fieldOf("size").withDefault(0).forGetter((oreFeatureConfig) -> oreFeatureConfig.size)
        )
    .apply(instance, ExtraOreFeatureConfig::new));

    public final BlockState target;
    public final int size;
    public final BlockState state;

    public ExtraOreFeatureConfig(BlockState target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }
}
