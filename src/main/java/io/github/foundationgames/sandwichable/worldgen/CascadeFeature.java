package io.github.foundationgames.sandwichable.worldgen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CascadeFeature extends Feature<CascadeFeatureConfig> {
    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    public CascadeFeature() {
        super(CascadeFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<CascadeFeatureConfig> ctx) {
        var pos = ctx.getOrigin();
        var world = ctx.getWorld();
        var random = ctx.getRandom();
        var config = ctx.getConfig();

        BlockPos origin = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()), pos.getZ());

        // Resolve all pool locations and sort them by height
        List<Pair<BlockPos, Integer>> discs = new ArrayList<>();
        int discCt = 2 + random.nextInt(3);
        for (int d = 0; d < discCt; d++) {
            BlockPos p = new BlockPos(
                    origin.getX() + (random.nextInt(config.minDiscSize * 3) - (config.minDiscSize * 1.5)),
                    0,
                    origin.getZ() + (random.nextInt(config.minDiscSize * 3) - (config.minDiscSize * 1.5)));
            p = new BlockPos(p.getX(), world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, p.getX(), p.getZ()), p.getZ());

            if (p.getY() < world.getSeaLevel()) continue;

            discs.add(new Pair<>(p, config.minDiscSize + random.nextInt(config.maxDiscSize - config.minDiscSize)));
        }

        if (discs.size() <= 0) {
            return false;
        }

        discs.sort(Comparator.comparingInt(p -> p.getLeft().getY()));
        Pair<BlockPos, Integer> bottom = discs.get(0);

        // Generate the air gaps and rocks above every pool first
        long salt = random.nextLong();
        for (Pair<BlockPos, Integer> disc : discs) {
            makeDisc(world, random, salt, false, disc.getLeft().add(0, 1, 0), null, AIR, disc.getRight() + 4);
            makeDisc(world, random, salt, false, disc.getLeft().add(0, 2, 0), null, AIR, disc.getRight() + 2);
            makeDisc(world, random, salt, false, disc.getLeft().add(0, 3, 0), null, AIR, disc.getRight() - 1);
            BlockPos.Mutable mpos = new BlockPos.Mutable();
            BlockPos center = disc.getLeft();
            int r = disc.getRight() + 4;
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    mpos.set(center.getX() + x, center.getY() + 1, center.getZ() + z);
                    if (mpos.isWithinDistance(center, disc.getRight() + 3.4) &&
                            !mpos.isWithinDistance(center, disc.getRight() + 1.3) &&
                            random.nextInt(7) == 1
                    ) {
                        for (int h = 0; h < random.nextInt(3); h++) {
                            world.setBlockState(mpos.up(h), config.rocks.getBlockState(random, mpos.up(h)), 2);
                        }
                    }
                }
            }
        }

        // Generate the outer disc for every pool
        for (Pair<BlockPos, Integer> disc : discs) {
            makeDisc(world, random, salt, false, disc.getLeft(), config.outerDisc, AIR, disc.getRight() + 4);
        }

        // Generate the inner disc for every pool
        for (Pair<BlockPos, Integer> disc : discs) {
            BlockPos discPos2d = disc.getLeft().add(0, -disc.getLeft().getY(), 0);
            BlockPos bottomPos2d = bottom.getLeft().add(0, -bottom.getLeft().getY(), 0);
            if (disc != bottom && discPos2d.isWithinDistance(bottomPos2d, (disc.getRight() + bottom.getRight() + 4))) {
                BlockPos offset = disc.getLeft().subtract(bottom.getLeft());
                makeDisc(world, random, salt, true, disc.getLeft(), config.innerDisc, AIR, disc.getRight() + 1, disc.getRight() + 4, Math.atan2(-offset.getZ(), -offset.getX()));
                continue;
            }
            makeDisc(world, random, salt, true, disc.getLeft(), config.innerDisc, AIR, disc.getRight() + 1);
        }

        // Generate the pool discs for every pool
        for (Pair<BlockPos, Integer> disc : discs) {
            BlockPos discPos2d = disc.getLeft().add(0, -disc.getLeft().getY(), 0);
            BlockPos bottomPos2d = bottom.getLeft().add(0, -bottom.getLeft().getY(), 0);
            if (disc != bottom && discPos2d.isWithinDistance(bottomPos2d, (disc.getRight() + bottom.getRight() + 4))) {
                BlockPos offset = disc.getLeft().subtract(bottom.getLeft());
                makeDisc(world, random, salt, true, disc.getLeft(), null, config.pool, disc.getRight(), disc.getRight() + 4, Math.atan2(-offset.getZ(), -offset.getX()));
                continue;
            }
            makeDisc(world, random, salt, true, disc.getLeft(), null, config.pool, disc.getRight());
        }

        // Generate the base block below every pool
        for (Pair<BlockPos, Integer> disc : discs) {
            makeDisc(world, random, salt, false, disc.getLeft().add(0, -1, 0), config.base, AIR, disc.getRight() + 4);
            makeDisc(world, random, salt, false, disc.getLeft().add(0, -2, 0), config.base, AIR, disc.getRight() + 2);
            makeDisc(world, random, salt, false, disc.getLeft().add(0, -3, 0), config.base, AIR, disc.getRight() - 1);
        }

        // Generate the floor discs for every pool
        for (Pair<BlockPos, Integer> disc : discs) {
            BlockPos discPos2d = disc.getLeft().add(0, -disc.getLeft().getY(), 0);
            BlockPos bottomPos2d = bottom.getLeft().add(0, -bottom.getLeft().getY(), 0);
            if (disc != bottom && discPos2d.isWithinDistance(bottomPos2d, (disc.getRight() + bottom.getRight() + 3))) {
                BlockPos offset = disc.getLeft().subtract(bottom.getLeft());
                makeDisc(world, random, salt, true, disc.getLeft().add(0, -1, 0), null, config.floor, disc.getRight(), disc.getRight() + 4, Math.atan2(-offset.getZ(), -offset.getX()));
                continue;
            }
            makeDisc(world, random, salt, true, disc.getLeft().add(0, -1, 0), null, config.floor, disc.getRight());
        }

        return true;
    }

    private void makeDisc(StructureWorldAccess world, Random random, long salt, boolean vary, BlockPos center, @Nullable BlockStateProvider providedBlock, BlockState block, int radius) {
        makeDisc(world, random, salt, vary, center, providedBlock, block, radius, radius, 0);
    }

    private void makeDisc(StructureWorldAccess world, Random random, long salt, boolean vary, BlockPos center, @Nullable BlockStateProvider providedBlock, BlockState block, int radius, int maxRadius, double extAngleRad) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        Random saltedRng = new Random(salt);
        int r = maxRadius + 1;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                pos.set(center.getX() + x, center.getY(), center.getZ() + z);
                double variation = vary ? ((saltedRng.nextDouble() * 1.3)) : 0;
                boolean place = pos.isWithinDistance(center, radius + variation);
                if (maxRadius > radius) {
                    double cAngle = Math.atan2(z, x);
                    if (Math.abs(cAngle - extAngleRad) < (0.05 * radius)) {
                        place = pos.isWithinDistance(center, maxRadius + variation);
                    }
                }
                if (place) {
                    BlockState state = providedBlock == null ? block : providedBlock.getBlockState(random, pos);
                    world.setBlockState(pos, state, 2);
                    if (!state.getFluidState().isEmpty()) {
                        world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), 0);
                    }
                }
            }
        }
    }
}
