package io.github.foundationgames.sandwichable.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FertileSoilBlock extends Block {
    public static final BooleanProperty SOLID;

    public FertileSoilBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(SOLID, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return solidify(super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom), world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(0, 0, 0, 16, state.get(SOLID) ? 16 : 15, 16);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SOLID);
    }

    private static BlockState solidify(BlockState state, WorldAccess world, BlockPos pos) {
        return state.with(SOLID, world.getBlockState(pos.up()).isSideSolidFullSquare(world, pos.up(), Direction.DOWN));
    }

    static {
        SOLID = BooleanProperty.of("solid");
    }
}
