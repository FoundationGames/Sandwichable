package io.github.foundationgames.sandwichable.worldgen;

import com.mojang.serialization.Codec;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.ShrubBlock;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class ShrubsFeature extends Feature<DefaultFeatureConfig> {

    public ShrubsFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos bpos, DefaultFeatureConfig featureConfig) {
        SandwichableConfig sconfig = Util.getConfig();

        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, bpos);

        BlockState blockState = BlocksRegistry.SHRUB.getDefaultState();
        int i = 0;
        for(int j = 0; j < sconfig.shrubGenOptions.spawnTries; j++) {
            BlockPos blockPos = randPosInRadius(random, pos, 5);
            if (world.isAir(blockPos) && blockPos.getY() < 255 && ShrubBlock.canGenerateOn(blockState, world, blockPos)) {
                world.setBlockState(blockPos, blockState, 2);
                i++;
            }
        }

        return i > 0;
    }

    private BlockPos randPosInRadius(Random random, BlockPos pos, int radius) {
        int x = pos.getX() + random.nextInt(radius*2) - radius;
        int y = pos.getY() + random.nextInt(radius*2) - radius;
        int z = pos.getZ() + random.nextInt(radius*2) - radius;
        return new BlockPos(x, y, z);
    }
}
