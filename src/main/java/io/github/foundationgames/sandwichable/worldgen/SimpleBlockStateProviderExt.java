package io.github.foundationgames.sandwichable.worldgen;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class SimpleBlockStateProviderExt extends SimpleBlockStateProvider {
    public SimpleBlockStateProviderExt(BlockState state) {
        super(state);
    }
}
