package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class RandomTwoTallPatchFeature extends Feature<RandomTwoTallPatchFeatureConfig> {
    public RandomTwoTallPatchFeature(Codec<RandomTwoTallPatchFeatureConfig> configCodec) {
        super(configCodec);
    }
    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, RandomTwoTallPatchFeatureConfig config) {
        BlockPos startPos;
        if (config.project) {
            startPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
        } else {
            startPos = pos;
        }
        int placeCount = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int j = 0; j < config.tries; ++j) {
            BlockState topState = config.topStateProvider.getBlockState(random, pos);
            BlockState bottomState = config.bottomStateProvider.getBlockState(random, pos);
            mutable.set(startPos, random.nextInt(config.spreadX + 1) - random.nextInt(config.spreadX + 1), random.nextInt(config.spreadY + 1) - random.nextInt(config.spreadY + 1), random.nextInt(config.spreadZ + 1) - random.nextInt(config.spreadZ + 1));
            mutable.set(mutable.add(0, config.yOffset, 0).mutableCopy());
            BlockPos placePos = mutable.down();
            BlockState downState = world.getBlockState(placePos);
            if (world.getStructures(ChunkSectionPos.from(pos), ConfiguredFeaturesRegistry.MARKET_FEATURE).count() < 1 && ((!config.canReplace && world.isAir(mutable)) || (config.canReplace && world.getBlockState(mutable).isSolidBlock(world, mutable) && (world.getBlockState(mutable.add(0, 1, 0)).isAir()) && (world.getBlockState(mutable.add(0, 2, 0)).isAir())) && (config.whitelist.isEmpty() || config.whitelist.contains(downState.getBlock())) && !config.blacklist.contains(downState) && !config.blacklist.contains(world.getBlockState(mutable)) && (!config.needsWater || world.getFluidState(placePos.west()).isIn(FluidTags.WATER) || world.getFluidState(placePos.east()).isIn(FluidTags.WATER) || world.getFluidState(placePos.north()).isIn(FluidTags.WATER) || world.getFluidState(placePos.south()).isIn(FluidTags.WATER)))) {
                config.blockPlacer.generate(world, mutable, bottomState, random);
                config.blockPlacer.generate(world, mutable.add(0, 1, 0), topState, random);
                ++placeCount;
            }
        }
        return placeCount > 0;
    }
}
