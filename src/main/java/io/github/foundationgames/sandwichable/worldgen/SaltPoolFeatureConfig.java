package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SaltPoolFeatureConfig implements FeatureConfig {
    public final boolean hasWater;

    public static final Codec<SaltPoolFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.BOOL.fieldOf("hasWater").orElse(true).forGetter((cfg) -> cfg.hasWater)
            )
    .apply(instance, SaltPoolFeatureConfig::new));

    public SaltPoolFeatureConfig(boolean hasWater) {
        this.hasWater = hasWater;
    }
}
