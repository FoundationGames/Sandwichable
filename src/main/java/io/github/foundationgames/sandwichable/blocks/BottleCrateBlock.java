package io.github.foundationgames.sandwichable.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BottleCrateBlock extends FacingBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final IntProperty BOTTLE_COUNT_STAGE = IntProperty.of("bottle_count_stage", 0, 4);

    protected BottleCrateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(OPEN, false).with(BOTTLE_COUNT_STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, BOTTLE_COUNT_STAGE);
    }

    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return null;
    }
}
