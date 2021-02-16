package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SaltPoolFeature extends Feature<SaltPoolFeatureConfig> {
    public SaltPoolFeature() {
        super(SaltPoolFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos spos, SaltPoolFeatureConfig config) {
        int yfs = 0;
        int topY = Math.max(world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, spos.getX(), spos.getZ()), 64);
        if(random.nextInt(2) == 2 && topY > 65) yfs = random.nextInt(3);
        spos = new BlockPos(spos.getX(), topY - (1 + yfs), spos.getZ());
        BlockState liquidState = config.hasWater ? Blocks.WATER.getDefaultState() : BlocksRegistry.SALTY_AIR.getDefaultState();
        for(int i = 0; i < 2 + random.nextInt(2); i++) {
            BlockPos pos = spos.add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
            int rad = random.nextInt(3) + 3;
            CuboidBlockIterator iter = new CuboidBlockIterator(pos.getX() - (rad + 5), pos.getY(), pos.getZ() - (rad + 5), pos.getX() + (rad + 5), pos.getY(), pos.getZ() + (rad + 5));
            double sBorder = 2.3 + (random.nextDouble() * 1.73);
            for(BlockPos.Mutable mpos = new BlockPos.Mutable(); iter.step();) {
                mpos.set(iter.getX(), iter.getY(), iter.getZ());
                double dist = Math.sqrt(Math.pow(pos.getX() - mpos.getX(), 2) + Math.pow(pos.getZ() - mpos.getZ(), 2));
                double variator = (random.nextDouble() - 0.5d) * 0.64d;
                if(dist <= rad + sBorder + variator) {
                    if(!world.getBlockState(mpos).equals(liquidState) && !world.getBlockState(mpos).isOf(BlocksRegistry.SALTY_ROCKS)) {
                        if(random.nextInt(4) == 2) world.setBlockState(mpos, BlocksRegistry.SALTY_SAND.getDefaultState(), 2);
                        else world.setBlockState(mpos, Blocks.SAND.getDefaultState(), 2);
                    }
                    if(!world.getBlockState(mpos.down()).isOf(BlocksRegistry.SALTY_STONE)) world.setBlockState(mpos.down(), Blocks.SANDSTONE.getDefaultState(), 2);
                    world.setBlockState(mpos.up(), Blocks.AIR.getDefaultState(), 2);
                }
                if(dist <= rad + sBorder - 1.2 + variator) {
                    world.setBlockState(mpos.down(2), Blocks.SANDSTONE.getDefaultState(), 2);
                    world.setBlockState(mpos.up(2), Blocks.AIR.getDefaultState(), 2);
                }
                if(dist <= rad + 1.18 + variator) {
                    if(!world.getBlockState(mpos).equals(liquidState)) world.setBlockState(mpos, BlocksRegistry.SALTY_ROCKS.getDefaultState(), 2);
                }
                if(dist <= rad + variator) {
                    world.setBlockState(mpos, liquidState, 2);
                    world.setBlockState(mpos.down(), BlocksRegistry.SALTY_STONE.getDefaultState(), 2);
                }
                if(dist <= rad + sBorder - 3.4 + variator) {
                    world.setBlockState(mpos.down(3), Blocks.SANDSTONE.getDefaultState(), 2);
                    world.setBlockState(mpos.up(3), Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
        for(int j = 0; j < 10 + random.nextInt(6); j++) {
            BlockPos pos = spos.add(random.nextInt(28) - 14, 1, random.nextInt(28) - 14);
            CuboidBlockIterator iter = new CuboidBlockIterator(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            for(BlockPos.Mutable mpos = new BlockPos.Mutable(); iter.step();) {
                mpos.set(iter.getX(), iter.getY(), iter.getZ());
                if(random.nextBoolean() && world.getBlockState(mpos.down()).isOf(Blocks.SAND)) {
                    for (int k = 0; k < random.nextInt(3); k++) {
                        world.setBlockState(mpos.up(k), random.nextBoolean() ? Blocks.COBBLESTONE.getDefaultState() : Blocks.STONE.getDefaultState(), 2);
                    }
                }
            }
        }
        return true;
    }
}
