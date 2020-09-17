package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RandomTwoTallPatchFeatureConfig implements FeatureConfig {

    public static final Codec<RandomTwoTallPatchFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("top_state_provider").forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.topStateProvider),
            BlockStateProvider.TYPE_CODEC.fieldOf("bottom_state_provider").forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.bottomStateProvider),
            BlockPlacer.TYPE_CODEC.fieldOf("block_placer").forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.blockPlacer),
            BlockState.CODEC.listOf().fieldOf("whitelist").forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.whitelist.stream().map(Block::getDefaultState).collect(Collectors.toList())),
            BlockState.CODEC.listOf().fieldOf("blacklist").forGetter((randomPatchFeatureConfig) -> ImmutableList.copyOf(randomPatchFeatureConfig.blacklist)),
            Codec.INT.fieldOf("tries").orElse(128).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.tries),
            Codec.INT.fieldOf("xspread").orElse(7).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.spreadX),
            Codec.INT.fieldOf("yspread").orElse(3).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.spreadY),
            Codec.INT.fieldOf("zspread").orElse(7).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.spreadZ),
            Codec.INT.fieldOf("yoffset").orElse(0).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.yOffset),
            Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.canReplace),
            Codec.BOOL.fieldOf("project").orElse(true).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.project),
            Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((randomPatchFeatureConfig) -> randomPatchFeatureConfig.needsWater)
    ).apply(instance, RandomTwoTallPatchFeatureConfig::new));

    public final BlockStateProvider topStateProvider;
    public final BlockStateProvider bottomStateProvider;
    public final BlockPlacer blockPlacer;
    public final Set<Block> whitelist;
    public final Set<BlockState> blacklist;
    public final int tries;
    public final int spreadX;
    public final int spreadY;
    public final int spreadZ;
    public final int yOffset;
    public final boolean canReplace;
    public final boolean project;
    public final boolean needsWater;

    private RandomTwoTallPatchFeatureConfig(BlockStateProvider topStateProvider, BlockStateProvider bottomStateProvider, BlockPlacer blockPlacer, Set<Block> whitelist, Set<BlockState> blacklist, int tries, int spreadX, int spreadY, int spreadZ, int yOffset, boolean canReplace, boolean project, boolean needsWater) {
        this.topStateProvider = topStateProvider;
        this.bottomStateProvider = bottomStateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.tries = tries;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
        this.yOffset = yOffset;
        this.canReplace = canReplace;
        this.project = project;
        this.needsWater = needsWater;
    }

    private RandomTwoTallPatchFeatureConfig(BlockStateProvider topStateProvider, BlockStateProvider bottomStateProvider, BlockPlacer blockPlacer, List<BlockState> whitelist, List<BlockState> blacklist, int tries, int spreadX, int spreadY, int spreadZ, int yOffset, boolean canReplace, boolean project, boolean needsWater) {
        this(topStateProvider, bottomStateProvider, blockPlacer, whitelist.stream().map(AbstractBlock.AbstractBlockState::getBlock).collect(Collectors.toSet()), ImmutableSet.copyOf(blacklist), tries, spreadX, spreadY, spreadZ, yOffset, canReplace, project, needsWater);
    }

    public static class Builder {
        private final BlockStateProvider topStateProvider;
        private final BlockStateProvider bottomStateProvider;
        private final BlockPlacer blockPlacer;
        private Set<Block> whitelist = ImmutableSet.of();
        private Set<BlockState> blacklist = ImmutableSet.of();
        private int tries = 64;
        private int spreadX = 7;
        private int spreadY = 3;
        private int spreadZ = 7;
        private int yOffset = 0;
        private boolean canReplace;
        private boolean project = true;
        private boolean needsWater = false;

        public Builder(BlockStateProvider topStateProvider, BlockStateProvider bottomStateProvider, BlockPlacer blockPlacer) {
            this.topStateProvider = topStateProvider;
            this.bottomStateProvider = bottomStateProvider;
            this.blockPlacer = blockPlacer;
        }

        public RandomTwoTallPatchFeatureConfig.Builder whitelist(Set<Block> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder blacklist(Set<BlockState> blacklist) {
            this.blacklist = blacklist;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder tries(int tries) {
            this.tries = tries;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder spreadX(int spreadX) {
            this.spreadX = spreadX;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder spreadY(int spreadY) {
            this.spreadY = spreadY;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder spreadZ(int spreadZ) {
            this.spreadZ = spreadZ;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder yOffset(int offset) {
            this.yOffset = offset;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder canReplace() {
            this.canReplace = true;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder cannotProject() {
            this.project = false;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig.Builder needsWater() {
            this.needsWater = true;
            return this;
        }

        public RandomTwoTallPatchFeatureConfig build() {
            return new RandomTwoTallPatchFeatureConfig(this.topStateProvider, this.bottomStateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.spreadX, this.spreadY, this.spreadZ, this.yOffset, this.canReplace, this.project, this.needsWater);
        }
    }
}
