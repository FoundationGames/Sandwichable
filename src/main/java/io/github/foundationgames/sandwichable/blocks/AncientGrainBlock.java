package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AncientGrainBlock extends CropBlock {
    public static final IntProperty AGE = Properties.AGE_5;

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public AncientGrainBlock(Settings settings) {
        super(settings);
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 5;
    }

    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return ItemsRegistry.ANCIENT_GRAIN_SEEDS;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(3) != 0) {
            super.scheduledTick(state, world, pos, random);
        }
    }

    @Override
    public float getVerticalModelOffsetMultiplier() {
        return super.getVerticalModelOffsetMultiplier();
    }

    @Override
    public float getMaxHorizontalModelOffset() {
        return 0.04f;
    }

    protected int getGrowthAmount(World world) {
        return super.getGrowthAmount(world) / 2;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }
}
