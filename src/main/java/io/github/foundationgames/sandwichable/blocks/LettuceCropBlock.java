package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class LettuceCropBlock extends CropBlock {
    public static final IntProperty AGE;
    private static final VoxelShape[] AGE_TO_SHAPE;

    public LettuceCropBlock(Settings settings) {
        super(settings);
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 4;
    }

    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return ItemsRegistry.LETTUCE_SEEDS;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(4) != 0) {
            super.scheduledTick(state, world, pos, random);
        }

    }

    protected int getGrowthAmount(World world) {
        return super.getGrowthAmount(world) / 2;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[(Integer)state.get(this.getAgeProperty())];
    }

    static {
        AGE = BlockProperties.AGE;
        AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D), Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 3.0D, 12.0D), Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 4.0D, 13.0D), Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D), Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};
    }
}
