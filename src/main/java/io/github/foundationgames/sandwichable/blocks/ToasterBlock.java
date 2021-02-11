package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.ToasterBlockEntity;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ToasterBlock extends HorizontalFacingBlock implements BlockEntityProvider, Waterloggable {

    public static final BooleanProperty ON;
    public static final BooleanProperty WATERLOGGED;

    public static final VoxelShape EAST_WEST_SHAPE;
    public static final VoxelShape NORTH_SOUTH_SHAPE;

    public ToasterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(ON, false).with(WATERLOGGED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ToasterBlockEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING, ON, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof ToasterBlockEntity) {
            return ((ToasterBlockEntity)world.getBlockEntity(pos)).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved);
        if(world.getBlockEntity(pos) instanceof ToasterBlockEntity) {
            boolean toasting = ((ToasterBlockEntity)world.getBlockEntity(pos)).isToasting();
            world.setBlockState(pos, world.getBlockState(pos).with(ON, toasting));
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(world.getBlockEntity(pos) instanceof ToasterBlockEntity) {
            boolean toasting = ((ToasterBlockEntity)world.getBlockEntity(pos)).isToasting();
            world.setBlockState(pos, world.getBlockState(pos).with(ON, toasting));
        }

    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ToasterBlockEntity) {
                ToasterBlockEntity blockEntity = (ToasterBlockEntity)world.getBlockEntity(pos);
                for (int i = 0; i < 2; i++) {
                    ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, blockEntity.getItems().get(i));
                    world.spawnEntity(item);
                }
                world.updateNeighbors(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof ToasterBlockEntity) {
            ToasterBlockEntity blockEntity = (ToasterBlockEntity)world.getBlockEntity(pos);
            if(!player.isSneaking()) {
                if(!blockEntity.isToasting()) {
                    if (!player.getStackInHand(hand).isEmpty() && !player.getStackInHand(hand).getItem().equals(BlocksRegistry.SANDWICH.asItem())) {
                        if (!blockEntity.addItem(hand, player)) {
                            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, blockEntity.takeItem());
                            world.spawnEntity(itemEntity);
                        }
                    } else {
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, blockEntity.takeItem());
                        world.spawnEntity(itemEntity);
                    }
                }
            } else {
                if(!blockEntity.isToasting()) { blockEntity.startToasting(); } else { blockEntity.stopToasting(); }
            }
            Util.sync(blockEntity, world);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        switch(dir) {
            case NORTH:
            case SOUTH:
                return NORTH_SOUTH_SHAPE;
            case EAST:
            case WEST:
                return EAST_WEST_SHAPE;
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return this.getOutlineShape(state, view, pos, ctx);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    static {
        NORTH_SOUTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3, 0, 1, 13, 1, 15),
            Block.createCuboidShape(4, 1, 2, 12, 10, 14)
        );
        EAST_WEST_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(1, 0, 3, 15, 1, 13),
                Block.createCuboidShape(2, 1, 4, 14, 10, 12)
        );

        ON = BlockProperties.ON;
        WATERLOGGED = Properties.WATERLOGGED;
    }
}
